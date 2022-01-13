package com.silang.superfileview.webplayer;

public interface WebPlayerListener {

    void onInit();

    void onCanPlay();

    void onPlay();

    void onPause();

    void onEnded();

    void onTimeUpDate(int currentTime);

    void onSeeked();

    void onSeeking();

    void onPlaying();

    void onLoadedMetadata();
}
