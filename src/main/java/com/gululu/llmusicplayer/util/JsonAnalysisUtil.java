package com.gululu.llmusicplayer.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gululu.llmusicplayer.entity.LoveMusic;
import com.gululu.llmusicplayer.entity.MusicApi;
import com.gululu.llmusicplayer.entity.User;
import com.gululu.llmusicplayer.service.ILoveMusicService;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.gululu.llmusicplayer.util.RestTemplateUtil.getJsonDataRestTemplate;

public class JsonAnalysisUtil {
    /**
     * @param timestamp 时间戳
     * @return 格式化时间 返回 某月某日
     */
    public static String getFormatDate(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日");
        return formatter.format(date);
    }

    /**
     * @param tracks 排行榜前三歌曲 列表
     * @return 返回字符串数组 格式如： 1 予你 - 队长
     */
    public static List<String> getTop3SOngInRankingList(JSONArray tracks) {
        List<String> reList = new ArrayList<>();
        for (int i = 0; i < tracks.size(); i++) {
            JSONObject jsonObject = tracks.getJSONObject(i);
            String str = (i + 1) + " " + jsonObject.getString("first") + " - " + jsonObject.getString("second");
            reList.add(str);
        }
        return reList;
    }

    /**
     * @param total 总数量
     * @param limit 分页限制
     * @param page  分页
     * @return 是否还有更多
     */
    public static boolean isHasMore(int total, int limit, int page) {
        int t = total / limit - 1;
        if (t < 0) {
            return false;
        } else return t > page;
    }

    /**
     * @param tagsList 标签列表
     * @return 标签字符串 【古典 ，下午茶 ，学习】 =>  #古典 #下午茶 #学习
     */
    public static String getFormatTagsStr(JSONArray tagsList) {
        StringBuilder reString = new StringBuilder();
        for (Object s : tagsList) {
            reString.append("#").append(s).append(" ");
        }
        return reString.toString();
    }

    /**
     * @param list 歌手列表
     * @return 歌手名字格式化，“/”隔开  如 张三/李四
     */
    public static String getMusicSingersString(List<Object> list) {
        StringBuilder str = new StringBuilder();
        if (list.size() > 1) {
            for (int i = 0; i < list.size() - 1; i++) {
                JSONObject jsonObject = (JSONObject) list.get(i);
                str.append(jsonObject.getString("name")).append("/");
            }
            JSONObject jsonObject1 = (JSONObject) list.get(list.size() - 1);
            str.append(jsonObject1.getString("name"));
        } else {
            JSONObject jsonObject2 = (JSONObject) list.get(0);
            str.append(jsonObject2.getString("name"));
        }
        return str.toString();
    }

    /**
     * @param list 歌曲列表
     * @return 返回歌曲id字符串 逗号隔开 如 123,124
     */
    public static String getIdsString(List<Map<String, Object>> list) {
        StringBuilder idsStr = new StringBuilder();
        if (list.size() > 1) {
            for (int i = 0; i < list.size() - 1; i++) {
                Map<String, Object> map = list.get(i);
                idsStr.append(map.get("music_id")).append(",");
            }
            Map<String, Object> map1 = list.get(list.size() - 1);
            idsStr.append(map1.get("music_id"));
        } else {
            Map<String, Object> map2 = list.get(0);
            idsStr.append(map2.get("music_id"));
        }
        return idsStr.toString();
    }

    /**
     * @param timestamp 时间毫秒级
     * @return 格式化时间如 242000 => 04:02
     */
    public static String getMusicTimeString(int timestamp) {
        int length = timestamp / 1000;
        int second = length % 60;
        int minute = (length - second) / 60;
        String timeStr = "";
        if (minute < 10) {
            timeStr = timeStr + "0" + minute + ":";
        } else {
            timeStr = timeStr + minute + ":";
        }
        if (second < 10) {
            timeStr = timeStr + "0" + second;
        } else {
            timeStr = timeStr + second;
        }
        return timeStr;
    }

    /**
     * @param number 播放次数
     * @return 格式化播放次数 如 10000 => 1万
     */
    public static String getNumberFormat(int number) {
        if (number < 1000) {
            return String.valueOf(number);
        } else if (number < 100000000) {
            return number / 10000 + "万";
        } else {
            return number / 100000000 + "亿";
        }
    }

    /**
     * 与数据库比对，判断是否为收藏歌曲
     *
     * @param id  歌曲id
     * @param map id对应歌曲map储存
     */

    public static void checkMusicIsLiked(String id, Map<String, Object> map) {
        QueryWrapper<LoveMusic> wrapper = new QueryWrapper<>();
        wrapper.eq("lo_music_id", id);
        ILoveMusicService iLoveMusicService = BeanUtil.getBean(ILoveMusicService.class);
        LoveMusic loveMusic = iLoveMusicService.getOne(wrapper);
        if (loveMusic == null) {
            map.put("is_like", false);
        } else {
            map.put("is_like", true);
        }
    }

    /**
     * @param list        歌曲列表 （未获得url）
     * @param musicUrlApi 音乐url获取接口
     * @param param       网络请求header参数
     */
    public static void getMusicUrl(List<Map<String, Object>> list, MusicApi musicUrlApi, Map<String, Object> param) {
        param.put("id", getIdsString(list));
        JSONObject musicUrlJsonInfo = getJsonDataRestTemplate(musicUrlApi.getApUrl(), param);
        List<Object> urlList = musicUrlJsonInfo.getJSONArray("data");
        for (Object o : urlList) {
            JSONObject jsonObject = (JSONObject) o;
            for (Map<String, Object> map : list) {
                if ((String.valueOf(map.get("music_id"))).equals(jsonObject.getString("id"))) {
                    map.put("music_src", jsonObject.getString("url"));
                    break;
                }
            }
        }
    }

    /**
     * @param body   数据接口请求结果
     * @param number 接口规定数据数量
     * @return index每日推荐歌单数据
     */
    public static List<Map<String, Object>> getRecommendedPlaylistsPageIndex(JSONObject body, int number) {
        List<Object> list = body.getJSONArray("recommend");
        List<Map<String, Object>> dataList = new ArrayList<>();
        if (list.size() < number) {
            number = list.size();
        }
        for (int i = 1; i < number; i++) {
            JSONObject json = (JSONObject) list.get(i);
            Map<String, Object> map = new HashMap<>();
            map.put("id", json.getString("id"));
            map.put("name", json.getString("name"));
            map.put("playCount", json.getString("playcount"));
            map.put("picUrl", json.getString("picUrl"));
            dataList.add(map);
        }
        return dataList;
    }

    /**
     * @param body   数据接口请求结果
     * @param number 接口规定数据数量
     * @return index页或者推荐Recommended页 每日推荐歌曲
     */
    public static List<Map<String, Object>> getRecommendedSongsPageRecommendedOrIndex(JSONObject body, int number) {
        JSONObject json = body.getJSONObject("data");
        List<Object> list = json.getJSONArray("dailySongs");
        List<Map<String, Object>> dataList = new ArrayList<>();
        if (list.size() < number || number == 0) {
            number = list.size();
        }
        for (int i = 0; i < number; i++) {
            JSONObject json1 = (JSONObject) list.get(i);
            Map<String, Object> map = new HashMap<>();
            map.put("music_id", json1.getString("id"));
            map.put("music_cover", json1.getJSONObject("al").getString("picUrl"));
            map.put("music_singer", getMusicSingersString(json1.getJSONArray("ar")) + " - " + json1.getJSONObject("al").getString("name"));
            map.put("music_name", json1.getString("name"));
            map.put("music_time", getMusicTimeString(json1.getInteger("dt")));
            checkMusicIsLiked(json1.getString("id"), map);
            dataList.add(map);
        }
        return dataList;
    }

    /**
     * @param body 数据接口请求结果
     * @return 歌单广场PlaylistPiazza页面 歌单种类列表
     */
    public static List<Map<String, Object>> getPlaylistCategoriesPagePlaylistPiazza(JSONObject body) {
        List<Map<String, Object>> returnData = new ArrayList<>();
        List<Object> subCategory = body.getJSONArray("sub");
        JSONObject categories = body.getJSONObject("categories");
        int temp = 1;
        for (Map.Entry<String, Object> entry : categories.entrySet()) {
            Map<String, Object> map = new HashMap<>();
            List<Map<String, Object>> list = new ArrayList<>();
            map.put("title", entry.getValue());
            for (Object o : subCategory) {
                JSONObject jsonObject = (JSONObject) o;
                if (Objects.equals(jsonObject.getString("category"), entry.getKey())) {
                    Map<String, Object> categoriesMap = new HashMap<>();
                    categoriesMap.put("name", jsonObject.getString("name"));
                    categoriesMap.put("hot", jsonObject.getBoolean("hot"));
                    categoriesMap.put("index", temp);
                    temp++;
                    list.add(categoriesMap);
                }
            }
            map.put("categories", list);
            returnData.add(map);
        }
        Map<String, Object> map1 = new HashMap<>();
        map1.put("title", "官方");
        List<Map<String, Object>> list1 = new ArrayList<>();
        Map<String, Object> map1_1 = new HashMap<>();
        map1_1.put("name", "全部");
        map1_1.put("hot", false);
        map1_1.put("index", 0);
        list1.add(map1_1);
        map1.put("categories", list1);
        returnData.add(0, map1);
        return returnData;
    }

    /**
     * @param body 数据接口请求结果
     * @return 歌单广场页 根据分类获得歌单列表
     */
    public static List<Map<String, Object>> getPlaylistByCategoryFromJson(JSONObject body) {
        List<Map<String, Object>> returnData = new ArrayList<>();
        List<Object> list = body.getJSONArray("playlists");
        for (Object o : list) {
            JSONObject jsonObject = (JSONObject) o;
            Map<String, Object> map = new HashMap<>();
            map.put("id", jsonObject.getString("id"));
            map.put("playCount", jsonObject.getString("playCount"));
            map.put("name", jsonObject.getString("name"));
            map.put("picUrl", jsonObject.getString("coverImgUrl"));
            returnData.add(map);
        }
        return returnData;
    }

    /**
     * @param body 数据接口请求结果
     * @return 歌单页面 歌单信息数据
     */
    public static Map<String, Object> getPlaylistInformation(JSONObject body, User user) {
        Map<String, Object> map = new HashMap<>();
        JSONObject json = body.getJSONObject("playlist");
        map.put("id", json.getString("id"));
        map.put("name", json.getString("name"));
        map.put("songsNumber", json.getIntValue("trackCount"));
        map.put("cover", json.getString("coverImgUrl"));
        if (user != null && !Objects.equals(user.getuPlaylistId(), json.getString("id"))) {
            map.put("playCount", json.getString("playCount"));
            map.put("loveCount", json.getString("subscribedCount"));
            map.put("chatCount", json.getString("commentCount"));
            map.put("introduction", json.getString("description"));
            map.put("updateTime", getFormatDate(json.getLongValue("updateTime")));
            map.put("creator", json.getJSONObject("creator").getString("nickname"));
            map.put("cat", getFormatTagsStr(json.getJSONArray("tags")));
        } else {
            map.put("playCount", "");
            map.put("loveCount", "");
            map.put("chatCount", "");
            map.put("introduction", "喜欢的歌曲都放在收藏里面吧，收藏的歌曲都会放在这里...");
            map.put("updateTime", "");
            map.put("creator", "我自己");
            map.put("cat", "#喜欢 #收藏");
        }

        return map;
    }

    /**
     * @param body 请求数据
     * @return 歌单页面 所有歌曲信息
     */
    public static List<Map<String, Object>> getPlaylistAllSongsList(JSONObject body) {
        List<Map<String, Object>> reDataList = new ArrayList<>();
        List<Object> list = body.getJSONArray("songs");
        for (Object o : list) {
            JSONObject jsonObject = (JSONObject) o;
            Map<String, Object> map = new HashMap<>();
            map.put("music_id", jsonObject.getString("id"));
            map.put("music_name", jsonObject.getString("name"));
            map.put("music_singer", getMusicSingersString(jsonObject.getJSONArray("ar")) + " - " + jsonObject.getJSONObject("al").getString("name"));
            map.put("music_cover", jsonObject.getJSONObject("al").getString("picUrl"));
            map.put("music_time", getMusicTimeString(jsonObject.getInteger("dt")));
            checkMusicIsLiked(jsonObject.getString("id"), map);
            reDataList.add(map);
        }
        return reDataList;
    }

    /**
     * @param body 接口请求返回数据
     * @return 榜单数据
     */
    public static Map<Object, Object> getRankingListInformation(JSONObject body) {
        Map<Object, Object> map = new HashMap<>();
        List<Object> list = body.getJSONArray("list");
        List<Map<String, Object>> officialLists = new ArrayList<>();
        List<Map<String, Object>> otherLists = new ArrayList<>();
        for (Object o : list) {
            JSONObject jsonObject = (JSONObject) o;
            Map<String, Object> playListMap = new HashMap<>();
            playListMap.put("id", jsonObject.getString("id"));
            playListMap.put("name", jsonObject.getString("name"));
            playListMap.put("updateTime", jsonObject.getString("updateFrequency"));
            playListMap.put("cover", jsonObject.getString("coverImgUrl"));
            if (jsonObject.containsKey("ToplistType")) {
                playListMap.put("top3Music", getTop3SOngInRankingList(jsonObject.getJSONArray("tracks")));
                officialLists.add(playListMap);
            } else {
                otherLists.add(playListMap);
            }
        }
        map.put("officialLists", officialLists);
        map.put("otherLists", otherLists);
        return map;
    }

    /**
     * @param body 接口请求数据
     * @return 单个歌曲的url
     */
    public static Map<Object, Object> getMusicURLByIdFunctionByJson(JSONObject body) {
        Map<Object, Object> map = new HashMap<>();
        List<Object> list = body.getJSONArray("data");
        JSONObject jsonObject = (JSONObject) list.get(0);
        map.put("id", jsonObject.getString("id"));
        map.put("url", jsonObject.getString("url"));
        return map;
    }

    /**
     * @param body       接口请求结果
     * @param musicId    音乐id
     * @param playlistId 歌单id
     * @param op         add/del
     * @return 是否成功
     */
    public static Map<Object, Object> collectOrCancelMusicFunctionByJson(JSONObject body, String musicId, String playlistId, String op) {
        Map<Object, Object> map = new HashMap<>();
        JSONObject jsonObject = body.getJSONObject("body");
        if (jsonObject.getInteger("code") > 200) {
            map.put("code", 404);
        } else {
            QueryWrapper<LoveMusic> wrapper = new QueryWrapper<>();
            wrapper.eq("lo_music_id", musicId).eq("lo_playlist_id", playlistId);
            ILoveMusicService iLoveMusicService = BeanUtil.getBean(ILoveMusicService.class);
            if (Objects.equals(op, "del")) {
                iLoveMusicService.remove(wrapper);
            } else if (Objects.equals(op, "add")) {
                LoveMusic loveMusic = new LoveMusic();
                loveMusic.setLoMusicId(musicId);
                loveMusic.setLoPlaylistId(playlistId);
                loveMusic.setLoTime(new Date());
                iLoveMusicService.save(loveMusic);
            }
            map.put("success", true);
        }
        return map;
    }

    /**
     * 搜索解析单曲
     *
     * @param map   存入容器
     * @param body  接口返回数据
     * @param limit 数据限制条数
     */
    public static void getSearchResultSongs(Map<Object, Object> map, JSONObject body, int limit, int page) {
        Map<String, Object> map1 = new HashMap<>();
        JSONObject jsonObject = body.getJSONObject("result");
        List<Map<String, Object>> returnList = new ArrayList<>();
        if (jsonObject.containsKey("songs") && jsonObject.getIntValue("songCount") != 0) {
            List<Object> list = jsonObject.getJSONArray("songs");
            if (list.size() < limit) {
                limit = list.size();
            }
            for (int i = 0; i < limit; i++) {
                JSONObject jsonObject1 = (JSONObject) list.get(i);
                Map<String, Object> map2 = new HashMap<>();
                map2.put("music_id", jsonObject1.getString("id"));
                map2.put("music_name", jsonObject1.getString("name"));
                map2.put("music_cover", jsonObject1.getJSONObject("al").getString("picUrl"));
                map2.put("music_singer", getMusicSingersString(jsonObject1.getJSONArray("ar")) + " - " + jsonObject1.getJSONObject("al").getString("name"));
                map2.put("music_time", getMusicTimeString(jsonObject1.getInteger("dt")));
                checkMusicIsLiked(jsonObject1.getString("id"), map2);
                returnList.add(map2);
            }
        }
        map.put("songsHasMore", isHasMore(jsonObject.getIntValue("songCount"), limit, page));
        map.put("totalSongCount", jsonObject.getIntValue("songCount"));
        map1.put("music_list", returnList);
        map1.put("id", "search " + new Date());
        map1.put("songsNumber", returnList.size());
        map.put("childSongLists", map1);

    }

    /**
     * 搜索解析歌单
     *
     * @param map   存入容器
     * @param body  接口返回数据
     * @param limit 数据限制条数
     */
    public static void getSearchResultPlaylists(Map<Object, Object> map, JSONObject body, int limit, int page) {
        Map<String, Object> map1 = new HashMap<>();
        JSONObject jsonObject = body.getJSONObject("result");
        List<Map<String, Object>> returnList = new ArrayList<>();
        if (jsonObject.containsKey("playlists")) {
            List<Object> list = jsonObject.getJSONArray("playlists");
            if (list.size() < limit) {
                limit = list.size();
            }
            for (int i = 0; i < limit; i++) {
                JSONObject jsonObject1 = (JSONObject) list.get(i);
                Map<String, Object> map2 = new HashMap<>();
                map2.put("id", jsonObject1.getString("id"));
                map2.put("cover", jsonObject1.getString("coverImgUrl"));
                map2.put("name", jsonObject1.getString("name"));
                String in = jsonObject1.getString("trackCount") + "首，by" + jsonObject1.getJSONObject("creator").getString("nickname") + "，播放" + getNumberFormat(jsonObject1.getIntValue("playCount")) + "次";
                if (jsonObject1.getString("recommendText") != null) {
                    in = in + "," + jsonObject1.getString("recommendText");
                }
                map2.put("introduction", in);
                returnList.add(map2);
            }
        }
        map.put("totalPlaylistCount", jsonObject.getIntValue("playlistCount"));
        map.put("playlistHasMore", isHasMore(jsonObject.getIntValue("playlistCount"), limit, page));
        map1.put("list", returnList);
        map1.put("playlistCount", returnList.size());
        map.put("playlistlist", map1);
    }
}
