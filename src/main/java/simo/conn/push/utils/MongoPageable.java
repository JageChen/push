package simo.conn.push.utils;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.io.Serializable;

/**
 * description: MongoPageable <br>
 * date: 2020/5/22 11:14 <br>
 * author: EDZ <br>
 * version: 1.0 <br>
 */
public class MongoPageable implements Serializable, Pageable {
    private static final long serialVersionUID = 1L;
    /**
     * 当前页
     */
    private Integer pagenumber;
    /**
     * 当前页面条数
     */
    private Integer pagesize;
    /**
     * 排序条件
     */
    private Sort sort;

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    /**
     * 当前页面
     * @return
     */
    @Override
    public int getPageNumber() {
        return getPagenumber();
    }

    /**
     * 每一页显示的条数
     * @return
     */
    @Override
    public int getPageSize() {
        return getPagesize();
    }

    /**
     * 第二页所需要增加的数量
     * @return
     */
    @Override
    public long getOffset() {
        return (getPagenumber() - 1) * getPagesize();
    }
    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return null;
    }

    @Override
    public Pageable previousOrFirst() {
        return null;
    }

    @Override
    public Pageable first() {
        return null;
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }

    public Integer getPagenumber() {
        return pagenumber;
    }
    public void setPagenumber(Integer pagenumber) {
        this.pagenumber = pagenumber;
    }
    public Integer getPagesize() {
        return pagesize;
    }
    public void setPagesize(Integer pagesize) {
        this.pagesize = pagesize;
    }

    public MongoPageable(Integer pagenumber, Integer pagesize) {
        this.pagenumber = pagenumber;
        this.pagesize = pagesize;
    }
}