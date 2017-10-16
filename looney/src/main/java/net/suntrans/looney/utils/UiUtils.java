package net.suntrans.looney.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import net.suntrans.looney.AppBase;

import java.util.HashMap;
import java.util.Map;


public class UiUtils {

    private static Toast mToast;



    public static void showToast(String str) {
        if (mToast == null) {
            mToast = Toast.makeText(AppBase.getApplication(), str, Toast.LENGTH_SHORT);
        }
        mToast.setText(str);
        mToast.show();
    }

    public static void showToastLong(String str) {
        if (mToast == null) {
            mToast = Toast.makeText(AppBase.getApplication(), str, Toast.LENGTH_LONG);
        }
        mToast.setText(str);
        mToast.show();
    }

    public static <T> T checkNotNull(T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }

    /**
     * 获取到字符数组
     *
     * @param tabNames 字符数组的id
     */
    public static String[] getStringArray(int tabNames) {
        return getResource1().getStringArray(tabNames);
    }

    public static Resources getResource1() {
        return AppBase.getApplication().getResources();
    }

    /**
     * dip转换px
     */
    public static int dip2px(int dip) {
        final float scale = getResource1().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    /**
     * dip转换px
     */
    public static int dip2px(float dip, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    /**
     * pxz转换dip
     */

    public static int px2dip(int px) {
        final float scale = getResource1().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    public static Context getContext() {
        return AppBase.getApplication();
    }

    //	public static void runOnUiThread(Runnable runnable) {
//		// 在主线程运行
//		if(android.os.Process.myTid()==App.getMainTid()){
//			runnable.run();
//		}else{
//			//获取handler
//			App.getHandler().post(runnable);
//		}
//	}


    /**
     * 加载view
     *
     * @param layoutId
     * @return
     */
    public static View inflate(int layoutId) {
        return View.inflate(getContext(), layoutId, null);
    }

    public static int getDimens(int homePictureHeight) {
        return (int) getResource1().getDimension(homePictureHeight);
    }

    /**
     * 检查网络是否可用
     */
    public static boolean isNetworkAvailable() {
        Context context = UiUtils.getContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
//					System.out.println(i + "===状态===" + networkInfo[i].getState());
//					System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


//

    /**
     * 将十六进制的字符串转化为十进制的数值
     */
    public static long HexToDec(String hexStr) {
        Map<String, Integer> hexMap = prepareDate(); // 先准备对应关系数据
        int length = hexStr.length();
        long result = 0L; // 保存最终的结果
        for (int i = 0; i < length; i++) {
            result += hexMap.get(hexStr.subSequence(i, i + 1)) * Math.pow(16, length - 1 - i);
        }
//        System.out.println("hexStr=" + hexStr + ",result=" + result);
        return result;
    }

    /**
     * 准备十六进制字符对应关系。如("1",1)...("A",10),("B",11)
     */
    private static HashMap<String, Integer> prepareDate() {
        HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
        for (int i = 0; i <= 9; i++) {
            hashMap.put(i + "", i);
        }
        hashMap.put("a", 10);
        hashMap.put("b", 11);
        hashMap.put("c", 12);
        hashMap.put("d", 13);
        hashMap.put("e", 14);
        hashMap.put("f", 15);
        return hashMap;
    }


    public static int getColor(Context context, int color) {
        TypedValue tv = new TypedValue();
        context.getTheme().resolveAttribute(color, tv, true);
        return tv.data;
    }

    public static boolean isVaild(String value) {
        if (value != null) {
            value = value.replace(" ", "");
            if (!TextUtils.equals("", value))
                return true;
        }
        return false;
    }

    public static int[] getDisplaySize(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }


    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {

            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 根据图片的Uri获取图片的绝对路径(已经适配多种API)
     *
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    public static String getRealPathFromUri(Context context, Uri uri) {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion < 11) {
            // SDK < Api11
            return getRealPathFromUri_BelowApi11(context, uri);
        }
        if (sdkVersion < 19) {
            // SDK > 11 && SDK < 19
            return getRealPathFromUri_Api11To18(context, uri);
        } else
            // SDK > 19
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                return getRealPathFromUri_AboveApi19(context, uri);
            }
        return null;
    }

    /**
     * 适配api19以上,根据uri获取图片的绝对路径
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String getRealPathFromUri_AboveApi19(Context context, Uri uri) {
        String filePath = null;
        String wholeID = DocumentsContract.getDocumentId(uri);
        // 使用':'分割
        String id = wholeID.split(":")[1];

        String[] projection = {MediaStore.Images.Media.DATA};
        String selection = MediaStore.Images.Media._ID + "=?";
        String[] selectionArgs = {id};

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                selection, selectionArgs, null);
        int columnIndex = cursor.getColumnIndex(projection[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    /**
     * 适配api11-api18,根据uri获取图片的绝对路径
     */
    private static String getRealPathFromUri_Api11To18(Context context, Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};

        CursorLoader loader = new CursorLoader(context, uri, projection, null,
                null, null);
        Cursor cursor = loader.loadInBackground();

        if (cursor != null) {
            cursor.moveToFirst();
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]));
            cursor.close();
        }
        return filePath;
    }

    /**
     * 适配api11以下(不包括api11),根据uri获取图片的绝对路径
     */
    private static String getRealPathFromUri_BelowApi11(Context context, Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]));
            cursor.close();
        }
        return filePath;
    }

}
