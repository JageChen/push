package simo.conn.push.entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import simo.conn.push.constant.PushStatusConstant;

import java.io.Serializable;

/**
 * description: GpsReportDTO
 * date: 2020/4/10 9:02
 * author: Jage
 * version: 1.0
 * @author EDZ
 */
@Data
@Document(collection="TAMPER_REPORT")
public class TamperReport implements Serializable {
    //(1:GpsService 2:TrackingService 3：Tamper alarm)
    @Id
    private ObjectId id;

    @Field("method_type")
    private Integer methodType;
    //流水号
    @Indexed
    @Field("trace_id")
    private Long traceId;
    //Sn
    @Indexed
    @Field("sn")
    private String sn;
    //防拆开关的状态，1 – safety，0 – broken
    private Integer tamperSensorStatus;
    //Timestamp
    private String timestamp ;
    //secretId
    @Indexed
    @Field("secret_id")
    private String secretId;
    //推送状态,默认false
    @Indexed
    @Field("push_status")
    private String pushStatus = PushStatusConstant.NOT_PUSHED;

}
