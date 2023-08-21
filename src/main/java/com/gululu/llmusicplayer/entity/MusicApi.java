package com.gululu.llmusicplayer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author llgululu
 * @since 2023-07-30
 */
@TableName("music_api")
public class MusicApi implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ap_id", type = IdType.AUTO)
    private Integer apId;

    /**
     * 接口url
     */
    private String apUrl;

    /**
     * 接口获得数据的长度(>=2)
     */
    private Integer apNumber;

    /**
     * 接口名称
     */
    private String apName;

    public Integer getApId() {
        return apId;
    }

    public void setApId(Integer apId) {
        this.apId = apId;
    }

    public String getApUrl() {
        return apUrl;
    }

    public void setApUrl(String apUrl) {
        this.apUrl = apUrl;
    }

    public Integer getApNumber() {
        return apNumber;
    }

    public void setApNumber(Integer apNumber) {
        this.apNumber = apNumber;
    }

    public String getApName() {
        return apName;
    }

    public void setApName(String apName) {
        this.apName = apName;
    }

    @Override
    public String toString() {
        return "MusicApi{" +
        "apId = " + apId +
        ", apUrl = " + apUrl +
        ", apNumber = " + apNumber +
        ", apName = " + apName +
        "}";
    }
}
