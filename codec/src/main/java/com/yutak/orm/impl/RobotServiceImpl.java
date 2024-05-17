package com.yutak.orm.impl;

import com.yutak.orm.domain.Robot;
import com.yutak.orm.mapper.RobotMapper;
import com.yutak.orm.service.RobotService;
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
public class RobotServiceImpl extends ServiceImpl<RobotMapper, Robot> implements RobotService {

}
