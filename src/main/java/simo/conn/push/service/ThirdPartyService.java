package simo.conn.push.service;

import simo.conn.push.entity.GpsReport;

import java.util.List;

/**
 * description: ThirdPartyService <br>
 * date: 2020/6/1 15:39 <br>
 * author: EDZ <br>
 * version: 1.0 <br>
 */
public interface ThirdPartyService {
    /**
     * 重新推送不成功的数据
     */
    public void pushUnsuccessfulData();

    /**
     * 推送未推送的数据
     * @param lGpss 推送数据
     */
    public void pushData(List lGpss);
}
