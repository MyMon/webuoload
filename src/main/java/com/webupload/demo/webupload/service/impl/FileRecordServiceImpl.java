package com.webupload.demo.webupload.service.impl;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webupload.demo.common.FileHandleUtil;
import com.webupload.demo.common.FileUploadConfig;
import com.webupload.demo.common.entity.Result;
import com.webupload.demo.common.entity.ResultCode;
import com.webupload.demo.common.util.IPUtils;
import com.webupload.demo.common.util.IdWorker;
import com.webupload.demo.webupload.dao.FileRecordMapper;
import com.webupload.demo.webupload.entity.FileRecord;
import com.webupload.demo.webupload.entity.FileZoneRecord;
import com.webupload.demo.webupload.service.IFileRecordService;
import com.webupload.demo.webupload.service.IFileZoneRecordService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * <p>
 * 文件上传记录 服务实现类
 * </p>
 *
 * @author haijun
 * @since 2020-02-14
 */
@Service
public class FileRecordServiceImpl extends ServiceImpl<FileRecordMapper, FileRecord> implements IFileRecordService {
    @Autowired
    private FileRecordMapper fileRecordMapper;

    @Autowired
    private FileUploadConfig fileUploadConfig;

//    @Autowired
//    private IFileUploadLogService fileUploadLogService;

//    @Autowired
//    private IFileFilterService fileFilterService;

    @Autowired
    private IFileZoneRecordService fileZoneRecordService;
//
//    @Autowired
//    private IFileDownloadLogService fileDownloadLogService;

    @Autowired
    private IdWorker idWorker;

    @Override
    public Result upload(HttpServletRequest request, Integer uploadType, Integer storageYear) {
        Long nowtime=System.currentTimeMillis();
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> files = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entry : files.entrySet()) {
            MultipartFile multipartFile = entry.getValue();
            if (multipartFile.isEmpty()) {
                return renderError("文件不能为空");
            }
            String contentType = multipartFile.getContentType();
            System.out.println("contentType:"+contentType);
//            if(contentType==null||!isFileter(contentType)){//拦截，是否在上传名单
//                return new Result(ResultCode.FILENOTSUPPORT);
//            }

            String fileName = multipartFile.getOriginalFilename();
            Long size = multipartFile.getSize();
            System.out.println(fileName + "-->" + size);
            String saticAccess = fileUploadConfig.getStaticAccessPath().replace("*", "");
            try {
                //本地路径  1 用户头像  2 商城图片
                uploadType = uploadType == 1 ? uploadType : 2;
                //计算MD5值
                String filemd5 = DigestUtils.md5DigestAsHex(multipartFile.getInputStream());
                //查询数据库是否已经有了，有直接写入，没有，写入磁盘
                FileRecord fileRecorddb = selByMD5AndUpType(filemd5, uploadType);
                Map<String, String> map = new HashMap<>();
                String fileType = contentType.split("/")[0];

                if (fileRecorddb == null) {
                    String pathTypeDir = (uploadType == 1 ? fileUploadConfig.getUserHeaderPicPath() : fileUploadConfig.getArchivesFilePath()) + fileType + "/";
                    // 年月日/时分  如果短时间内，上传并发量大，还可分得更细 秒 毫秒 等等
                    String path_date = DateUtil.format(new Date(), "yyyy") + "/" + DateUtil.format(new Date(), "MMdd") + "/" + DateUtil.format(new Date(), "HH");
                    String localPath = getUploadFolder() + fileUploadConfig.getLocalPath() + pathTypeDir + path_date;
                    //随机生成服务器本地路径
                    String fileSuffix = getFileSuffix(fileName);
                    String serverFileName = idWorker.nextId() + fileSuffix;
                    FileHandleUtil.upload(multipartFile.getInputStream(), localPath, serverFileName);
                    String netWorkPath = "/" + saticAccess + pathTypeDir + path_date + "/" + serverFileName;
                    map.put("network", netWorkPath);

                    FileRecord fileRecord = new FileRecord();
                    fileRecord.setDownloadCount(0);
                    fileRecord.setUploadCount(1);
                    fileRecord.setIsMerge(1);//单文件，完整文件
                    fileRecord.setIsZone(0);
                    fileRecord.setFileSize(size);
                    fileRecord.setFileType(fileType);
                    fileRecord.setMd5Value(filemd5);
                    fileRecord.setOrgName(fileName);
                    fileRecord.setUploadType(uploadType);
                    fileRecord.setServerLocalName(serverFileName);
                    fileRecord.setServerLocalPath(localPath + serverFileName);
                    fileRecord.setNetworkPath(netWorkPath);
                    fileRecord.setStorageDate(getDateToYear(storageYear));//默认一百年
                    String fileId=saveFileRecord(request, fileRecord);
                    map.put("fileId", fileId);
                    map.put("network", fileRecord.getNetworkPath());
                } else {
//                    fileRecorddb.setUploadCount(fileRecorddb.getUploadCount()+1);
                    String fileId=saveFileRecord(request, fileRecorddb);
                    map.put("fileId", fileId);
                    map.put("network", fileRecorddb.getNetworkPath());
                }
                System.out.println("耗时： "+(System.currentTimeMillis()-nowtime)+" ms");
                return renderDataSuccess(map);
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return renderError("文件上传错误，错误消息：" + e.getMessage());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return renderError("文件上传错误，错误消息：" + e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                return renderError("文件上传错误，错误消息：" + e.getMessage());
            }
        }
        return renderError("文件上传错误" );
    }

    @Override
    public Result zoneUpload(HttpServletRequest request,String contentType, FileZoneRecord fileZoneRecord) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> files = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entry : files.entrySet()) {
            MultipartFile multipartFile = entry.getValue();
            if(multipartFile.isEmpty()){
                return renderError("请选择文件");
            }
            String fileName = multipartFile.getOriginalFilename();
            if(fileName.equals("blob")){
                fileName=fileZoneRecord.getZoneName();
            }
//            String contentType = multipartFile.getContentType();
            System.out.println("contentType");
//            if(contentType==null||!isFileter(contentType)){
//                return new Result(ResultCode.FILENOTSUPPORT);
//            }
            String fileType=contentType.split("/")[0];
            Long size = multipartFile.getSize();
            System.out.println(fileName + "-->" + size);
            try {

                Map<String,Object> map=new HashMap<>();
                synchronized (UUID.randomUUID()){
                    //查询数据库是否已经有了，有直接写入，没有，写入磁盘
                    FileZoneRecord fileZoneRecorddb = fileZoneRecordService.selByMD5AndZoneTotalMd5(fileZoneRecord.getZoneMd5(), fileZoneRecord.getZoneTotalMd5());
                    if(fileZoneRecorddb==null){
                        String pathTypeDir=fileUploadConfig.getArchivesFilePath();
                        // 年月日/时分  如果短时间内，上传并发量大，还可分得更细 秒 毫秒 等等
                        //写入临时目录，用总文件MD5值做文件夹
                        String localPath="";
                        //随机生成服务器本地路径
                        String fileSuffix = getFileSuffix(fileName);
                        //分片文件MD5，如果前端没有计，算一下
                        String filemd5= "";
                        if(fileZoneRecord.getZoneMd5()==null||fileZoneRecord.getZoneMd5().trim().length()==0){
                            filemd5=DigestUtils.md5DigestAsHex(multipartFile.getInputStream());
                            fileZoneRecord.setZoneMd5(filemd5);
                        }else{
                            filemd5=fileZoneRecord.getZoneMd5();
                        }

                        String serverFileName=filemd5+fileSuffix+".temp";
                        String fileRecordId = "";
                        FileRecord fileRecorddb =null;
                        synchronized  (UUID.randomUUID().toString()) {
                            //记录 已经文件存在，就不更新了，否则新增一条记录,合并时用
                            fileRecorddb = this.selByMD5AndUpType(fileZoneRecord.getZoneTotalMd5(), 2);
                        }
                        if (fileRecorddb == null) {
                            localPath = getUploadFolder() + fileUploadConfig.getLocalPath() + pathTypeDir + "temp/" + fileZoneRecord.getZoneTotalMd5();

                            FileRecord fileRecord = new FileRecord();
                            fileRecord.setFileSize(fileZoneRecord.getZoneTotalSize());
                            fileRecord.setFileType(fileType);
                            fileRecord.setMd5Value(fileZoneRecord.getZoneTotalMd5());
                            fileRecord.setOrgName(fileName);
                            fileRecord.setUploadType(2);
                            fileRecord.setServerLocalPath(localPath);
                            fileRecord.setStorageDate(getDateToYear(100));//默认一百年
                            fileRecord.setIsZone(1);
                            fileRecord.setIsMerge(0);//没有合并
                            fileRecord.setDownloadCount(0);
                            fileRecord.setUploadCount(1);
                            System.out.println("fileRecord:"+fileRecord);
                            fileRecord.setZoneTotal(fileZoneRecord.getZoneTotalCount());
                            fileRecord.setZoneDate(new Date());
                            fileRecordId = saveFileRecord(request, fileRecord);
                            saveFileRecord(request,fileRecord);
                        } else {
                            //分片且已经合并过了，就不再往下执行，否则继续
                            if (fileRecorddb.getIsZone() == 1 && fileRecorddb.getIsMerge() == 1) {//如果文件已经合并过了，直接返回
                                return renderError("文件已经上传");
                            }
                            fileRecordId = fileRecorddb.getId();
                            localPath = fileRecorddb.getServerLocalPath();
                        }
//                        }
                        //将文件写入目录
                        FileHandleUtil.upload(multipartFile.getInputStream(), localPath, serverFileName);

                        //记录分片文件
                        fileZoneRecord.setId(idWorker.nextId() + "");
                        fileZoneRecord.setZoneMd5(filemd5);//计算当前分片MD5
                        fileZoneRecord.setFileRecordId(fileRecordId);
                        fileZoneRecord.setZoneName(serverFileName);
                        fileZoneRecord.setZonePath(localPath);//只存文件夹，合并时，直接读取这个文件所有文件
                        fileZoneRecord.setZoneRecordDate(new Date());
                        fileZoneRecord.setZoneSuffix(fileSuffix);
                        fileZoneRecordService.save(fileZoneRecord);
                        map.put("fileZone", fileZoneRecord);
                        map.put("isExist", false);//不存在
                        map.put("zoneNowIndex", fileZoneRecord.getZoneNowIndex());

                    }else{
                        map.put("fileZone",fileZoneRecorddb);
                        map.put("isExist", true);//存在
                        map.put("zoneNowIndex", fileZoneRecorddb.getZoneNowIndex());
                    }
                }
                return renderDataSuccess(map);
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return renderError("文件上传错误，错误消息："+e.getMessage());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return renderError("文件上传错误，错误消息："+e.getMessage());
            }
        }
        return renderError("文件上传失败");
    }

    @Override
    public Result md5Check(FileZoneRecord fileZoneRecord, Integer checkType,String contentType,HttpServletRequest request) {
        if(checkType==1){//校验文件
//            if(contentType==null||!isFileter(contentType)){
//                return new Result(ResultCode.FILENOTSUPPORT);
//            }
            FileRecord fileRecordb = this.selByMD5AndUpType(fileZoneRecord.getZoneTotalMd5(), 2);
            if(fileRecordb!=null){
                saveFileRecord(request,fileRecordb);
            }
            return fileRecordb!=null&&fileRecordb.getIsMerge()==1?new Result(ResultCode.FILEUPLOADED,fileRecordb):renderError("请选择文件上传");
        }else{
            FileZoneRecord fileZoneRecordb = fileZoneRecordService.selByMD5AndZoneTotalMd5(fileZoneRecord.getZoneMd5(), fileZoneRecord.getZoneTotalMd5());
            return fileZoneRecordb!=null?renderDataSuccess(fileZoneRecordb):renderError("分片文件不存在，继续上传");
        }
    }

    @Override
    public Result mergeZoneFile(String totalmd5,HttpServletRequest request) {
        //查询所有的分片文件
        if(totalmd5!=null&&totalmd5.trim().length()>0){
            FileRecord fileRecordb = this.selByMD5AndUpType(totalmd5, 2);
            Map<String,Object> map=new HashMap<>();
            if(fileRecordb.getIsZone()==1&&fileRecordb.getIsMerge()==1){
                map.put("netWorkPath",fileRecordb.getNetworkPath());
                map.put("fileId",fileRecordb.getId());
                map.put("fileInfo",fileRecordb);
                map.put("message","文件已经上传成功了，文件路径："+fileRecordb.getNetworkPath());
                return renderDataSuccess(map);
            }
            String fileType=fileRecordb.getFileType();
            List<FileZoneRecord> fileZoneRecords = fileZoneRecordService.selByTotalMd5(totalmd5);
            if(fileZoneRecords.size()>0){
                // 年月日/时分  如果短时间内，上传并发量大，还可分得更细 秒 毫秒 等等
                String path_date= DateUtil.format(new Date(),"yyyy")+"/"+DateUtil.format(new Date(),"MMdd")+"/"+DateUtil.format(new Date(),"HH")+"/";
                String pathTypeDir=fileUploadConfig.getArchivesFilePath()+fileType+"/";
                String localPath=getUploadFolder() + fileUploadConfig.getLocalPath()+pathTypeDir+path_date;
                //随机生成服务器本地路径
                String fileSuffix = getFileSuffix(fileRecordb.getOrgName());
                String serverFileName=idWorker.nextId()+fileSuffix;
                String saticAccess = fileUploadConfig.getStaticAccessPath().replace("*", "");
                String netWorkPath="/"+saticAccess+pathTypeDir+path_date+"/"+ serverFileName;
                fileRecordb.setServerLocalName(serverFileName);
                fileRecordb.setServerLocalPath(localPath+serverFileName);
                fileRecordb.setNetworkPath(netWorkPath);
                FileOutputStream destTempfos =null;
                try {
                    String zonePath = fileZoneRecords.get(0).getZonePath();
                    File parentFileDir = new File(zonePath);//得到上级文件夹
                    if(parentFileDir.isDirectory()){
                        FileHandleUtil.createDirIfNotExists(localPath);
                        File destTempFile = new File(localPath, serverFileName);
                        if(!destTempFile.exists()){
                            //先得到文件的上级目录，并创建上级目录，在创建文件,
                            destTempFile.getParentFile().mkdir();
                            try {
                                //创建文件
                                destTempFile.createNewFile(); //上级目录没有创建，这里会报错
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        System.out.println(parentFileDir.listFiles().length);
                        List<String> ids=new ArrayList<>();
                        for(FileZoneRecord fileZoneR:fileZoneRecords){
                            File partFile = new File(parentFileDir, fileZoneR.getZoneName());
                            destTempfos = new FileOutputStream(destTempFile, true);
                            //遍历"所有分片文件"到"最终文件"中
                            FileUtils.copyFile(partFile, destTempfos);
                            destTempfos.close();
                            ids.add(fileZoneR.getId());
                        }

                        // 删除临时目录中的分片文件
                        String tempDir=getUploadFolder() + fileUploadConfig.getLocalPath()+fileUploadConfig.getArchivesFilePath()+"/temp/"+fileRecordb.getMd5Value();
                        FileHandleUtil.deleteFolder(tempDir);
                        fileZoneRecordService.removeByIds(ids);//删除分片信息
                        fileRecordb.setZoneMergeDate(new Date());
                        fileRecordb.setIsMerge(1);//更新已经合并
                        this.updateById(fileRecordb);//更新到文件记录
                        String fileId = saveFileRecord(request, fileRecordb);
                        map.put("netWorkPath",netWorkPath);
                        map.put("fileInfo",fileRecordb);
                        map.put("fileId",fileId);
                        return renderDataSuccess(map);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return renderError("操作失败，原因："+e.getMessage());
                }finally {
                    try {
                        if(destTempfos!=null){
                            destTempfos.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return renderError("合并错误");
    }

    @Override
    public Result delZoneFile(String totalmd5) {
        return null;
    }

    @Override
    public Result delFile(String fileId) {
        return null;
    }

//    @Override
//    public void recordDownloadLog(String fileId,FileRecord fileRecord) {
//        FileDownloadLog fileDownloadLog=new FileDownloadLog();
//        fileDownloadLog.setSysFileRecordId(fileId);
//        fileDownloadLog.setOrgFileName(fileRecord.getOrgName());
//        fileDownloadLog.setFileSize(fileRecord.getFileSize());
//        fileDownloadLog.setServerLocalName(fileRecord.getServerLocalName());
//        fileDownloadLog.setServerLocalPath(fileRecord.getServerLocalPath());
//        fileDownloadLogService.save(fileDownloadLog);
//        //更新下载量
//        fileRecord.setDownloadCount(fileRecord.getDownloadCount()+1);
//        this.updateById(fileRecord);
//    }

    public Result renderError(String msg){
        return new Result(ResultCode.SERVER_ERROR,msg );
    }
    public Result renderDataSuccess(Object obj){
        return new Result(ResultCode.SUCCESS,obj);
    }
    //
    public FileRecord selByMD5AndUpType(String md5,Integer uploadType){
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("md5_value",md5);
        queryWrapper.eq("upload_type",uploadType);
        List<FileRecord> list = fileRecordMapper.selectList(queryWrapper);
        if(list.size()>0){
            return list.get(0);
        }
        return null;
    }
    /***
     * 获取文件后缀
     * @param fileName
     * @return
     */
    private String getFileSuffix(String fileName){
        if(fileName==null||fileName.length()==0){
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }


    /**
     * 将当前时间往后推一年
     */
    public Date getDateToYear(Integer year){
        if(year==null){//默认一百年
            year=100;
        }
        //获取时间加一年
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);//设置起时间
        cal.add(Calendar.YEAR, year);//增加year年
        return cal.getTime();
    }

    public  String getUploadFolder(){
//        String uploadFolderdb = configService.getValueByKey("uploadFolder");
//        System.out.println("uploadFolderdb:"+uploadFolderdb);
//        if(uploadFolderdb==null||uploadFolderdb.trim().length()==0){
//            return fileUploadConfig.getUploadFolder();
//        }
//        return uploadFolderdb;
        return fileUploadConfig.getUploadFolder();
    }

    /**
     * 文件保存记录
     */
    public String saveFileRecord(HttpServletRequest request, FileRecord fileRecord){
        fileRecord.setDelFlag(1);
        String device = request.getHeader("User-Agent");
        fileRecord.setUploadDevice(device);
        String ipAddr = IPUtils.getIpAddr(request);
        if(fileRecord.getUploadCount()==1&&fileRecord.getId()==null){
            fileRecord.setUploadIp(ipAddr);
            this.save(fileRecord);
        }
        //记录上传日志
//        FileUploadLog fileUploadLogdb = selFileUploadLog(fileRecord.getId(), fileRecord.getCreateBy(), ipAddr);
//        if(fileUploadLogdb==null){//如果ID
//            fileUploadLogdb=new FileUploadLog();
//            fileUploadLogdb.setCreateBy(fileRecord.getCreateBy());
//            fileUploadLogdb.setCreateTime(new Date());
//            fileUploadLogdb.setDelFlag(1);
//            fileUploadLogdb.setOrgFileName(fileRecord.getOrgName());
//            fileUploadLogdb.setSysFileRecordId(fileRecord.getId());
//            fileUploadLogdb.setUploadIp(IPUtils.getIpAddr(request));
//            fileUploadLogdb.setUploadDevice(device);
//            fileUploadLogService.save(fileUploadLogdb);
//        }
//        fileRecord.setUploadCount(selFileUploadLog(fileRecord.getId()));
        this.updateById(fileRecord);
        return fileRecord.getId();
    }

//    /**
//     * 查询上传记录
//     */
//    public FileUploadLog selFileUploadLog(String fileId,String createBy,String uploadIp){
//        QueryWrapper queryWrapper=new QueryWrapper();
//        queryWrapper.eq("sys_file_record_id",fileId);
//        queryWrapper.eq("upload_ip",uploadIp);
//        queryWrapper.eq("create_by",createBy);
//        FileUploadLog one = fileUploadLogService.getOne(queryWrapper);
//        return one;
//    }

//    /**
//     * 查询上传记录
//     */
//    public Integer selFileUploadLog(String fileId){
//        QueryWrapper queryWrapper=new QueryWrapper();
//        queryWrapper.eq("sys_file_record_id",fileId);
//        return fileUploadLogService.count(queryWrapper);
//    }

    //是否拦截此文件
//    private boolean isFileter(String contentType){
//        QueryWrapper queryWrapper=new QueryWrapper();
//        queryWrapper.eq("content_type",contentType);
//        queryWrapper.eq("is_filter",1);
//        int count = fileFilterService.count(queryWrapper);
//        return count>0;
//    }
}
