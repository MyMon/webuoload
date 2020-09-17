package com.webupload.demo.webupload.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.webupload.demo.common.entity.Result;
import com.webupload.demo.webupload.entity.FileRecord;
import com.webupload.demo.webupload.entity.FileZoneRecord;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 文件上传记录 服务类
 * </p>
 *
 * @author haijun
 * @since 2020-02-14
 */
public interface IFileRecordService extends IService<FileRecord> {

    Result upload(HttpServletRequest request, Integer uploadType, Integer storageYear);

    Result zoneUpload(HttpServletRequest request, String contentType, FileZoneRecord fileZoneRecord);

    Result md5Check(FileZoneRecord fileZoneRecord, Integer checkType, String contentType, HttpServletRequest request);

    Result mergeZoneFile(String totalmd5, HttpServletRequest request);

    Result delZoneFile(String totalmd5);

    Result delFile(String fileId);

//    void recordDownloadLog(String fileId, FileRecord fileRecord);
}
