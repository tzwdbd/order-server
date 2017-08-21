package com.oversea.task.domain;

import java.io.Serializable;
import java.util.Date;
public class Resources implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2612112721334183355L;
	
	private String id;
	private String name;
	private String resValue;
	private String type;
	private Integer priority;
	private String value1;
	private String value2;
	private String creator;
	private Date createTime;
	private String value3;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id=id;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name=name;
	}
	public String getResValue() {
		return resValue;
	}

	public void setResValue(String resValue) {
		this.resValue=resValue;
	}
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type=type;
	}
	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority=priority;
	}
	public String getValue1() {
		return value1;
	}

	public void setValue1(String value1) {
		this.value1=value1;
	}
	public String getValue2() {
		return value2;
	}

	public void setValue2(String value2) {
		this.value2=value2;
	}
	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator=creator;
	}
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime=createTime;
	}
	public String getValue3() {
		return value3;
	}

	public void setValue3(String value3) {
		this.value3=value3;
	}

	@Override
	public String toString() {
		return "Resources [id=" + id + ", name=" + name + ", resValue=" + resValue + ", type=" + type + ", priority="
				+ priority + ", value1=" + value1 + ", value2=" + value2 + ", creator=" + creator + ", createTime="
				+ createTime + ", value3=" + value3 + "]";
	}
	
}