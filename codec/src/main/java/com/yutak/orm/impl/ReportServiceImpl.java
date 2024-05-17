package com.yutak.orm.impl;

import com.yutak.orm.domain.Report;
import com.yutak.orm.mapper.ReportMapper;
import com.yutak.orm.service.ReportService;
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
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report> implements ReportService {

}
