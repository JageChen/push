package simo.conn.push.dao;

import simo.conn.push.entity.GpsReport;
import simo.conn.push.entity.PushRelationship;

import java.util.List;
import java.util.Map;

/**
 * description:  推送关联 MONGODB 接口类
 * date: 2020/5/22 15:27 <br>
 * author: EDZ <br>
 * version: 1.0 <br>
 */
public interface PushRelationshipDao {
    /**
     *
     * @return  根据SN查询DEVICEID
     */
    Map<String,Object> findAllByPushStatus(String sn);


    /**
     *
     * @return  新增SN DEVICEID关联关系
     */
    Boolean insRelationship(PushRelationship pushRelationship);


}
