package com.yutak.orm.impl;

import com.yutak.orm.domain.GiteeUser;
import com.yutak.orm.mapper.GiteeUserMapper;
import com.yutak.orm.service.GiteeUserService;
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
public class GiteeUserServiceImpl extends ServiceImpl<GiteeUserMapper, GiteeUser> implements GiteeUserService {

}
