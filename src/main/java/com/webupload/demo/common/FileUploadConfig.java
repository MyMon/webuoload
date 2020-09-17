package com.webupload.demo.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description TODO 文件上传配置
 * @Author haijun
 * @Date 2019/12/28 20:00
 * @ClassName FileUploadConfig
 ***/
@Getter
@Setter
@ConfigurationProperties("fileupload.config")
public class FileUploadConfig {
    private String uploadFolder;
    private String staticAccessPath;
    private String localPath;
    private String userHeaderPicPath;
    private String archivesFilePath;
    private String domain;


}
