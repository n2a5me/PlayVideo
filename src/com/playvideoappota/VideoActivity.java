package com.playvideoappota;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.appota.media.PlayerEngine;
import com.appota.media.PlayerEngineListener;
import com.appota.playota.model.MusicObject;
import com.appota.playota.model.Playlist;
import com.appota.playota.model.Playlist.PlaylistPlaybackMode;
import com.appota.widget.VerticalSeekBar;
import com.example.playvideoappota.R;

public class VideoActivity extends Activity implements Callback,
		AnimationListener {

	private boolean ischangeLayout = false;

	private Playlist mPlaylist;

	private int animation_duration_long = 5000;

	private int animation_duration_short = 500;

	private TextView textViewPlayed;

	private TextView textViewLength;

	private TextView textTitle;

	private SeekBar timeSeekbar;
	private VerticalSeekBar volumeSeekbar;

	private SurfaceView surfaceViewFrame;

	private ImageButton imgPauseAndPlay;
	private ImageButton imgBacktoEnd;
	private ImageButton imgBackVideo;
	private ImageButton imgNextVideo;
	private ImageButton imgNextToEnd;
	private ImageButton imgBackActivity;
	private ImageButton imgVolume;
	private ImageButton imgShare;

	private LinearLayout header_control_group;
	private LinearLayout media_control_group;
	private LinearLayout timebar_control_group;
	private LinearLayout volume_control_group;

	private SurfaceHolder holder;

	private Animation hideMediaController;

	private RelativeLayout linearLayoutMediaController;

	private static final String TAG = "androidEx2 = VideoSample";

	private String url = "http://channelz9.org.mp3.zdn.vn/zv/d5de5392f4d0caeed0be7827bad81c83/516b8950/2013/04/12/4/b/4bf1e8332089465b01db6bb51836489f.mp4";

	private String local_file = "/mnt/sdcard/Android/data/com.appota.playota/files/GangnamStyle-PSY%5BMP4MV360p%5D.mp4";

	private String local_file1 = "/mnt/sdcard/Android/data/com.appota.playota/files/ChoNguoiNoiAy-UyenLinh%5BMP4MV360p%5D.mp4";

	private String local_file2 = "/mnt/sdcard/Android/data/com.appota.playota/files/Gentleman-PSY%5BMP4MV480p%5D.mp4";

	private PlayerEngineListener playerEngineListener = new PlayerEngineListener() {

		@Override
		public void onTrackStreamError() {
			// TODO Auto-generated method stub
			Log.e(TAG, "onTrackStreamError");
		}

		@Override
		public void onTrackStop() {
			// TODO Auto-generated method stub
			Log.e(TAG, "onTrackStop");
			imgPauseAndPlay
					.setBackgroundResource(R.drawable.play_video_button_bg);
		}

		@Override
		public boolean onTrackStart(int duration) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onTrackStart");
			imgPauseAndPlay
					.setBackgroundResource(R.drawable.pause_video_button_bg);
			// prepare();
			textViewLength
					.setText(CommonUtils.secondsToString(duration / 1000));
			linearLayoutMediaController.setVisibility(View.VISIBLE);
			animationHandler.postDelayed(animationCallBack,
					animation_duration_long);
			timeSeekbar.setMax(duration / 1000);
			return true;
		}

		@Override
		public void onTrackProgress(int seconds, int duration) {
			// TODO Auto-generated method stub
			curenprogress = seconds;
			textViewPlayed.setText(CommonUtils.secondsToString(seconds) + "/");
			timeSeekbar.setMax(duration / 1000);
			timeSeekbar.setProgress(seconds);
		}

		@Override
		public void onTrackPause() {
			// TODO Auto-generated method stub
			Log.e(TAG, "onTrackPause");
			imgPauseAndPlay
					.setBackgroundResource(R.drawable.play_video_button_bg);
		}

		@Override
		public void onTrackChanged(MusicObject playlistEntry) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onTrackChanged");
			textTitle.setText(playlistEntry.getTitle());
			imgPauseAndPlay
					.setBackgroundResource(R.drawable.play_video_button_bg);

		}

		@Override
		public void onTrackBuffering(int percent) {
			// TODO Auto-generated method stub
			timeSeekbar.setSecondaryProgress(percent);
		}
	};

	protected int curenprogress;

	private AudioManager audioManager;

	private int maxVolume;

	private int curVolume;

	private PlayerEngine getPlayerEngine() {
		return UILApplication.getInstance().getPlayerEngineInterface();
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);
		initView();
		Log.e(TAG, "ischangeLayout " + ischangeLayout);
		if (getPlayerEngine().getPlaylist() != null) {
			mPlaylist = getPlayerEngine().getPlaylist();
		} else {
			initPlaylist();
		}
		UILApplication.getInstance().setPlayerEngineListener(
				playerEngineListener);

	}

	private void initPlaylist() {
		// TODO Auto-generated method stub
		mPlaylist = new Playlist();
		MusicObject musicObject = new MusicObject("Gangnam style ", "gsy");
		musicObject.setFilePath(local_file);
		mPlaylist.addTrack(musicObject);
		MusicObject musicObject1 = new MusicObject("Cho nguoi noi ay ",
				"uyen linh");
		musicObject1.setFilePath(local_file1);
		mPlaylist.addTrack(musicObject1);
		MusicObject musicObject2 = new MusicObject("Gentleman ", "psy");
		musicObject2.setFilePath(local_file2);
		mPlaylist.addTrack(musicObject2);
		mPlaylist.calculateOrder(true);
		mPlaylist.select(0);
		if (getPlayerEngine().getPlaylist() != mPlaylist && mPlaylist != null) {
			Log.e(TAG, "open playlist ");
			getPlayerEngine().openPlaylist(mPlaylist);
		}

	}

	private void initView() {
		// TODO Auto-generated method stub
		textTitle = (TextView) findViewById(R.id.txTitle);
		linearLayoutMediaController = (RelativeLayout) findViewById(R.id.linearLayoutMediaController);
		linearLayoutMediaController.setVisibility(View.GONE);
		header_control_group = (LinearLayout) findViewById(R.id.header_control_group);
		media_control_group = (LinearLayout) findViewById(R.id.media_controll_group);
		timebar_control_group = (LinearLayout) findViewById(R.id.timebar_control_group);
		volume_control_group = (LinearLayout) findViewById(R.id.volume_controll_group);
		hideMediaController = AnimationUtils.loadAnimation(this,
				R.anim.disapearing);
		hideMediaController.setAnimationListener(this);
		imgBackActivity = (ImageButton) findViewById(R.id.imgBack);
		imgVolume = (ImageButton) findViewById(R.id.imgVol);
		imgShare = (ImageButton) findViewById(R.id.imgShare);
		imgBacktoEnd = (ImageButton) findViewById(R.id.imgBacktoEnd);
		imgBackVideo = (ImageButton) findViewById(R.id.imgBackVideo);
		imgPauseAndPlay = (ImageButton) findViewById(R.id.imgPauseAndPlay);
		imgNextVideo = (ImageButton) findViewById(R.id.imgNextVideo);
		imgNextToEnd = (ImageButton) findViewById(R.id.imgNextToEnd);
		textViewPlayed = (TextView) findViewById(R.id.textViewPlayed);
		textViewLength = (TextView) findViewById(R.id.textViewLength);
		surfaceViewFrame = (SurfaceView) findViewById(R.id.surfaceViewFrame);
		surfaceViewFrame.setClickable(false);
		timeSeekbar = (SeekBar) findViewById(R.id.seekBarProgress);
		timeSeekbar.setProgress(0);
		volumeSeekbar = (VerticalSeekBar) findViewById(R.id.volumeSeekbar);
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		volumeSeekbar.setMax(maxVolume);
		volumeSeekbar.setProgress(curVolume);
		holder = surfaceViewFrame.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		setUserEventListenner();

	}

	private void setUserEventListenner() {
		// TODO Auto-generated method stub
		surfaceViewFrame.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (linearLayoutMediaController.getVisibility() == View.GONE) {
					linearLayoutMediaController.setVisibility(View.VISIBLE);
					showControl();
					animationHandler.postDelayed(animationCallBack,
							animation_duration_long);
				}
			}
		});

		volumeSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				animationHandler.postDelayed(animationCallBack,
						animation_duration_short);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				animationHandler.removeCallbacks(animationCallBack);
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						progress, 0);

			}
		});

		timeSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				int progress = timeSeekbar.getProgress();
				if (progress > curenprogress) {
					getPlayerEngine()
							.forward((progress - curenprogress) * 1000);
				} else {
					if (getPlayerEngine().isPlaying()) {
						getPlayerEngine().rewind(
								(curenprogress - progress) * 1000);
					} else {
						getPlayerEngine().play(
								getPlayerEngine().getPlaylist()
										.getSelectedIndex());
						getPlayerEngine().forward((progress) * 1000);
					}
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub

			}
		});

		imgBacktoEnd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mPlaylist != null && mPlaylist.getMusicObjects().size() > 0) {
					if (getPlayerEngine().getPlaybackMode() == PlaylistPlaybackMode.NORMAL) {
						if (getPlayerEngine().getPlaylist().getSelectedIndex() > 0) {
							mPlaylist.select(0);
							getPlayerEngine().play();
						}
					} else {
						mPlaylist.select(0);
						getPlayerEngine().play();
					}
				}
			}
		});

		imgBackVideo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mPlaylist != null && mPlaylist.getMusicObjects().size() > 0) {
					if (getPlayerEngine().getPlaybackMode() == PlaylistPlaybackMode.NORMAL) {
						if (getPlayerEngine().getPlaylist().getSelectedIndex() > 0) {

							getPlayerEngine().prev();
						}
					} else {
						getPlayerEngine().prev();
					}
				}
			}
		});

		imgNextToEnd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mPlaylist != null && mPlaylist.getMusicObjects().size() > 0) {
					if (getPlayerEngine().getPlaybackMode() == PlaylistPlaybackMode.NORMAL) {
						mPlaylist
								.select(mPlaylist.getMusicObjects().size() - 1);
						getPlayerEngine().play();
					} else {
						mPlaylist
								.select(mPlaylist.getMusicObjects().size() - 1);
						getPlayerEngine().play();
					}
				}
			}
		});

		imgNextVideo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mPlaylist != null && mPlaylist.getMusicObjects().size() > 0) {
					if (getPlayerEngine().getPlaybackMode() == PlaylistPlaybackMode.NORMAL) {
						if (getPlayerEngine().getPlaylist().getSelectedIndex() < (getPlayerEngine()
								.getPlaylist().size() - 1)) {
							getPlayerEngine().next();
						}
					} else {
						getPlayerEngine().next();
					}
				}
			}
		});

		imgPauseAndPlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (getPlayerEngine().isPlaying()) {
					getPlayerEngine().pause();
					animationHandler.removeCallbacks(animationCallBack);
				} else {
					getPlayerEngine().play();
					animationHandler.postDelayed(animationCallBack,
							animation_duration_short);
				}
			}
		});

		imgBackActivity.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});

		imgVolume.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				volume_control_group.setVisibility(View.VISIBLE);
				animationHandler.removeCallbacks(animationCallBack);
				animationHandler.postDelayed(animationCallBack,
						animation_duration_long);
			}
		});

		imgShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});

	}

	private Runnable animationCallBack = new Runnable() {
		public void run() {
			hideControl();
		}
	};

	private Handler animationHandler = new Handler();

	private boolean isShow = false;

	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		Log.i(TAG, "========== onProgressChanged : " + progress
				+ " from user: " + fromUser);
		if (!fromUser) {
			textViewPlayed.setText(CommonUtils.secondsToString(progress));
		}
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
	}

	// public void onStopTrackingTouch(SeekBar seekBar) {
	// if (player.isPlaying()) {
	// progressBarWait.setVisibility(View.VISIBLE);
	// player.seekTo(seekBar.getProgress() * 1000);
	// Log.i(TAG, "========== SeekTo : " + seekBar.getProgress());
	// }
	// }

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method

	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.e(TAG, "surfaceCreated");
		getPlayerEngine().setSurfaceHolder(holder);
		if (!getPlayerEngine().isPlaying()) {
			getPlayerEngine().play();
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.e(TAG, "surfaceDestroyed");
		if (getPlayerEngine().isPlaying()) {
			getPlayerEngine().pause();
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.e(TAG, "onResume");
	}

	public void prepare() {
		// Get the dimensions of the video
		int videoWidth = getPlayerEngine().getVideoWidth();
		int videoHeight = getPlayerEngine().getVideoHeight();
		float videoProportion = (float) videoWidth / (float) videoHeight;
		Log.e(TAG, "VIDEO SIZES: W: " + videoWidth + " H: " + videoHeight
				+ " PROP: " + videoProportion);

		// Get the width of the screen
		int screenWidth = CommonUtils.getWidthScreen(this);
		int screenHeight = CommonUtils.getHeightScreen(this);
		float screenProportion = (float) screenWidth / (float) screenHeight;
		Log.e(TAG, "VIDEO SIZES: W: " + screenWidth + " H: " + screenHeight
				+ " PROP: " + screenProportion);

		// Get the SurfaceView layout parameters
		android.view.ViewGroup.LayoutParams lp = surfaceViewFrame
				.getLayoutParams();

		if (videoProportion > screenProportion) {
			lp.width = screenWidth;
			lp.height = (int) ((float) screenWidth / videoProportion);
		} else {
			lp.width = (int) (videoProportion * (float) screenHeight);
			lp.height = screenHeight;
		}
		// Commit the layout parameters
		surfaceViewFrame.setLayoutParams(lp);
		surfaceViewFrame.setClickable(true);
	}

	public void onSeekComplete(MediaPlayer mp) {
	}

	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		linearLayoutMediaController.setVisibility(View.GONE);

	}

	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	public void onAnimationStart(Animation animation) {
		linearLayoutMediaController.setVisibility(View.GONE);
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		// TODO Auto-generated method stub
		ischangeLayout = !ischangeLayout;
		return ischangeLayout;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onConfigurationChanged");
		prepare();
		super.onConfigurationChanged(newConfig);
	}

	protected void hideTimeBarControl() {
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.slide_in_top);
		timebar_control_group.setVisibility(View.INVISIBLE);
		timebar_control_group.startAnimation(animation);
	}

	protected void showTimeBarControl() {
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.slide_out_top);

		timebar_control_group.startAnimation(animation);
		timebar_control_group.setVisibility(View.VISIBLE);
	}

	protected void hideHeaderControl() {
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.slide_in_top);
		header_control_group.setVisibility(View.INVISIBLE);
		header_control_group.startAnimation(animation);
	}

	protected void showHeaderControl() {
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.slide_out_top);

		header_control_group.startAnimation(animation);
		header_control_group.setVisibility(View.VISIBLE);
	}

	protected void showControl() {
		showHeaderControl();
		showTimeBarControl();
		media_control_group.setVisibility(View.VISIBLE);
	}

	protected void hideControl() {
		hideHeaderControl();
		hideTimeBarControl();
		media_control_group.setVisibility(View.GONE);
		volume_control_group.setVisibility(View.GONE);
		linearLayoutMediaController.setVisibility(View.GONE);
	}
}
