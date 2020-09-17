package com.webupload.demo.webupload.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.webupload.demo.common.base.BaseController;
import com.webupload.demo.common.consts.QueryWrapperConst;
import com.webupload.demo.common.consts.entity.SoulTableParam;
import com.webupload.demo.common.entity.Result;
import com.webupload.demo.webupload.entity.FileRecord;
import com.webupload.demo.webupload.entity.FileZoneRecord;
import com.webupload.demo.webupload.service.IFileRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * <p>
 *  文件上传记录 前端控制器
 * </p>
 *
 * @author haijun
 * @since 2020-02-14
 */
@RestController
@RequestMapping("/upload/fileRecord")
public class FileRecordController extends BaseController {

     @Autowired
     private IFileRecordService fileRecordService;

//     @Autowired
//     private IFileFilterService fileFilterService;

    /**
     * @author haijun
     * @Description  TODO  条件分页查询
     * @Date 2020-02-14
     * @Param
     * @return
     */
    @PostMapping("/getList")
    public Result getList(FileRecord fileRecord, SoulTableParam soulTableParam){
        Page<FileRecord> objectPage = new Page<>();
        if(soulTableParam.getPage()==null){//默认分页
            soulTableParam.setPage(1L);
        }
        if(soulTableParam.getSize()==null){
            soulTableParam.setSize(10L);
        }
        if(soulTableParam.getSize()>1000){
            soulTableParam.setSize(1000L);//最大支持1000条数据
        }
        objectPage.setCurrent(soulTableParam.getPage());
        objectPage.setSize(soulTableParam.getSize());
        QueryWrapper queryWrapper=new QueryWrapper(fileRecord);
        queryWrapper= QueryWrapperConst.soulTableConst(queryWrapper,soulTableParam);
        IPage pageResult = fileRecordService.page(objectPage, queryWrapper);
        return  renderDataPageSuccess(pageResult.getTotal(),pageResult.getRecords());
    }

    /***
    * 根据ID查找
    */
    @GetMapping("/{id}")
    public Result findById(@PathVariable("id")String id){
        return renderDataSuccess(fileRecordService.getById(id));
    }

    /***
     * 保存数据
     * id存在就更新
     */
    @PostMapping("/save")
    public Result save(@RequestBody FileRecord fileRecord){
        boolean b = fileRecordService.saveOrUpdate(fileRecord);
        return b?renderSuccess():renderError();
    }

    /***
     * 根据ID删除数据
     */
    @DeleteMapping("/delById/{id}")
    public Result delById(@PathVariable("id") String id){
        boolean b = fileRecordService.removeById(id);
        return b?renderSuccess():renderError();
    }

    /***
    * 根据多个ID删除(批量删除)
    */
    @DeleteMapping("/delByIds/{ids}")
    public Result delByIds(@PathVariable("ids") String ids){
        boolean b = fileRecordService.removeByIds(new ArrayList<>(Arrays.asList(ids.split(","))));
        return b?renderSuccess():renderError();
    }

    /**************************文件上传操作*********************************/
    /***
     * 单文件上传（<5M）
     */
    @PostMapping("/upload")
    public Result upload(HttpServletRequest request, Integer uploadType, Integer storageYear){
        Result result=fileRecordService.upload(request,uploadType,storageYear);
        return result;
    }

    /***
     * 大文件分片上传
     */
    @PostMapping("/zone/upload")
    public Result zoneUpload(HttpServletRequest request,String contentType, FileZoneRecord fileZoneRecord){
       return fileRecordService.zoneUpload(request,contentType,fileZoneRecord);
    }

    /**
     * @author haijun 校验MD5，传入分片MD5和总的MD5，校验当前分片
     * @Description  //TODO
     * @Date 21:54 2019/12/31
     * @Param
     * @return
     **/
    @PostMapping("/zone/upload/md5Check")
    public Result md5Check(FileZoneRecord fileZoneRecord, Integer checkType, String contentType, HttpServletRequest request){
        return fileRecordService.md5Check(fileZoneRecord,checkType,contentType,request);
    }

    /**
     * 合并文件，前端所有分片上传完成时，发起请求，将所有的文件合并成一个完整的文件，并删除服务器分片文件
     * 前端需要传入总文件的MD5值
     */
    @PostMapping("/zone/upload/merge/{totalmd5}")
    public Result mergeZoneFile(@PathVariable("totalmd5") String totalmd5,HttpServletRequest request){
        return fileRecordService.mergeZoneFile(totalmd5,request);
    }

    /***
     * 删除文件分片
     */
    @PostMapping("/zone/upload/del/{totalmd5}")
    public Result delZoneFile(@PathVariable("totalmd5") String totalmd5){
        return fileRecordService.delZoneFile(totalmd5);
    }
    /***
     * 删除文件
     */
    @PostMapping("/upload/del/{fileId}")
    public Result delFile(@PathVariable("fileId") String fileId){
        return fileRecordService.delFile(fileId);
    }

    /***
     * @author haijun
     * @Description  //TODO 文件下载
     * @Date 12:25 2020/1/7
     * @Param
     * @return
     **/
    @GetMapping("/download/{fileId}")
    public Result downloadFile(HttpServletRequest request, HttpServletResponse response, @PathVariable("fileId") String fileId) throws UnsupportedEncodingException {
        FileRecord fileRecorddb = fileRecordService.getById(fileId);
        String filePath = fileRecorddb.getServerLocalPath();// 设置文件名，根据业务需要替换成要下载的文件名
        String fileName = fileRecorddb.getOrgName();
        if (filePath != null) {
            //设置文件路径
            System.out.println("filePath:"+filePath);
            File file = new File(filePath);
            if (file.exists()) {
                response.setContentType("application/force-download");// 设置强制下载不打开
                response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名
                response.setContentType("multipart/form-data;charset=UTF-8");//也可以明确的设置一下UTF-8，测试中不设置也可以。
                response.setHeader("Content-Disposition", "attachment;fileName="+ new String(fileName.getBytes("GB2312"),"ISO-8859-1"));
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        os.flush();
                        i = bis.read(buffer);
                    }
//                    System.out.println("下载成功");
//                    fileRecordService.recordDownloadLog(fileId,fileRecorddb);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return renderSuccess();
            }
        }
        return renderError("下载错误");
    }

}
