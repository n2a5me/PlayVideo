/*
 * Copyright (C) 2009 Teleca Poland Sp. z o.o. <android@teleca.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.appota.media;

import java.io.FileInputStream;
import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

import com.appota.playota.model.MusicObject;
import com.appota.playota.model.Playlist;
import com.appota.playota.model.Playlist.PlaylistPlaybackMode;
import com.playvideoappota.CommonUtils;

public class PlayerEngineImpl implements PlayerEngine {

	/**
	 * Time frame - used for counting number of fails within that time
	 */
	private static final long FAIL_TIME_FRAME = 1000;

	/**
	 * Acceptable number of fails within FAIL_TIME_FRAME
	 */
	private static final int ACCEPTABLE_FAIL_NUMBER = 2;

	protected static final int MSG_MP_RELEASE = 5;

	/**
	 * Beginning of last FAIL_TIME_FRAME
	 */
	private long mLastFailTime;

	/**
	 * Number of times failed within FAIL_TIME_FRAME
	 */
	private long mTimesFailed;

	/**
	 * Simple MediaPlayer extensions, adds reference to the current track
	 * 
	 * @author Lukasz Wisniewski
	 */
	private class InternalMediaPlayer extends MediaPlayer {

		/**
		 * Keeps record of currently played track, useful when dealing with
		 * multiple instances of MediaPlayer
		 */
		public MusicObject playlistEntry;

		/**
		 * Still buffering
		 */
		public boolean preparing = false;

		/**
		 * Determines if we should play after preparation, e.g. we should not
		 * start playing if we are pre-buffering the next track and the old one
		 * is still playing
		 */
		public boolean playAfterPrepare = false;

	}

	/**
	 * InternalMediaPlayer instance (maybe add another one for cross-fading)
	 */
	private InternalMediaPlayer mCurrentMediaPlayer;

	/**
	 * Listener to the engine events
	 */
	private PlayerEngineListener mPlayerEngineListener;

	/**
	 * Playlist
	 */
	private Playlist mPlaylist = null;

	/**
	 * Playlist of song played before
	 */
	private Playlist prevPlaylist = null;

	private Context context;

	/**
	 * Handler to the context thread
	 */
	private Handler mHandler;

	/**
	 * Runnable periodically querying Media Player about the current position of
	 * the track and notifying the listener
	 */
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {

			if (mPlayerEngineListener != null) {

				if (mCurrentMediaPlayer != null) {
					mPlayerEngineListener.onTrackProgress(
							mCurrentMediaPlayer.getCurrentPosition() / 1000,
							mCurrentMediaPlayer.getDuration());

				}
				mHandler.postDelayed(this, 1000);
			}
		}
	};

	protected Handler mMPHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MSG_MP_RELEASE) {
				if (mPlaylist.getPlaylistPlaybackMode() == PlaylistPlaybackMode.SHUFFLE_AND_REPEAT1
						|| mPlaylist.getPlaylistPlaybackMode() == PlaylistPlaybackMode.REPEAT1) {
					Log.e("",
							"onCompletion getPlaylistPlaybackMode "
									+ mPlaylist.getPlaylistPlaybackMode()
									+ " seleected track id "
									+ mPlaylist.getSelectedIndex());
					play(mPlaylist.getSelectedIndex());
				} else if (!mPlaylist.isLastTrackOnList()
						|| mPlaylist.getPlaylistPlaybackMode() == PlaylistPlaybackMode.REPEAT
						|| mPlaylist.getPlaylistPlaybackMode() == PlaylistPlaybackMode.SHUFFLE_AND_REPEAT) {
					next();
				} else

				{
					stop();
				}
			}
		}
	};

	/**
	 * Default constructor
	 */
	public PlayerEngineImpl(Context context) {
		mLastFailTime = 0;
		mTimesFailed = 0;
		mHandler = new Handler();
		this.context = context;
	}

	@Override
	public void next() {
		if (mPlaylist != null) {
			mPlaylist.selectNext();
			play();
		}
	}

	@Override
	public void openPlaylist(Playlist playlist) {
		Log.e("", "playlist onopen " + playlist.size());
		if (!playlist.isEmpty()) {
			prevPlaylist = mPlaylist;
			mPlaylist = playlist;
		} else
			mPlaylist = null;
	}

	@Override
	public void pause() {
		if (mCurrentMediaPlayer != null) {

			if (mCurrentMediaPlayer.preparing) {
				mCurrentMediaPlayer.playAfterPrepare = false;
				return;
			}

			if (mCurrentMediaPlayer.isPlaying()) {
				mCurrentMediaPlayer.pause();
				if (mPlayerEngineListener != null)
					mPlayerEngineListener.onTrackPause();
				return;
			}
		}
	}

	@Override
	public void play(int index) {

		if (mPlaylist != null) {
			mPlaylist.select(index);

			if (mCurrentMediaPlayer == null) {
				mCurrentMediaPlayer = build(mPlaylist.getSelectedTrack());
			}

			if (mPlayerEngineListener.onTrackStart(mCurrentMediaPlayer
					.getDuration()) == false) {
				return;
			}

			if (mCurrentMediaPlayer != null
					&& mCurrentMediaPlayer.playlistEntry != mPlaylist
							.getSelectedTrack()) {
				cleanUp();
				mCurrentMediaPlayer = build(mPlaylist.getSelectedTrack());
			} else {
				cleanUp();
				mCurrentMediaPlayer = build(mPlaylist.getSelectedTrack());
			}

			if (mCurrentMediaPlayer == null)
				return;

			if (!mCurrentMediaPlayer.preparing) {

				if (!mCurrentMediaPlayer.isPlaying()) {
					mHandler.removeCallbacks(mUpdateTimeTask);
					mHandler.postDelayed(mUpdateTimeTask, 1000);

					mCurrentMediaPlayer.start();
				}
			} else {

				mCurrentMediaPlayer.playAfterPrepare = true;
			}
		}

	}

	@Override
	public void play() {
		Log.e("", "mPlaylist play" + mPlaylist);
		if (mPlaylist != null) {

			if (mCurrentMediaPlayer == null) {
				mCurrentMediaPlayer = build(mPlaylist.getSelectedTrack());
			}

			if (mPlayerEngineListener.onTrackStart(mCurrentMediaPlayer
					.getDuration()) == false) {
				return;
			}

			if (mCurrentMediaPlayer != null
					&& mCurrentMediaPlayer.playlistEntry != mPlaylist
							.getSelectedTrack()) {
				Log.e("", "mCurrentMediaPlayer != null"
						+ "mCurrentMediaPlayer.playlistEntry != mPlaylist ="
						+ true);
				cleanUp();
				mCurrentMediaPlayer = build(mPlaylist.getSelectedTrack());
			}

			if (mCurrentMediaPlayer == null)
				return;
			Log.e("preparing", "mCurrentMediaPlayer.isPlaying() "+ mCurrentMediaPlayer.isPlaying());
			if (!mCurrentMediaPlayer.preparing) {

				if (!mCurrentMediaPlayer.isPlaying()) {

					mHandler.removeCallbacks(mUpdateTimeTask);
					mHandler.postDelayed(mUpdateTimeTask, 1000);
					mCurrentMediaPlayer.setDisplay(surfaceHolder);
					mCurrentMediaPlayer.start();
				}
			} else {

				mCurrentMediaPlayer.playAfterPrepare = true;
			}
		}

	}

	@Override
	public void prev() {
		if (mPlaylist != null) {
			mPlaylist.selectPrev();
			play();
		}
	}

	@Override
	public void skipTo(int index) {
		mPlaylist.select(index);
		play();
	}

	@Override
	public void stop() {
		cleanUp();

		if (mPlayerEngineListener != null) {
			mPlayerEngineListener.onTrackStop();
		}
	}

	/**
	 * Stops & destroys media player
	 */
	private void cleanUp() {
		// nice clean-up job
		if (mCurrentMediaPlayer != null) {
			try {
				mCurrentMediaPlayer.stop();
			} catch (IllegalStateException e) {
				// this may happen sometimes
			} finally {
				mCurrentMediaPlayer.release();
				mCurrentMediaPlayer = null;
			}

		}
	}

	private InternalMediaPlayer build(MusicObject musicObject) {
		if (mCurrentMediaPlayer == null) {
			mCurrentMediaPlayer = new InternalMediaPlayer();
		} else {
			mCurrentMediaPlayer.reset();
		}
		try {
			try {
				Log.e("file music ", musicObject.getFilePath());
				// FileInputStream fileInputStream = new FileInputStream(
				// musicObject.getFilePath());
				mCurrentMediaPlayer.setDataSource(musicObject.getFilePath());
				// fileInputStream.close();

			} catch (IOException e1) {

				e1.printStackTrace();
			}
			mCurrentMediaPlayer.playlistEntry = musicObject;

			mCurrentMediaPlayer
					.setOnCompletionListener(new OnCompletionListener() {

						@Override
						public void onCompletion(MediaPlayer mp) {
							Log.e("",
									"onCompletion getPlaylistPlaybackMode "
											+ mPlaylist
													.getPlaylistPlaybackMode()
											+ " seleected track id "
											+ mPlaylist.getSelectedIndex());
							mMPHandler.sendEmptyMessageDelayed(MSG_MP_RELEASE,
									500);
						}

					});

			mCurrentMediaPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					mCurrentMediaPlayer.preparing = false;
					// we may start playing
					if (mPlaylist.getSelectedTrack() == mCurrentMediaPlayer.playlistEntry
							&& mCurrentMediaPlayer.playAfterPrepare) {
						mCurrentMediaPlayer.playAfterPrepare = false;
//						mCurrentMediaPlayer.setDisplay(surfaceHolder);

						play();
						Log.d("onprepared", "true");
					}

				}

			});

			mCurrentMediaPlayer
					.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

						@Override
						public void onBufferingUpdate(MediaPlayer mp,
								int percent) {
							if (mPlayerEngineListener != null) {
								mPlayerEngineListener.onTrackBuffering(percent);
							}
						}

					});

			mCurrentMediaPlayer.setOnErrorListener(new OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {

						if (mPlayerEngineListener != null) {
							mPlayerEngineListener.onTrackStreamError();
						}
						stop();
						return true;
					}

					if (what == -1) {
						long failTime = System.currentTimeMillis();
						if (failTime - mLastFailTime > FAIL_TIME_FRAME) {

							mTimesFailed = 1;
							mLastFailTime = failTime;
						} else {

							mTimesFailed++;
							if (mTimesFailed > ACCEPTABLE_FAIL_NUMBER) {
								if (mPlayerEngineListener != null) {
									mPlayerEngineListener.onTrackStreamError();
								}
								stop();
								return true;
							}
						}
					}
					return false;
				}
			});

			mCurrentMediaPlayer.preparing = true;
			try {
				mCurrentMediaPlayer.prepare();
			} catch (IOException e) {
				Log.e("ErrorTag", e.getMessage(), e);
				e.printStackTrace();
			}

			if (mPlayerEngineListener != null) {
				mPlayerEngineListener.onTrackChanged(mPlaylist
						.getSelectedTrack());
			}

			return mCurrentMediaPlayer;
		} catch (IllegalArgumentException e) {
			Log.e("ErrorTag", e.getMessage(), e);
			e.printStackTrace();
		} catch (IllegalStateException e) {
			Log.e("ErrorTag", e.getMessage(), e);
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Playlist getPlaylist() {
		return mPlaylist;
	}

	@Override
	public boolean isPlaying() {

		if (mCurrentMediaPlayer == null)
			return false;

		if (mCurrentMediaPlayer.preparing)
			return false;

		return mCurrentMediaPlayer.isPlaying();
	}

	@Override
	public void setListener(PlayerEngineListener playerEngineListener) {
		mPlayerEngineListener = playerEngineListener;
	}

	@Override
	public void setPlaybackMode(PlaylistPlaybackMode aMode) {
		mPlaylist.setPlaylistPlaybackMode(aMode);
	}

	@Override
	public PlaylistPlaybackMode getPlaybackMode() {
		return mPlaylist.getPlaylistPlaybackMode();
	}

	public void forward(int time) {
		if (mCurrentMediaPlayer != null)
			mCurrentMediaPlayer.seekTo(mCurrentMediaPlayer.getCurrentPosition()
					+ time);

	}

	@Override
	public void rewind(int time) {
		mCurrentMediaPlayer.seekTo(mCurrentMediaPlayer.getCurrentPosition()
				- time);
	}

	@Override
	public void prevList() {
		if (prevPlaylist != null) {
			openPlaylist(prevPlaylist);
			play();
		}
	}

	SurfaceHolder surfaceHolder;

	public SurfaceHolder getSurfaceHolder() {
		return surfaceHolder;
	}

	public void setSurfaceHolder(SurfaceHolder surfaceHolder) {
		this.surfaceHolder = surfaceHolder;
	}

	@Override
	public int getVideoWidth() {
		// TODO Auto-generated method stub
		if(mCurrentMediaPlayer!=null){
			return mCurrentMediaPlayer.getVideoWidth();
		}
		return 0;
	}

	@Override
	public int getVideoHeight() {
		// TODO Auto-generated method stub
		if(mCurrentMediaPlayer!=null){
			return mCurrentMediaPlayer.getVideoHeight();
		}
		return 0;
	}

}
