package com.slodon.b2b2c.socket.helpdesk.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.slodon.b2b2c.core.constant.BizTypeConst;
import com.slodon.b2b2c.core.constant.HelpdeskConst;
import com.slodon.b2b2c.core.constant.VendorConst;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.helpdesk.dto.VendorContactDTO;
import com.slodon.b2b2c.helpdesk.example.HelpdeskAutomaticMsgExample;
import com.slodon.b2b2c.helpdesk.pojo.HelpdeskAutomaticMsg;
import com.slodon.b2b2c.helpdesk.pojo.HelpdeskMsg;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.model.helpdesk.HelpdeskAutomaticMsgModel;
import com.slodon.b2b2c.model.member.MemberModel;
import com.slodon.b2b2c.model.seller.StoreModel;
import com.slodon.b2b2c.model.seller.VendorModel;
import com.slodon.b2b2c.seller.example.VendorExample;
import com.slodon.b2b2c.seller.pojo.Store;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.socket.helpdesk.entity.ImSocketUser;
import com.slodon.b2b2c.socket.helpdesk.entity.VendorServiceInfo;
import com.slodon.b2b2c.util.MongoDBUtil;
import com.slodon.b2b2c.vo.helpdesk.HelpdeskMsgVO;
import com.slodon.smartid.client.utils.SmartId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ImSocketServer {

    @Resource
    private SocketIOServer imSocketIOServer;
    @Resource
    private RedisTemplate<String, Object> objectRedisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private HelpdeskAutomaticMsgModel helpdeskAutomaticMsgModel;
    @Resource
    private MemberModel memberModel;
    @Resource
    private VendorModel vendorModel;
    @Resource
    private StoreModel storeModel;

    public static String ROOM_ID_PREFIX = "helpdesk_room_id_";//房间号前缀，前缀+storeId+'_'+memberId
    public static String MSG_PREFIX = "helpdesk_msg";//存入MongoDB中的消息记录
    public static String MEMBER_CONTACT_PREFIX = "helpdesk_member_contact";//存入MongoDB中的会员最近联系人
    public static String VENDOR_CONTACT_PREFIX = "helpdesk_vendor_contact";//存入MongoDB中的客服最近联系人
    public static String MEMBER_SOURCE_URL_PREFIX = "helpdesk_member_source_url";//会员聊天来源链接

    private static Map<String/*店铺id*/, Map<String/*vendor客服id*/, VendorServiceInfo/*服务信息*/>> storeVendorMap = new HashMap<>();//店铺内在线的客服
    private static Set<String/*memberId-storeId*/> memberStateSet = new HashSet<>();//会员对某店铺的在线状态，会员未关闭聊天框说明对该店铺在线
    private static Map<String/*memberId*/, List<SocketIOClient>> memberListenClientMap = new HashMap<>();//处于最近联系店铺列表状态下的会员客户端map，当有客服发送消息时，更新最近联系人
    private static Map<String/*socket clientId*/, ImSocketUser/*socket客户端对应的用户信息*/> socketUserMap = new HashMap<>();//客户端连接后，保存其连接的用户信息
    private static Map<String/*memberId-storeId*/,List<SocketIOClient>> memberStoreClientsMap = new HashMap<>();//会员与店铺聊天的客户端列表map

    /**
     * Spring IoC容器创建之后，在加载 SlodonSocketServer Bean之后启动
     */
    @PostConstruct
    public void autoStartup() {
        onStart();
    }

    /**
     * Spring IoC容器在销毁 SlodonSocketServer Bean之前关闭,避免重启项目服务端口占用问题
     *
     * @throws Exception
     */
    @PreDestroy
    public void autoStop() throws Exception {
        stop();
    }

    private void onStart() {
        //连接监听器
        imSocketIOServer.addConnectListener(client -> {//连接的客户端
            log.debug("---------------------------------");
            log.debug("客户端：{}连接成功",client.getSessionId().toString());
            log.debug("---------------------------------");
        });

        imSocketIOServer.addEventListener("connect_success", Map.class, (client, data, ackSender) -> {
            //获取参数
            String storeId = data.get("storeId").toString();//店铺id
            String userId =data.get("userId").toString();//memberId或vendorId
            String role = data.get("role").toString();//角色类型，1==会员，2==vendor客服

            //保存客户端信息
            socketUserMap.put(client.getSessionId().toString(),new ImSocketUser(storeId,userId,role));

            if (StringUtils.isEmpty(storeId)) {
                //会员查看最近聊天列表
                //将会员客户端加入到最近联系人监听map
                if (memberListenClientMap.containsKey(userId)) {
                    memberListenClientMap.get(userId).add(client);
                } else {
                    List<SocketIOClient> clientList = new ArrayList<>();
                    clientList.add(client);
                    memberListenClientMap.put(userId, clientList);
                }
                return;
            }

            if (HelpdeskConst.ROLE_TYPE_MEMBER.equals(role)) {
                //会员连接
                //分配客服
                VendorServiceInfo vendorServiceInfo = this.assignVendor(userId, storeId);
                String vendorId;
                String roomId = ROOM_ID_PREFIX + storeId + "_" + userId;
                if (vendorServiceInfo == null) {
                    log.debug("-------------------没有在线客服，分配给主账号");
                    //没有客服在线,查询店铺主账号
                    VendorExample example = new VendorExample();
                    example.setStoreId(Long.valueOf(storeId));
                    example.setIsStoreAdmin(VendorConst.IS_STORE_ADMIN_1);
                    Vendor vendor = vendorModel.getVendorList(example, null).get(0);

                    client.joinRoom(roomId);
                    vendorId = vendor.getVendorId().toString();
                } else {
                    //创建客服-会员房间
                    client.joinRoom(roomId);
                    vendorServiceInfo.getVendorClient().forEach(socketIOClient -> {
                        if (socketIOClient.isChannelOpen()) {
                            socketIOClient.joinRoom(roomId);
                        }
                    });
                    vendorId = vendorServiceInfo.getVendorId();
                    log.debug("-------------------分配给在线客服：{}", vendorId);
                }

                String memberStoreKey = userId + "-" + storeId;
                //会员对该店铺在线
                memberStateSet.add(memberStoreKey);
                //保存会员-店铺的客户端
                if (memberStoreClientsMap.containsKey(memberStoreKey)){
                    memberStoreClientsMap.get(memberStoreKey).add(client);
                }else {
                    List<SocketIOClient> list = new ArrayList<>();
                    list.add(client);
                    memberStoreClientsMap.put(memberStoreKey,list);
                }
                while (StringUtils.isEmpty(vendorId)) {
                    //阻塞等待
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                //发送房间信息
                JSONObject roomInfo = new JSONObject();
                roomInfo.put("memberId", userId);
                roomInfo.put("roomId", roomId);
                roomInfo.put("vendorId", vendorId);
                imSocketIOServer.getRoomOperations(roomId).sendEvent("get_room_info", roomInfo);

                String welcomeRedisKey = "helpdesk_welcome_msg_" + storeId + "_" + userId;//欢迎语消息记录redis key
                if (stringRedisTemplate.opsForValue().get(welcomeRedisKey) == null) {
                    //今天未发送过欢迎语，发送欢迎消息
                    HelpdeskAutomaticMsgExample example = new HelpdeskAutomaticMsgExample();
                    example.setIsShow(HelpdeskConst.IS_SHOW_YES);
                    example.setStoreId(Long.valueOf(storeId));
                    List<HelpdeskAutomaticMsg> automaticMsgList = helpdeskAutomaticMsgModel.getHelpdeskAutomaticMsgList(example, null);
                    if (!CollectionUtils.isEmpty(automaticMsgList)) {
                        //店铺有欢迎语，发送信息
                        ModelMap modelMap = new ModelMap();
                        modelMap.put("content", automaticMsgList.get(0).getMsgContent());
                        this.sendMsg(userId, vendorId, storeId, modelMap, HelpdeskConst.ROLE_TYPE_VENDOR, roomId, HelpdeskConst.MSG_TYPE_TEXT + "", HelpdeskConst.MSG_STATE_YES);

                        //记录店铺对该会员已发送过欢迎语，到今天24点过期
                        //计算今天还有多少毫秒过完
                        long remainedTime = TimeUtil.getDayEndFormatYMDHMS(new Date()).getTime() - new Date().getTime();
                        stringRedisTemplate.opsForValue().set(welcomeRedisKey, "1", remainedTime, TimeUnit.MILLISECONDS);
                    }
                }

                //来源链接
                String sourceUrl = data.get("sourceUrl").toString();
                //记录来源url
                MongoDBUtil.upsertSourceUrl(MEMBER_SOURCE_URL_PREFIX, sourceUrl, Long.parseLong(storeId), Integer.valueOf(userId));
            } else {
                //客服连接，添加到在线客服列表
                VendorServiceInfo vendorServiceInfo = new VendorServiceInfo();
                vendorServiceInfo.setVendorId(userId);
                vendorServiceInfo.getVendorClient().add(client);
                if (storeVendorMap.containsKey(storeId)) {
                    //店铺客服map存在,构造新的客服加入到店铺中
                    storeVendorMap.get(storeId).put(userId, vendorServiceInfo);
                } else {
                    //店铺没有在线的客服，构造新的客服map
                    Map<String/*vendor客服id*/, VendorServiceInfo/*服务信息*/> vendorMap = new HashMap<>();
                    vendorMap.put(userId, vendorServiceInfo);
                    storeVendorMap.put(storeId, vendorMap);
                }
            }
        });

        //断开连接监听器
        imSocketIOServer.addDisconnectListener(client -> {
            //获取用户信息
            ImSocketUser imSocketUser = socketUserMap.get(client.getSessionId().toString());
            String storeId = imSocketUser.getStoreId();//店铺id
            String userId = imSocketUser.getUserId();//memberId或vendorId
            String role = imSocketUser.getRole();//角色类型，1==会员，2==vendor客服

            if (role.equals(HelpdeskConst.ROLE_TYPE_MEMBER)) {
                //会员离线
                //退出房间
                client.getAllRooms().forEach(client::leaveRoom);
                client.disconnect();

                String memberStoreKey = userId + "-" + storeId;
                //移除会员当前查看的聊天窗口
                memberStateSet.remove(memberStoreKey);
                if (memberStoreClientsMap.containsKey(memberStoreKey)){
                    memberStoreClientsMap.get(memberStoreKey).remove(client);
                }
            } else {
                //客服离线
                VendorServiceInfo vendorServiceInfo = storeVendorMap.get(storeId).get(userId);
                //查询该商户所有客户端
                vendorServiceInfo.getVendorClient().forEach(socketIOClient -> {
                    if (socketIOClient.isChannelOpen()) {
                        socketIOClient.getAllRooms().forEach(socketIOClient::leaveRoom);
                    }
                });
                //更新客服当前查看的聊天窗口
                storeVendorMap.get(storeId).remove(userId);
                client.disconnect();
            }
        });

        //发送消息监听器
        imSocketIOServer.addEventListener("send_msg", Map.class, (client, data, ackSender) -> {
            //获取握手数据
            String storeId = data.get("storeId").toString();//店铺id
            String userId = data.get("userId").toString();//memberId或vendorId
            String role = data.get("role").toString();//角色类型，1==会员，2==vendor客服
            log.debug(data.toString());
            String memberId;
            String vendorId;
            Map msg = (Map) data.get("msg");
            String msgType = data.get("msgType").toString(); //"消息类型:1.text(文本) 2.img(图片) 3.goods(商品) 4.order(订单)用户, 5.常见问题"
            String roomId;//房间id
            if (role.equals(HelpdeskConst.ROLE_TYPE_MEMBER)) {
                //会员发送
                memberId = userId;
                vendorId = data.get("vendorId").toString();
            } else {
                //客服发送
                memberId = data.get("memberId").toString();
                vendorId = userId;
            }
            roomId = ROOM_ID_PREFIX + storeId + "_" + memberId;

            //发送消息
            if (msgType.equals(HelpdeskConst.MSG_TYPE_FAQ + "")) {
                //常见问题，发送两次普通消息
                String content = msg.get("content").toString();
                ModelMap msgContent = new ModelMap();//问题内容
                msgContent.put("content", content);
                //发送问题
                this.sendMsg(memberId, vendorId, storeId, msgContent, HelpdeskConst.ROLE_TYPE_MEMBER, roomId, HelpdeskConst.MSG_TYPE_TEXT + "", HelpdeskConst.MSG_STATE_YES);

                ModelMap replyContent = new ModelMap();//回复内容
                String reply = msg.get("reply").toString();
                replyContent.put("content", reply);
                //发送回复
                this.sendMsg(memberId, vendorId, storeId, replyContent, HelpdeskConst.ROLE_TYPE_VENDOR, roomId, HelpdeskConst.MSG_TYPE_TEXT + "", HelpdeskConst.MSG_STATE_YES);
            } else {
                this.sendMsg(memberId, vendorId, storeId, msg, role, roomId, msgType, null);
            }
        });

        //客服切换房间号监听器
        imSocketIOServer.addEventListener("vendor_change_room", Map.class, (client, data, ackSender) -> {
            //获取握手数据
            String storeId = data.get("storeId").toString();//店铺id
            String userId = data.get("userId").toString();//memberId或vendorId
            String role = data.get("role").toString();//角色类型，1==会员，2==vendor客服

            //切换前的会员id
            Object oldMemberId = data.get("oldMemberId");
            if (oldMemberId != null) {
                String oldRoomId = ROOM_ID_PREFIX + storeId + "_" + oldMemberId;
                //更新客服当前查看的聊天窗口
                storeVendorMap.get(storeId).get(userId).getRoomId().remove(oldRoomId);
                //离开房间
                client.leaveRoom(oldRoomId);
            }

            //切换后的房间号
            String memberId = data.get("memberId").toString();
            String roomId = ROOM_ID_PREFIX + storeId + "_" + memberId;
            //更新客服当前查看的聊天窗口
            storeVendorMap.get(storeId).get(userId).getRoomId().add(roomId);
            //进房间
            client.joinRoom(roomId);

            //发送房间信息
            JSONObject roomInfo = new JSONObject();
            roomInfo.put("memberId", memberId);
            roomInfo.put("roomId", roomId);
            roomInfo.put("vendorId", userId);
            client.sendEvent("get_room_info", roomInfo);

            //发送会员来源url
            String sourceUrl = MongoDBUtil.getSourceUrl(MEMBER_SOURCE_URL_PREFIX, Long.parseLong(storeId), Integer.parseInt(memberId));//来源链接
            JSONObject sourceUrlInfo = new JSONObject();
            sourceUrlInfo.put("sourceUrl", sourceUrl);
            client.sendEvent("get_member_source_url", sourceUrlInfo);

        });

        //会员切换房间号监听器
        imSocketIOServer.addEventListener("member_change_room", Map.class, (client, data, ackSender) -> {
            //获取握手数据
            String storeId = data.get("storeId").toString();//店铺id
            String userId = data.get("userId").toString();//memberId或vendorId
            String role = data.get("role").toString();//角色类型，1==会员，2==vendor客服

            //切换前的会员id
            Object oldStoreId = data.get("oldStoreId");
            if (oldStoreId != null) {
                String oldRoomId = ROOM_ID_PREFIX + oldStoreId + "_" + userId;
                //更新会员当前查看的聊天窗口
                memberStateSet.remove(userId + "-" + oldStoreId);
                //离开房间
                client.leaveRoom(oldRoomId);
            }

            //切换后的房间号
            String roomId = ROOM_ID_PREFIX + storeId + "_" + userId;
            //更新会员当前查看的聊天窗口
            memberStateSet.add(userId + "-" + storeId);
            //进房间
            client.joinRoom(roomId);
        });

        //预转接事件，返回店铺在线的客服列表
        imSocketIOServer.addEventListener("pre_switch_vendor", Map.class, (client, data, ackSender) -> {
            //获取握手数据
            String storeId = data.get("storeId").toString();//店铺id
            String userId = data.get("userId").toString();//memberId或vendorId
            String role = data.get("role").toString();//角色类型，1==会员，2==vendor客服

            List<Map<String, Object>> vendorList = new ArrayList<>();
            storeVendorMap.get(storeId).keySet().forEach(vendorId -> {
                if (!vendorId.equals(userId)) {
                    //过滤当前请求转接的客服
                    Vendor vendor = vendorModel.getVendorByVendorId(Long.valueOf(vendorId));
                    Map<String, Object> map = new HashMap<>();
                    map.put("vendorId", vendor.getVendorId());
                    map.put("vendorName", vendor.getVendorName());
                    map.put("vendorAvatar", FileUrlUtil.getFileUrl(vendor.getAvatarUrl(), null));
                    vendorList.add(map);
                }
            });
            log.debug("=============" + vendorList.toString());
            //发送客服列表数据
            ackSender.sendAckData(vendorList);
        });

        //客服转接
        imSocketIOServer.addEventListener("switch_vendor", Map.class, (client, data, ackSender) -> {
            //获取握手数据
            String storeId = data.get("storeId").toString();//店铺id
            String userId = data.get("userId").toString();//memberId或vendorId
            String role = data.get("role").toString();//角色类型，1==会员，2==vendor客服

            String switchVendorId = data.get("switchVendorId").toString();
            String memberId = data.get("memberId").toString();
            String roomId = ROOM_ID_PREFIX + storeId + "_" + memberId;

            Map<String, Object> returnData = new HashMap<>();
            //查询要转接的客服是否在线
            if (!storeVendorMap.get(storeId).containsKey(switchVendorId)) {
                //要转接的客服不在线
                returnData.put("state", 250);
                returnData.put("msg", "转接失败，客服已离线");
            } else {
                //客服在线
                VendorServiceInfo oldVendor = storeVendorMap.get(storeId).get(userId);
                //将原客服从房间移除
                client.leaveRoom(roomId);
                oldVendor.getVendorClient().forEach(socketIOClient -> {
                    socketIOClient.leaveRoom(roomId);
                });
                //获取最近一次聊天记录的id
                VendorContactDTO vendorContact = MongoDBUtil.getVendorContact(VENDOR_CONTACT_PREFIX, Long.parseLong(userId), Integer.valueOf(memberId));
                Long msgId = vendorContact.getMsgId();
                //移除最近联系记录
                MongoDBUtil.deleteByVendorIdAndMemberId(VENDOR_CONTACT_PREFIX, Long.parseLong(userId), Integer.valueOf(memberId));
                //修改原客服服务客户数
                oldVendor.getRoomId().remove(roomId);
                oldVendor.setMemberNum(oldVendor.getMemberNum() - 1);
                oldVendor.getMemberIdList().remove(memberId);

                //新客服信息修改
                VendorServiceInfo newVendor = storeVendorMap.get(storeId).get(switchVendorId);
                newVendor.setMemberNum(newVendor.getMemberNum() + 1);
                newVendor.getMemberIdList().add(memberId);
                //新客服加入房间
                newVendor.getVendorClient().forEach(socketIOClient -> {
                    if (socketIOClient.isChannelOpen()) {
                        socketIOClient.joinRoom(roomId);
                    }
                });
                //插入新客服的最近联系人
                MongoDBUtil.upsertVendorContact(VENDOR_CONTACT_PREFIX, Long.parseLong(switchVendorId), Integer.valueOf(memberId), msgId,vendorContact.getMemberName());

                //给新客服发送消息变动
                HelpdeskMsg helpdeskMsg = MongoDBUtil.getChatDetail(MSG_PREFIX, msgId);
                //处理未读消息数
                Long unreadNum = MongoDBUtil.getUnReadNum(MSG_PREFIX, Long.parseLong(storeId), Integer.parseInt(memberId), HelpdeskConst.ROLE_TYPE_MEMBER);
                helpdeskMsg.setReceiveMsgNumber(StringUtil.isNullOrZero(unreadNum) ? 0 : unreadNum.intValue());

                HelpdeskMsgVO vo = new HelpdeskMsgVO(helpdeskMsg);
                newVendor.getVendorClient().forEach(socketIOClient -> {
                    socketIOClient.sendEvent("contact_change", vo);
                });

                String memberStoreKey = memberId + "-" + storeId;
                //给会员发送新的房间客服信息
                if (memberStoreClientsMap.containsKey(memberStoreKey)){
                    //发送房间信息
                    JSONObject roomInfo = new JSONObject();
                    roomInfo.put("memberId", memberId);
                    roomInfo.put("roomId", roomId);
                    roomInfo.put("vendorId", switchVendorId);
                    memberStoreClientsMap.get(memberStoreKey).forEach(socketIOClient -> {
                        socketIOClient.sendEvent("get_room_info", roomInfo);
                    });
                }

                returnData.put("state", 200);
                returnData.put("msg", "转接成功");
            }

            ackSender.sendAckData(returnData);
        });

        //聊天消息已读
        imSocketIOServer.addEventListener("read_msg", Map.class, (client, data, ackSender) -> {
            //获取握手数据
            String storeId = data.get("storeId").toString();//店铺id
            String userId = data.get("userId").toString();//memberId或vendorId
            String role = data.get("role").toString();//角色类型，1==会员，2==vendor客服

            String msgIds = data.get("msgIds").toString();
            if (StringUtils.isEmpty(msgIds)) {
                //没有要读的消息，直接返回
                return;
            }
            String memberId;
            String roomId;
            Long unreadNum = 0L;
            if (role.equals(HelpdeskConst.ROLE_TYPE_MEMBER)) {
                //会员读信息
                memberId = userId;
                roomId = ROOM_ID_PREFIX + storeId + "_" + memberId;
                //未读消息标记已读
                unreadNum = MongoDBUtil.getUnReadNum(MSG_PREFIX, Long.parseLong(storeId), Integer.parseInt(memberId), HelpdeskConst.ROLE_TYPE_VENDOR);
                unreadNum = unreadNum - msgIds.split(",").length;
            } else {
                //商户读信息
                memberId = data.get("memberId").toString();
                roomId = ROOM_ID_PREFIX + storeId + "_" + memberId;
                //未读消息标记已读
                unreadNum = MongoDBUtil.getUnReadNum(MSG_PREFIX, Long.parseLong(storeId), Integer.parseInt(memberId), HelpdeskConst.ROLE_TYPE_MEMBER);
                unreadNum = unreadNum - msgIds.split(",").length;
            }

            ModelMap modelMap = new ModelMap();
            modelMap.put("unreadNum", Long.max(unreadNum, 0L));
            modelMap.put("memberId", memberId);
            modelMap.put("storeId", storeId);

            client.sendEvent("unread_num_change", modelMap);

            //给客服发送剩余未读数
            if (!CollectionUtils.isEmpty(storeVendorMap.get(storeId))) {
                //有客服在线
                storeVendorMap.get(storeId).forEach((vId, vendorServiceInfo) -> {
                    if (vendorServiceInfo.getMemberIdList().contains(memberId)) {
                        //正在服务当前用户
                        vendorServiceInfo.getVendorClient().forEach(socketIOClient -> {
                            if (!socketIOClient.getSessionId().equals(client.getSessionId())) {
                                //当前客户端已发送，不再发送
                                socketIOClient.joinRoom(roomId);
                                socketIOClient.sendEvent("unread_num_change", modelMap);
                            }
                        });
                    }
                });
            }
            //给会员发送剩余未读数
            if (!CollectionUtils.isEmpty(memberListenClientMap.get(memberId))) {
                memberListenClientMap.get(memberId).forEach(socketIOClient -> {
                    if (!socketIOClient.getSessionId().equals(client.getSessionId())) {
                        //当前客户端已发送，不再发送
                        socketIOClient.sendEvent("unread_num_change", modelMap);
                    }
                });
            }

            Map<String, Object> result = new HashMap<>();
            result.put("msgIds", msgIds);
            //通知客户端已读消息
            imSocketIOServer.getRoomOperations(roomId).sendEvent("get_read_msg", result);

            if (!StringUtils.isEmpty(msgIds)) {
                //更新数据库已读
                MongoDBUtil.upsertUnReadNum(MSG_PREFIX, null, null, null, msgIds);
            }
        });

        //会员端全部已读
        imSocketIOServer.addEventListener("member_read_all", Map.class, (client, data, ackSender) -> {
            //获取握手数据
            String userId = data.get("userId").toString();//memberId或vendorId
            String role = data.get("role").toString();//角色类型，1==会员，2==vendor客服

            String storeId = data.get("storeId").toString();//店铺id

            String roomId = ROOM_ID_PREFIX + storeId + "_" + userId;
            Map<String, Object> result = new HashMap<>();
            result.put("memberId", userId);
            client.sendEvent("get_member_read_all", result);
            if (!CollectionUtils.isEmpty(storeVendorMap.get(storeId))) {
                storeVendorMap.get(storeId).forEach((vId, vendorServiceInfo) -> {
                    if (vendorServiceInfo.getRoomId().contains(roomId)) {
                        //该客服正在看此会员的聊天，发送会员全读事件
                        vendorServiceInfo.getVendorClient().forEach(socketIOClient -> {
                            //通知客户端已读消息
                            socketIOClient.sendEvent("get_member_read_all", result);
                        });
                    }
                });
            }

            //未读消息清零
            ModelMap modelMap = new ModelMap();
            modelMap.put("unreadNum", 0);
            modelMap.put("memberId", userId);
            modelMap.put("storeId", storeId);

            //给客服发送剩余未读数
            if (!CollectionUtils.isEmpty(storeVendorMap.get(storeId))) {
                //有客服在线
                storeVendorMap.get(storeId).forEach((vId, vendorServiceInfo) -> {
                    if (vendorServiceInfo.getMemberIdList().contains(userId)) {
                        //正在服务当前用户
                        vendorServiceInfo.getVendorClient().forEach(socketIOClient -> {
                            if (!socketIOClient.getSessionId().equals(client.getSessionId())) {
                                socketIOClient.joinRoom(roomId);
                                socketIOClient.sendEvent("unread_num_change", modelMap);
                            }
                        });
                    }
                });
            }
            //给会员发送剩余未读数
            if (!CollectionUtils.isEmpty(memberListenClientMap.get(userId))) {
                memberListenClientMap.get(userId).forEach(socketIOClient -> {
                    if (!socketIOClient.getSessionId().equals(client.getSessionId())) {
                        //当前客户端已发送，不再发送
                        socketIOClient.sendEvent("unread_num_change", modelMap);
                    }
                });
            }

            //未读消息标记已读
            List<HelpdeskMsg> msgList = MongoDBUtil.getHelpDeskMsgList(MSG_PREFIX, Long.parseLong(storeId), Integer.parseInt(userId), null, 0);
            if (!CollectionUtils.isEmpty(msgList)) {
                MongoDBUtil.upsertUnReadNum(MSG_PREFIX, Long.parseLong(storeId), Integer.parseInt(userId), HelpdeskConst.ROLE_TYPE_MEMBER, null);
            }

        });

        //会员移除最近联系人
        imSocketIOServer.addEventListener("member_remove_contact", Map.class, (client, data, ackSender) -> {
            //获取握手数据，此事件握手信息中无店铺id
            String userId = data.get("userId").toString();//memberId或vendorId
            String role = data.get("role").toString();//角色类型，1==会员，2==vendor客服

            //从参数中获取店铺id
            String storeId = data.get("storeId").toString();//店铺id
            //最近联系记录存入MongoDB（会员）
            MongoDBUtil.deleteByMemberIdAndStoreId(MEMBER_CONTACT_PREFIX, Integer.parseInt(userId), Long.parseLong(storeId));
        });

        //vendor移除最近联系人
        imSocketIOServer.addEventListener("vendor_remove_contact", Map.class, (client, data, ackSender) -> {
            //获取握手数据
            String storeId = data.get("storeId").toString();//店铺id
            String userId = data.get("userId").toString();//memberId或vendorId
            String role = data.get("role").toString();//角色类型，1==会员，2==vendor客服

            String memberId = data.get("memberId").toString();//会员id
            //最近联系记录存入MongoDB（vendor）
            MongoDBUtil.deleteByVendorIdAndMemberId(VENDOR_CONTACT_PREFIX, Long.parseLong(userId), Integer.parseInt(memberId));

            //如果客服处于要移除的会员房间中，则退出房间
            String roomId = ROOM_ID_PREFIX + storeId + "_" + memberId;
            List<String> lookRoomIds = storeVendorMap.get(storeId).get(userId).getRoomId();
            if (lookRoomIds.contains(roomId)){
                //客服正在查看当前房间，将其移除房间
                client.leaveRoom(roomId);
                lookRoomIds.remove(roomId);
            }
        });

        imSocketIOServer.start();
    }

    /**
     * 分配客服
     * 回头客优先
     * 按照客服当前接待空闲度分配
     * 按照客服加入的时间
     *
     * @param memberId
     * @param storeId
     * @return
     */
    private VendorServiceInfo assignVendor(String memberId, String storeId) {
        //店铺内的所有在线客服
        Map<String, VendorServiceInfo> vendorMap = storeVendorMap.get(storeId);
        if (CollectionUtils.isEmpty(vendorMap)) {
            //没有在线的客服
            return null;
        }
        //回头客查询
        //获取最后一次聊天的消息id
        Long msgId = MongoDBUtil.getMemberContact(MEMBER_CONTACT_PREFIX, Integer.parseInt(memberId), Long.parseLong(storeId));
        if (!StringUtil.isNullOrZero(msgId)) {
            //有聊天历史，查询聊天详情
            HelpdeskMsg helpdeskMsg = MongoDBUtil.getChatDetail(MSG_PREFIX, msgId);
            if (helpdeskMsg != null) {
                //获取消息详情中的vendor
                Long vendorId = helpdeskMsg.getVendorId();
                if (vendorMap.containsKey(vendorId.toString())) {
                    //上一次聊天的客服在线，分配给此客服
                    VendorServiceInfo vendorServiceInfo = vendorMap.get(vendorId.toString());
                    //增加服务人数
                    vendorServiceInfo.setMemberNum(vendorServiceInfo.getMemberNum() + 1);
                    vendorServiceInfo.getMemberIdList().add(memberId);
                    return vendorServiceInfo;
                } else {
                    //上一次聊天的客服不在线，将此客服与该会员的最近联系记录删除
                    MongoDBUtil.deleteByVendorIdAndMemberId(VENDOR_CONTACT_PREFIX, vendorId, Integer.parseInt(memberId));
                }
            }
        }

        //不是回头客，或者上一次联系的客服不在线，找最闲的客服
        int memberNum = Integer.MAX_VALUE;//最闲的客服服务的人数
        VendorServiceInfo free = null;//最闲的客服
        for (Map.Entry<String, VendorServiceInfo> entry : vendorMap.entrySet()) {
            VendorServiceInfo vendorServiceInfo = entry.getValue();
            if (vendorServiceInfo.getMemberNum().compareTo(memberNum) < 0) {
                memberNum = vendorServiceInfo.getMemberNum();
                free = vendorServiceInfo;
            }
        }
        //增加服务人数
        free.setMemberNum(free.getMemberNum() + 1);
        free.getMemberIdList().add(memberId);
        return free;
    }

    /**
     * 发送消息
     *
     * @param memberId
     * @param vendorId
     * @param storeId
     * @param msg      原始消息
     * @param role     发送消息的角色，1==会员，2==商家
     * @param roomId   房间号
     * @param msgType  消息类型:1.text(文本) 2.img(图片) 3.goods(商品) 4.order(订单)用户" 5.常见问题
     * @param msgState 消息状态:1为已读,2为未读
     */
    private void sendMsg(String memberId, String vendorId, String storeId, Map msg, String role, String roomId, String msgType, Integer msgState) {
        Member member = memberModel.getMemberByMemberId(Integer.valueOf(memberId));
        Vendor vendor = vendorModel.getVendorByVendorId(Long.valueOf(vendorId));
        Store store = storeModel.getStoreByStoreId(Long.valueOf(storeId));
        //将信息缓存到MongoDB
        HelpdeskMsg helpdeskMsg = new HelpdeskMsg();
        helpdeskMsg.setMsgId(SmartId.nextId(BizTypeConst.MSG_ID));
        helpdeskMsg.setMemberId(member.getMemberId());
        helpdeskMsg.setMemberName(member.getMemberName());
        helpdeskMsg.setMemberAvatar(StringUtils.isEmpty(member.getMemberAvatar())
                ? stringRedisTemplate.opsForValue().get("default_image_user_portrait")
                : member.getMemberAvatar());
        helpdeskMsg.setVendorId(vendor.getVendorId());
        helpdeskMsg.setVendorName(vendor.getVendorName());
        helpdeskMsg.setVendorAvatar(StringUtils.isEmpty(store.getStoreLogo())
                ? stringRedisTemplate.opsForValue().get("default_image_store_logo")
                : store.getStoreLogo());//客服头像暂时用店铺logo
        helpdeskMsg.setStoreId(store.getStoreId());
        helpdeskMsg.setStoreName(store.getStoreName());
        helpdeskMsg.setMsgContent(JSON.toJSONString(msg));
        if (!StringUtil.isNullOrZero(msgState)) {
            helpdeskMsg.setMsgState(msgState);
        } else {
            helpdeskMsg.setMsgState(getMsgState(storeId, vendorId, role, memberId, roomId));
        }
        helpdeskMsg.setMsgType(Integer.valueOf(msgType));
        helpdeskMsg.setUserType(Integer.valueOf(role));
        helpdeskMsg.setAddTime(new Date());
        helpdeskMsg.setIsDelete(HelpdeskConst.IS_DELETE_NO);
        helpdeskMsg.setWxAvatarImg(member.getWxAvatarImg());

        if (memberListenClientMap.containsKey(memberId)) {
            //此会员正在监听最近联系人列表，给会员发送联系人变动消息
            //未读数
            Long unreadNum = MongoDBUtil.getUnReadNum(MSG_PREFIX, Long.parseLong(storeId), Integer.parseInt(memberId), HelpdeskConst.ROLE_TYPE_VENDOR);
            helpdeskMsg.setReceiveMsgNumber(role.equals(HelpdeskConst.ROLE_TYPE_VENDOR) ?
                    (helpdeskMsg.getMsgState() == HelpdeskConst.MSG_STATE_NO ? unreadNum.intValue() + 1 : unreadNum.intValue()) : //商户发送的消息，会员未读数根据此条消息的未读状态判断
                    unreadNum.intValue());//会员发送的消息，直接取会员历史未读数
            HelpdeskMsgVO msgVO = new HelpdeskMsgVO(helpdeskMsg);
            memberListenClientMap.get(memberId).forEach(socketIOClient -> {
                socketIOClient.sendEvent("contact_change", msgVO);
            });
        }
        Map<String, VendorServiceInfo> stringVendorServiceInfoMap = storeVendorMap.get(storeId);
        if (!CollectionUtils.isEmpty(stringVendorServiceInfoMap)) {
            //店铺有在线的客服
            VendorServiceInfo vendorServiceInfo = stringVendorServiceInfoMap.get(vendorId);
            if (vendorServiceInfo != null) {
                //当前vendor存在客户端
                //未读数
                Long unreadNum = MongoDBUtil.getUnReadNum(MSG_PREFIX, Long.parseLong(storeId), Integer.parseInt(memberId), HelpdeskConst.ROLE_TYPE_MEMBER);
                helpdeskMsg.setReceiveMsgNumber(role.equals(HelpdeskConst.ROLE_TYPE_MEMBER) ?
                        (helpdeskMsg.getMsgState() == HelpdeskConst.MSG_STATE_NO ? unreadNum.intValue() + 1 : unreadNum.intValue()) : //会员发送的消息，商户未读数根据此条消息的未读状态判断
                        unreadNum.intValue());//商户发送的消息，直接取商户历史未读数
                HelpdeskMsgVO msgVO = new HelpdeskMsgVO(helpdeskMsg);
                //给vendor发送联系人变动
                vendorServiceInfo.getVendorClient().forEach(socketIOClient -> {
                    socketIOClient.sendEvent("contact_change", msgVO);
                });
            }
        }

        //消息存入MongoDB
        MongoDBUtil.insertHelpdeskMsg(helpdeskMsg, MSG_PREFIX);
        //最近联系记录存入MongoDB（会员）
        MongoDBUtil.upsertMemberContact(MEMBER_CONTACT_PREFIX, Integer.valueOf(memberId), Long.parseLong(storeId), helpdeskMsg.getMsgId(),store.getStoreName());
        //最近联系记录存入MongoDB（vendor）
        MongoDBUtil.upsertVendorContact(VENDOR_CONTACT_PREFIX, Long.parseLong(vendorId), Integer.valueOf(memberId), helpdeskMsg.getMsgId(),member.getMemberName());

        //发送消息给房间中的所有人
        //处理图片信息
        helpdeskMsg.setMemberAvatar(FileUrlUtil.getFileUrl(helpdeskMsg.getMemberAvatar(), null));
        helpdeskMsg.setVendorAvatar(FileUrlUtil.getFileUrl(helpdeskMsg.getVendorAvatar(), null));

        imSocketIOServer.getRoomOperations(roomId).sendEvent("get_send_msg", helpdeskMsg);

    }

    /**
     * 构造消息状态
     *
     * @param storeId
     * @param vendorId
     * @param role     发送消息的角色，1==会员，2==商家
     * @param memberId
     * @param roomId
     * @return
     */
    private Integer getMsgState(String storeId, String vendorId, String role, String memberId, String roomId) {
        if (role.equals(HelpdeskConst.ROLE_TYPE_VENDOR)) {
            //商户发的,查看会员是否在线
            return memberStateSet.contains(memberId + "-" + storeId) ? HelpdeskConst.MSG_STATE_YES : HelpdeskConst.MSG_STATE_NO;
        }
        //会员发的消息，查看客服是否在线
        return storeVendorMap.containsKey(storeId) //店铺有在线的客服
                && storeVendorMap.get(storeId).containsKey(vendorId) //当前聊天客服在线
                && storeVendorMap.get(storeId).get(vendorId).getRoomId().contains(roomId) //当前聊天客服正在看该用户的聊天
                ? HelpdeskConst.MSG_STATE_YES //消息已读
                : HelpdeskConst.MSG_STATE_NO; //消息未读
    }

    /**
     * 断开服务
     */
    private void stop() {
        log.debug("-----------------------------------------------------------");
        log.debug("执行销毁");
        log.debug("-----------------------------------------------------------");
        if (imSocketIOServer != null) {
            //端口所有客户端连接
            Collection<SocketIOClient> allClients = imSocketIOServer.getAllClients();
            if (!CollectionUtils.isEmpty(allClients)) {
                allClients.forEach(socketIOClient -> {
                    socketIOClient.getAllRooms().forEach(socketIOClient::leaveRoom);
                    socketIOClient.disconnect();
                });
            }
            imSocketIOServer.stop();
            imSocketIOServer = null;
        }
    }

}
