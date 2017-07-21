package com.oversea.task.domain;

/**  
 * djb-domain-object
 * com.haitao.task.domain
 * AlimamaTaskDetailQueue
 * 
 * 2013 淘粉吧-版权所有
 */

import java.io.Serializable;
import java.util.Date;

import com.oversea.task.enums.Platform;


/**  
 * @类描述
 * com.haitao.task.domainAlimamaTaskDetailQueue
 * 数据库表为： 爬虫表
 *
 * @创建描述
 * 创建时间：2015-07-04 15:00:24
 * 创建者：LIUYUAN
 * @version：
 *
 */
public class HaitaoProductTask implements Serializable {

	private static final long serialVersionUID = -279386535554942613L;

	// 主键ID
	private Long id;

	// 平台
	private Platform platform;

	// 目标URL
	private String url;

	// 商品ID
	private String itemId;

	// 优先级
	private Integer priority;

	// 创建时间
	private Date createTime;

	// 爬取时间
	private Date spiderTime;

	// 爬取次数
	private Integer spiderTimes;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Platform getPlatform() {
		return platform;
	}

	public void setPlatform(Platform platform) {
		this.platform = platform;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getSpiderTime() {
		return spiderTime;
	}

	public void setSpiderTime(Date spiderTime) {
		this.spiderTime = spiderTime;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Integer getSpiderTimes() {
		return spiderTimes;
	}

	public void setSpiderTimes(Integer spiderTimes) {
		this.spiderTimes = spiderTimes;
	}

    @Override
	public String toString() {
		return "AlimamaTaskDetailQueue [id=" + id + ", url=" + url
				+ ", itemId=" + itemId + "]";
	}

}