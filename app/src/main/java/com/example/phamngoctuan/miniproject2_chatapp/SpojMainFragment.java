package com.example.phamngoctuan.miniproject2_chatapp;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;



/**
 * Created by phamngoctuan on 29/05/2016.
 */
public class SpojMainFragment extends Fragment {
    String _link;
    WebView _wvSpoj;

    SpojMainFragment(String l)
    {
        _link = l;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.spoj_fragment, container, false);

        _wvSpoj = (WebView) rootView.findViewById(R.id.wv_main_spoj);
        _wvSpoj.getSettings().setUserAgentString("Android");
        _wvSpoj.getSettings().setJavaScriptEnabled(true);
        _wvSpoj.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        _wvSpoj.setWebViewClient(new WebViewClient());

        _wvSpoj.loadUrl(_link);
        return rootView;
    }
}
