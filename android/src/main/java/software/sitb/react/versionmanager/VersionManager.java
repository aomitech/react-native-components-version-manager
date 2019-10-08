package software.sitb.react.versionmanager;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import androidx.annotation.RequiresApi;
import com.facebook.react.bridge.*;

/**
 * @author Sean sean.snow@live.com createAt 2017/5/12
 */
public class VersionManager extends ReactContextBaseJavaModule {

    private static final String TAG = "VersionManager";

    private ReactApplicationContext context;

    public VersionManager(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext;
    }

    @Override
    public String getName() {
        return "RNCVersionManager";
    }

    @ReactMethod
    public void get(Promise promise) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            WritableMap response = Arguments.createMap();
            response.putString("packageName", info.packageName);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                response.putInt("versionCode", (int) (info.getLongVersionCode()));
            } else {
                response.putInt("versionCode", info.versionCode);
            }
            response.putString("versionName", info.versionName);

            promise.resolve(response);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "获取PackageInfo信息失败", e);
            promise.reject(e);
        }

    }

    @ReactMethod
    public void install(ReadableMap args, final Promise promise) {
        Log.d(TAG, "开始安装app");
        String downloadUrl = args.getString("downloadUrl");
        Log.d(TAG, "下载地址: " + downloadUrl);

        final DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));

        if (args.hasKey("title")) {
            request.setTitle(args.getString("title"));
        }

        if (args.hasKey("description")) {
            request.setDescription(args.getString("description"));
        }

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setMimeType("application/vnd.android.package-archive");

        request.allowScanningByMediaScanner();
        //在通知栏显示下载进度
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        final long downloadId = downloadManager.enqueue(request);

        Log.d(TAG, "download id = " + downloadId);

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context defaultContext, Intent intent) {
                long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (completeDownloadId == downloadId) {
                    Uri uri = downloadManager.getUriForDownloadedFile(downloadId);
                    if (null == uri) {
                        Log.w(TAG, "下载失败,可能被用户取消");
                        promise.reject("FAILURE", "下载失败,可能被用户取消");
                        return;
                    }
                    Log.d(TAG, "app 下载成功");

                    Uri apkUri;
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        apkUri = uri;
                    } else {
                        String filePath = FileUtils.getFilePathFromContentUri(context.getContentResolver(), uri);
                        Log.d(TAG, "file path: " + filePath);
                        apkUri = Uri.parse("file://" + filePath);
                    }
                    Log.d(TAG, "apk uri:" + apkUri.toString());

                    Intent installIntent = new Intent(Intent.ACTION_VIEW);
                    installIntent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(installIntent);
                    context.unregisterReceiver(this);
                }
            }
        };
        context.registerReceiver(broadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

}
