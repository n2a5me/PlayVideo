package com.appota.playota.model;

public class MusicObject {

	public MusicObject(String title, String artist) {
		super();
		this.title = title;
		this.artist = artist;
	}

	public boolean isSelect = false;
	public boolean ispause = false;

	public MusicObject() {

	}

	private int id;
	private String title;
	private String artist;
	private String bitrate;
	private boolean downloaded;
	private String filePath;
	private String duration;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getBitrate() {
		return bitrate;
	}

	public void setBitrate(String bitrate) {
		this.bitrate = bitrate;
	}

	public boolean isDownloaded() {
		return downloaded;
	}

	public void setDownloaded(boolean downloaded) {
		this.downloaded = downloaded;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}
}
