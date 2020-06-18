package simo.conn.push.dao.Impl;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import simo.conn.push.dao.DataIterable;
import simo.conn.push.constant.PushStatusConstant;
import simo.conn.push.entity.GpsReport;
import simo.conn.push.dao.ReportDao;

import java.util.List;
import java.util.Set;

/**
 * description: GpsReportServiceImpl <br>
 * date: 2020/5/22 11:01 <br>
 * author: EDZ <br>
 * version: 1.0 <br>
 */
@Service
public class ReportDaoImpl implements ReportDao {

    private MongoTemplate mongoTemplate;
    @Autowired
    private MappingMongoConverter converter;

    @Autowired
    public ReportDaoImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    /**
     *  查找未推送得数据（查询最大数量为：10000，后续业务数量上来后再进行调整）
     * @return  GPS上报集合
     */
    @Override
    public List<GpsReport> findAllByPushStatus() {
        // 条件筛选操作
        AggregationOperation matchOperation = Aggregation.match(Criteria.where("push_status").is(PushStatusConstant.NOT_PUSHED));
        // 排序操作
        AggregationOperation sortOperation = Aggregation.sort(Sort.Direction.DESC,"timestamp");
        // 选择条数
        AggregationOperation limitOperation =Aggregation.limit(500);
        // 从左到右按顺序操作,注意顺序不一样结果会不一样
        // 例如先排序后取条数，和先去条数后排序是完全不一样的意思
        Aggregation aggregation = Aggregation.newAggregation(matchOperation,
                sortOperation,limitOperation);
        // 查询
        AggregationResults<GpsReport> aggregationResults =
                mongoTemplate.aggregate(aggregation, GpsReport.class, GpsReport.class);
        // 获取结果集
        return aggregationResults.getMappedResults();
    }

    @Override
    public <T> DataIterable<T> findData(String collectionName, Class<T> clazz, String status) {
        MongoCollection<Document> coll = mongoTemplate.getCollection(collectionName);
        BasicDBObject query = new BasicDBObject("push_status", status);
        BasicDBObject sort = new BasicDBObject("timestamp", 1);
        FindIterable<Document> documents = coll.find(query);
        documents.sort(sort);
        return new DataIterable<>(documents,converter,clazz);
    }

    public <T> DataIterable<T> findData1(String collectionName, Class<T> clazz, String status) {
        MongoCollection<Document> coll = mongoTemplate.getCollection(collectionName);
        BasicDBObject query = new BasicDBObject("push_status", status);
        BasicDBObject sort = new BasicDBObject("timestamp", 1);
        FindIterable<Document> documents = coll.find(query);
        documents.sort(sort);
        return new DataIterable<>(documents,converter,clazz);
    }

    /**
     *  查批量修改GPS推送状态
     * @param  ids  批量修改ID集合
     * @param  pushStatus  推送状态
     * @return Boolean
     */
    @Override
    public Boolean batchModifyPushStatus(Set<Object> ids, String pushStatus, Class type) {
        // 回写设备push状态
        Criteria where = new Criteria();
        where.and("id").in(ids);
        Query query=new Query(where);
        Update update = new Update();
        // 更新内容
        update.set("push_status",pushStatus);

        UpdateResult updateResult = mongoTemplate.updateMulti(query,update,type);
        return updateResult.getModifiedCount() > 0;
    }

    @Override
    public Boolean modifyPushStatus(ObjectId id, String pushStatus,Class type) {
        // 回写设备push状态
        Criteria where = new Criteria();
        where.and("id").is(id);
        Query query=new Query(where);
        Update update = new Update();
        // 更新内容
        update.set("push_status",pushStatus);

        UpdateResult updateResult = mongoTemplate.updateMulti(query,update,type);
        return updateResult.getModifiedCount() > 0;
    }

    @Override
    public List<GpsReport> findFailedPushData() {
        // 条件筛选操作
        AggregationOperation matchOperation = Aggregation.match(Criteria.where("push_status").is(PushStatusConstant.PUSH_FAILED));
        // 排序操作
        AggregationOperation sortOperation = Aggregation.sort(Sort.Direction.DESC,"timestamp");
        // 选择条数
        AggregationOperation limitOperation =Aggregation.limit(10000);
        // 从左到右按顺序操作,注意顺序不一样结果会不一样
        // 例如先排序后取条数，和先去条数后排序是完全不一样的意思
        Aggregation aggregation = Aggregation.newAggregation(matchOperation,
                sortOperation,limitOperation);
        // 查询
        AggregationResults<GpsReport> aggregationResults =
                mongoTemplate.aggregate(aggregation, GpsReport.class, GpsReport.class);
        // 获取结果集
        return aggregationResults.getMappedResults();
    }
}
