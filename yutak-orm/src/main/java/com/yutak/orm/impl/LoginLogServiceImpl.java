package com.yutak.orm.impl;

import com.yutak.orm.domain.LoginLog;
import com.yutak.orm.mapper.LoginLogMapper;
import com.yutak.orm.service.LoginLogService;
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
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {

}
