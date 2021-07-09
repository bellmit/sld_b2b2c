package com.slodon.b2b2c.util;

import com.mongodb.client.result.UpdateResult;
import com.slodon.b2b2c.core.constant.HelpdeskConst;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.helpdesk.dto.MemberContactDTO;
import com.slodon.b2b2c.helpdesk.dto.VendorContactDTO;
import com.slodon.b2b2c.helpdesk.pojo.HelpdeskMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @program: slodon
 * @Description MongoDB工具类
 * @Author wuxy
 */
@Component
public class MongoDBUtil {

    private static MongoTemplate mongoTemplate;

    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        MongoDBUtil.mongoTemplate = mongoTemplate;
    }

    /**
     * 单条聊天消息插入
     *
     * @param helpdeskMsg    客服聊天消息
     * @param collectionName mongodb表名（消息类型）
     * @return 插入数据后生成的id
     */
    public static HelpdeskMsg insertHelpdeskMsg(HelpdeskMsg helpdeskMsg, String collectionName) {
        return mongoTemplate.insert(helpdeskMsg, collectionName);
    }

    /**
     * 记录来源url(数据库有，就更新，没有，就新增)
     *
     * @param collectionName mongodb表名（消息类型）
     * @param sourceUrl      来源url
     * @return 插入数据后生成的id
     */
    public static void upsertSourceUrl(String collectionName, String sourceUrl, Long storeId, Integer memberId) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("storeId").is(storeId).and("memberId").is(memberId);
        query.addCriteria(criteria);

        Update update = new Update();
        update.set("sourceUrl", sourceUrl);

        mongoTemplate.upsert(query, update, collectionName);
    }

    /**
     * 会员最近联系记录(数据库有，就更新，没有，就新增)
     *
     * @param collectionName mongodb表名（消息类型）
     * @param memberId       会员id
     * @param storeId        店铺id
     * @param msgId          消息id
     */
    public static void upsertMemberContact(String collectionName, Integer memberId, Long storeId, Long msgId,String storeName) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("memberId").is(memberId).and("storeId").is(storeId).and("storeName").is(storeName);
        query.addCriteria(criteria);

        Update update = new Update();
        update.set("msgId", msgId);

        mongoTemplate.upsert(query, update, collectionName);
    }

    /**
     * 客服最近联系记录(数据库有，就更新，没有，就新增)
     *
     * @param collectionName mongodb表名（消息类型）
     * @param vendorId       客服id
     * @param memberId       会员id
     * @param msgId          消息id
     */
    public static void upsertVendorContact(String collectionName, Long vendorId, Integer memberId, Long msgId,String memberName) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("vendorId").is(vendorId).and("memberId").is(memberId).and("memberName").is(memberName);
        query.addCriteria(criteria);

        Update update = new Update();
        update.set("msgId", msgId);

        mongoTemplate.upsert(query, update, collectionName);
    }

    /**
     * 数据库有，就更新，没有，就新增
     *
     * @param collectionName mongodb表名（消息类型）
     * @param storeId        店铺id
     * @param memberId       会员id
     * @param userType       用户类型
     * @param msgIds         消息id集合
     */
    public static long upsertUnReadNum(String collectionName, Long storeId, Integer memberId, String userType, String msgIds) {
        Query query = new Query();
        if (!StringUtil.isEmpty(msgIds)) {
            List<Long> list = Arrays.stream(msgIds.split(",")).map(Long::valueOf).collect(Collectors.toList());
            query.addCriteria(Criteria.where("msgId").in(list));
        } else {
            Criteria criteria = new Criteria();
            criteria.and("storeId").is(storeId)
                    .and("memberId").is(memberId)
                    .and("userType").is(Integer.parseInt(userType));
            query.addCriteria(criteria);
        }
        Update update = new Update();
        update.set("msgState", HelpdeskConst.MSG_STATE_YES);

        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, collectionName);
        return updateResult.getModifiedCount();
    }

    /**
     * 根据msgId查询聊天详情
     *
     * @param collectionName 表名
     * @param msgId          消息id
     * @return
     */
    public static HelpdeskMsg getChatDetail(String collectionName, Long msgId) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("msgId").is(msgId);
        query.addCriteria(criteria);
        List<HelpdeskMsg> mapList = mongoTemplate.find(query, HelpdeskMsg.class, collectionName);
        return mapList.get(0);
    }

    /**
     * 根据msgId查询聊天详情
     *
     * @param collectionName 表名
     * @return
     */
    public static String getSourceUrl(String collectionName, Long storeId, Integer memberId) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("storeId").is(storeId).and("memberId").is(memberId);
        Sort sort = Sort.by(Sort.Direction.DESC, "msgId");//排序
        query.addCriteria(criteria).with(sort);
        List<ModelMap> mapList = mongoTemplate.find(query, ModelMap.class, collectionName);
        if (CollectionUtils.isEmpty(mapList)) {
            return null;
        }
        return mapList.get(0).get("sourceUrl").toString();
    }

    /**
     * 获取会员最近联系人
     *
     * @param collectionName 表名
     * @param memberId       会员id
     * @param storeId        店铺id
     * @return
     */
    public static Long getMemberContact(String collectionName, Integer memberId, Long storeId) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("memberId").is(memberId).and("storeId").is(storeId);
        query.addCriteria(criteria);
        List<MemberContactDTO> mapList = mongoTemplate.find(query, MemberContactDTO.class, collectionName);
        if (CollectionUtils.isEmpty(mapList)) {
            return null;
        }
        return mapList.get(0).getMsgId();
    }

    /**
     * 获取会员最近联系人列表
     *
     * @param collectionName 表名
     * @param memberId       会员id
     * @param storeName          店铺名称
     * @param pager          分页条件
     * @return
     */
    public static List<MemberContactDTO> getMemberContactList(String collectionName, Integer memberId, Long msgId,String storeName, PagerInfo pager) {
        Pageable pageable = PageRequest.of(0, pager.getPageSize());
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("memberId").is(memberId).and("msgId").lt(msgId);
        if (!StringUtils.isEmpty(storeName)){
            criteria.and("storeName").regex("^.*" +storeName+ ".*$");
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "msgId");//排序
        query.addCriteria(criteria).with(sort).with(pageable);
        List<MemberContactDTO> list = mongoTemplate.find(query, MemberContactDTO.class, collectionName);
        return list;
    }

    /**
     * 获取客服最近联系人
     *
     * @param collectionName 表名
     * @param vendorId       客服id
     * @param memberId       会员id
     * @return
     */
    public static VendorContactDTO getVendorContact(String collectionName, Long vendorId, Integer memberId) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("vendorId").is(vendorId).and("memberId").is(memberId);
        query.addCriteria(criteria);
        List<VendorContactDTO> list = mongoTemplate.find(query, VendorContactDTO.class, collectionName);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 获取客服最近联系人列表
     *
     * @param collectionName 表名
     * @param memberName      会员名称
     * @param vendorId       客服id
     * @param msgId          消息id
     * @param pager          分页条件
     * @return
     */
    public static List<VendorContactDTO> getVendorContactList(String collectionName,String memberName, Long vendorId, Long msgId, PagerInfo pager) {
        Pageable pageable = PageRequest.of(0, pager.getPageSize());
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("vendorId").is(vendorId).and("msgId").lt(msgId);
        if (!StringUtils.isEmpty(memberName)){
            criteria.and("memberName").regex("^.*" +memberName+ ".*$");
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "msgId");//排序
        query.addCriteria(criteria).with(sort).with(pageable);
        pager.setRowsCount((int) mongoTemplate.count(query,collectionName));
        List<VendorContactDTO> list = mongoTemplate.find(query, VendorContactDTO.class, collectionName);
        return list;
    }

    /**
     * 查询列表
     *
     * @param collectionName 表名
     * @param storeId        店铺id
     * @param memberId       会员id
     * @return
     */
    public static List<HelpdeskMsg> getHelpDeskMsgList(String collectionName, Long storeId, Integer memberId, Long msgId, int pageSize) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("storeId").is(storeId).and("memberId").is(memberId);
        if (!StringUtil.isNullOrZero(msgId)) {
            criteria.and("msgId").lt(msgId);
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "msgId");//排序
        if (!StringUtil.isNullOrZero(pageSize)) {
            Pageable pageable = PageRequest.of(0, pageSize);
            query.addCriteria(criteria).with(sort).with(pageable);
        } else {
            query.addCriteria(criteria).with(sort);
        }
        List<HelpdeskMsg> msgList = mongoTemplate.find(query, HelpdeskMsg.class, collectionName);
        return msgList;
    }

    /**
     * 查询聊天记录列表
     *
     * @param collectionName 表名
     * @param storeId        店铺id
     * @param memberId       会员id
     * @return
     */
    public static List<HelpdeskMsg> getHelpDeskMsgLogList(String collectionName, Long storeId, Integer memberId, Long vendorId,
                                                          String msgContent, Date startTime, Date endTime, Long msgId, PagerInfo pager) {
        Pageable pageable = PageRequest.of(0, pager.getPageSize());
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (!StringUtil.isNullOrZero(storeId)) {
            criteria.and("storeId").is(storeId);
        }
        if (!StringUtil.isNullOrZero(memberId)) {
            criteria.and("memberId").is(memberId);
        }
        if (!StringUtil.isNullOrZero(vendorId)) {
            criteria.and("vendorId").is(vendorId);
        }
        if (!StringUtil.isEmpty(msgContent)) {
            //模糊匹配
            Pattern pattern = Pattern.compile("^.*" + msgContent + ".*$", Pattern.CASE_INSENSITIVE);
            criteria.and("msgContent").regex(pattern);
        }
        if (!StringUtils.isEmpty(startTime)) {
            criteria.and("startTime").gte(startTime);
        }
        if (!StringUtils.isEmpty(endTime)) {
            criteria.and("endTime").lte(endTime);
        }
        if (!StringUtil.isNullOrZero(msgId)) {
            criteria.and("msgId").lt(msgId);
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "msgId");//排序
        query.addCriteria(criteria).with(sort).with(pageable);
        pager.setRowsCount((int) mongoTemplate.count(query,collectionName));
        List<HelpdeskMsg> msgList = mongoTemplate.find(query, HelpdeskMsg.class, collectionName);
        return msgList;
    }

    /**
     * 查询未读消息条数
     *
     * @param collectionName 表名
     * @param storeId        店铺id
     * @param memberId       会员id
     * @return
     */
    public static Long getUnReadNum(String collectionName, Long storeId, Integer memberId, String userType) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("storeId").is(storeId)
                .and("memberId").is(memberId)
                .and("userType").is(Integer.parseInt(userType))
                .and("msgState").is(HelpdeskConst.MSG_STATE_NO);
        query.addCriteria(criteria);
        return mongoTemplate.count(query, collectionName);
    }

    /**
     * 删除会员与店铺的最近联系记录
     *
     * @param collectionName 表名
     * @param memberId       会员id
     */
    public static void deleteByMemberIdAndStoreId(String collectionName, Integer memberId, Long storeId) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("memberId").is(memberId).and("storeId").is(storeId);
        query.addCriteria(criteria);
        mongoTemplate.remove(query, collectionName);
    }

    /**
     * 删除客服与会员的最近联系记录
     *
     * @param collectionName 表名
     * @param vendorId       客服id
     * @param memberId       会员id
     */
    public static void deleteByVendorIdAndMemberId(String collectionName, Long vendorId, Integer memberId) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("vendorId").is(vendorId).and("memberId").is(memberId);
        query.addCriteria(criteria);
        mongoTemplate.remove(query, collectionName);
    }
}
