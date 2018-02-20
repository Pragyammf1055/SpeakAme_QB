package com.speakameqb.Activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.speakameqb.R;

public class ReferFreindActivity extends AppCompatActivity {

    private static final String TAG = "ReferFreindActivity";
    private WebView mWebView;
    private ProgressBar pDialog;
    private String description = "Cannot laod Url...!!!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer_a_freind);
        initView();
    }

    private void initView() {

        mWebView = (WebView) findViewById(R.id.webView);

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        pDialog = (ProgressBar) findViewById(R.id.progressBar);
        pDialog.setMax(100);
//         Force links and redirects to open in the WebView instead of in a browser
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(TAG, "Processing webview url click...");
                view.loadUrl("http://www.speakameqb.com/");
                ReferFreindActivity.this.pDialog.setProgress(0);
//                view.loadUrl("http://www.google.com");
                return true;
            }

            /*@Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.i(TAG, "Processing webview url click...");
                view.loadUrl("http://percolate.blogtalkradio.com/offsiteplayer?hostId=1017901");
                return true;
            }
*/
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                Log.i(TAG, "Finished loading URL: " + url);

            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e(TAG, "Error: " + description);
                Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();

            }
        });

        mWebView.loadUrl("http://www.google.com");

        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                Log.i(TAG, " setOnKeyListener...");
                if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
                    mWebView.goBack();
                    return true;
                }
                return false;
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {

            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 && pDialog.getVisibility() == ProgressBar.GONE) {
                    pDialog.setVisibility(ProgressBar.VISIBLE);
//                    txtview.setVisibility(View.VISIBLE);
                }

                pDialog.setProgress(progress);
                if (progress == 100) {
                    pDialog.setVisibility(ProgressBar.GONE);
//                    txtview.setVisibility(View.GONE);
                }
            }

        });
    }
}
