package simo.conn.push.dao.Impl;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import simo.conn.push.dao.PushRelationshipDao;
import simo.conn.push.entity.PushRelationship;

import java.util.List;
import java.util.Map;

/**
 * description: PushRelationshipDaoImpl <br>
 * date: 2020/5/28 11:14 <br>
 * author: EDZ <br>
 * version: 1.0 <br>
 */
@Service
public class PushRelationshipDaoImpl implements PushRelationshipDao {

    private MongoTemplate mongoTemplate;

    @Autowired
    public PushRelationshipDaoImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Map<String,Object> findAllByPushStatus(String sn) {
        DBObject obj = new BasicDBObject();
        //添加条件
        obj.put("sn", new BasicDBObject("$eq", sn));

        BasicDBObject fieldsObject = new BasicDBObject();
         //也可以返回数组内Document的字段
        //返回普通字段
        fieldsObject.put("device_id", true);

        Query query = new BasicQuery( obj.toString(), fieldsObject.toString());
        // 注意这里泛型的选择
        Map<String,Object> result = mongoTemplate.findOne(query, Map.class, "PUSH_RELATIONSHIP");

        return result;
    }

    @Override
    public Boolean insRelationship(PushRelationship pushRelationship) {
        return mongoTemplate.insert(pushRelationship)!=null ;
    }
}
