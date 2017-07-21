CREATE TABLE `task_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `group` varchar(255) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  `cron_expression` varchar(255) DEFAULT NULL,
  `param` varchar(255) DEFAULT NULL,
  `cid` bigint(20) DEFAULT NULL,
  `method_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;


CREATE TABLE `task_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tid` bigint(20) DEFAULT NULL,
  `bootstarp_task_id` varchar(30) DEFAULT NULL,
  `hander_task_id` varchar(30) DEFAULT NULL,
  `executor_task_id` varchar(30) DEFAULT NULL,
  `type` enum('SERVER_TASK','CLIENT_TASK','BOOTSTRAP') DEFAULT NULL,
  `max_concurrent_count` int(11) DEFAULT NULL,
  `priority_queue_size` int(11) DEFAULT NULL,
  `work_hand_timeout` int(15) DEFAULT NULL,
  `timeout_handler_type` enum('REASSIGN','DISCARD') DEFAULT NULL,
  `concurrent` int(11) DEFAULT NULL,
  `group_name` varchar(255) DEFAULT NULL,
  `max_alive_time` int(11) DEFAULT NULL,
  `asyn_handle` int(11) DEFAULT NULL,
  `max_retry_times` int(11) DEFAULT NULL,
  `task_reuslt_gzip_on` int(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;