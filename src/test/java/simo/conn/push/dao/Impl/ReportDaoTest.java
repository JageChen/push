package simo.conn.push.dao.Impl;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import simo.conn.push.dao.DataIterable;
import simo.conn.push.SimoConnPushApplication;
import simo.conn.push.constant.PushStatusConstant;
import simo.conn.push.entity.GpsReport;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SimoConnPushApplication.class)

public class ReportDaoTest {

    @Autowired
    private ReportDaoImpl reportDao;

    @org.junit.Test
    public void findData() {
        DataIterable<GpsReport> iter = reportDao.findData1("GPS_REPORT", GpsReport.class, PushStatusConstant.NOT_PUSHED);
        for (GpsReport record : iter) {
            System.out.println(record);
        }
    }
}