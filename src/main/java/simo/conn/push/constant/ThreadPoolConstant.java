package simo.conn.push.constant;

/**
 * description: ThreadPoolConstant <br>
 * date: 2020/5/27 10:45 <br>
 * author: EDZ <br>
 * version: 1.0 <br>
 */
public class ThreadPoolConstant {
    /**
     *  推送任务线程池核心线程数量
     */
    public static Integer THREAD_CORE_COUNT = 8;

    /**
     *  管理线程池核心线程数
     */
    public static Integer MANAGE_THREAD = 2;

    /**
     *  推送任务线程池名称
     */
    public static String THREAD_NAME = "push";

    /**
     *  管理线程池名称
     */
    public static String MANAGE_THREAD_NAME = "manage";
}
