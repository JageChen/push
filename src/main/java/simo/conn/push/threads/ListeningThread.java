package simo.conn.push.threads;

import lombok.extern.slf4j.Slf4j;
import simo.conn.push.service.ThirdPartyService;

import static java.lang.Thread.sleep;

/**
 * description: ListeningThread <br>
 * date: 2020/5/28 10:00 <br>
 * author: EDZ <br>
 * version: 1.0 <br>
 */
@Slf4j
public class ListeningThread implements  Runnable {

    private ThirdPartyService thirdPartyService;

    public ListeningThread(ThirdPartyService thirdPartyService) {
        this.thirdPartyService = thirdPartyService;
    }

    @Override
    public void run() {
      log.info("------------------Push failure data is re-pushed on a regular basis");
      try{
          while (true){
              // 5m检查一次
              sleep(50000);
              thirdPartyService.pushUnsuccessfulData();
          }
      }catch (Exception e){
          log.error("------------------Error in timing failed data push, error message:",e);
      }

    }
}
