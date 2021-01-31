package ru.slatinin.serverinfotcp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class DownloadPdfView extends RelativeLayout implements DownloadPdf.OnDownloadListener {
    private final ImageView ivPdf;
    private final ImageView ivNoConnection;
    private final DownloadPdf downloadPdf;
    private ProgressBar progressBar;
    private Activity activity;
    private final Context context;
    private OnCloseDialogListener onCloseDialogListener;

    public DownloadPdfView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
        downloadPdf = new DownloadPdf(this);
        @SuppressLint("CustomViewStyleable") TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.DownloadPdf, 0, 0);

        setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.download_pdf_component, this, true);

        ivPdf = findViewById(R.id.dc_pdf);
        ivNoConnection = findViewById(R.id.dc_no_connection);
        a.recycle();


    }

    public void setUrl(String url, String fileName) {
        ivPdf.setOnClickListener(v -> {
            if (downloadPdf.isConnectionAvailable(context)) {
                if (onCloseDialogListener != null) {
                    onCloseDialogListener.onCloseDialog();
                }
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                progressBar = new ProgressBar(context);
                if (activity != null) {
                    activity.addContentView(progressBar, layoutParams);
                    progressBar.setBackground(ContextCompat.getDrawable(context, R.drawable.progress_background));
                }
                progressBar.setOnClickListener(v1 -> {
                    downloadPdf.stopDownload();
                    removeProgress();
                });
                downloadPdf.downloadFile(url, fileName, context);
            } else {
                ivNoConnection.setVisibility(VISIBLE);
                ivPdf.setVisibility(GONE);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> {
                    ivNoConnection.setVisibility(GONE);
                    ivPdf.setVisibility(VISIBLE);
                }, 1000);
            }
        });
    }

    @Override
    public void onDownloadPdf(String fileName) {
        if (activity != null) {
            activity.runOnUiThread(() -> {
                downloadPdf.view(activity, fileName);
                removeProgress();
            });
        }
    }

    @Override
    public void onDownloadPdfError() {
        if (activity != null) {
            activity.runOnUiThread(() -> {
                removeProgress();
                Toast.makeText(activity, "Ошибка скачивания pdf файла", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void removeProgress() {
        if (progressBar != null) {
            ViewGroup vg = (ViewGroup) progressBar.getParent();
            if (vg != null) {
                vg.removeView(progressBar);
            }
        }
    }

    public void setOnCloseDialogListener(OnCloseDialogListener onCloseDialogListener) {
        this.onCloseDialogListener = onCloseDialogListener;
    }

    public interface OnCloseDialogListener {
        void onCloseDialog();
    }
}
