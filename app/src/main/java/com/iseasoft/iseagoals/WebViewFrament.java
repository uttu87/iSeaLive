package com.iseasoft.iseagoals;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.iseasoft.iseagoals.api.ISeaLiveAPI;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

@SuppressLint("ValidFragment")
public class WebViewFrament extends Fragment {

    public static final String TAG = WebViewFrament.class.getSimpleName();

    public static final String WEB_URL = "url";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String TITLE = "title";

    Unbinder unbinder;
    @BindView(R.id.webview)
    WebView webView;
    @BindView(R.id.progress)
    ProgressBar progress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String url = ISeaLiveAPI.getBaseURLDev();
        Bundle bundle = getArguments();
        if (bundle != null) {
            url = bundle.getString(WEB_URL);
        }
        loadWeb(url);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void loadWeb(String url) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUserAgentString(Utils.getSpecialUserAgent(getActivity()));
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setWebChromeClient(new WebChromeClient());
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                removingHeader(view);
                progress.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                if (url.contains("match")) {
                    String matchUrl = ISeaLiveAPI.getInstance().getMatchURLFromWeb(url);
                    ((AdminActivity) getActivity()).updateMatchUrl(matchUrl);
                } else if (url.contains("youtube")) {
                    String videoId = getYoutubeVideoId(url);
                    ((AdminActivity) getActivity()).updateMatchUrl(videoId);
                }
            }

            private void removingHeader(WebView view) {
                String script = "javascript:(function() {document.getElementsByTagName('header')[0].style['display']='none'})()";
                view.loadUrl(script);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
        });
        loadUrl(url, null);
    }

    private String getYoutubeVideoId(String url) {
        if (url.contains("=")) {
            int index = url.indexOf("=");
            return url.substring(index + 1, index + 12);
        } else {
            return url;
        }
    }


    private void loadUrl(String url, String accessToken) {
        if (accessToken != null) {
            HashMap<String, String> header = new HashMap<>();
            header.put("X-Access-Token", accessToken);
            webView.loadUrl(url, header);
        } else {
            webView.loadUrl(url);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
