package com.smarthub.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Vibrator;
import android.webkit.JavascriptInterface;
import android.widget.Toast;
import android.app.WallpaperManager;
import android.app.NotificationManager;
import android.app.NotificationChannel;
import android.app.Notification;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

public class WebAppInterface {
    private Context context;

    public WebAppInterface(Context c) {
        this.context = c;
    }

    @JavascriptInterface
    public void vibrate(int ms) {
        try {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null) {
                v.vibrate(ms);
            }
        } catch (Exception ignored) {}
    }

    @JavascriptInterface
    public void toast(String message) {
        try {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } catch (Exception ignored) {}
    }

    @JavascriptInterface
    public void showNotification(String title, String message) {
        try {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            String channelId = "smarthub_broadcasts";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "SmartHub Broadcasts",
                    NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription("SmartHub Network Announcements and Alerts");
                if (nm != null) {
                    nm.createNotificationChannel(channel);
                }
            }

            Notification.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder = new Notification.Builder(context, channelId);
            } else {
                builder = new Notification.Builder(context);
            }

            builder.setContentTitle(title)
                   .setContentText(message)
                   .setSmallIcon(android.R.drawable.ic_dialog_info)
                   .setAutoCancel(true);

            if (nm != null) {
                nm.notify((int) System.currentTimeMillis(), builder.build());
            }
        } catch (Exception ignored) {}
    }

    @JavascriptInterface
    public void setDeviceWallpaper(String imageUrl) {
        new Thread(() -> {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(6000);
                conn.setReadTimeout(10000);
                conn.connect();
                try (InputStream is = conn.getInputStream()) {
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    if (bitmap != null) {
                        WallpaperManager wm = WallpaperManager.getInstance(context);
                        wm.setBitmap(bitmap);
                        toast("Phone wallpaper updated successfully!");
                    } else {
                        toast("Could not decode image for wallpaper.");
                    }
                }
            } catch (Exception e) {
                toast("Wallpaper error: " + e.getMessage());
            }
        }).start();
    }

    @JavascriptInterface
    public void sendDialPlay(String ip, int port, String videoId) {
        new Thread(() -> {
            try {
                URL url = new URL("http://" + ip + ":" + port + "/ws/apps/YouTube");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(3000);
                conn.setDoOutput(true);
                byte[] data = ("v=" + videoId).getBytes();
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(data);
                }
                conn.getResponseCode();
            } catch (Exception ignored) {}
        }).start();
    }

    @JavascriptInterface
    public void sendDialStop(String ip, int port) {
        new Thread(() -> {
            try {
                URL url = new URL("http://" + ip + ":" + port + "/ws/apps/YouTube/run");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");
                conn.setConnectTimeout(3000);
                conn.getResponseCode();
            } catch (Exception ignored) {}
        }).start();
    }

    @JavascriptInterface
    public void openBrowser(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception ignored) {}
    }
}
