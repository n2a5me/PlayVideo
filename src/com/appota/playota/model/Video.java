package com.appota.playota.model;

public class Video extends MusicObject {

	public Video(String title, String artist) {
		super(title, artist);
		// TODO Auto-generated constructor stub
	}

	private String type = "video";

	public String getType() {
		return type;
	}

}
