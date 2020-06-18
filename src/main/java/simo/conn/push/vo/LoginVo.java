package simo.conn.push.vo;

import lombok.Data;
import simo.conn.push.utils.DateUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * description: LoginVo <br>
 * date: 2020/6/5 14:18 <br>
 * author: EDZ <br>
 * version: 1.0 <br>
 */
@Data
public class LoginVo {
    private String email;
    private String password;
    private Integer acctype;
    private Long expire;

    public LoginVo(String email, String password) {
        this.email = email;
        this.password = password;
        this.acctype = 0;
        this.expire = DateUtil.getTimeStamp();
    }




    public static void main(String[] args) {
        long aa =System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        long bb = calendar.getTime().getTime();

        long cc = bb -aa;
        System.out.println(aa);
        System.out.println(bb);
        System.out.println(cc);
    }
}
