package simo.conn.push.vo;

import lombok.Data;

import java.util.List;

/**
 * description: GetDeviceIdVo <br>
 * date: 2020/6/5 15:25 <br>
 * author: EDZ <br>
 * version: 1.0 <br>
 */
@Data
public class FindDeviceIdVo {
    private String search_key;
    private String[] tags;

    public FindDeviceIdVo(String search_key) {
        this.search_key = search_key;
        this.tags = new String[0];
    }
}
