package com.webupload.demo.webupload.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webupload.demo.webupload.dao.FileZoneRecordMapper;
import com.webupload.demo.webupload.entity.FileZoneRecord;
import com.webupload.demo.webupload.service.IFileZoneRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 文件分片记录 服务实现类
 * </p>
 *
 * @author haijun
 * @since 2020-02-14
 */
@Service
public class FileZoneRecordServiceImpl extends ServiceImpl<FileZoneRecordMapper, FileZoneRecord> implements IFileZoneRecordService {
    @Autowired
    private  FileZoneRecordMapper fileZoneRecordMapper;

    @Override
    public FileZoneRecord selByMD5AndZoneTotalMd5(String zoneMd5, String zoneTotalMd5) {
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("zone_md5",zoneMd5);
        queryWrapper.eq("zone_total_md5",zoneTotalMd5);
        List<FileZoneRecord> list = fileZoneRecordMapper.selectList(queryWrapper);
        if(list.size()>0){
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<FileZoneRecord> selByTotalMd5(String zoneTotalMd5) {
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("zone_total_md5",zoneTotalMd5);
        queryWrapper.orderByAsc("zone_now_index");
        List<FileZoneRecord> list = fileZoneRecordMapper.selectList(queryWrapper);
        return list;
    }
}
