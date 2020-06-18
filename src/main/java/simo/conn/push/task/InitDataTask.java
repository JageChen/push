package simo.conn.push.task;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import simo.conn.push.constant.PushStatusConstant;
import simo.conn.push.dao.DataIterable;
import simo.conn.push.entity.GpsReport;
import simo.conn.push.entity.TamperReport;
import simo.conn.push.service.ReportService;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * description: InitDataTask <br>
 * date: 2020/5/21 16:48 <br>
 * author: EDZ <br>
 * version: 1.0 <br>
 */
@Component
@Slf4j
public class InitDataTask {

    private static final int BATCH_SIZE = 200;
    private ReportService reportService;

    // redisson 操作类
    private RedissonClient redissonClient;
    private static final ScheduledExecutorService service = Executors.newScheduledThreadPool(2);


    @Autowired
    public InitDataTask(ReportService reportService, RedissonClient redissonClient) {
        this.reportService = reportService;
        this.redissonClient = redissonClient;
    }

    /**
     * 跟随项目启动加载方法
     */
    @PostConstruct()
    public void init() {
        service.scheduleWithFixedDelay(this::runGPSPush, 5, 60, TimeUnit.SECONDS);
        service.scheduleWithFixedDelay(this::runTamperPush, 5, 60, TimeUnit.SECONDS);
    }

    private void runTamperPush() {
        log.info("runTamperPush start");

        DataIterable<TamperReport> iter = null;
        RLock rlock = redissonClient.getLock("tamper_push");
        try {
            boolean bLock = rlock.tryLock(10, 5, TimeUnit.SECONDS);
            if (!bLock) {
                log.info("runTamperPush try lock fail");
                return;
            }
            List<TamperReport> batchList = new ArrayList<>(BATCH_SIZE);
            iter = reportService.findData("TAMPER_REPORT", TamperReport.class, PushStatusConstant.NOT_PUSHED);
            long total = 0;
            for (TamperReport record : iter) {
                batchList.add(record);
                if (batchList.size() == BATCH_SIZE) {
                    reportService.executiveGpsPush(batchList, TamperReport.class);
                    batchList = new ArrayList<>(BATCH_SIZE);
                }
                total++;
            }
            if (batchList.size() > 0) {
                reportService.executiveGpsPush(batchList, TamperReport.class);
            }
            log.info("runTamperPush finish total:{}", total);
        } catch (Exception e) {
            log.error("runTamperPush error", e);
        } finally {
            rlock.unlock();
            closeDataIter(iter, "runTamperPush");
        }
    }

    private void closeDataIter(DataIterable<?> iter, String name) {
        if (iter != null) {
            try {
                iter.close();
            } catch (IOException e) {
                log.error("{} close error", name, e);
            }
        }
    }

    private void runGPSPush() {
        log.info("runGPSPush start");
        DataIterable<GpsReport> iter = null;
        RLock rlock = redissonClient.getLock("gps_push");
        try {
            boolean bLock = rlock.tryLock(10, 5, TimeUnit.SECONDS);
            if (!bLock) {
                log.info("runTamperPush try lock fail");
                return;
            }
            List<GpsReport> batchList = new ArrayList<>(BATCH_SIZE);
            iter = reportService.findData("GPS_REPORT", GpsReport.class, PushStatusConstant.NOT_PUSHED);
            long total = 0;
            for (GpsReport record : iter) {
                batchList.add(record);
                if (batchList.size() == BATCH_SIZE) {
                    reportService.executiveGpsPush(batchList, GpsReport.class);
                    batchList = new ArrayList<>(BATCH_SIZE);
                }
                total++;
            }
            if (batchList.size() > 0) {
                reportService.executiveGpsPush(batchList, GpsReport.class);
            }
            log.info("runGPSPush finish total:{}", total);
        } catch (Exception e) {
            log.error("runGPSPush error", e);
        } finally {
            rlock.unlock();
            closeDataIter(iter, "runGPSPush");
        }
    }
}
