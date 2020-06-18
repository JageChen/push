package simo.conn.push.utils;

import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.*;
import simo.conn.push.vo.PushVo;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AikaanHttpApi {
    private static AsyncHttpClientConfig cf = new DefaultAsyncHttpClientConfig
            .Builder()
            .setConnectTimeout(2000)
            .setRequestTimeout(20000)
            .build();

    private static final AsyncHttpClient client = new DefaultAsyncHttpClient(cf);

    private static final String URL = "https://experience.aikaan.io";
    private static final String TOKEN_PATH = "/api/_external/auth/v1/signin";
    private static final String DEVICE_ID_PATH = "/dm/api/dm/v1/device/search?limit=1&offset=0&profileType=0";
    private static final String EVENT_PATH = "/es/api/es/v1/device/%s/busev";
    private static final Gson gson = new Gson();

    public String getToken(String user, String passwd, long expireAt) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("acctype", 0);
        params.put("email", user);
        params.put("expire", expireAt);
        params.put("password", passwd);

        ListenableFuture<Response> future = client
                .preparePost(URL + TOKEN_PATH)
                .addHeader("content-type", "application/json")
                .setBody(gson.toJson(params))
                .execute();
        String responseBody = null;
        try {
            responseBody = future.get().getResponseBody();
            JsonObject object = gson.fromJson(responseBody, JsonObject.class);
            object = object.getAsJsonObject("data");
            String token = object.get("token").toString();
            token = token.replace("\"", "");
            if (StrUtil.isNotBlank(token)) {
                token = "aicon-jwt=" + token;
            }
            return token;
        } catch (Exception e) {
            log.error("getToken error, params:{} response:{}", params, responseBody, e);
        }
        return null;
    }

    public String getDeviceId(String sn, String token) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("search_key", sn);
        params.put("tags", Collections.emptyList());

        ListenableFuture<Response> future = client
                .preparePost(URL + DEVICE_ID_PATH)
                .addHeader("content-type", "application/json")
                .addHeader("Cookie", token)
                .setBody(gson.toJson(params))
                .execute();
        String responseBody = null;
        try {
            responseBody = future.get().getResponseBody();
            JsonObject object1 = gson.fromJson(responseBody, JsonObject.class);
            JsonArray array = object1.get("devices").getAsJsonArray();

            for (int i = 0; i < array.size(); i++) {
                JsonObject object = array.get(i).getAsJsonObject();
                String devoceOd = object.get("id").getAsString();
                if (StrUtil.isNotBlank(devoceOd)) {
                    return devoceOd;
                }
            }
        } catch (Exception e) {
            log.error("getDeviceId error, params:{} response:{}", params, responseBody, e);
        }
        return null;
    }

    public boolean pushIndividual(String deviceId, PushVo vo, String token) throws Exception {
        ListenableFuture<Response> future = client
                .preparePost(URL + String.format(EVENT_PATH, deviceId))
                .addHeader("content-type", "application/json")
                .addHeader("Cookie", token)
                .setBody(gson.toJson(vo))
                .execute();
        Response response = null;
        try {
            response = future.get();
            return response.getStatusCode() == 200;
        } catch (Exception e) {
            if (response != null) {
                log.error("pushIndividual error, code:{} body:{}",
                        response.getStatusCode(), response.getResponseBody(), e);
            //todo custom Expire Excetpion
            }
        }
        return false;
    }

    public void close() throws IOException {
        client.close();
    }

    public static void main(String[] args) throws Exception {
        AikaanHttpApi api = new AikaanHttpApi();

        String user = "demo@aikaan.io";
        String passwd = "MyDemoPass123";
        long expireAt = System.currentTimeMillis() + 3600_000;

        String token = api.getToken(user, passwd, expireAt);
        System.out.println(token);

        String deviceName = "";
        String deviceId = api.getDeviceId(deviceName, token);
        System.out.println(deviceId);
        PushVo pushVo = new PushVo();
        Map<String, Object> map = new HashMap<>(10);
        map.put("latitude", 12.3456);
        map.put("longitude", 65.4321);
        map.put("timestamp", System.currentTimeMillis());
        map.put("sn", deviceName);
        map.put("reportType", "gpsReport");
        pushVo.setData(map);
        boolean pushResult = api.pushIndividual(deviceId, pushVo, token);
        System.out.println("推送结果：" + pushResult);

        api.close();
    }

}
