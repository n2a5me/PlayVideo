package com.appota.media;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.playvideoappota.UILApplication;

public class MyPhoneStateListener extends  PhoneStateListener {

    private static final String TAG = null;
    private boolean playing=false;
    private PlayerEngine getPlayerEngine() {
		return UILApplication.getInstance().getPlayerEngineInterface();
	};
	@Override
    public void onCallStateChanged(int state, String incomingNumber) {
       super.onCallStateChanged(state, incomingNumber);
       
       switch (state){
       case TelephonyManager.CALL_STATE_RINGING:
          Log.e(TAG, "ringing");
          if(getPlayerEngine().isPlaying()){
        	  playing=true;
        	  getPlayerEngine().pause();
          }
          break;
       
       case TelephonyManager.CALL_STATE_IDLE:
          Log.e(TAG, "idle");
          if(playing)
          {
        	  if(!getPlayerEngine().isPlaying()){
            	  getPlayerEngine().play();
              }
          }
          
          break;

       case TelephonyManager.CALL_STATE_OFFHOOK :
          Log.e(TAG, "offhook");
          if(getPlayerEngine().isPlaying()){
        	  playing=true;
        	  getPlayerEngine().pause();
          }else
          {
        	  playing=false;
          }
          break;

       }
    }
}