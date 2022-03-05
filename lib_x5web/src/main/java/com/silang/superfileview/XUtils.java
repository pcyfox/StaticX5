package com.silang.superfileview;


import android.content.Context;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class XUtils {
    private static final String TAG = "XUtils";

    private XUtils() {
    }

    private static String[] imgs = {"jpg", "png", "jpeg", "html"};
    private static String[] avs = {"mp3", "m4a", "aac", "amr", "wav", "ogg", "mid", "ra", "wma", "mpga", "ape", "flac", "RTSP", "RTP", "SDP", "RTMP", "mp4", "flv", "avi", "3gp", "3gpp", "webm", "ts", "ogv", "m3u8", "asf", "wmv", "rmvb", "rm", "f4v", "dat", "mov", "mpg", "mkv", "mpeg", "mpeg1", "mpeg2", "xvid", "dvd", "vcd", "vob", "divx"};
    private static int[] fileImg = {
            R.drawable.x5_file_doc,
            R.drawable.x5_file_docx,
            R.drawable.x5_file_epub,
            R.drawable.x5_file_jepg,
            R.drawable.x5_file_jpg,
            R.drawable.x5_file_pdf,
            R.drawable.x5_file_png,
            R.drawable.x5_file_ppt,
            R.drawable.x5_file_ppx,
            R.drawable.x5_file_txt,
            R.drawable.x5_file_xsl,
            R.drawable.x5_file_xslx,
            R.drawable.x5_ic_file_yellow,
            R.drawable.x5_file_video
    };

    public static boolean isHttpURL(String str) {
        String regex = "^((https|http|)?://)";
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(str);
        return matcher.find();
    }

    public static boolean isDoc(String url) {
        String[] types = {"doc", "docx", "pdf", "ppt", "pptx", "xsl", "xlsx"};
        return isInTypes(url, types);
    }

    public static boolean isAV(String url) {
        return isInTypes(url, avs);
    }

    public static boolean isWebImg(String url) {
        return isInTypes(url, imgs);
    }


    /***
     * 获取文件类型
     *
     * @param paramString
     * @return
     */
    public  static String getFileType(String paramString) {
        String str = "";

        if (TextUtils.isEmpty(paramString)) {
            return str;
        }
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            return str;
        }

        str = paramString.substring(i + 1);
        return str;
    }

    public static boolean isLocalImg(String url) {
        return new File(url).exists() && isInTypes(url, imgs);
    }

    public static boolean isInTypes(String url, String[] types) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "isInTypes() called with: url = [" + url + "], types = [" + types + "]");
        }
        String sub = "";
        int index = url.indexOf("?");
        if (index > 0) {
            sub = url.substring(0, index);
        }
        for (String i : types) {
            if (!TextUtils.isEmpty(sub) && sub.toLowerCase().endsWith(i)) {
                return true;
            }
            if (url.toLowerCase().endsWith(i)) {
                return true;
            }
        }
        return false;
    }

    public static int getFileImg(String url) {
        if (TextUtils.isEmpty(url)) {
            return fileImg[12];
        }
        if (url.toLowerCase().endsWith("doc")) return fileImg[0];
        if (url.toLowerCase().endsWith("docx")) return fileImg[1];
        if (url.toLowerCase().endsWith("epub")) return fileImg[2];
        if (url.toLowerCase().endsWith("jepg")) return fileImg[3];
        if (url.toLowerCase().endsWith("jpg")) return fileImg[4];
        if (url.toLowerCase().endsWith("pdf")) return fileImg[5];
        if (url.toLowerCase().endsWith("png")) return fileImg[6];
        if (url.toLowerCase().endsWith("ppt")) return fileImg[7];
        if (url.toLowerCase().endsWith("ppx")) return fileImg[8];
        if (url.toLowerCase().endsWith("pptx")) return fileImg[8];
        if (url.toLowerCase().endsWith("txt")) return fileImg[9];
        if (url.toLowerCase().endsWith("xsl")) return fileImg[10];
        if (url.toLowerCase().endsWith("xlsx")) return fileImg[11];
        if (url.toLowerCase().endsWith("mp4")) return fileImg[13];
        if (url.toLowerCase().endsWith("flv")) return fileImg[13];
        if (url.toLowerCase().endsWith("avi")) return fileImg[13];
        if (url.toLowerCase().endsWith("3gp")) return fileImg[13];
        if (url.toLowerCase().endsWith("rmvb")) return fileImg[13];
        if (url.toLowerCase().endsWith("rm")) return fileImg[13];
        if (url.toLowerCase().endsWith("mkv")) return fileImg[13];
        if (url.toLowerCase().endsWith("mov")) return fileImg[13];
        return fileImg[12];
    }

    public static String getFileExtension(final String filePath) {
        if (isSpace(filePath)) return "";
        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastPoi == -1 || lastSep >= lastPoi) return "";
        return filePath.substring(lastPoi + 1);
    }

    public static String getFileName(final String filePath) {
        if (isSpace(filePath)) return "";
        int lastSep = filePath.lastIndexOf(File.separator);
        return lastSep == -1 ? filePath : filePath.substring(lastSep + 1);
    }

    public static List<String> getFileName(final List<String> filePaths) {
        if (filePaths == null || filePaths.isEmpty()) {
            return null;
        }
        List<String> names = new ArrayList<>(filePaths.size());
        for (String p : filePaths) {
            names.add(getFileName(p));
        }
        return names;
    }


    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static List<Volume> getStoragePath(Context mContext) {
        List<Volume> volumeList = new ArrayList<>();
        StorageManager mStorageManager = (StorageManager) mContext
                .getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            Method getUserLabel = storageVolumeClazz.getMethod("getUserLabel");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");

            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String userLabel = (String) getUserLabel.invoke(storageVolumeElement);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean isRemovableResult = (boolean) isRemovable.invoke(storageVolumeElement);//是否可移除
//                Log.d("Utils", "getStoragePath path: " + path);
//                Log.d("Utils", "getStoragePath userLabel: " + userLabel);
//                Log.d("Utils", "getStoragePath isRemovableResult: " + isRemovableResult);
                if (userLabel != null) {
                    volumeList.add(new Volume(path, isRemovableResult, userLabel));
                }
            }
        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return volumeList;
    }

    public static boolean isSupportFile(File file) {
        String type = XUtils.getFileExtension(file.getName());
        return true;
    }

    public static boolean isTbModeFile(File file) {
        String type = XUtils.getFileExtension(file.getName());
        return true;
    }

    public static boolean isQBModeFile(File file) {
        String type = XUtils.getFileExtension(file.getName());
        return true;
    }


    public static boolean isSubRootPath(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File parent = new File(path).getParentFile();
        if (parent == null) return false;
        Log.d("isSubRootPath", "isSubRootPath() called with: parent = [" + parent.getAbsolutePath() + "]");
        return parent.getParent() == null;
    }


    public static class Volume {
        public String path;
        public boolean isUSB;
        public String userLabel;

        public Volume(String path, boolean isUSB, String userLabel) {
            this.path = path;
            this.isUSB = isUSB;
            this.userLabel = userLabel;
        }
    }


}
