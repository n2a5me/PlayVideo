package com.appota.playota.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import android.util.Log;

public class Playlist {

	private int id;
	private String name;
	private String playlistArtist;
	private ArrayList<MusicObject> musicObjects;
	private String playlistName;

	public String getPlaylistArtist() {
		return playlistArtist;
	}

	public void setPlaylistArtist(String playlistArtist) {
		this.playlistArtist = playlistArtist;
	}

	public String getPlaylistName() {
		return playlistName;
	}

	public void setPlaylistName(String playlistName) {
		this.playlistName = playlistName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArtist() {
		return playlistArtist;
	}

	public void setArtist(String playlistArtist) {
		this.playlistArtist = playlistArtist;
	}

	public ArrayList<MusicObject> getMusicObjects() {
		return musicObjects;
	}

	public void setMusicObjects(ArrayList<MusicObject> musicObjects) {
		this.musicObjects = musicObjects;
	}

	private static final String TAG = "Playlist";

	public enum PlaylistPlaybackMode {
		NORMAL, SHUFFLE, REPEAT, SHUFFLE_AND_REPEAT, REPEAT1, SHUFFLE_AND_REPEAT1
	}

	/**
	 * Keep order in which tracks will be play
	 */
	private ArrayList<Integer> mPlayOrder = new ArrayList<Integer>();

	/**
	 * Keep playlist playback mode
	 */
	private PlaylistPlaybackMode mPlaylistPlaybackMode = PlaylistPlaybackMode.NORMAL;

	/**
	 * Give playlist playback mode
	 * 
	 * @return enum with playback mode
	 */
	public PlaylistPlaybackMode getPlaylistPlaybackMode() {
		return mPlaylistPlaybackMode;
	}

	/**
	 * Set playlist playback mode
	 * 
	 * @param aPlaylistPlaybackMode
	 */
	public void setPlaylistPlaybackMode(
			PlaylistPlaybackMode aPlaylistPlaybackMode) {
		Log.e(TAG, "(Set mode) selected = " + selected);
		Log.e(TAG, "Plyback mode set on: " + aPlaylistPlaybackMode);
		boolean force = false;
		switch (aPlaylistPlaybackMode) {
		case NORMAL:
			if (mPlaylistPlaybackMode == PlaylistPlaybackMode.SHUFFLE
					|| mPlaylistPlaybackMode == PlaylistPlaybackMode.SHUFFLE_AND_REPEAT
					|| mPlaylistPlaybackMode == PlaylistPlaybackMode.REPEAT1) {
				force = true;
			}
			break;
		case REPEAT:
			if (mPlaylistPlaybackMode == PlaylistPlaybackMode.SHUFFLE
					|| mPlaylistPlaybackMode == PlaylistPlaybackMode.SHUFFLE_AND_REPEAT
					|| mPlaylistPlaybackMode == PlaylistPlaybackMode.REPEAT1) {
				force = true;
			}
			break;
		case REPEAT1:
			if (mPlaylistPlaybackMode == PlaylistPlaybackMode.SHUFFLE_AND_REPEAT1) {
				force = true;
			}
			break;
		case SHUFFLE:
			if (mPlaylistPlaybackMode == PlaylistPlaybackMode.NORMAL) {
				force = true;
			}
			break;
		case SHUFFLE_AND_REPEAT:
			if (mPlaylistPlaybackMode == PlaylistPlaybackMode.SHUFFLE_AND_REPEAT1) {
				force = true;
			}
			break;
		case SHUFFLE_AND_REPEAT1:
			if (mPlaylistPlaybackMode == PlaylistPlaybackMode.REPEAT1
					|| mPlaylistPlaybackMode == PlaylistPlaybackMode.SHUFFLE) {
				force = true;
			}
			break;
		}
		Log.e(TAG, "force " + force);
		mPlaylistPlaybackMode = aPlaylistPlaybackMode;
		calculateOrder(force);
	}

	/**
	 * Keeps playlist's entries
	 */

	/**
	 * Keeps record of currently selected track
	 */
	protected int selected = -1;

	public Playlist() {
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, "Playlist constructor start");
		}
		musicObjects = new ArrayList<MusicObject>();
		calculateOrder(true);
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, "Playlist constructor stop");
		}
	}

	/**
	 * Add single track to the playlist
	 * 
	 * @param track
	 *            <code>Track</code> instance
	 * @param album
	 *            <code>Album</code> instance
	 */
	public void addTrack(MusicObject musicObject) {
		musicObjects.add(musicObject);
		mPlayOrder.add(size() - 1);
		Log.e("", "mPlayOrder" + mPlayOrder);
	}

	/**
	 * Add multiple tracks from one album to the playlist
	 * 
	 * @param album
	 *            <code>Album</code> instance with loaded tracks
	 */
	// public void addTracks(Album album) {
	// for (Track track : album.getTracks()) {
	// addTrack(track, album);
	// }
	// }

	/**
	 * Checks if the playlist is empty
	 * 
	 * @return boolean value
	 */
	public boolean isEmpty() {
		return musicObjects.size() == 0;
	}

	/**
	 * Selects next song from the playlist
	 */
	public void selectNext() {
		if (!isEmpty()) {
			selected++;
			selected %= musicObjects.size();
			if (Log.isLoggable(TAG, Log.DEBUG)) {
				Log.d("TAG", "Current (next) selected = " + selected);
			}
		}
	}

	/**
	 * Selects previous song from the playlist
	 */
	public void selectPrev() {
		if (!isEmpty()) {
			selected--;
			if (selected < 0)
				selected = musicObjects.size() - 1;
		}
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d("TAG", "Current (prev) selected = " + selected);
		}
	}

	/**
	 * Select song with a given index
	 * 
	 * @param startSongIndex
	 */
	public void select(int startSongIndex) {
		if (!isEmpty()) {
			if (startSongIndex >= 0 && startSongIndex < musicObjects.size()) {
				selected = mPlayOrder.indexOf(startSongIndex);
				Log.e("", "select " + selected);
			}

		}
	}

	public void selectOrAdd(MusicObject track) {

		// first search thru available tracks
		for (int i = 0; i < musicObjects.size(); i++) {
			if (musicObjects.get(i).getId() == track.getId()) {
				select(i);
				return;
			}
		}

		// add track if necessary
		addTrack(track);
		select(musicObjects.size() - 1);
	}

	/**
	 * Return index of the currently selected song
	 * 
	 * @return int value (-1 if the playlist is empty)
	 */
	public int getSelectedIndex() {
		if (isEmpty()) {
			selected = -1;
		}
		if (selected == -1 && !isEmpty()) {
			selected = 0;
		}
		return selected;
	}

	/**
	 * Return currently selected song
	 * 
	 * @return <code>PlaylistEntry</code> instance
	 */
	public MusicObject getSelectedTrack() {
		MusicObject playlistEntry = null;

		int index = getSelectedIndex();
		Log.e("", "index " + index);
		if (index == -1) {
			return null;
		}
		// Log.e("", "mPlayOrder"+mPlayOrder);
		index = mPlayOrder.get(index);
		// Log.e("", "index " + index);
		if (index == -1) {
			return null;
		}
		// Log.e("", "getSelectedTrack"+index);
		playlistEntry = musicObjects.get(index);

		return playlistEntry;

	}

	/**
	 * Adds PlaylistEntry object to the playlist
	 * 
	 * @param playlistEntry
	 */
	public void addPlaylistEntry(MusicObject playlistEntry) {
		if (playlistEntry != null) {
			musicObjects.add(playlistEntry);
			mPlayOrder.add(size() - 1);
		}
	}

	/**
	 * Count of playlist entries
	 * 
	 * @return
	 */
	public int size() {
		return musicObjects == null ? 0 : musicObjects.size();
	}

	/**
	 * Given track index getter
	 * 
	 * @param index
	 * @return
	 */
	public MusicObject getTrack(int index) {
		return musicObjects.get(index);
	}

	/**
	 * Give all entrys in playlist
	 * 
	 * @return
	 */
	public MusicObject[] getAllTracks() {
		MusicObject[] out = new MusicObject[musicObjects.size()];
		musicObjects.toArray(out);
		return out;
	}

	/**
	 * Remove a track with a given index from the playlist
	 * 
	 * @param position
	 */
	public void remove(int position) {
		if (musicObjects != null && position < musicObjects.size()
				&& position >= 0) {

			if (selected >= position) {
				selected--;
			}

			musicObjects.remove(position);
			mPlayOrder.remove(position);
		}
	}

	/**
	 * Change order playback list when it is needed
	 * 
	 * @param force
	 */
	public void calculateOrder(boolean force) {
		if (mPlayOrder.isEmpty() || force) {
			int oldSelected = 0;

			if (!mPlayOrder.isEmpty()) {
				oldSelected = mPlayOrder.get(selected);
				mPlayOrder.clear();
			}
			Log.e(TAG, "oldSelected " +oldSelected);

			for (int i = 0; i < size(); i++) {
				mPlayOrder.add(i, i);
			}

			if (mPlaylistPlaybackMode == null) {
				mPlaylistPlaybackMode = PlaylistPlaybackMode.NORMAL;
			}
			Log.e(TAG, "mplayorder " + mPlayOrder);
			switch (mPlaylistPlaybackMode) {
			case NORMAL:
			case REPEAT:
				selected = oldSelected;
				Log.e(TAG, "playbackmode " + getPlaylistPlaybackMode()
						+ "mplayorder after " + mPlayOrder);
				break;
			case SHUFFLE:
			case SHUFFLE_AND_REPEAT:
				mPlayOrder.remove(selected);
				Collections.shuffle(mPlayOrder);

				mPlayOrder.add(0, selected);
				selected = mPlayOrder.indexOf(selected);
				Log.e(TAG, "playbackmode " + getPlaylistPlaybackMode()
						+ "mplayorder after " + mPlayOrder);
				break;
			case SHUFFLE_AND_REPEAT1:
				selected = mPlayOrder.indexOf(oldSelected);
				Log.e(TAG, "playbackmode " + getPlaylistPlaybackMode()
						+ "mplayorder after " + mPlayOrder+ "selected "+ selected);
				break;
			}
		}
	}

	/**
	 * Inform weather it is last track on playlist
	 * 
	 * @return
	 */
	public boolean isLastTrackOnList() {
		if (selected == size() - 1)
			return true;
		else
			return false;
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		// This method is used when playlist is deserializable form DB
		in.defaultReadObject();
		if (mPlayOrder == null) {
			if (Log.isLoggable(TAG, Log.DEBUG)) {
				Log.d(TAG, "mPlayOrder is NULL");
			}
			mPlayOrder = new ArrayList<Integer>();
			calculateOrder(true);
		}
	}

	public int musocObjectCount = 0;

}
