package com.yutak.orm.impl;

import com.yutak.orm.domain.AppVersion;
import com.yutak.orm.mapper.AppVersionMapper;
import com.yutak.orm.service.AppVersionService;
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
public class AppVersionServiceImpl extends ServiceImpl<AppVersionMapper, AppVersion> implements AppVersionService {

}
