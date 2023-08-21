package com.gululu.llmusicplayer.service.impl;

import com.gululu.llmusicplayer.entity.User;
import com.gululu.llmusicplayer.mapper.UserMapper;
import com.gululu.llmusicplayer.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author llgululu
 * @since 2023-08-12
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
