package com.disusered;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;

import android.net.Uri;
import android.content.Intent;
import android.os.Environment;
import android.provider.ContactsContract;
import android.webkit.MimeTypeMap;
import android.content.ActivityNotFoundException;
import android.os.Build;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * This class starts an activity for an intent to view files
 */
public class Open extends CordovaPlugin {

    public static final String OPEN_ACTION = "open";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals(OPEN_ACTION)) {
            String path = args.getString(0);
            this.chooseIntent(path, callbackContext);
            return true;
        }
        return false;
    }

    /**
     * Returns the MIME type of the file.
     *
     * @param path
     * @return
     */
    private static String getMimeType(String path) {
        String mimeType = null;

        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
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
     * @param url ,file name or full path
     * @param callbackContext
     */
    private void chooseIntent(String url, CallbackContext callbackContext) {
        if (url != null && url.length() > 0) {
            try {
                String realUrl="";

                //包含文件分割符，传的是完整路径
                if(url.contains(File.separator))
                {
                    //// TODO: 2016/8/13 外部传入的路径格式要做特殊处理才能使用
                    realUrl =  url;
                }
                // 只有一个文件 名，补全路径
                else {
                    realUrl  = Environment.getExternalStorageDirectory().getPath() + File.separator + "huatechTemp" + File.separator + url ;
                }
                File f = new File(realUrl);
                if (!f.exists()) {
                    throw new FileNotFoundException();
                }

                //  Uri uri = Uri.parse(realUrl);
                Uri uri = Uri.fromFile(new File(realUrl));
                String mime = getMimeType(realUrl);
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
