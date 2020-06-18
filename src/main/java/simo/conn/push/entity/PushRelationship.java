package simo.conn.push.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * description: PushRelationship <br>
 * date: 2020/5/21 16:07 <br>
 * author: EDZ <br>
 * version: 1.0 <br>
 */
@Data
@Document(collection="PUSH_RELATIONSHIP")
public class PushRelationship {
    //流水号
    @Indexed
    @Field("sn")
    private String sn;
    //流水号
    @Indexed
    @Field("device_id")
    private String deviceId;

    public PushRelationship(String sn, String deviceId) {
        this.sn = sn;
        this.deviceId = deviceId;
    }
}
