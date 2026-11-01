package com.smarthub.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Vibrator;
import android.webkit.JavascriptInterface;
import android.widget.Toast;
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
