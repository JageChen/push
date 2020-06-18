package simo.conn.push.dao;

import com.mongodb.client.FindIterable;
import org.bson.types.ObjectId;
import simo.conn.push.entity.GpsReport;
import simo.conn.push.entity.TamperReport;

import java.util.List;
import java.util.Set;

/**
 * description: GPS MONGODB 接口类
 * date: 2020/4/10 9:02
 * author: Jage
 * version: 1.0
 */
public interface ReportDao {
    /**
     *  查找未推送得数据（查询最大数量为：1000，后续业务数量上来后再进行调整）
     * @return  GPS上报集合
     */
    public List<GpsReport> findAllByPushStatus();


    <T> DataIterable<T> findData(String collectionName, Class<T> clazz, String status);

    /**
     *  查批量修改GPS推送状态
     * @param  ids  批量修改ID集合
     * @param  pushStatus  推送状态
     * @return  GPS上报集合
     */
    public Boolean batchModifyPushStatus(Set<Object> ids, String pushStatus, Class type);


    /**
     *  查批量修改GPS推送状态
     * @param  id  ID
     * @param  pushStatus  推送状态
     * @return  GPS上报集合
     */
    public Boolean modifyPushStatus(ObjectId id, String pushStatus,Class type);


    /**
     *  查找推送失败的数据（查询最大数量为：10000，后续业务数量上来后再进行调整）
     * @return  GPS上报集合
     */
    public List<GpsReport> findFailedPushData();
}
