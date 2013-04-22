package com.appota.playota.model;

public class LoginBean {
	private String access_token, refresh_token, system_notification,
			user_notification, expires_in, login_error = "";
	private int user_id, error_code;

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public int getError_code() {
		return error_code;
	}

	public void setError_code(int error_code) {
		this.error_code = error_code;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getError_message() {
		return login_error;
	}

	public void setError_message(String error_message) {
		this.login_error = error_message;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}

	public String getSystem_notification() {
		return system_notification;
	}

	public void setSystem_notification(String system_notification) {
		this.system_notification = system_notification;
	}

	public String getUser_notification() {
		return user_notification;
	}

	public void setUser_notification(String user_notification) {
		this.user_notification = user_notification;
	}

	public String getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(String expires_in) {
		this.expires_in = expires_in;
	}

}
