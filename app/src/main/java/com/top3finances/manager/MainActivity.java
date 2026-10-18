package com.top3finances.manager;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.net.Uri;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.view.Gravity;

public class MainActivity extends Activity {
private static final String HOME_URL = "https://top3finances.work.gd/";
private WebView webView;
private TextView errorOverlay;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getWindow().setStatusBarColor(Color.rgb(22, 34, 58));
    getWindow().setNavigationBarColor(Color.rgb(22, 34, 58));

    FrameLayout root = new FrameLayout(this);
    root.setBackgroundColor(Color.rgb(244, 239, 225));

    webView = new WebView(this);
    WebSettings settings = webView.getSettings();

    settings.setJavaScriptEnabled(true);
    settings.setDomStorageEnabled(true);
    settings.setDatabaseEnabled(true);
    settings.setCacheMode(WebSettings.LOAD_DEFAULT);
    settings.setSupportZoom(true);
    settings.setBuiltInZoomControls(false);
    settings.setDisplayZoomControls(false);
    settings.setAllowFileAccess(false);
    settings.setAllowContentAccess(true);
    settings.setJavaScriptCanOpenWindowsAutomatically(false);
    settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);

    CookieManager cookies = CookieManager.getInstance();
    cookies.setAcceptCookie(true);
    cookies.setAcceptThirdPartyCookies(webView, false);

    webView.setWebChromeClient(new WebChromeClient());

    webView.setWebViewClient(new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(
                WebView view, WebResourceRequest request) {

            Uri uri = request.getUrl();
            String scheme = uri.getScheme();
            String host = uri.getHost();

            if ("https".equalsIgnoreCase(scheme)
                    && host != null
                    && (host.equals("top3finances.work.gd")
                    || host.endsWith(".top3finances.work.gd"))) {
                return false;
            }

            return true;
        }

        @Override
        public void onReceivedError(
                WebView view,
                WebResourceRequest request,
                android.webkit.WebResourceError error) {

            super.onReceivedError(view, request, error);

            if (request.isForMainFrame() && errorOverlay != null) {
                errorOverlay.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            if (errorOverlay != null) {
                errorOverlay.setVisibility(View.GONE);
            }
        }
    });

    root.addView(webView, new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT));

    errorOverlay = new TextView(this);
    errorOverlay.setText(
            "Unable to load Top3 Finances. Check your internet connection and tap to retry.");
    errorOverlay.setTextColor(Color.rgb(22, 34, 58));
    errorOverlay.setTextSize(16);
    errorOverlay.setGravity(Gravity.CENTER);
    errorOverlay.setPadding(36, 24, 36, 24);
    errorOverlay.setBackgroundColor(Color.rgb(244, 239, 225));
    errorOverlay.setVisibility(View.GONE);

    errorOverlay.setOnClickListener(v -> {
        errorOverlay.setVisibility(View.GONE);
        webView.reload();
    });

    root.addView(errorOverlay, new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT));

    setContentView(root);

    if (savedInstanceState == null) {
        webView.loadUrl(HOME_URL);
    } else {
        webView.restoreState(savedInstanceState);
    }
}

@Override
public void onBackPressed() {
    if (webView != null && webView.canGoBack()) {
        webView.goBack();
    } else {
        super.onBackPressed();
    }
}

@Override
protected void onSaveInstanceState(Bundle outState) {
    if (webView != null) {
        webView.saveState(outState);
    }

    super.onSaveInstanceState(outState);
}

@Override
protected void onDestroy() {
    if (webView != null) {
        webView.stopLoading();
        webView.destroy();
    }

    super.onDestroy();
}

      }
