package simo.conn.push.utils;

import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.micrometer.core.instrument.util.IOUtils;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import simo.conn.push.constant.PushStatusConstant;
import simo.conn.push.dao.ReportDao;
import simo.conn.push.entity.GpsReport;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.ExecutorService;

/**
 * description: HttpConnectUtil <br>
 * date: 2020/5/25 17:40 <br>
 * author: EDZ <br>
 * version: 1.0 <br>
 * @author EDZ
 */
@Component
@Slf4j
public class HttpConnectUtil {

    private static ReportDao reportDao;

    private ExecutorService newFixedThreadPool;

    @Autowired
    public HttpConnectUtil(ReportDao reportDao, ExecutorService newFixedThreadPool) {
        HttpConnectUtil.reportDao = reportDao;
        this.newFixedThreadPool = newFixedThreadPool;
    }

    /**
     * doPost
     * @param  url 请求地址
     * @param connectionManager
     * @throws Exception
     */
    public static CloseableHttpResponse doPost(HttpClientConnectionManager connectionManager, String url,Object requestParameters,String token,String contentType) throws Exception {
        //  从连接池中获取client对象，多例
            CloseableHttpClient httpClient = HttpClients.custom()
                    .disableRedirectHandling()
                .setConnectionManager(connectionManager)
                .build();

        // 创建http POST请求
        HttpPost httpPost = new HttpPost (url);

        // 构建请求配置信息
        // 创建连接的最长时间
        RequestConfig config = RequestConfig.custom().setConnectTimeout(10000)
                // 从连接池中获取到连接的最长时间
                .setConnectionRequestTimeout(5000)
                // 数据传输的最长时间10s
                .setSocketTimeout(10 * 1000)
                // 提交请求前测试连接是否可用
                .setStaleConnectionCheckEnabled(true)
                .build();
        // 设置请求配置信息
        httpPost.setConfig(config);
        // 设置请求参数
        if (requestParameters != null) {
            StringEntity entity = new StringEntity(requestParameters.toString(), "UTF-8");
            entity.setContentEncoding("UTF-8");

            if(StringUtils.isNotBlank(contentType)){
                entity.setContentType(contentType);
            }
            if (StrUtil.isNotBlank(token)){
                httpPost.setHeader("Cookie",token);
            }
             httpPost.setEntity(entity);
        }

        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = httpClient.execute(httpPost);
            // 判断返回状态是否为200
            return response;
        }catch(Exception e){
//            if (requestParameters instanceof GpsReport){
//                reportDao.modifyPushStatus(((GpsReport) requestParameters).getId(),PushStatusConstant.PUSH_FAILED);
//            }
          log.error("HTTPClient request error, the error message is: ",e);
        } finally {
            //TODO 此处不能关闭httpClient，如果关闭httpClient，连接池也会销毁
            // httpClient.close();
        }
        return null;
    }

    /**
     * 关闭response
     * @param response
     * @throws IOException
     */
    public static void closeResponse(CloseableHttpResponse response) throws IOException {
        if (response != null){
            response.close();
        }
    }

        /**
         * TODO 监听连接池中空闲连接，清理无效连接
         */
        public static class IdleConnectionEvictor extends Thread {

            private final HttpClientConnectionManager connectionManager;

            public IdleConnectionEvictor(HttpClientConnectionManager connectionManager) {
                this.connectionManager = connectionManager;
            }

            @Override
            public void run() {
                try {
                    while (true) {
                        // 3s检查一次
                        sleep(3000);
                        // 关闭失效的连接
                        connectionManager.closeExpiredConnections();
                    }
                } catch (InterruptedException ex) {
                    // 结束
                    ex.printStackTrace();
                }
            }
        }
}
