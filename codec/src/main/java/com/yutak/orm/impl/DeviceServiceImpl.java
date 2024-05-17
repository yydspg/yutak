package com.yutak.orm.impl;

import com.yutak.orm.domain.Device;
import com.yutak.orm.mapper.DeviceMapper;
import com.yutak.orm.service.DeviceService;
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
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService {

}
