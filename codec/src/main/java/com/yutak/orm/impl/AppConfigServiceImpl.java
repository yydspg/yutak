package com.yutak.orm.impl;

import com.yutak.orm.domain.AppConfig;
import com.yutak.orm.mapper.AppConfigMapper;
import com.yutak.orm.service.AppConfigService;
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
public class AppConfigServiceImpl extends ServiceImpl<AppConfigMapper, AppConfig> implements AppConfigService {

}
