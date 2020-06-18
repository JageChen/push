package simo.conn.push;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import simo.conn.push.constant.ThreadPoolConstant;
import simo.conn.push.utils.ExecutorsUtil;
import simo.conn.push.utils.SslUtils;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;

@SpringBootApplication
@EnableApolloConfig
public class SimoConnPushApplication {

	public static void main(String[] args) {
		try{
			SpringApplication.run(SimoConnPushApplication.class, args);
		}catch (Exception e){
			System.out.println(e);
		}

	}

	@Bean("newFixedThreadPool")
	public ExecutorService newFixedThreadPool(){
		return ExecutorsUtil.newFixedThreadPool(ThreadPoolConstant.THREAD_CORE_COUNT,ThreadPoolConstant.THREAD_NAME);
	}

	@Bean("manageThreadPool")
	public ExecutorService manageThreadPool(){
		return ExecutorsUtil.newFixedThreadPool(ThreadPoolConstant.MANAGE_THREAD,ThreadPoolConstant.MANAGE_THREAD_NAME);
	}

	@Bean("connectionPool")
	public PoolingHttpClientConnectionManager getConnectionPool() throws KeyManagementException, NoSuchAlgorithmException {
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.INSTANCE)
				.register("https", new SSLConnectionSocketFactory(SslUtils.createIgnoreVerifySSL()))
				.build();

		PoolingHttpClientConnectionManager connectionPool = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

		//将最大连接数增加到200
		connectionPool.setMaxTotal(200);
		//将每个路由的默认最大连接数增加到20
		connectionPool.setDefaultMaxPerRoute(20);
		return connectionPool;
	}
}
