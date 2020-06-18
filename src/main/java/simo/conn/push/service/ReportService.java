package simo.conn.push.service;

import com.mongodb.client.FindIterable;
import simo.conn.push.dao.DataIterable;
import simo.conn.push.entity.GpsReport;
import simo.conn.push.entity.TamperReport;

import java.util.List;

/**
 * description: GPS MONGODB 接口类
 * date: 2020/4/10 9:02
 * author: Jage
 * version: 1.0
 */
public interface ReportService {
    <T> DataIterable<T> findData(String collectionName, Class<T> clazz, String status);

    /**
     *  然后分发到多线程执行消费。
     */
    public void executiveGpsPush(List list,Class type);



}
