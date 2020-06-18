package simo.conn.push.utils;

import cn.hutool.core.util.ReflectUtil;
import org.bson.types.ObjectId;
import simo.conn.push.entity.GpsReport;

import java.util.*;

/**
 * description: TODO 集合工具类 <br>
 * date: 2020/5/25 10:32 <br>
 * author: EDZ <br>
 * version: 1.0 <br>
 */
public class CollectionUtil {

    public static List assemblyDeduplication(List list) {
        final boolean sta = null != list && list.size() > 0;
        if (sta) {
            Set set = new HashSet(list);
            return new ArrayList(set);
        }
        return null;
    }


    /**
     * TODO 根据属性名反射获取值并去重
     * @param list  集合数据
     * @param fieldName 属性名
     * @return set
     */
    public static Set<Object> valueDeduplication(List list, String fieldName) {
        Set<Object> lSns = new HashSet<>();
        for (int i = 0; i <list.size() ; i++) {
            lSns.add(ReflectUtil.getFieldValue(list.get(i), fieldName));
        }
        return lSns;
    }
}
