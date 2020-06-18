package simo.conn.push.threads;

import lombok.extern.slf4j.Slf4j;
import simo.conn.push.entity.GpsReport;
import simo.conn.push.service.ThirdPartyService;

import java.util.*;

import static java.lang.Thread.sleep;

/**
 * description: GPS消费者线程类 <br>
 * date: 2020/5/25 14:34 <br>
 * author: EDZ <br>
 * version: 1.0 <br>
 * @author EDZ
 */
@Slf4j
public class ConsumerThreads implements Runnable{

    private ThirdPartyService thirdPartyService;

    private List list;

    public ConsumerThreads(ThirdPartyService thirdPartyService,List list) {
        this.thirdPartyService = thirdPartyService;
        this.list = list;
    }
    /**
     *  执行线程消费逻辑
     */
    @Override
    public void run(){
        log.info("------------------Push unpushed data");
        try{
            thirdPartyService.pushData(list);
        }catch (Exception e){
            log.error("------------------GPS thread class RUN method execution error, error message: ",e);
        }
    }
}
