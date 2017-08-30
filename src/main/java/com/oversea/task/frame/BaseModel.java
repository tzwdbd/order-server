package com.oversea.task.frame;

import java.io.Serializable;

public class BaseModel implements Serializable {
	
	private static final long serialVersionUID = 114112768558473958L;
	
	private Boolean success;
	private String message;
	
	public BaseModel() {}
	
	public BaseModel(String message) {
		this(false, message);
	}
	
	public BaseModel(Boolean success, String message) {
		this.success = success;
		this.message = message;
	}
	
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
