package com.webupload.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description TODO
 * @Author haijun
 * @Date 2019/12/30 13:57
 * @ClassName FileMvcConfig
 ***/
@Configuration
public class FileMvcConfig implements WebMvcConfigurer {

    /**
     * 网络路径配置
     */
    @Value("${fileupload.config.staticAccessPath}")
    private String staticAccessPath;

    /**
     * 服务器储存路径（带盘符，liunx不用）
     */
    @Value("${fileupload.config.uploadFolder}")
    private String uploadFolder;

    /**
     * 服务器储存子路径
     */
    @Value("${fileupload.config.localPath}")
    private String localPath;
    /**
     * 用户头像
     **/
    @Value("${fileupload.config.uploadFolder}")
    private String userHeaderPic;
    /**
     * 档案文件数据
     */
    @Value("${fileupload.config.archivesFilePath}")
    private String archivesFilePath;
//
//    @Autowired
//    private IConfigService configService;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //配置server虚拟路径，handler为前台访问的目录，locations为files相对应的本地路径
        registry.addResourceHandler(staticAccessPath)
                .addResourceLocations("file:"+getUploadFolder()+localPath);
    }

    public  String getUploadFolder(){
//        String uploadFolderdb = configService.getValueByKey("uploadFolder");
//        System.out.println("uploadFolderdb:"+uploadFolderdb);
//        if(uploadFolderdb==null||uploadFolderdb.trim().length()==0){
//            return uploadFolder;
//        }
//        return uploadFolderdb;
        return uploadFolder;
    }
}