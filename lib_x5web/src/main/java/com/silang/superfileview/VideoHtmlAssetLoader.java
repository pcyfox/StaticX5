package com.silang.superfileview;


import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class VideoHtmlAssetLoader {
    private String rawHtml;
    public String loadGroupData(Context context, String src) {
        if (rawHtml != null) {
            return rawHtml.replace("VIDEO_URL", src);
        }
        BufferedReader reader = null;
        try {
            InputStream in = context.getAssets().open("web/VideoPlayer.html");
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            rawHtml = builder.toString();
            return rawHtml.replace("VIDEO_URL", src);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }
}
