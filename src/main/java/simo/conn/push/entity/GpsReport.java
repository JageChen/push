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
 */
@Data
@Document(collection="GPS_REPORT")
public class GpsReport implements Serializable {
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
    //GPS 纬度
    private String latitude;
    //GPS 经度
    private String longitude;
    //时间
    @Field("create_date")
    private Long createDate;
    //当前运行状态
    private String mode;
    //电池电量，显示百分比
    private String batteryLevel;
    //防拆开关的状态，1 – safety，0 – broken
    private Integer tamperSensorStatus;
    //Timestamp
    @Indexed
    private String timestamp ;
    //secretId
    @Indexed
    @Field("secret_id")
    private String secretId;
    //推送状态,默认false
    @Indexed
    @Field("push_status")
    private String pushStatus = PushStatusConstant.NOT_PUSHED;
    public GpsReport() {
        traceId = 625L;
        sn = "itb111030cdngvdc";
        longitude = "0.0";
        latitude = "0.0";
        mode = "passiveMode";
        batteryLevel= "1";
        pushStatus = "NOT_PUSHED";
    }
}
