package com.silang.superfileview.webplayer;

public interface WebPlayerController {
    void load(String url);

    void play();

    void pause();

    void seekTo(int position);

    void muted(boolean isMuted);
}
