package com.chengtao.pianoview.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.SparseIntArray;
import com.chengtao.pianoview.entity.Piano;
import com.chengtao.pianoview.entity.PianoKey;
import com.chengtao.pianoview.listener.LoadAudioMessage;
import com.chengtao.pianoview.listener.OnLoadAudioListener;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ChengTao on 2016-11-26.
 */

/**
 * 音频工具类
 */
public class AudioUtils implements LoadAudioMessage {
  //线程池,用于加载和播放音频
  private ExecutorService service = Executors.newCachedThreadPool();
  //最大音频数目
  private final static int MAX_STREAM = 5;
  private static AudioUtils instance = null;
  //消息ID
  private final static int LOAD_START = 1;
  private final static int LOAD_FINISH = 2;
  private final static int LOAD_ERROR = 3;
  private final static int LOAD_PROGRESS = 4;
  //发送进度的间隙时间
  private final static int SEND_PROGRESS_MESSAGE_BREAK_TIME = 500;
  //音频池，用于播放音频
  private SoundPool pool;
  //上下文
  private Context context;
  //加载音频接口
  public static OnLoadAudioListener loadAudioListener;
  //存放黑键和白键的音频加载后的ID的集合
  private SparseIntArray whiteKeyMusics = new SparseIntArray();
  private SparseIntArray blackKeyMusics = new SparseIntArray();
  //是否加载成功
  private boolean isLoadFinish = false;
  //是否正在加载
  private boolean isLoading = false;
  //用于处理进度消息
  private Handler handler;
  private AudioManager audioManager;
  private long currentTime;
  private int loadNum;

  @SuppressWarnings("deprecation")
  private AudioUtils(Context context, OnLoadAudioListener loadAudioListener) {
    this(context, loadAudioListener, MAX_STREAM);
  }

  private AudioUtils(Context context, OnLoadAudioListener loadAudioListener, int maxStream) {
    this.context = context;
    this.loadAudioListener = loadAudioListener;
    handler = new AudioStatusHandler(context.getMainLooper());
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      pool = new SoundPool.Builder().setMaxStreams(maxStream)
          .setAudioAttributes(
              new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                  .setUsage(AudioAttributes.USAGE_MEDIA)
                  .build())
          .build();
    } else {
      pool = new SoundPool(maxStream, AudioManager.STREAM_SYSTEM, 0);
    }
    audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
  }

  //单例模式，只返回一个工具实例
  public static AudioUtils getInstance(Context context, OnLoadAudioListener listener) {
    if (instance == null || instance.pool == null) {
      synchronized (AudioUtils.class) {
        if (instance == null || instance.pool == null) {
          instance = new AudioUtils(context, listener);
        }
      }
    }
    return instance;
  }

  public static AudioUtils getInstance(Context context, OnLoadAudioListener listener,
      int maxStream) {
    if (instance == null || instance.pool == null) {
      synchronized (AudioUtils.class) {
        if (instance == null || instance.pool == null) {
          instance = new AudioUtils(context, listener, maxStream);
        }
      }
    }
    return instance;
  }

  /**
   * 加载音乐
   *
   * @param piano 钢琴实体
   * @throws Exception 异常
   */
  public void loadMusic(final Piano piano) throws Exception {
    if (pool == null) {
      throw new Exception("请初始化SoundPool");
    }
    if (piano != null) {
      if (!isLoading && !isLoadFinish) {
        isLoading = true;
        pool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
          loadNum++;
          if (loadNum == Piano.PIANO_NUMS) {
            isLoadFinish = true;
            sendProgressMessage(100);
            sendFinishMessage();
          } else {
            if (System.currentTimeMillis() - currentTime >= SEND_PROGRESS_MESSAGE_BREAK_TIME) {
              sendProgressMessage((int) (((float) loadNum / (float) Piano.PIANO_NUMS) * 100f));
              currentTime = System.currentTimeMillis();
            }
          }
        });
        service.execute(() -> {
          sendStartMessage();
          ArrayList<PianoKey[]> whiteKeys = piano.getWhitePianoKeys();
          int whiteKeyPos = 0;
          for (int i = 0; i < whiteKeys.size(); i++) {
            for (PianoKey key : whiteKeys.get(i)) {
              try {
                int soundID = pool.load(context, key.getVoiceId(), 1);
                whiteKeyMusics.put(whiteKeyPos, soundID);
                whiteKeyPos++;
              } catch (Exception e) {
                isLoading = false;
                sendErrorMessage(e);
                return;
              }
            }
          }
          ArrayList<PianoKey[]> blackKeys = piano.getBlackPianoKeys();
          int blackKeyPos = 0;
          for (int i = 0; i < blackKeys.size(); i++) {
            for (PianoKey key : blackKeys.get(i)) {
              try {
                int soundID = pool.load(context, key.getVoiceId(), 1);
                blackKeyMusics.put(blackKeyPos, soundID);
                blackKeyPos++;
              } catch (Exception e) {
                isLoading = false;
                sendErrorMessage(e);
                return;
              }
            }
          }
        });
      }
    }
  }

  /**
   * 播放音乐
   *
   * @param key 钢琴键
   */
  public void playMusic(final PianoKey key) {
    if (key != null) {
      if (isLoadFinish) {
        if (key.getType() != null) {
          service.execute(() -> {
            switch (key.getType()) {
              case BLACK:
                try {
                  playBlackKeyMusic(key.getGroup(), key.getPositionOfGroup());
                } catch (NullPointerException ex) {
                  ex.printStackTrace();
                  playBlackKeyMusic(key.getGroup(), key.getPositionOfGroup());
                } catch (Exception e) {
                  e.printStackTrace();
                }
                break;
              case WHITE:
                try {
                  playWhiteKeyMusic(key.getGroup(), key.getPositionOfGroup());
                } catch (NullPointerException ex) {
                  ex.printStackTrace();
                  playWhiteKeyMusic(key.getGroup(), key.getPositionOfGroup());
                } catch (Exception e) {
                  e.printStackTrace();
                }
                break;
            }
          });
        }
      }
    }
  }

  /**
   * 播放白键音乐
   *
   * @param group 组数，从0开始
   * @param positionOfGroup 组内位置
   */
  private void playWhiteKeyMusic(int group, int positionOfGroup) {
    int position;
    if (group == 0) {
      position = positionOfGroup;
    } else {
      position = (group - 1) * 7 + 2 + positionOfGroup;
    }
    int volume = 1;
    if (audioManager != null) {
      volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }
    try {
      pool.play(whiteKeyMusics.get(position), volume, volume, 1, 0, 1f);
    } catch (NullPointerException ex) {
      ex.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 播放黑键音乐
   *
   * @param group 组数，从0开始
   * @param positionOfGroup 组内位置
   */
  private void playBlackKeyMusic(int group, int positionOfGroup) {
    int position;
    if (group == 0) {
      position = positionOfGroup;
    } else {
      position = (group - 1) * 5 + 1 + positionOfGroup;
    }
    int volume = 1;
    if (audioManager != null) {
      volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }
    try {
      pool.play(blackKeyMusics.get(position), volume, volume, 1, 0, 1f);
    } catch (NullPointerException ex) {
      ex.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 结束
   */
  public void stop() {
    try {
      context = null;
      pool.release();
      pool = null;
      whiteKeyMusics.clear();
      whiteKeyMusics = null;
      blackKeyMusics.clear();
      blackKeyMusics = null;
    } catch (NullPointerException ex) {
      ex.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override public void sendStartMessage() {
    handler.sendEmptyMessage(LOAD_START);
  }

  @Override public void sendFinishMessage() {
    handler.sendEmptyMessage(LOAD_FINISH);
  }

  @Override public void sendErrorMessage(Exception e) {
    handler.sendMessage(Message.obtain(handler, LOAD_ERROR, e));
  }

  @Override public void sendProgressMessage(int progress) {
    handler.sendMessage(Message.obtain(handler, LOAD_PROGRESS, progress));
  }

  /**
   * 自定义handler,处理加载状态
   */
  private class AudioStatusHandler extends Handler {
    AudioStatusHandler(Looper looper) {
      super(looper);
    }

    @Override public void handleMessage(Message msg) {
      super.handleMessage(msg);
      handleAudioStatusMessage(msg);
    }
  }

  private void handleAudioStatusMessage(Message msg) {
    if (loadAudioListener != null) {
      switch (msg.what) {
        case LOAD_START:
          loadAudioListener.loadPianoAudioStart();
          break;
        case LOAD_FINISH:
          loadAudioListener.loadPianoAudioFinish();
          break;
        case LOAD_ERROR:
          loadAudioListener.loadPianoAudioError((Exception) msg.obj);
          break;
        case LOAD_PROGRESS:
          loadAudioListener.loadPianoAudioProgress((int) msg.obj);
          break;
        default:
          break;
      }
    }
  }
}
