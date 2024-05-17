package com.yutak.orm.impl;

import com.yutak.orm.domain.GithubUser;
import com.yutak.orm.mapper.GithubUserMapper;
import com.yutak.orm.service.GithubUserService;
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
public class GithubUserServiceImpl extends ServiceImpl<GithubUserMapper, GithubUser> implements GithubUserService {

}
