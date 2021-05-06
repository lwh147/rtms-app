package com.lwh147.rtms.data.model;

/**
 * @description: 居民model
 * @author: lwh
 * @create: 2021/5/6 13:44
 * @version: v1.0
 **/
public class Resident {
    /**
     * 序号
     */
    private Integer order;

    /**
     * 主键
     */
    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别（0：男，1：女）
     */
    private Byte sex;

    /**
     * 楼号
     */
    private Byte building;

    /**
     * 单元号
     */
    private Byte entrance;

    /**
     * 房间号
     */
    private Integer room;

    /**
     * 联系电话
     */
    private String phone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Byte getSex() {
        return sex;
    }

    public void setSex(Byte sex) {
        this.sex = sex;
    }

    public Byte getBuilding() {
        return building;
    }

    public void setBuilding(Byte building) {
        this.building = building;
    }

    public Byte getEntrance() {
        return entrance;
    }

    public void setEntrance(Byte entrance) {
        this.entrance = entrance;
    }

    public Integer getRoom() {
        return room;
    }

    public void setRoom(Integer room) {
        this.room = room;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
