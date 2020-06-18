package simo.conn.push.threads.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import simo.conn.push.entity.GpsReport;
import simo.conn.push.service.ThirdPartyService;
import simo.conn.push.threads.ConsumerThreads;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * description: PushThreadPoolManager <br>
 * date: 2020/5/25 14:42 <br>
 * author: EDZ <br>
 * version: 1.0 <br>
 */
@Component
@Slf4j
public class PushThreadPoolManager  {

    private ExecutorService newFixedThreadPool;

    private ThirdPartyService thirdPartyService;


    @Autowired
    public PushThreadPoolManager(ExecutorService newFixedThreadPool, ThirdPartyService thirdPartyService) {
        this.newFixedThreadPool = newFixedThreadPool;
        this.thirdPartyService = thirdPartyService;
    }

    /**
     *  执行多线程多线程任务
     * @param list
     */
    public void executivePush(List list) {
        // 线程池开始执行任务
        log.info("------------------The thread pool starts executing tasks,The number of tasks performed is:{}",list.size());
        newFixedThreadPool.submit(new ConsumerThreads(thirdPartyService,list));
    }
}
