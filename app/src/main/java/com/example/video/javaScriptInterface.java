package com.example.video;

import android.webkit.JavascriptInterface;

public class javaScriptInterface  {

    callActivity activity =new callActivity();

    public javaScriptInterface(callActivity activity) {
    }

@JavascriptInterface
    public void onPeerConnected(){
        activity.onPeerConnected();
    }

}
