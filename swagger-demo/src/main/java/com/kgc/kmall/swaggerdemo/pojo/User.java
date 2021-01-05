package com.kgc.kmall.swaggerdemo.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author shkstart
 * @create 2020-12-24 14:50
 */
@ApiModel("用户实体类")
public class User {

    @ApiModelProperty("用户编号")
    private int id;
    @ApiModelProperty("用户姓名")
    private String name;
    @ApiModelProperty("年龄")
    private int age;
    @ApiModelProperty("手机")
    private String phone;
    public User(int id, String name, int age, String phone) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.phone = phone;
    }

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}