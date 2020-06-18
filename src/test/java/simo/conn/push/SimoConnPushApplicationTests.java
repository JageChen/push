package simo.conn.push;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.codec.AvroJacksonCodec;
import org.redisson.codec.FstCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.yaml.snakeyaml.util.ArrayUtils;
import simo.conn.push.entity.GpsReport;
import simo.conn.push.threads.manager.PushThreadPoolManager;
import simo.conn.push.utils.ExecutorsUtil;
import simo.conn.push.utils.HttpConnectUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class SimoConnPushApplicationTests {

	@Autowired
	private RedissonClient redissonClient;

	@Test
	void contextLoads() {
		List<String> data = new ArrayList<String>();
		for (int i = 0; i < 6666; i++) {
			data.add("item" + i);
		}
		handleList(data, 5);
		System.out.println(data.toString());
	}

	public synchronized void handleList(List<String> data, int threadNum) {
		int length = data.size();
		int tl = length % threadNum == 0 ? length / threadNum : (length/threadNum+1);
		for (int i = 0; i < threadNum; i++) {
			int end = (i + 1) * tl;
			HandleThread thread = new HandleThread("线程[" + (i + 1) + "] ",  data, i * tl, end > length ?length : end);
			thread.start();
		}
	}
	class HandleThread extends Thread {
		private String threadName;
		private List<String> data;
		private int start;
		private int end;


		public HandleThread(String threadName, List<String> data, int start, int end) {
			this.threadName = threadName;
			this.data = data;
			this.threadName = threadName;
			this.start = start;
			this.end = end;
		}

		public void run() {
			List<String> subList = data.subList(start, end);
			System.out.println(threadName+"处理了"+subList.size()+"条！");
		}
	}
	/**
	 *TODO 模拟这个是商品库存
	 */
	public static volatile Integer TOTAL = 10;
	@Test
	void testRedissonLock() throws InterruptedException {
		for (int i = 0; i <100 ; i++) {
			RLock rlock = redissonClient.getLock("key");
			rlock.lock(1,TimeUnit.SECONDS);
			if (TOTAL > 0) {
				TOTAL--;
			}
			Thread.sleep(50);
			System.out.println("======减完库存后,当前库存===" + TOTAL);
			//TODO 如果该线程还持有该锁，那么释放该锁。如果该线程不持有该锁，说明该线程的锁已到过期时间，自动释放锁
			if (rlock.isHeldByCurrentThread()) {
				rlock.unlock();
			}
		}
	}


	public static void main(String[] args) throws Exception {
		//创建HTTP的连接池管理对象
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		//将最大连接数增加到200
		connectionManager.setMaxTotal(200);
		//将每个路由的默认最大连接数增加到20
		connectionManager.setDefaultMaxPerRoute(20);
		//将http://www.baidu.com:80的最大连接增加到50
		//HttpHost httpHost = new HttpHost("http://www.baidu.com",80);
		//connectionManager.setMaxPerRoute(new HttpRoute(httpHost),50);
		long start = System.currentTimeMillis();
		//发起3次GET请求
		String url ="http://localhost:8029/getDeviceId";
//		for (int i=0;i<100;i++){
		GpsReport gpsReport = new GpsReport();
//			HttpConnectUtil.doPost(connectionManager,url,gpsReport,null);
//		}
		long end = System.currentTimeMillis();
		System.out.println("consume -> " + (end - start));
		//清理无效连接
		new HttpConnectUtil.IdleConnectionEvictor(connectionManager).start();
	}
}
