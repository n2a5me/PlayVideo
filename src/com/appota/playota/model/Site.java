package com.appota.playota.model;

public class Site {
	private String siteName, siteCover, siteUrl;
	private String siteId;
	private int is_active;
	private String siteJs;

	public Site() {
		super();
	}

	public Site(String siteName, String siteCover, String siteUrl, String siteId) {
		super();
		this.siteName = siteName;
		this.siteCover = siteCover;
		this.siteUrl = siteUrl;
		this.siteId = siteId;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getSiteCover() {
		return siteCover;
	}

	public void setSiteCover(String siteCover) {
		this.siteCover = siteCover;
	}

	public String getSiteUrl() {
		return siteUrl;
	}

	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public int getIs_active() {
		return is_active;
	}

	public void setIs_active(int is_active) {
		this.is_active = is_active;
	}

	public String getSiteJs() {
		return siteJs;
	}

	public void setSiteJs(String siteJs) {
		this.siteJs = siteJs;
	}

}
