package com.sctw.bonniedraw.fragment;


import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.PxDpConvert;
import com.sctw.bonniedraw.widget.ToastUtil;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class PublicFragment extends DialogFragment {
    // 1.關於BONNIEDRAW  2.隱私權條款  3.使用條款
    TextView mTextViewTitle;
    ImageButton mImgBtnBack;
    WebView mWebView;
    ProgressBar mProgressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_public, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTextViewTitle = view.findViewById(R.id.textView_public_fragment_title);
        mImgBtnBack = view.findViewById(R.id.imgBtn_public_back);
        mWebView = view.findViewById(R.id.public_webview);
        mProgressBar = view.findViewById(R.id.progressBar_public);
        mProgressBar.bringToFront();
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                ToastUtil.createToastIsCheck(getContext(), getString(R.string.uc_connection_failed), false, PxDpConvert.getSystemHight(getContext()) / 3);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                // this will ignore the Ssl error and will go forward to your site
                handler.proceed();
            }
        });
        Bundle bundle = getArguments();
        if (bundle != null) {
            String lang = Locale.getDefault().getLanguage();
            if(lang.equals("zh"))
                lang = "zh-tw";
            int item = bundle.getInt("type");
            String title = "" , url_link="";
            switch (item) {
                case 0:
                    title = getString(R.string.u06_04_about_bonniedraw);
                    url_link = GlobalVariable.HTML_ABOUT_LINK + "?lang=" + lang;
                    break;
                case 1:
                    title = getString(R.string.u06_04_privacy_policy);
                    url_link = GlobalVariable.HTML_PRIVACY_LINK + "?lang=" + lang;
                    break;
                case 2:
                    title = getString(R.string.u06_04_terms_of_service);
                    url_link = GlobalVariable.HTML_TERMS_LINK + "?lang=" + lang;
                    break;
            }
            mWebView.loadUrl(url_link);
            mTextViewTitle.setText(title);
        }

        mImgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PublicFragment.this.dismiss();
            }
        });
    }
}
