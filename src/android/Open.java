package com.disusered;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.net.Uri;
import android.content.Intent;
import android.os.Environment;
import android.provider.ContactsContract;
import android.webkit.MimeTypeMap;
import android.content.ActivityNotFoundException;
import android.os.Build;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;

/**
 * This class starts an activity for an intent to view files
 */
public class Open extends CordovaPlugin {

    public static final String OPEN_ACTION = "open";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals(OPEN_ACTION)) {
            String fileName = args.getString(0);
            String fileId = args.getString(1);

            this.chooseIntent(fileId,fileName, callbackContext);
            return true;
        }
        return false;
    }

    /**
     * Returns the MIME type of the file.
     *
     * @param fileName
     * @return
     */
    private static String getMimeType(String fileName) {
        String mimeType = null;

        //String extension = MimeTypeMap.getFileExtensionFromUrl(fileName);
        int index = fileName.lastIndexOf(".");
        String extension = fileName.substring(index + 1).toLowerCase(Locale.US);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            mimeType = mime.getMimeTypeFromExtension(extension);
        }

        System.out.println("Mime type: " + mimeType);

        return mimeType;
    }

    /**
     * Creates an intent for the data of mime type
     *
     * @param url             ,file name or full fileName
     * @param callbackContext
     */
    private void chooseIntent(String fileId,String fileName, CallbackContext callbackContext) {
        if (fileId != null && fileId.length() > 0) {
            try {
                //String realPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "huatechTemp" + File.separator + fileId;

                //包含文件分割符，传的是完整路径
//                if(fileId.contains(File.separator))
//                {
//                    //// TODO: 2016/8/13 外部传入的路径格式要做特殊处理才能使用
//                    realPath =  fileId;
//                }
//                // 只有一个文件 名，补全路径
//                else {
//                    realPath  = Environment.getExternalStorageDirectory().getPath() + File.separator + "huatechTemp" + File.separator + fileId ;
//                }
                //String realPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "huatechTemp" + File.separator + fileId;


                String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
                String realPath = cordova.getActivity().getExternalFilesDir("")  + File.separator + fileId +"."+ suffix;

                File f = new File(realPath);
                if (!f.exists()) {
                    throw new FileNotFoundException();
                }

                //  Uri uri = Uri.parse(realPath);
                Uri uri = Uri.fromFile(new File(realPath));
                String mime = getMimeType(fileName);
                Intent fileIntent = new Intent(Intent.ACTION_VIEW);
                fileIntent.addCategory("android.intent.category.DEFAULT");
                fileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT > 15) {
                    fileIntent.setDataAndTypeAndNormalize(uri, mime); // API Level 16 -> Android 4.1
                } else {
                    fileIntent.setDataAndType(uri, mime);
                }
                cordova.getActivity().startActivity(fileIntent);
                callbackContext.success();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                callbackContext.error(0);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                callbackContext.error(1);
            }
        } else {
            callbackContext.error(2);
        }
    }
}
