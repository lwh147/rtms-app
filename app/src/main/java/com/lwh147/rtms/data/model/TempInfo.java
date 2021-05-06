package com.lwh147.rtms.data.model;

import java.util.Date;

/**
 * @description: 体温信息类
 * @author: lwh
 * @create: 2021/5/6 13:42
 * @version: v1.0
 **/
public class TempInfo {
    /**
     * 主键
     */
    private Long id;

    /**
     * 检测时间
     */
    private Date time;

    /**
     * 检测结果，温度摄氏度
     */
    private Float temp;

    /**
     * 外键 居民id
     */
    private Long residentId;

    /**
     * 外键 居民姓名
     */
    private String residentName;

    public String getResidentName() {
        return residentName;
    }

    public Long getResidentId() {
        return residentId;
    }

    public Long getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public Float getTemp() {
        return temp;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setTemp(Float temp) {
        this.temp = temp;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public void setResidentName(String residentName) {
        this.residentName = residentName;
    }
}
