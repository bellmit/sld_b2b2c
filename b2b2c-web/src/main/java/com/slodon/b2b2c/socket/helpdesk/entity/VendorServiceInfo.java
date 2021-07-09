package com.slodon.b2b2c.socket.helpdesk.entity;

import com.corundumstudio.socketio.SocketIOClient;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 在线vendor客服服务信息
 */
@Data
public class VendorServiceInfo {
    private String vendorId;                        //vendorId
    private List<SocketIOClient> vendorClient = new ArrayList<>(); //vendor socket客户端,多端登录，可能有多个客户端连接
    private Integer memberNum = 0;                    //正在服务的会员数量
    private List<String> memberIdList = new ArrayList<>(); //正在服务的会员id列表
    private List<String> roomId = new ArrayList<>();  //当前正在看的房间号,多端登录，可能同时查看多个房间
}
