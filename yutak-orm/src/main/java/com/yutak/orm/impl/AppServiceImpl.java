package com.yutak.orm.impl;

import com.yutak.orm.domain.App;
import com.yutak.orm.mapper.AppMapper;
import com.yutak.orm.service.AppService;
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
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {

}
