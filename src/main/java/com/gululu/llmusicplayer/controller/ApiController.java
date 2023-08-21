package com.gululu.llmusicplayer.controller;

import com.gululu.llmusicplayer.entity.MusicApi;
import com.gululu.llmusicplayer.entity.Setting;
import com.gululu.llmusicplayer.entity.User;
import com.gululu.llmusicplayer.service.IMusicApiService;
import com.gululu.llmusicplayer.service.ISettingService;
import com.gululu.llmusicplayer.service.IUserService;
import com.gululu.llmusicplayer.util.R;
import com.gululu.llmusicplayer.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.gululu.llmusicplayer.util.FunctionInPage.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author llgululu
 * @since 2023-07-29
 */
@RestController
@CrossOrigin //允许跨域请求
@RequestMapping("/api")
public class ApiController {
    @Autowired
    private ISettingService iSettingService;
    @Autowired
    private IMusicApiService iMusicApiService;
    @Autowired
    private IUserService iUserService;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 首页初始化数据
     *
     * @return 首页初始化数据 包括每日推荐歌单、歌曲
     */
    @RequestMapping(value = "/index/getRecommendedData")
    @ResponseBody
    public ResponseEntity<Object> getRecommendedData() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<Object, Object> map;
        if (redisUtil.hasKey("llmusic_index_recommended")) {
            map = redisUtil.hmget("llmusic_index_recommended");
        } else {
            Setting setting = iSettingService.getById(1);
            MusicApi recommendedPlaylistsApi = iMusicApiService.getById(1);
            MusicApi recommendedSongsApi = iMusicApiService.getById(2);
            MusicApi musicUrlApi = iMusicApiService.getById(3);
            map = onloadInPageIndex(setting, recommendedPlaylistsApi, recommendedSongsApi, musicUrlApi);
        }
        if (map.containsKey("code")) {
            return new ResponseEntity<>(R.error("index页数据查询失败"), headers, HttpStatus.BAD_REQUEST);
        } else {
            redisUtil.hmset("llmusic_index_recommended", map, 60 * 60);
            return new ResponseEntity<>(R.ok("index页数据查询成功", map), headers, HttpStatus.OK);
        }
    }

    /**
     * 推荐页面 初始化数据
     *
     * @return 每日推荐歌曲
     */
    @RequestMapping(value = "/recommend/getRecommendedData")
    @ResponseBody
    public ResponseEntity<Object> getRecommendedDataPageRecommend() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<Object, Object> map;
        if (redisUtil.hasKey("llmusic_recommend_recommended")) {
            map = redisUtil.hmget("llmusic_recommend_recommended");
        } else {
            Setting setting = iSettingService.getById(1);
            MusicApi musicUrlApi = iMusicApiService.getById(3);
            MusicApi recommendedSongsApi = iMusicApiService.getById(4);
            map = onloadInPageRecommended(setting, recommendedSongsApi, musicUrlApi);
        }
        if (map.containsKey("code")) {
            return new ResponseEntity<>(R.error("recommend页数据查询失败"), headers, HttpStatus.BAD_REQUEST);
        } else {
            redisUtil.hmset("llmusic_recommend_recommended", map, 30 * 60);
            return new ResponseEntity<>(R.ok("recommend页数据查询成功", map), headers, HttpStatus.OK);
        }
    }

    /**
     * 歌单广场页面初始化数据
     *
     * @return 歌单的种类
     */
    @RequestMapping(value = "/playlistPiazza/getDataPagePlaylistPiazza")
    @ResponseBody
    public ResponseEntity<Object> getDataPagePlaylistPiazza() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<Object, Object> map;
        if (redisUtil.hasKey("llmusic_playlistPiazza_playlistCategory")) {
            map = redisUtil.hmget("llmusic_playlistPiazza_playlistCategory");
        } else {
            Setting setting = iSettingService.getById(1);
            MusicApi playlistCategoryApi = iMusicApiService.getById(5);
            map = onloadInPagePlaylistPiazza(setting, playlistCategoryApi);
        }
        if (map.containsKey("code")) {
            return new ResponseEntity<>(R.error("playlistPiazza页数据查询失败"), headers, HttpStatus.BAD_REQUEST);
        } else {
            redisUtil.hmset("llmusic_playlistPiazza_playlistCategory", map, 60 * 60);
            return new ResponseEntity<>(R.ok("playlistPiazza页数据查询成功", map), headers, HttpStatus.OK);
        }
    }

    /**
     * @param cat  种类
     * @param page 当前页码
     * @return 当前种类歌单列表
     */
    @RequestMapping(value = "/playlistPiazza/getPlaylistByCategory")
    @ResponseBody
    public ResponseEntity<Object> getPlaylistByCategory(@RequestParam(value = "cat", required = false) String cat, @RequestParam(value = "page") int page) {
        if (cat == null) {
            cat = "全部";
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<Object, Object> map;
        if (redisUtil.hasKey("llmusic_playlistPiazza_category_" + cat+"_page_"+page)) {
            map = redisUtil.hmget("llmusic_playlistPiazza_category_" + cat+"_page_"+page);
        } else {
            Setting setting = iSettingService.getById(1);
            MusicApi playlistByCategoryApi = iMusicApiService.getById(6);
            map = getPlaylistByCategoryPagePlaylistPiazza(setting, playlistByCategoryApi, page, cat);
        }
        if (map.containsKey("code")) {
            return new ResponseEntity<>(R.error("playlistPiazza页歌单列表数据查询失败"), headers, HttpStatus.BAD_REQUEST);
        } else {
            redisUtil.hmset("llmusic_playlistPiazza_category_" + cat+"_page_"+page, map, 30 * 60);
            return new ResponseEntity<>(R.ok("playlistPiazza页歌单列表数据查询成功", map), headers, HttpStatus.OK);
        }
    }

    /**
     * @param id 歌单id
     * @return 歌单信息 包含其所有歌曲
     */
    @RequestMapping(value = "/playlist/getPlaylistInformation")
    @ResponseBody
    public ResponseEntity<Object> getPlaylistInformation(HttpServletRequest request, @RequestParam(value = "id") String id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<Object, Object> map;
        if (redisUtil.hasKey("llmusic_playlist_playlistId_" + id)) {
            map = redisUtil.hmget("llmusic_playlist_playlistId_" + id);
        } else {
            Setting setting = iSettingService.getById(1);
            MusicApi playlistInfoApi = iMusicApiService.getById(7);
            MusicApi playlistAllSongsApi = iMusicApiService.getById(8);
            String uid = (String) request.getAttribute("uid");
            User user = iUserService.getById(uid);
            map = getPlaylistInformationPagePlaylist(setting, playlistInfoApi, playlistAllSongsApi, id, user);
        }
        if (map.containsKey("code")) {
            return new ResponseEntity<>(R.error("playlist页数据查询失败"), headers, HttpStatus.BAD_REQUEST);
        } else {
            redisUtil.hmset("llmusic_playlist_playlistId_" + id, map, 10 * 60);
            return new ResponseEntity<>(R.ok("playlist页数据查询成功", map), headers, HttpStatus.OK);
        }
    }

    /**
     * @return 榜单信息
     */
    @RequestMapping(value = "/rankingList/getRankingListInfo")
    @ResponseBody
    public ResponseEntity<Object> getRankingListInformation() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<Object, Object> map;
        if (redisUtil.hasKey("llmusic_rankingList_rankingList")) {
            map = redisUtil.hmget("llmusic_rankingList_rankingList");
        } else {
            Setting setting = iSettingService.getById(1);
            MusicApi rankingListInfoApi = iMusicApiService.getById(9);
            map = getRankingListInformationPageRankingList(setting, rankingListInfoApi);
        }
        if (map.containsKey("code")) {
          
            return new ResponseEntity<>(R.error("rankingList页数据查询失败"), headers, HttpStatus.BAD_REQUEST);
        } else {
            redisUtil.hmset("llmusic_rankingList_rankingList", map, 60 * 60);
            return new ResponseEntity<>(R.ok("rankingList页数据查询成功", map), headers, HttpStatus.OK);
        }
    }

    /**
     * @param id 歌曲id
     * @return 歌曲url
     */
    @RequestMapping(value = "/music/getMusicURLById")
    @ResponseBody
    public ResponseEntity<Object> getMusicURLById(@RequestParam(value = "id") String id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<Object, Object> map;
        if (redisUtil.hasKey("llmusic_musicUrl_id_"+id)) {
            map = redisUtil.hmget("llmusic_musicUrl_id_"+id);
        } else {
            Setting setting = iSettingService.getById(1);
            MusicApi musicUrlApi = iMusicApiService.getById(3);
            map = getMusicURLByIdFunction(setting, musicUrlApi, id);
        }
        if (map.containsKey("code")) {
            return new ResponseEntity<>(R.error("数据查询失败"), headers, HttpStatus.BAD_REQUEST);
        } else {
            redisUtil.hmset("llmusic_musicUrl_id_"+id, map, 60 * 5);
            return new ResponseEntity<>(R.ok("数据查询成功", map), headers, HttpStatus.OK);
        }
    }

    /**
     * 对歌单添加或删除歌曲
     *
     * @param musicId    歌曲id
     * @param playlistId 歌单id
     * @return 信息
     */
    @RequestMapping(value = "/music/collectOrCancelMusic")
    @ResponseBody
    public ResponseEntity<Object> collectOrCancelMusic(@RequestParam(value = "musicId") String musicId, @RequestParam(value = "playlistId") String playlistId, @RequestParam(value = "op") String op) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Setting setting = iSettingService.getById(1);
        MusicApi musicApi = iMusicApiService.getById(11);
        Map<Object, Object> map = collectOrCancelMusicFunction(setting, musicApi, musicId, playlistId, op);
        if (map.containsKey("code")) {
            return new ResponseEntity<>(R.error("数据查询失败"), headers, HttpStatus.BAD_REQUEST);
        } else {
            if (redisUtil.hasKey("llmusic_playlist_playlistId_" + playlistId)) {
                redisUtil.del("llmusic_playlist_playlistId_" + playlistId);
            }
            return new ResponseEntity<>(R.ok("数据查询成功", map), headers, HttpStatus.OK);
        }
    }

    /**
     * @param keywords 搜索关键词
     * @return 数据
     */
    @RequestMapping(value = "/search/getSearchResult")
    @ResponseBody
    public ResponseEntity<Object> getSearchResultPageSearch(@RequestParam(value = "keywords") String keywords) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<Object, Object> map;
        if (redisUtil.hasKey("llmusic_search_keywords_"+keywords)) {
            map = redisUtil.hmget("llmusic_search_keywords_"+keywords);
        } else {
            Setting setting = iSettingService.getById(1);
            MusicApi musicApi = iMusicApiService.getById(12);
            map = getSearchResultFunctionPageSearch(setting, musicApi, keywords);
        }
        if (map.containsKey("code")) {
            return new ResponseEntity<>(R.error("search数据查询失败"), headers, HttpStatus.BAD_REQUEST);
        } else {
            redisUtil.hmset("llmusic_search_keywords_"+keywords, map, 30 * 60);
            return new ResponseEntity<>(R.ok("search数据查询成功", map), headers, HttpStatus.OK);
        }
    }

    /**
     * @param keywords 搜索关键词
     * @param page     分页
     * @return 搜索单曲
     */
    @RequestMapping(value = "/searchSong/getSearchSongResult")
    @ResponseBody
    public ResponseEntity<Object> getSearchSongResultPageSearchSong(@RequestParam(value = "keywords") String keywords, @RequestParam(value = "page") int page) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<Object, Object> map;
        if (redisUtil.hasKey("llmusic_searchSong_keywords_"+keywords+"_page_"+page)) {
            map = redisUtil.hmget("llmusic_searchSong_keywords_"+keywords+"_page_"+page);
        } else {
            Setting setting = iSettingService.getById(1);
            MusicApi musicApi = iMusicApiService.getById(14);
            map = getSearchResultSongFunctionPageSearchSong(setting, musicApi, keywords, page);
        }
        if (map.containsKey("code")) {
            return new ResponseEntity<>(R.error("searchSong数据查询失败"), headers, HttpStatus.BAD_REQUEST);
        } else {
            redisUtil.hmset("llmusic_searchSong_keywords_"+keywords+"_page_"+page, map, 30 * 60);
            return new ResponseEntity<>(R.ok("searchSong数据查询成功", map), headers, HttpStatus.OK);
        }
    }

    /**
     * @param keywords 搜索关键词
     * @param page     分页
     * @return 搜索歌单
     */
    @RequestMapping(value = "/searchPlaylist/getSearchPlaylistResult")
    @ResponseBody
    public ResponseEntity<Object> getSearchPlaylistResultPageSearchPlaylist(@RequestParam(value = "keywords") String keywords, @RequestParam(value = "page") int page) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<Object, Object> map;
        if (redisUtil.hasKey("llmusic_searchPlaylist_keywords_"+keywords+"_page_"+page)) {
            map = redisUtil.hmget("llmusic_searchPlaylist_keywords_"+keywords+"_page_"+page);
        } else {
            Setting setting = iSettingService.getById(1);
            MusicApi musicApi = iMusicApiService.getById(13);
            map = getSearchResultPlaylistFunctionPageSearchPlaylist(setting, musicApi, keywords, page);
        }
        if (map.containsKey("code")) {
            return new ResponseEntity<>(R.error("searchPlaylist数据查询失败"), headers, HttpStatus.BAD_REQUEST);
        } else {
            redisUtil.hmset("llmusic_searchPlaylist_keywords_"+keywords+"_page_"+page, map, 30 * 60);
            return new ResponseEntity<>(R.ok("searchPlaylist数据查询成功", map), headers, HttpStatus.OK);
        }
    }
}

