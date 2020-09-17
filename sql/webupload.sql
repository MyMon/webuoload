CREATE TABLE `sys_file_zone_record` (
  `id` varchar(40) NOT NULL COMMENT '分片ID',
  `zone_name` varchar(100) DEFAULT NULL COMMENT '分片名称',
  `zone_path` varchar(1000) DEFAULT NULL COMMENT '分片的文件路径',
  `zone_md5` varchar(100) DEFAULT NULL COMMENT '分片MD5',
  `zone_record_date` datetime DEFAULT NULL COMMENT '分片记录MD5值',
  `zone_check_date` datetime DEFAULT NULL COMMENT '上传完成校验日期',
  `zone_total_count` int(11) DEFAULT NULL COMMENT '总的分片数',
  `zone_total_size` bigint(11) DEFAULT NULL COMMENT '总的文件大小',
  `zone_start_size` bigint(11) DEFAULT NULL COMMENT '分片起始位置',
  `zone_end_size` bigint(11) DEFAULT NULL COMMENT '分片结束位置',
  `zone_total_md5` varchar(100) DEFAULT NULL COMMENT '总文件的MD5值',
  `zone_now_index` int(11) DEFAULT NULL COMMENT '当前分片索引',
  `zone_suffix` varchar(100) DEFAULT NULL COMMENT '分片文件后缀',
  `file_record_id` varchar(40) DEFAULT NULL COMMENT '文件记录ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='文件分片记录';


CREATE TABLE `sys_file_record` (
  `id` varchar(40) NOT NULL COMMENT '记录ID',
  `org_name` varchar(1000) DEFAULT NULL COMMENT '源文件名',
  `server_local_name` varchar(100) DEFAULT NULL COMMENT '服务器生成的文件名',
  `server_local_path` varchar(1000) DEFAULT NULL COMMENT '服务器储存路径',
  `network_path` varchar(1000) DEFAULT NULL COMMENT '网络路径，生成的文件夹+系统生成文件名',
  `upload_type` int(5) DEFAULT '1' COMMENT '上传类型',
  `md5_value` varchar(120) DEFAULT NULL COMMENT '文件MD5值',
  `file_size` bigint(20) DEFAULT NULL COMMENT '文件大小',
  `is_merge` int(5) DEFAULT NULL COMMENT '是否合并',
  `is_zone` int(11) DEFAULT NULL COMMENT '是否分片 0 否 1是',
  `zone_total` int(11) DEFAULT NULL COMMENT '分片总数',
  `zone_date` datetime DEFAULT NULL COMMENT '分片时间',
  `zone_merge_date` datetime DEFAULT NULL COMMENT '分片合并时间',
  `file_type` varchar(100) DEFAULT NULL COMMENT '文件类型',
  `upload_device` varchar(1000) DEFAULT NULL COMMENT '设备信息',
  `upload_ip` varchar(100) DEFAULT NULL COMMENT '上传设备IP',
  `upload_count` bigint(11) DEFAULT '1' COMMENT '上传统计',
  `download_count` bigint(11) DEFAULT '1' COMMENT '下载统计',
  `storage_date` datetime DEFAULT NULL COMMENT '储存日期',
  `create_by` varchar(40) DEFAULT NULL COMMENT '上传人员',
  `create_time` datetime DEFAULT NULL COMMENT '上传日期',
  `del_flag` int(5) DEFAULT '1' COMMENT '删除标记 1正常 -1删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='文件上传记录';

