package com.tk.edu.lib_video.video;

import android.content.Context;
import android.graphics.Point;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.View;

import androidx.annotation.Keep;
import androidx.annotation.LayoutRes;

import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoViewBridge;
import com.tk.edu.lib_video.video.manager.CustomManager;
import com.tk.lib_video.R;

/**
 * 无任何控制ui的播放
 * Created by guoshuyu on 2017/8/6.
 */
@Keep
public class EmptyControlVideo extends StandardGSYVideoPlayer {
    private final static String TAG = "MultiEmptyVideo";
    private VideoPlayerOnClickListener onClickListener;
    private int layoutId = R.layout.empty_control_video;
    private boolean isUseSimpleLayout = true;

    public EmptyControlVideo(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public EmptyControlVideo(Context context) {
        super(context);
    }

    public EmptyControlVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        if (mThumbImageViewLayout != null &&
                (mCurrentState == -1 || mCurrentState == CURRENT_STATE_NORMAL || mCurrentState == CURRENT_STATE_ERROR)) {
            mThumbImageViewLayout.setVisibility(VISIBLE);
        }
        onAudioFocusChangeListener = focusChange -> {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    //todo 判断如果不是外界造成的就不处理
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    //todo 判断如果不是外界造成的就不处理
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    break;
            }
        };
    }

    public void setOnVideoPlayerClickListener(VideoPlayerOnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public GSYVideoViewBridge getGSYVideoManager() {
        CustomManager.getCustomManager(getKey()).initContext(getContext().getApplicationContext());
        CustomManager.getCustomManager(getKey()).setNeedMute(mPlayPosition != 0);
        CustomManager.getCustomManager(getKey()).setTimeOut(3000, true);
        return CustomManager.getCustomManager(getKey());
    }


    @Override
    protected boolean backFromFull(Context context) {
        return CustomManager.backFromWindowFull(context, getKey());
    }

    @Override
    protected void releaseVideos() {
        CustomManager.releaseAllVideos(getKey());
    }


    @Override
    protected int getFullId() {
        return CustomManager.FULLSCREEN_ID;
    }

    @Override
    protected int getSmallId() {
        return CustomManager.SMALL_ID;
    }


    @Override
    public int getLayoutId() {
        if (isUseSimpleLayout) {
            return R.layout.empty_control_video;
        } else {
            return R.layout.empty_control_video_with_nice_bg;
        }
    }

    @Override
    public GSYBaseVideoPlayer startWindowFullscreen(Context context, boolean actionBar, boolean statusBar) {
        GSYBaseVideoPlayer gsyBaseVideoPlayer = super.startWindowFullscreen(context, actionBar, statusBar);
        return (EmptyControlVideo) gsyBaseVideoPlayer;
    }


    @Override
    public GSYBaseVideoPlayer showSmallVideo(Point size, boolean actionBar, boolean statusBar) {
        //下面这里替换成你自己的强制转化
        return (EmptyControlVideo) super.showSmallVideo(size, actionBar, statusBar);
    }

    void setLayoutId(@LayoutRes int layoutId) {
        this.layoutId = layoutId;
    }

    public boolean isUseSimpleLayout() {
        return isUseSimpleLayout;
    }

    public void setUseSimpleLayout(boolean useSimpleLayout) {
        isUseSimpleLayout = useSimpleLayout;
    }

    public String getKey() {
        if (mPlayPosition == -22) {
            Debuger.printfError(getClass().getSimpleName() + " used getKey() " + "******* PlayPosition never set. ********");
        }
        if (TextUtils.isEmpty(mPlayTag)) {
            Debuger.printfError(getClass().getSimpleName() + " used getKey() " + "******* PlayTag never set. ********");
        }
        return TAG + mPlayPosition + mPlayTag;
    }

    @Override
    public boolean onSurfaceDestroyed(Surface surface) {
        return super.onSurfaceDestroyed(surface);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (onClickListener != null) {
            onClickListener.onClick();
        }
    }

    public interface VideoPlayerOnClickListener {
        void onClick();
    }

    @Override
    public void onCompletion() {
        getGSYVideoManager().stop();
        super.onCompletion();
    }

}
