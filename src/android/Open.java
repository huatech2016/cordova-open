package com.disusered;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.webkit.MimeTypeMap;

import com.disusered.WpsModel.ClassName;
import com.disusered.WpsModel.OpenMode;
import com.disusered.WpsModel.PackageName;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;


/**
 * This class starts an activity for an intent to view files
 */
public class Open extends CordovaPlugin {

    public static final String OPEN_ACTION = "open";
    public static final String FILE_EXIST_ACTION = "isFileExist";

    private static final int OPEN_FILE_REQUEST = 1;
    private CallbackContext callbackContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        String fileName = args.getString(0);
        String fileId = args.getString(1);
        if (action.equals(OPEN_ACTION)) {
            this.tryToOpenFile(fileId, fileName);
            return true;
        } else if (action.equals(FILE_EXIST_ACTION)) {
            isFileExist(fileId, fileName);
            return true;
        }
        return false;
    }

    private void isFileExist(String fileId, String fileName) {
        String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String realPath = cordova.getActivity().getExternalFilesDir("") + File.separator + fileId + "." + fileSuffix;

        File f = new File(realPath);
        if (f.exists()) {
            callbackContext.success();// 文件存在
        } else {
            callbackContext.error(0);// 文件不存在
        }
    }

    /**
     * Returns the MIME type of the file.
     *
     * @param fileName
     * @return
     */
    private static String getMimeType(String fileName) {
        String mimeType = null;
        int index = fileName.lastIndexOf(".");
        String extension = fileName.substring(index + 1).toLowerCase(Locale.US);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            mimeType = mime.getMimeTypeFromExtension(extension);
        }
        return mimeType;
    }

    /**
     * Creates an intent for the data of mime type
     */
    private void tryToOpenFile(String fileId, String fileName) {

        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String realPath = cordova.getActivity().getExternalFilesDir("") + File.separator + fileId + "." + suffix;
        if (isWpsFile(realPath)) {
            openWpsFile(realPath);
        } else {
            openNotWpsFile(fileId, fileName);
        }
    }

    private boolean isWpsFile(String path) {
        if (path != null) {
            path = path.toLowerCase();
            boolean isDoc = path.endsWith("doc") || path.endsWith("docx");
            boolean isExcel = path.endsWith("xls") || path.endsWith("xlsx");
            boolean isPpt = path.endsWith("ppt") || path.endsWith("pptx");
            boolean isPdf = path.endsWith("pdf");

            if (isDoc || isExcel || isPpt || isPdf) {
                return true;
            } else {
                return false;
            }
        } else {
            callbackContext.error("文件路径有误");
            return false;
        }

    }

    boolean openWpsFile(String path) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(WpsModel.OPEN_MODE, OpenMode.NORMAL); // 打开模式
        bundle.putBoolean(WpsModel.SEND_CLOSE_BROAD, true); // 关闭时是否发送广播
        bundle.putString(WpsModel.THIRD_PACKAGE, cordova.getActivity().getPackageName()); // 第三方应用的包名，用于对改应用合法性的验证
        bundle.putBoolean(WpsModel.CLEAR_TRACE, true);// 清除打开记录
        // bundle.putBoolean(CLEAR_FILE, true); //关闭后删除打开文件
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setClassName(PackageName.NORMAL, ClassName.NORMAL);

        File file = new File(path);
        if (file == null || !file.exists()) {
            callbackContext.error("文件不存在");
            return false;
        }

        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        intent.putExtras(bundle);
        try {
            cordova.getActivity().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            callbackContext.error("未检测到可以打开此文件的应用，请先安装wps");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void openNotWpsFile(String fileId, String fileName) {
        if (fileId != null && fileId.length() > 0) {
            try {
                String suffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                String realPath = cordova.getActivity().getExternalFilesDir("") + File.separator + fileId + "." + suffix;
                File f = new File(realPath);
                if (!f.exists()) {
                    throw new FileNotFoundException();
                }

                //  Uri uri = Uri.parse(realPath);
                Uri uri = Uri.fromFile(new File(realPath));

               
                if (suffix != null && suffix.indexOf("aip") >= 0) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri,"application/aip");
                    cordova.getActivity().startActivity(intent);
                    callbackContext.success();
                }
                else if (suffix != null && suffix.indexOf("ofd") >= 0) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri,"application/ofd");
                    cordova.getActivity().startActivity(intent);
                    callbackContext.success();
                }
                else{
                    String mime = getMimeType(fileName);
                    Intent fileIntent = new Intent(Intent.ACTION_VIEW);
                    fileIntent.addCategory("android.intent.category.DEFAULT");
                    fileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (Build.VERSION.SDK_INT > 15) {
                        fileIntent.setDataAndTypeAndNormalize(uri, mime); // API Level 16 ->Android 4.1
                    } else {
                        fileIntent.setDataAndType(uri, mime);
                    }
                    cordova.getActivity().startActivity(fileIntent);
                    callbackContext.success();
                }
               
               
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                callbackContext.error("文件不存在");
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                callbackContext.error("未检测到可以打开此文件的应用");
            }
        }
    }
}
