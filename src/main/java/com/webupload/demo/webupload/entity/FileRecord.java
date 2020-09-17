package com.webupload.demo.webupload.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 文件上传记录
 * </p>
 *
 * @author haijun
 * @since 2020-02-14
 */
@Data
@TableName("sys_file_record")
public class FileRecord extends Model<FileRecord> {

    private static final long serialVersionUID = 1L;


    /**
     * 记录ID
     */
	@TableId(value="id",type=IdType.ID_WORKER_STR)
    private String id;

    /**
     * 源文件名
     */
	@TableField("org_name")
    private String orgName;

    /**
     * 服务器生成的文件名
     */
	@TableField("server_local_name")
    private String serverLocalName;

    /**
     * 服务器储存路径
     */
	@TableField("server_local_path")
    private String serverLocalPath;

    /**
     * 网络路径，生成的文件夹+系统生成文件名
     */
	@TableField("network_path")
    private String networkPath;

    /**
     * 上传类型
     */
	@TableField("upload_type")
    private Integer uploadType;

    /**
     * 文件MD5值
     */
	@TableField("md5_value")
    private String md5Value;

    /**
     * 文件大小
     */
	@TableField("file_size")
    private Long fileSize;

    /**
     * 是否分片 0 否 1是
     */
	@TableField("is_zone")
    private Integer isZone;

    /**
     * 分片总数
     */
	@TableField("zone_total")
    private Integer zoneTotal;

    /**
     * 分片时间
     */
	@TableField("zone_date")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date zoneDate;

    /**
     * 分片合并时间
     */
	@TableField("zone_merge_date")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date zoneMergeDate;

    /**
     * 文件类型
     */
	@TableField("file_type")
    private String fileType;

    /**
     * 设备信息
     */
	@TableField("upload_device")
    private String uploadDevice;

    /**
     * 上传设备IP
     */
	@TableField("upload_ip")
    private String uploadIp;

    /**
     * 储存日期
     */
	@TableField("storage_date")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date storageDate;

	/**
	 * 下载统计
	 */
	@TableField("download_count")
	private Integer downloadCount;

	/**
	 * 上传统计
	 */
	@TableField("upload_count")
	private Integer uploadCount;

	/**
	 * 是否合并
	 */
	@TableField("is_merge")
	private Integer isMerge;

    /**
     * 上传人员
     */
	@TableField("create_by")
    private String createBy;

    /**
     * 上传日期
     */
	@TableField("create_time")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 删除标记 1正常 -1删除
     */
	@TableField("del_flag")
	@TableLogic
    private Integer delFlag;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
