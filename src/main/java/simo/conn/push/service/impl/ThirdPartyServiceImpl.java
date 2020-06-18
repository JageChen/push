package simo.conn.push.service.impl;

import cn.hutool.core.util.StrUtil;
import com.google.gson.*;
import io.micrometer.core.instrument.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.bson.types.ObjectId;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import simo.conn.push.constant.PushStatusConstant;
import simo.conn.push.constant.RequestConstant;
import simo.conn.push.utils.URLUtil;
import simo.conn.push.dao.PushRelationshipDao;
import simo.conn.push.dao.ReportDao;
import simo.conn.push.entity.GpsReport;
import simo.conn.push.entity.PushRelationship;
import simo.conn.push.entity.TamperReport;
import simo.conn.push.service.ThirdPartyService;
import simo.conn.push.utils.DateUtil;
import simo.conn.push.utils.HttpConnectUtil;
import simo.conn.push.utils.ReflexObjectUtil;
import simo.conn.push.vo.FindDeviceIdVo;
import simo.conn.push.vo.LoginVo;
import simo.conn.push.vo.PushVo;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * description: ThirdPartyService <br>
 * date: 2020/6/1 15:39 <br>
 * author: EDZ <br>
 * version: 1.0 <br>
 */
@Service
@Slf4j
public class ThirdPartyServiceImpl implements ThirdPartyService {

    @Value("${login.account}")
    private String loginAccount;

    @Value("${login.password}")
    private String password;

    private ReportDao reportDao;

    private PoolingHttpClientConnectionManager connectionPool;

    private RedissonClient redissonClient;

    private PushRelationshipDao pushRelationshipDao;

    private static Map<String,String> sdr = new ConcurrentHashMap<>();

    private URLUtil urlUtil;

    public ThirdPartyServiceImpl(URLUtil urlUtil,ReportDao reportDao,PoolingHttpClientConnectionManager connectionPool,RedissonClient redissonClient, PushRelationshipDao pushRelationshipDao) {
        this.connectionPool = connectionPool;
        this.reportDao = reportDao;
        this.redissonClient = redissonClient;
        this.pushRelationshipDao = pushRelationshipDao;
        this.urlUtil = urlUtil;
    }

    /**
     * 获取token
     * @return token
     */
    public String getToken() {
        String token = null;
        try{
            InputStream in = null;
            JsonObject object = null;
            //查看token是否在缓存中
            RBucket<Object> rToken = redissonClient.getBucket("token");
            token = (String) rToken.get();

            //缓存中没有，重新从客户那边认证获取并存入缓存
            if (StrUtil.isEmpty(token)) {
                LoginVo loginVo = new LoginVo(loginAccount,password);
                CloseableHttpResponse response = HttpConnectUtil.doPost(connectionPool, urlUtil.getGetToken(),new Gson().toJson(loginVo),null,null);

                if (response == null ){
                    return null;
                }
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        object = new Gson().fromJson(IOUtils.toString(entity.getContent()), JsonObject.class);
                    }
                    HttpConnectUtil.closeResponse(response);
                }

                if (object != null){
                    object = object.getAsJsonObject("data");
                    token = object.get("token").toString();
                    token = token.replace("\"","");
                    if (StrUtil.isNotBlank(token)){
                        token = "aicon-jwt="+token;
                        rToken.set(token);
                        rToken.expire(DateUtil.expire(), TimeUnit.MILLISECONDS);
                    }
                }

            }
        }catch (Exception e){
            log.error("------------------getToken method error:",e);
        }
        return token;
    }

    /**
     * 第三方接口获取deviceId
     * @return token
     */
    public String getDeviceId(Object obj,String token) throws Exception {

        String sn =(String) ReflexObjectUtil.getValueByKey(obj,"sn");

        if (StrUtil.isBlank(sn)){
            return null;
        }
        //获取SN对应设备ID
        log.info("------------------push method Get the device ID corresponding to SN");
        String deviceId = sdr.get(sn);
        JsonObject object = null;
        //从本地缓存获取deviceId
        if (StrUtil.isNotBlank(deviceId)){
           return deviceId;
        }

        //从mongodb里面获取deviceId
        Map<String,Object> value = pushRelationshipDao.findAllByPushStatus(sn);

        if (value != null){
            deviceId = (String) value.get("device_id");
            if (StrUtil.isNotBlank(deviceId)){
                return deviceId;
            }
        }

        //第三方接口获取deviceID
        FindDeviceIdVo deviceIdVo = new FindDeviceIdVo(sn);
        String requestParameters = new Gson().toJson(deviceIdVo);
        CloseableHttpResponse response = HttpConnectUtil.doPost(connectionPool, urlUtil.getGetDeviceidUrl(),requestParameters,token,null);

        if (response == null){
            return null;
        }
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                object = new Gson().fromJson(IOUtils.toString(entity.getContent()), JsonObject.class);
            }
            HttpConnectUtil.closeResponse(response);
        }else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED){
            //鉴权失败401 重新获取token
            getToken();
        }


        deviceId = getDeviceId(object);
        if (StrUtil.isNotBlank(deviceId)){
            pushRelationshipDao.insRelationship(new PushRelationship(sn,deviceId));
            sdr.put(sn,deviceId);
            return deviceId;
        }

        return null;
    }

    /**
     * 重新推送不成功的数据
     */
    @Override
    public void pushUnsuccessfulData() {
        try{
            //执行推送方法
            push(reportDao.findFailedPushData());
        }catch (Exception e){
            log.error("------------------pushUnsuccessfulData method error:",e);
        }
    }

    /**
     * 推送未推送的数据
     * @param lGpss 推送数据
     */
    @Override
    public void pushData(List lGpss) {
        try{
            //执行推送方法
            push(lGpss);
        }catch (Exception e){
            log.error("------------------pushData method error:",e);
        }
    }

    /**
     * 执行推送
     * @param list 推送数据
     * @throws Exception
     */
    private void push(List list) throws Exception{
        //获取token
        String token = getToken();

        for (int i = 0; i <list.size() ; i++) {
            try{

                //第三方接口出问题时，可能存在token为null的情况，这个时候直接返回，修改推送状态等待下一次的推送
                if (StrUtil.isBlank(token)){
                    reportDao.modifyPushStatus((ObjectId) ReflexObjectUtil.getValueByKey(list.get(i),"id"), PushStatusConstant.PUSH_FAILED,list.get(i).getClass());
                    getToken();
                    return;
                }

                //获取deviceId
                String deviceId = getDeviceId(list.get(i),token);

                //推送数据,必须是有设备ID的情况下。
                if (StrUtil.isNotBlank(deviceId)){
                    log.info("------------------push method Push");
                    String url = urlUtil.getPushData()+deviceId+"/busev";
                    String requestParameters = new Gson().toJson(assemblyData(list.get(i)));
                    CloseableHttpResponse response = HttpConnectUtil.doPost(connectionPool,url,requestParameters,token,RequestConstant.REQUEST_TYPE);

                    if (response == null){
                        reportDao.modifyPushStatus((ObjectId) ReflexObjectUtil.getValueByKey(list.get(i),"id"), PushStatusConstant.PUSH_FAILED,list.get(i).getClass());
                        return;
                    }

                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        HttpConnectUtil.closeResponse(response);
                    }else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED){
                        //鉴权失败401 重新获取token
                        reportDao.modifyPushStatus((ObjectId) ReflexObjectUtil.getValueByKey(list.get(i),"id"), PushStatusConstant.PUSH_FAILED,list.get(i).getClass());
                        getToken();
                    } else {
                        reportDao.modifyPushStatus((ObjectId) ReflexObjectUtil.getValueByKey(list.get(i),"id"), PushStatusConstant.PUSH_FAILED,list.get(i).getClass());
                    }
                }else{
                    //第三方接口出问题时，可能存在deviceId为null的情况，这个时候直接返回，修改推送状态等待下一次的推送
                    reportDao.modifyPushStatus((ObjectId) ReflexObjectUtil.getValueByKey(list.get(i),"id"), PushStatusConstant.PUSH_FAILED,list.get(i).getClass());
                    log.info("------------------There is no device ID, no push");
                }

            }catch (Exception e){
                //异常错误时，修改推送状态为推送失败
                reportDao.modifyPushStatus((ObjectId) ReflexObjectUtil.getValueByKey(list.get(i),"id"), PushStatusConstant.PUSH_FAILED,list.get(i).getClass());
            }
        }
    }

    public String getDeviceId(JsonObject obj) {
        if (obj == null){
            return null;
        }

        JsonElement devices = obj.get("devices") ;
        if (devices.isJsonNull()){
            return null;
        }

        JsonArray array = devices.getAsJsonArray();
        for(int i=0;i<array.size();i++) {
            JsonObject object = array.get(i).getAsJsonObject();
            String devoceOd = object.get("id").getAsString();
            if (StrUtil.isNotBlank(devoceOd)) {
                return devoceOd;
            }
        }
        return null;
    }

    public PushVo assemblyData(Object obj){
        PushVo pushVo = new PushVo();
        Map map = new HashMap(10);
        if (obj instanceof GpsReport){
            map.put("latitude",((GpsReport) obj).getLatitude());
            map.put("longitude",((GpsReport) obj).getLongitude());
            map.put("timestamp",((GpsReport) obj).getTimestamp());
            map.put("sn",((GpsReport) obj).getSn());
            map.put("reportType","gpsReport");
        }else if(obj instanceof TamperReport){
            map.put("timestamp",((TamperReport) obj).getTimestamp());
            map.put("sn",((TamperReport) obj).getSn());
            map.put("reportType","tamperReport");
        }
        pushVo.setData(map);
        return pushVo;
    }
}

