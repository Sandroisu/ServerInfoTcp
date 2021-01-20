package ru.slatinin.serverinfotcp;

import android.content.Context;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

public class DownloadPdf {
    private static final int MEGABYTE = 1024 * 1024;
    private final OnDownloadListener onDownloadListener;
    private Thread thread;
    private boolean isRunning;

    public DownloadPdf(OnDownloadListener onDownloadListener) {
        this.onDownloadListener = onDownloadListener;
    }

    public void downloadFile(String fileUrl, String fileName, Context context) {
        thread = new Thread(() -> {
            isRunning = true;
            try {
                File file = new File(context.getExternalFilesDir(null), fileName);
                URL url = new URL(fileUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.connect();
                if (urlConnection.getResponseCode() == 200) {
                    InputStream inputStream = urlConnection.getInputStream();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);

                    byte[] buffer = new byte[MEGABYTE];
                    int bufferLength = 0;
                    while ((bufferLength = inputStream.read(buffer)) > 0 && isRunning) {
                        fileOutputStream.write(buffer, 0, bufferLength);
                    }
                    fileOutputStream.close();
                    if (isRunning) {
                        onDownloadListener.onDownloadPdf(fileName);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                onDownloadListener.onDownloadPdfError();
            }
            isRunning = false;
        });
        thread.start();
    }

    public void view(Context context, String fileName) {
        File pdfFile = new File(context.getExternalFilesDir(null), fileName);
        Uri path = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", pdfFile);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setFlags(FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION);
        pdfIntent.setData(path);
        context.startActivity(pdfIntent);
    }

    public boolean isConnectionAvailable(Context context) {
        ConnectivityManager manager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    public void stopDownload() {
        if (thread != null) {
            thread.interrupt();
            isRunning = false;
        }
    }

    public interface OnDownloadListener {
        void onDownloadPdf(String fileName);

        void onDownloadPdfError();
    }
}
