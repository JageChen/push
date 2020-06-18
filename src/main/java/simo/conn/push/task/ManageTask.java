package simo.conn.push.task;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import simo.conn.push.dao.PushRelationshipDao;
import simo.conn.push.dao.ReportDao;
import simo.conn.push.service.ThirdPartyService;
import simo.conn.push.threads.ListeningThread;
import simo.conn.push.utils.HttpConnectUtil;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;

/**
 * description: ManageTask <br>
 * date: 2020/6/1 10:15 <br>
 * author: EDZ <br>
 * version: 1.0 <br>
 */
@Component
@Slf4j
public class ManageTask {

    private PoolingHttpClientConnectionManager connectionPool;

    private ExecutorService manageThreadPool;

    private ThirdPartyService thirdPartyService;

    @Autowired
    public ManageTask(PoolingHttpClientConnectionManager connectionPool, ExecutorService manageThreadPool, ThirdPartyService thirdPartyService) {
        this.connectionPool = connectionPool;
        this.manageThreadPool = manageThreadPool;
        this.thirdPartyService = thirdPartyService;
    }



    /**
     *  跟随项目启动加载方法
     */
    @PostConstruct
    public void init(){
        // 额外使用一个线程定时去查询推送失败的数据并且重新推送
//        log.info("------------------Start the timer monitoring push failed thread task");
//        manageThreadPool.submit(new ListeningThread(thirdPartyService));

        // 监控无用的连接并清除
        log.info("------------------Monitor useless connections and clear");
        manageThreadPool.submit(new HttpConnectUtil.IdleConnectionEvictor(connectionPool));
    }
}
