package com.appota.playota.model;

public class Song extends MusicObject {

	public Song(String title, String artist) {
		super(title, artist);
		// TODO Auto-generated constructor stub
	}

	public Song() {
		// TODO Auto-generated constructor stub
	}

	private String type = "song";
	private String duration="--:--";
	
	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getType() {
		return type;
	}

}
