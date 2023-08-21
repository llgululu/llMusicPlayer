package com.gululu.llmusicplayer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author llgululu
 * @since 2023-08-12
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    

    @TableId(value = "u_id", type = IdType.AUTO)
    private Integer uId;

    /**
     * openid微信
     */
    private String uOpenId;

    /**
     * 用户收藏歌单id
     */
    private String uPlaylistId;

    /**
     * 用户最新使用时间
     */
    private Date uUpdateTime;

    /**
     * 用户注册时间
     */
    private Date uSignTime;

    public Integer getuId() {
        return uId;
    }

    public void setuId(Integer uId) {
        this.uId = uId;
    }

    public String getuOpenId() {
        return uOpenId;
    }

    public void setuOpenId(String uOpenId) {
        this.uOpenId = uOpenId;
    }

    public String getuPlaylistId() {
        return uPlaylistId;
    }

    public void setuPlaylistId(String uPlaylistId) {
        this.uPlaylistId = uPlaylistId;
    }

    public Date getuUpdateTime() {
        return uUpdateTime;
    }

    public void setuUpdateTime(Date uUpdateTime) {
        this.uUpdateTime = uUpdateTime;
    }

    public Date getuSignTime() {
        return uSignTime;
    }

    public void setuSignTime(Date uSignTime) {
        this.uSignTime = uSignTime;
    }

    @Override
    public String toString() {
        return "User{" +
        "uId = " + uId +
        ", uOpenId = " + uOpenId +
        ", uPlaylistId = " + uPlaylistId +
        ", uUpdateTime = " + uUpdateTime +
        ", uSignTime = " + uSignTime +
        "}";
    }
}
