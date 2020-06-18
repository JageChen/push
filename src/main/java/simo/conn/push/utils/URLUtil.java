package simo.conn.push.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * description: URLConstant <br>
 * date: 2020/5/26 14:34 <br>
 * author: EDZ <br>
 * version: 1.0 <br>
 */
@Component
@Data
public class URLUtil {
    /**
     *  获取设备IDurl
     */
    @Value(value = "${url.getDeviceId}")
    private String getDeviceidUrl;

    /**
     *  获取Token
     */
    @Value(value = "${url.getToken}")
    private String getToken;

    /**
     *  推送GPS数据URL
     */
    @Value(value = "${url.pushData}")
    private String pushData;

}
