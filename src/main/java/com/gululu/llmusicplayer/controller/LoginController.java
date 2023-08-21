package com.gululu.llmusicplayer.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gululu.llmusicplayer.entity.LoveMusic;
import com.gululu.llmusicplayer.entity.MusicApi;
import com.gululu.llmusicplayer.entity.Setting;
import com.gululu.llmusicplayer.entity.User;
import com.gululu.llmusicplayer.service.*;
import com.gululu.llmusicplayer.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.gululu.llmusicplayer.util.FunctionInPage.*;
import static com.gululu.llmusicplayer.util.JWTUtil.createJwt;
import static com.gululu.llmusicplayer.util.RestTemplateUtil.getUserOpenID;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author llgululu
 * @since 2023-07-30
 */
@RestController
@CrossOrigin //允许跨域请求
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private ISettingService iSettingService;
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IMusicApiService iMusicApiService;
    @Autowired
    private ILoveMusicService iLoveMusicService;
    @Autowired
    private MailService mailService;
    @RequestMapping(value = "/user/login")
    @ResponseBody
    public ResponseEntity<Object> userLogin(@RequestParam(value = "code") String code) {
        HttpHeaders headers = new HttpHeaders();
        String token;
        Map<Object,Object> map = new HashMap<>();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Setting setting = iSettingService.getById(1);
        MusicApi createPlaylistApi = iMusicApiService.getById(10);
        String openid = getUserOpenID(setting, code);
        if (openid==null){
            return new ResponseEntity<>(R.error("code错误"), headers, HttpStatus.BAD_REQUEST);
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("u_open_id", openid);
        User user = iUserService.getOne(wrapper);
        if (user == null) {
            User user1 = new User();
            user1.setuOpenId(openid);
            String createPlaylistId = getCreateNewPlaylistId(setting,createPlaylistApi);
            if (Objects.equals(createPlaylistId, "")){
                return new ResponseEntity<>(R.error("登录失败"), headers, HttpStatus.BAD_REQUEST);
            }
            user1.setuPlaylistId(createPlaylistId);
            user1.setuSignTime(new Date());
            user1.setuUpdateTime(new Date());
            iUserService.save(user1);
            user1 = iUserService.getOne(wrapper);
            token =createJwt(user1);
            map.put("playlistId",user1.getuPlaylistId());
            map.put("token",token);
        }else {
            user.setuUpdateTime(new Date());
            token =createJwt(user);
            map.put("playlistId",user.getuPlaylistId());
            map.put("token",token);
        }
        return new ResponseEntity<>(R.ok("登录成功", map), headers, HttpStatus.OK);
    }
    @Scheduled(cron = "0 0 11 * * *")
    public void checkCookieJob1(){
        Setting setting = iSettingService.getById(1);
        MusicApi musicApi = iMusicApiService.getById(1);
        if (checkCookie(setting,musicApi)){
            mailService.sendTextMailMessage("检测cookie是否过期","cookie已经过期，请及时更换!!!");
        }
    }
    @Scheduled(cron = "0 0 23 * * *")
    public void checkCookieJob2(){
        Setting setting = iSettingService.getById(1);
        MusicApi musicApi = iMusicApiService.getById(1);
        if (checkCookie(setting,musicApi)){
            mailService.sendTextMailMessage("检测cookie是否过期","cookie已经过期，请及时更换!!!");
        }
    }
    @Scheduled(cron = "0 10 3 * * *")
    public void dailyJob(){
        Setting setting = iSettingService.getById(1);
        MusicApi musicApi = iMusicApiService.getById(8);
        QueryWrapper<LoveMusic> wrapper = new QueryWrapper<>();
        iLoveMusicService.remove(wrapper);
        List<User> list = iUserService.list();
        for (User user : list) {
            List<String> musicIdList = getUserPlaylistAllSongs(setting, musicApi, user.getuPlaylistId());
            for (String s : musicIdList) {
                LoveMusic loveMusic = new LoveMusic();
                loveMusic.setLoPlaylistId(user.getuPlaylistId());
                loveMusic.setLoMusicId(s);
                loveMusic.setLoTime(new Date());
                iLoveMusicService.save(loveMusic);
            }
        }
    }
}
