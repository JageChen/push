package simo.conn.push.vo;

import lombok.Data;

import java.util.Map;

/**
 * description: PushVo <br>
 * date: 2020/6/5 18:28 <br>
 * author: EDZ <br>
 * version: 1.0 <br>
 */
@Data
public class PushVo {
    private String message;
    private Boolean notify;
    private Integer severity;
    private Map<String,Object> data;

    public PushVo() {
        notify = true;
        severity = 0;
        message = "push";
    }
}
