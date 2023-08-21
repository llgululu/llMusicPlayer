package com.gululu.llmusicplayer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author llgululu
 * @since 2023-07-30
 */
public class Setting implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "se_id", type = IdType.AUTO)
    private Integer seId;

    /**
     * 微信小程序appid
     */
    private String seAppId;

    /**
     * 微信小程序appSecret
     */
    private String seAppSecret;
    
    private String seOpenIdUrl;
    
    /**
     * 网易云音乐 登录后的接口cookie
     */
    private String seMusicCookie;

    public String getSeOpenIdUrl() {
        return seOpenIdUrl;
    }

    public void setSeOpenIdUrl(String seOpenIdUrl) {
        this.seOpenIdUrl = seOpenIdUrl;
    }

    public String getSeMusicCookie() {
        return seMusicCookie;
    }

    public void setSeMusicCookie(String seMusicCookie) {
        this.seMusicCookie = seMusicCookie;
    }

    public Integer getSeId() {
        return seId;
    }

    public void setSeId(Integer seId) {
        this.seId = seId;
    }

    public String getSeAppId() {
        return seAppId;
    }

    public void setSeAppId(String seAppId) {
        this.seAppId = seAppId;
    }

    public String getSeAppSecret() {
        return seAppSecret;
    }

    public void setSeAppSecret(String seAppSecret) {
        this.seAppSecret = seAppSecret;
    }


    @Override
    public String toString() {
        return "Setting{" +
                "seId=" + seId +
                ", seAppId='" + seAppId + '\'' +
                ", seAppSecret='" + seAppSecret + '\'' +
                ", seOpenIdUrl='" + seOpenIdUrl + '\'' +
                ", seMusicCookie='" + seMusicCookie + '\'' +
                '}';
    }
}
