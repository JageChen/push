package simo.conn.push.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import simo.conn.push.constant.PushStatusConstant;
import simo.conn.push.dao.DataIterable;
import simo.conn.push.dao.ReportDao;
import simo.conn.push.entity.GpsReport;
import simo.conn.push.service.ReportService;
import simo.conn.push.threads.manager.PushThreadPoolManager;
import simo.conn.push.utils.CollectionUtil;

import java.util.List;

/**
 * description: GPS MONGODB 接口类
 * date: 2020/4/10 9:02
 * author: Jage
 * version: 1.0
 */
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    // gps mongodb操作类
    private ReportDao reportDao;

    private PushThreadPoolManager pushThreadPoolManager;


    public ReportServiceImpl(ReportDao reportDao, PushThreadPoolManager pushThreadPoolManager) {
        this.reportDao = reportDao;
        this.pushThreadPoolManager = pushThreadPoolManager;
    }

    @Override
    public <T> DataIterable<T> findData(String collectionName, Class<T> clazz, String status) {
        return reportDao.findData(collectionName, clazz, status);
    }

    /**
     * 分发GPS数据到多线程执行消费。
     */
    @Override
    public void executiveGpsPush(List list,Class type) {
        try{
            // 防止重复消费读到的数据后立即回写push状态，如果回写失败会记录在失败的集合中这里不做任何处理。等待定时调度重试
            if (reportDao.batchModifyPushStatus(CollectionUtil.valueDeduplication(list,"id"), PushStatusConstant.PUSHED,type)){
                // 将任务加入到多线程执行消费
                log.info("------------------Adding tasks to multi-threaded executive consumption");
                pushThreadPoolManager.executivePush(list);
            }
        }catch (Exception e){
            log.error("------------------executivePush method error:",e);
        }

    }
}
