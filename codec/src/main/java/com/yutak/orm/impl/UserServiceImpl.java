package com.yutak.orm.impl;

import com.yutak.orm.domain.User;
import com.yutak.orm.mapper.UserMapper;
import com.yutak.orm.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/*
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-05-17
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
