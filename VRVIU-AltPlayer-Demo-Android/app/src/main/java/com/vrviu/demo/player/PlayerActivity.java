/**
 * @version V1.0
 * @Title: com.silver.player.vrplayer
 * @Description: Added by kevin.zha@vrviu.com
 * @date 2018/3/5 20:05
 */

package com.vrviu.demo.player;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.viu.demo.player.R;
import com.viu.player.IMediaPlayer;
import com.viu.player.VRVIULivePlayer;
import com.viu.player.AltVideoView;
import com.viu.player.callback.OnBufferingUpdateListener;
import com.viu.player.callback.OnErrorListener;
import com.viu.player.callback.OnInfoListener;
import com.viu.player.callback.OnPreparedListener;
import com.viu.player.callback.OnVideoSizeChangedListener;

import static com.viu.player.PlayerState.BUFFERING;
import static com.viu.player.PlayerState.PLAYING;
import static com.viu.player.PlayerState.READY;

public class PlayerActivity extends BaseActivity implements OnBufferingUpdateListener,
        OnInfoListener,OnErrorListener,OnVideoSizeChangedListener,OnPreparedListener,SeekBar.OnSeekBarChangeListener {
  private static final String TAG = "PlayerActivity";
  public static final String URI_LIST_EXTRA = "uri_list";
  public static final String EXTENSION_LIST_EXTRA = "extension_list";
  public static final String EXTENSION_EXTRA = "extension";
  public static final String ACTION_VIEW_LIST = "player.action.VIEW_LIST";
  public static final String ACTION_VIEW = "player.action.VIEW";
  private AltVideoView videoView;
  private AltVideoView mVideoView;
  private VRVIULivePlayer player;
  private ProgressBar loadingPb;
  private SeekBar mSeekBar;
  private TextView mPlayPositionTv;
  private TextView mDurationTv;
  private Button mPlayBtn;
  private EventHandler mHander;
  private long m_durationMs;
  private RelativeLayout mControlRl;
  private String mAppId ;
  private String mAccessKey;
  private String mAccessKeyId;
  private String mBizId;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Intent intent = getIntent();
    String uriString = intent.getStringExtra(URI_LIST_EXTRA);
    setContentView(R.layout.activity_player);
    mControlRl = (RelativeLayout)findViewById(R.id.control_rl);
    mHander = new EventHandler();
    loadingPb = (ProgressBar)findViewById(R.id.loading_pb);
    mSeekBar = (SeekBar)findViewById(R.id.seekBar);
    mSeekBar.setOnSeekBarChangeListener(this);
    mDurationTv = (TextView)findViewById(R.id.duration_tv);
    mPlayPositionTv = (TextView)findViewById(R.id.play_position_tv);
    mPlayBtn = (Button)findViewById(R.id.play_btn);

    mVideoView =  (AltVideoView)findViewById(R.id.surfaceView);
    mVideoView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (mControlRl.isShown()) {
          mControlRl.setVisibility(View.GONE);
        }else{
          mControlRl.setVisibility(View.VISIBLE);
        }
      }
    });

    mAppId = "vrviu_altsdk";
    mAccessKey = "87ab4019c7f624c0310b5c52f1c76419";
    mAccessKeyId = "c832b744e6983a8df217f8af27f1395f";
    mBizId = "altsdk_demo";
    mVideoView.init(mAppId,mAccessKey, mAccessKeyId,mBizId);
    mVideoView.setOnErrorListener(this);
    mVideoView.setOnVideoSizeChangedListener(this);
    mVideoView.setUrl(uriString);
    mVideoView.start();

  }

  @Override
  protected void onPause() {
    super.onPause();
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mVideoView.release();
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    if (hasFocus) {
      setImmersiveSticky();
    }
  }

  @Override
  public boolean dispatchKeyEvent(KeyEvent event) {
    // Avoid accidental volume key presses while the phone is in the VR headset.
    if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP
        || event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
      return false;
    }
    return super.dispatchKeyEvent(event);
  }

  private void setImmersiveSticky() {
    getWindow()
        .getDecorView()
        .setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
  }

  @Override
  public void onBufferingUpdate(IMediaPlayer mp, int percent) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {

      }
    });
  }

  @Override
  public boolean onInfo(IMediaPlayer mp,final int what, int extra) {
    Message msg = mHander.obtainMessage(what);
    msg.arg1 = extra;
    mHander.sendMessage(msg);
    return false;
  }
  boolean isTryToSeek = false;
  @Override
  public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
//      if (player != null && m_durationMs > 0){
//        player.seekTo(progress*m_durationMs/100);
//      }
  }

  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {
    isTryToSeek = true;
  }

  @Override
  public void onStopTrackingTouch(SeekBar seekBar) {
    isTryToSeek = false;
  }

  @Override
  public void onPrepared(IMediaPlayer mp) {
    mHander.sendEmptyMessage(READY);
  }

  @Override
  public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
    return false;
  }

  @Override
  public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1) {

  }

  private class EventHandler extends Handler {
    public EventHandler() {
      super();
    }

    @Override
    public void handleMessage(Message msg) {

      switch (msg.what) {
        case BUFFERING:
          loadingPb.setVisibility(View.VISIBLE);
          break;
        case PLAYING:
          loadingPb.setVisibility(View.GONE);
          if (mVideoView != null){
            m_durationMs = mVideoView.getDuration();
            mDurationTv.setText(formatTime(m_durationMs));
          }
          long currentPositionMs = mVideoView.getCurrentPosition();
          if (m_durationMs > 0) {
            float progress = currentPositionMs * 100 / m_durationMs;
            mSeekBar.setProgress((int) progress);
          }

          break;
        case READY:

          break;
        default:
          break;

      }
    }
  }

  public String formatTime(long ms) {
    Integer ss = 1000;
    Integer mi = ss * 60;
    Integer hh = mi * 60;
    Integer dd = hh * 24;

    Long day = ms / dd;
    Long hour = (ms - day * dd) / hh;
    Long minute = (ms - day * dd - hour * hh) / mi;
    Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
    Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

    StringBuffer sb = new StringBuffer();
    if(day > 0) {
      sb.append(day+":");
    }
    if(hour > 0) {
      sb.append(hour+":");
    }
    if(minute > 0) {
      sb.append(minute+":");
    }else if (minute == 0){
      sb.append("00:");
    }
    if(second > 0) {
      sb.append(second+":");
    }else if (second == 0){
      sb.append("00");
    }
//    if(milliSecond > 0) {
//      sb.append(milliSecond+"毫秒");
//    }
    return sb.toString();
  }

}
