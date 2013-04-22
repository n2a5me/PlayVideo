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

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.appota.playota.model.MusicObject;
import com.playvideoappota.UILApplication;

/**
 * Background player
 * 
 * @author Lukasz Wisniewski
 * @author Marcin Gil
 */
public class PlayerService extends Service {

	public static final String ACTION_PLAY = "play";

	public static final String ACTION_NEXT = "next";

	public static final String ACTION_PREV = "prev";

	public static final String ACTION_STOP = "stop";

	public static final String ACTION_CHECK = "check";

	public static final String ACTION_BIND_LISTENER = "bind_listener";

	private PlayerEngine mPlayerEngine;

	private NotificationManager mNotificationManager = null;

	private static final int PLAYING_NOTIFY_ID = 667664;

	public static final String LASTFM_INTENT = "fm.last.android.metachanged";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.i(UILApplication.TAG, "Player Service onCreate");

		mPlayerEngine = new PlayerEngineImpl(getApplicationContext());
		mPlayerEngine.setListener(mLocalEngineListener);

		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		UILApplication.getInstance().setConcretePlayerEngine(mPlayerEngine);
		mRemoteEngineListener = UILApplication.getInstance()
				.fetchPlayerEngineListener();
		MyPhoneStateListener myPhoneStateListener = new MyPhoneStateListener();
		((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE))
				.listen(myPhoneStateListener,
						PhoneStateListener.LISTEN_CALL_STATE);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (intent == null) {
			return;
		}

		String action = intent.getAction();
		Log.i(UILApplication.TAG, "Player Service onStart - " + action);

		if (action.equals(ACTION_STOP)) {
			Log.e("service ", "stop");
			stopSelfResult(startId);
			return;
		}

		if (action.equals(ACTION_BIND_LISTENER)) {
			mRemoteEngineListener = UILApplication.getInstance()
					.fetchPlayerEngineListener();
			return;
		}

		// we need to have up-to-date playlist if any of play,next,prev buttons
		// is pressed
		updatePlaylist();

		if (action.equals(ACTION_PLAY)) {
			mPlayerEngine.play();
			// MyPhoneStateService myPhoneStateService = new
			// MyPhoneStateService(getApplicationContext());
			// Intent mIntent = new Intent();
			// myPhoneStateService.startService(mIntent);
			return;
		}
		// if (action.equals(ACTION_PLAY_SELECTION_SONG)) {
		// int index = intent.getIntExtra("song_index", 0);
		// Log.d("song index", index + "");
		// mPlayerEngine.skipTo(index);
		// return;
		// }


		if (action.equals(ACTION_NEXT)) {
			mPlayerEngine.next();
			return;
		}

		if (action.equals(ACTION_PREV)) {
			mPlayerEngine.prev();
			return;
		}
	}

	/**
	 * Fetches a new playlist if its reference address differs from the current
	 * one
	 */
	private void updatePlaylist() {
		if (mPlayerEngine.getPlaylist() != UILApplication.getInstance()
				.fetchPlaylist()) {
			mPlayerEngine.openPlaylist(UILApplication.getInstance()
					.fetchPlaylist());
		}
	}

	@Override
	public void onDestroy() {
		Log.i(UILApplication.TAG, "Player Service onDestroy");
		UILApplication.getInstance().setConcretePlayerEngine(null);
		mPlayerEngine.stop();
		mPlayerEngine = null;

		super.onDestroy();
	}

	/**
	 * Hint: if necessary this can be extended to ArrayList of listeners in the
	 * future, though I do not expect that it will be necessary
	 */
	private PlayerEngineListener mRemoteEngineListener;

	/**
	 * Sends notification to the status bar + passes other notifications to
	 * remote listeners
	 */
	private PlayerEngineListener mLocalEngineListener = new PlayerEngineListener() {

		@Override
		public void onTrackBuffering(int percent) {
			if (mRemoteEngineListener != null) {
				mRemoteEngineListener.onTrackBuffering(percent);
			}

		}

		@Override
		public void onTrackChanged(MusicObject playlistEntry) {
			displayNotifcation(playlistEntry);
			if (mRemoteEngineListener != null) {
				mRemoteEngineListener.onTrackChanged(playlistEntry);
			}

		}

		@Override
		public void onTrackProgress(int seconds, int duration) {
			if (mRemoteEngineListener != null) {
				mRemoteEngineListener.onTrackProgress(seconds, duration);
			}
		}

		@Override
		public void onTrackStop() {

			mNotificationManager.cancel(PLAYING_NOTIFY_ID);
			if (mRemoteEngineListener != null) {
				mRemoteEngineListener.onTrackStop();
			}

		}

		@Override
		public boolean onTrackStart(int duration) {

			if (mRemoteEngineListener != null) {
				if (!mRemoteEngineListener.onTrackStart(duration))
					return false;
			}

			return true;
		}

		@Override
		public void onTrackPause() {
			if (mRemoteEngineListener != null) {
				mRemoteEngineListener.onTrackPause();
			}
		}

		@Override
		public void onTrackStreamError() {
			if (mRemoteEngineListener != null) {
				mRemoteEngineListener.onTrackStreamError();
			}
		}

	};


	private void displayNotifcation(MusicObject musicObject) {

		// Notification notification = new Notification(R.drawable.header_icon,
		// musicObject.getTitle(), System.currentTimeMillis());
		// Intent notificationIntent = new Intent(this, RootActivity.class);
		// notificationIntent.putExtra("open_tab", 3);
		// PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
		// notificationIntent, 0);
		// notification.setLatestEventInfo(this, musicObject.getTitle(),
		// musicObject.getTitle(), pendingIntent);
		// startForeground(PLAYING_NOTIFY_ID, notification);
	}

}
