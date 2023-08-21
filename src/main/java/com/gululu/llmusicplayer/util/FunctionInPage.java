package com.gululu.llmusicplayer.util;

import com.alibaba.fastjson.JSONObject;
import com.gululu.llmusicplayer.entity.MusicApi;
import com.gululu.llmusicplayer.entity.Setting;
import com.gululu.llmusicplayer.entity.User;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gululu.llmusicplayer.util.JsonAnalysisUtil.*;
import static com.gululu.llmusicplayer.util.RestTemplateUtil.getJsonDataRestTemplate;

@Slf4j
public class FunctionInPage {
    /**
     * @param setting                 设置信息
     * @param recommendedPlaylistsApi 请求每日推荐歌单接口
     * @param recommendedSongsApi     请求每日推荐歌曲接口
     * @param musicUrlApi             歌曲url获得接口
     * @return 整体数据
     */
    public static Map<Object, Object> onloadInPageIndex(Setting setting, MusicApi recommendedPlaylistsApi, MusicApi recommendedSongsApi, MusicApi musicUrlApi) {
        Map<Object, Object> map = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        param.put("cookie", setting.getSeMusicCookie());
        // 每日推荐歌单
        JSONObject recommendedPlaylistsBody = getJsonDataRestTemplate(recommendedPlaylistsApi.getApUrl(), param);
        // 每日推荐歌曲
        JSONObject recommendedSongsBody = getJsonDataRestTemplate(recommendedSongsApi.getApUrl(), param);
        if (recommendedPlaylistsBody.getInteger("code") > 200 || recommendedSongsBody.getInteger("code") > 200) {
            log.info("url地址错误，地址为{}或{}", recommendedPlaylistsApi.getApUrl(), recommendedSongsApi.getApUrl());
            map.put("code", 404);
        } else {
            List<Map<String, Object>> recommendedPlaylistsList = getRecommendedPlaylistsPageIndex(recommendedPlaylistsBody, recommendedPlaylistsApi.getApNumber());
            map.put("recommendedPlaylists", recommendedPlaylistsList);
            List<Map<String, Object>> recommendedSongsList = getRecommendedSongsPageRecommendedOrIndex(recommendedSongsBody, recommendedSongsApi.getApNumber());
            Map<String, Object> map1 = new HashMap<>();
            map1.put("id", "recommendedSongsInIndex");
            map1.put("songsNumber", recommendedSongsList.size());
            map1.put("music_list", recommendedSongsList);
            map.put("playlist", map1);
        }
        return map;
    }

    /**
     * @param setting             设置信息
     * @param recommendedSongsApi 请求每日推荐歌曲接口
     * @param musicUrlApi         歌曲url获得接口
     * @return 整体数据
     */
    public static Map<Object, Object> onloadInPageRecommended(Setting setting, MusicApi recommendedSongsApi, MusicApi musicUrlApi) {
        Map<Object, Object> map = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        param.put("cookie", setting.getSeMusicCookie());
        // 每日推荐歌曲
        JSONObject recommendedSongsBody = getJsonDataRestTemplate(recommendedSongsApi.getApUrl(), param);
        if (recommendedSongsBody.getInteger("code") > 200) {
            log.info("url地址错误，地址为 {}", recommendedSongsApi.getApUrl());
            map.put("code", 404);
        } else {
            List<Map<String, Object>> recommendedSongsList = getRecommendedSongsPageRecommendedOrIndex(recommendedSongsBody, recommendedSongsApi.getApNumber());
            Map<String, Object> map1 = new HashMap<>();
            map1.put("id", "recommendedSongsInRecommended");
            map1.put("songsNumber", recommendedSongsList.size());
            map1.put("music_list", recommendedSongsList);
            map.put("playlist", map1);
        }
        return map;
    }

    /**
     * @param setting             设置信息
     * @param playlistCategoryApi 歌单分类数据接口
     * @return 整体数据
     */
    public static Map<Object, Object> onloadInPagePlaylistPiazza(Setting setting, MusicApi playlistCategoryApi) {
        Map<Object, Object> map = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        param.put("cookie", setting.getSeMusicCookie());
        // 歌单分类信息数据
        JSONObject playlistCategoryBody = getJsonDataRestTemplate(playlistCategoryApi.getApUrl(), param);
        if (playlistCategoryBody.getInteger("code") > 200) {
            log.info("url地址错误，地址为 {}", playlistCategoryApi.getApUrl());
            map.put("code", 404);
        } else {
            List<Map<String, Object>> categoriesList = getPlaylistCategoriesPagePlaylistPiazza(playlistCategoryBody);
            map.put("category_list", categoriesList);
        }
        return map;
    }

    /**
     * @param setting               设置信息
     * @param playlistByCategoryApi 根据分类查询歌曲api
     * @return 歌曲列表
     */
    public static Map<Object, Object> getPlaylistByCategoryPagePlaylistPiazza(Setting setting, MusicApi playlistByCategoryApi, int page, String cat) {
        Map<Object, Object> map = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        param.put("cookie", setting.getSeMusicCookie());
        int limit = playlistByCategoryApi.getApNumber();
        String url = playlistByCategoryApi.getApUrl() + "?cat=" + cat + "&limit=" + limit + "&offset=" + page * limit;
        // 根据分类查询歌单数据
        JSONObject playlistCategoryBody = getJsonDataRestTemplate(url, param);
        if (playlistCategoryBody.getInteger("code") > 200) {
            log.info("url地址错误，地址为{}", url);
            map.put("code", 404);
        } else {
            List<Map<String, Object>> playlist_list = getPlaylistByCategoryFromJson(playlistCategoryBody);
            int total = playlistCategoryBody.getIntValue("total");
            map.put("playlist_list", playlist_list);
            map.put("totalPage", total / playlistByCategoryApi.getApNumber() - 1);
        }
        return map;
    }

    /**
     * @param setting             设置信息
     * @param playlistInfoApi     歌单信息接口
     * @param playlistAllSongsApi 歌单全部歌曲接口
     * @param id                  歌单id
     * @return 返回数据
     */
    public static Map<Object, Object> getPlaylistInformationPagePlaylist(Setting setting, MusicApi playlistInfoApi, MusicApi playlistAllSongsApi, String id, User user) {
        Map<Object, Object> map = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        param.put("cookie", setting.getSeMusicCookie());
        String getPlaylistInfoUrl = playlistInfoApi.getApUrl() + "?id=" + id;
        String getPlaylistAllSongs = playlistAllSongsApi.getApUrl() + "?id=" + id;
        // 获取id 为 id 的歌单信息
        JSONObject playlistInfoBody = getJsonDataRestTemplate(getPlaylistInfoUrl, param);
        // 获取id 为 id 的歌单信息里面的所有歌曲
        JSONObject playlistAllSongsBody = getJsonDataRestTemplate(getPlaylistAllSongs, param);
        if (playlistInfoBody.getInteger("code") > 200 || playlistAllSongsBody.getInteger("code") > 200) {
            log.info("url地址错误，地址为{} 或者 {}", getPlaylistInfoUrl, getPlaylistAllSongs);
            map.put("code", 404);
        } else {
            Map<String, Object> map1 = getPlaylistInformation(playlistInfoBody, user);
            List<Map<String, Object>> songsList = getPlaylistAllSongsList(playlistAllSongsBody);
            map1.put("music_list", songsList);
            map.put("playlist", map1);
        }
        return map;
    }

    /**
     * @param setting            设置信息
     * @param rankingListInfoApi 榜单信息api
     * @return 榜单数据
     */
    public static Map<Object, Object> getRankingListInformationPageRankingList(Setting setting, MusicApi rankingListInfoApi) {
        Map<Object, Object> map = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        param.put("cookie", setting.getSeMusicCookie());
        // 获取id 为 id 的歌单信息
        JSONObject rankingListInfoBody = getJsonDataRestTemplate(rankingListInfoApi.getApUrl(), param);
        if (rankingListInfoBody.getInteger("code") > 200) {
            log.info("url地址错误，地址为{}", rankingListInfoApi.getApUrl());
            map.put("code", 404);
        } else {
            map = getRankingListInformation(rankingListInfoBody);
        }
        return map;
    }

    /**
     * @param setting     设置信息
     * @param musicUrlApi 获取音乐url 接口
     * @param id          歌曲id
     * @return 歌曲url
     */
    public static Map<Object, Object> getMusicURLByIdFunction(Setting setting, MusicApi musicUrlApi, String id) {
        Map<Object, Object> map = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        param.put("cookie", setting.getSeMusicCookie());
        // 获取id 为 id 的歌曲URL
        JSONObject musicURLBody = getJsonDataRestTemplate(musicUrlApi.getApUrl() + "?id=" + id, param);
        if (musicURLBody.getInteger("code") > 200) {
            log.info("url地址错误，地址为{}", musicUrlApi.getApUrl());
            map.put("code", 404);
        } else {
            map = getMusicURLByIdFunctionByJson(musicURLBody);
        }
        return map;
    }

    /**
     * @param setting  设置信息
     * @param musicApi 新建歌单api
     * @return 新建歌单id
     */
    public static String getCreateNewPlaylistId(Setting setting, MusicApi musicApi) {
        Map<String, Object> param = new HashMap<>();
        param.put("cookie", setting.getSeMusicCookie());
        // 新用户新建歌单
        JSONObject body = getJsonDataRestTemplate(musicApi.getApUrl(), param);
        if (body.getInteger("code") > 200) {
            log.info("url地址错误，地址为{}", musicApi.getApUrl());
            return "";
        } else {
            return body.getString("id");
        }
    }

    /**
     * @param setting    设置信息
     * @param musicApi   收藏或删除歌曲接口
     * @param musicId    音乐id
     * @param playlistId 歌单id
     * @param op         添加或者删除 add/del
     * @return 是否成功
     */
    public static Map<Object, Object> collectOrCancelMusicFunction(Setting setting, MusicApi musicApi, String musicId, String playlistId, String op) {
        Map<Object, Object> map = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        param.put("cookie", setting.getSeMusicCookie());
        String url = musicApi.getApUrl() + "?op=" + op + "&pid=" + playlistId + "&tracks=" + musicId;
        // 把id 为musicId的歌曲添加到id为playlistId的歌单里面
        JSONObject body = getJsonDataRestTemplate(url, param);
        if (body.containsKey("code") && body.getInteger("code") > 200) {
            log.info("url地址错误，地址为{}，请检查cookie", musicApi.getApUrl());
            map.put("code", 404);
        } else {
            map = collectOrCancelMusicFunctionByJson(body, musicId, playlistId, op);
        }
        return map;
    }

    /**
     * @param setting  设置信息
     * @param musicApi 音乐接口
     * @param keywords 搜索关键词
     * @return 搜索search页
     */
    public static Map<Object, Object> getSearchResultFunctionPageSearch(Setting setting, MusicApi musicApi, String keywords) {
        Map<Object, Object> map = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        param.put("cookie", setting.getSeMusicCookie());
        String getSongsUrl = musicApi.getApUrl() + "?keywords=" + keywords + "&limit=" + musicApi.getApNumber() + "&offset=0";
        String getPlaylistsUrl = musicApi.getApUrl() + "?type=1000&keywords=" + keywords + "&limit=" + musicApi.getApNumber() + "&offset=0";
        // 搜索结果 歌曲
        JSONObject body1 = getJsonDataRestTemplate(getSongsUrl, param);
        // 搜索结果 歌单
        JSONObject body2 = getJsonDataRestTemplate(getPlaylistsUrl, param);
        if (body1.getInteger("code") > 200 || body2.getInteger("code") > 200) {
            log.info("url地址错误，地址为{}，请检查cookie", musicApi.getApUrl());
            map.put("code", 404);
        } else {
            getSearchResultSongs(map, body1, musicApi.getApNumber(), 0);
            getSearchResultPlaylists(map, body2, musicApi.getApNumber(), 0);
        }
        return map;
    }

    /**
     * @param setting  设置信息
     * @param musicApi 音乐接口
     * @param keywords 关键字
     * @param page     分页数
     * @return 搜索单曲
     */
    public static Map<Object, Object> getSearchResultSongFunctionPageSearchSong(Setting setting, MusicApi musicApi, String keywords, int page) {
        Map<Object, Object> map = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        param.put("cookie", setting.getSeMusicCookie());
        String url = musicApi.getApUrl() + "?keywords=" + keywords + "&limit=" + musicApi.getApNumber() + "&offset=" + musicApi.getApNumber() * page;
        // 搜索结果 歌曲
        JSONObject body = getJsonDataRestTemplate(url, param);
        if (body.getInteger("code") > 200) {
            log.info("url地址错误，地址为{}，请检查cookie", musicApi.getApUrl());
            map.put("code", 404);
        } else {
            getSearchResultSongs(map, body, musicApi.getApNumber(), page);
        }
        return map;
    }

    /**
     * @param setting  设置信息
     * @param musicApi 音乐接口
     * @param keywords 关键字
     * @param page     分页数
     * @return 搜索歌单
     */
    public static Map<Object, Object> getSearchResultPlaylistFunctionPageSearchPlaylist(Setting setting, MusicApi musicApi, String keywords, int page) {
        Map<Object, Object> map = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        param.put("cookie", setting.getSeMusicCookie());
        String url = musicApi.getApUrl() + "&keywords=" + keywords + "&limit=" + musicApi.getApNumber() + "&offset=" + musicApi.getApNumber() * page;
        // 搜索结果 歌曲
        JSONObject body = getJsonDataRestTemplate(url, param);
        if (body.getInteger("code") > 200) {
            log.info("url地址错误，地址为{}，请检查cookie", musicApi.getApUrl());
            map.put("code", 404);
        } else {
            getSearchResultPlaylists(map, body, musicApi.getApNumber(), page);
        }
        return map;
    }

    /**
     * @param setting  设置信息
     * @param musicApi 音乐接口
     * @return 是否cookie可用
     */
    public static boolean checkCookie(Setting setting, MusicApi musicApi) {
        Map<String, Object> param = new HashMap<>();
        param.put("cookie", setting.getSeMusicCookie());
        JSONObject body = getJsonDataRestTemplate(musicApi.getApUrl(), param);
        return body.getIntValue("code") > 200;
    }

    public static  List<String> getUserPlaylistAllSongs(Setting setting, MusicApi musicApi, String playlistId) {
        Map<String, Object> param = new HashMap<>();
        List<String> re = new ArrayList<>();
        param.put("cookie", setting.getSeMusicCookie());
        String url = musicApi.getApUrl()+"?id="+playlistId;
        JSONObject body = getJsonDataRestTemplate(url, param);
        List<Object> list = body.getJSONArray("songs");
        for (Object o : list) {
            JSONObject jsonObject = (JSONObject) o;
            re.add(jsonObject.getString("id"));
        }
        return re;
    }
}
