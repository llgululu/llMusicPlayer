package com.gululu.llmusicplayer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author llgululu
 * @since 2023-07-30
 */
@TableName("love_music")
public class LoveMusic implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "lo_id", type = IdType.AUTO)
    private Integer loId;

    /**
     * 歌曲id
     */
    private String loMusicId;
    

    /**
     * 用户id
     */
    private String loPlaylistId;

    /**
     * 收藏时间
     */
    private Date loTime;

    public Integer getLoId() {
        return loId;
    }

    public void setLoId(Integer loId) {
        this.loId = loId;
    }

    public String getLoMusicId() {
        return loMusicId;
    }

    public void setLoMusicId(String loMusicId) {
        this.loMusicId = loMusicId;
    }
    

    public String getLoPlaylistId() {
        return loPlaylistId;
    }

    public void setLoPlaylistId(String loPlaylistId) {
        this.loPlaylistId = loPlaylistId;
    }

    public Date getLoTime() {
        return loTime;
    }

    public void setLoTime(Date loTime) {
        this.loTime = loTime;
    }

    @Override
    public String toString() {
        return "LoveMusic{" +
        "loId = " + loId +
        ", loMusicId = " + loMusicId +
        ", loPlaylistId = " + loPlaylistId +
        ", loTime = " + loTime +
        "}";
    }
}
