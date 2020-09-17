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
 * 文件分片记录
 * </p>
 *
 * @author haijun
 * @since 2020-02-14
 */
@Data
@TableName("sys_file_zone_record")
//@ApiModel(value = "文件分片记录，FileZoneRecord")
public class FileZoneRecord extends Model<FileZoneRecord> {

    private static final long serialVersionUID = 1L;


    /**
     * 分片ID
     */
	@TableId(value="id",type=IdType.ID_WORKER_STR)
	//@ApiModelProperty(value = "分片ID")
    private String id;

    /**
     * 分片名称
     */
	@TableField("zone_name")
	//@ApiModelProperty(value = "分片名称")
    private String zoneName;

    /**
     * 分片的文件路径
     */
	@TableField("zone_path")
	//@ApiModelProperty(value = "分片的文件路径")
    private String zonePath;

    /**
     * 分片MD5
     */
	@TableField("zone_md5")
	//@ApiModelProperty(value = "分片MD5")
    private String zoneMd5;

    /**
     * 分片记录MD5值
     */
	@TableField("zone_record_date")
	//@ApiModelProperty(value = "分片记录MD5值")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date zoneRecordDate;

    /**
     * 上传完成校验日期
     */
	@TableField("zone_check_date")
	//@ApiModelProperty(value = "上传完成校验日期")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date zoneCheckDate;

    /**
     * 总的分片数
     */
	@TableField("zone_total_count")
	//@ApiModelProperty(value = "总的分片数")
    private Integer zoneTotalCount;

    /**
     * 总文件的MD5值
     */
	@TableField("zone_total_md5")
	//@ApiModelProperty(value = "总文件的MD5值")
    private String zoneTotalMd5;

    /**
     * 当前分片索引
     */
	@TableField("zone_now_index")
	//@ApiModelProperty(value = "当前分片索引")
    private Integer zoneNowIndex;

	/**
	 * 文件开始位置
	 */
	@TableField("zone_start_size")
	//@ApiModelProperty(value = "文件开始位置")
	private Long zoneStartSize;

	/**
	 * 文件结束位置
	 */
	@TableField("zone_end_size")
	//@ApiModelProperty(value = "文件结束位置")
	private Long zoneEndSize;

	/**
	 * 文件总大小
	 */
	@TableField("zone_total_size")
	//@ApiModelProperty(value = "文件总大小")
	private Long zoneTotalSize;
    /**
     * 分片文件后缀
     */
	@TableField("zone_suffix")
	//@ApiModelProperty(value = "分片文件后缀")
    private String zoneSuffix;

    /**
     * 文件记录ID
     */
	@TableField("file_record_id")
	//@ApiModelProperty(value = "文件记录ID")
    private String fileRecordId;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
