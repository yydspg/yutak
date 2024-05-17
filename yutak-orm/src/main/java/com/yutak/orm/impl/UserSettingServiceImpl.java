package com.yutak.orm.impl;

import com.yutak.orm.domain.UserSetting;
import com.yutak.orm.mapper.UserSettingMapper;
import com.yutak.orm.service.UserSettingService;
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
public class UserSettingServiceImpl extends ServiceImpl<UserSettingMapper, UserSetting> implements UserSettingService {

}
