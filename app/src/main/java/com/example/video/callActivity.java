package com.example.video;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class callActivity extends AppCompatActivity {

    Button call;
    EditText endUser;
    ImageView accept, reject, video, audio;
    WebView webView;
    ProgressBar progressBar;
    RelativeLayout callLayout, dataLayout, featureLayout;
    String uniqueId;


    String userName, endUserName;
    Boolean isPeerConnected = false;
    DatabaseReference firebaseReference = FirebaseDatabase.getInstance().getReference("users");
    Boolean isAudio = true;
    Boolean isVideo = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        accept = findViewById(R.id.acceptCall);
        reject = findViewById(R.id.endCall);
        audio = findViewById(R.id.audio);
        video = findViewById(R.id.video);
        endUser = findViewById(R.id.endUserName);
        call = findViewById(R.id.button);
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        callLayout = findViewById(R.id.callLayout);
        dataLayout = findViewById(R.id.dataLayout);
        featureLayout = findViewById(R.id.featureLayout);


        userName = getIntent().getStringExtra("userName");

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                endUserName = endUser.getText().toString();
                sendCallRequest();
            }
        });
        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAudio = !isAudio;
                callJsData("javascript:toggleAudio(\"${isAudio}\")");
                if (!isAudio) {
                    audio.setImageResource(R.drawable.ic_baseline_mic_off_24);
                } else {
                    audio.setImageResource(R.drawable.ic_baseline_mic_24);
                }
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVideo = !isVideo;
                callJsData("javascript:toggleVideo(\"${isVideo}\")");
                if (!isVideo) {
                    video.setImageResource(R.drawable.ic_baseline_mic_off_24);
                } else {
                    video.setImageResource(R.drawable.ic_baseline_mic_24);
                }
            }
        });


    }

    private void sendCallRequest() {
        if (!isPeerConnected) {
            Toast.makeText(this, "you are not Connected Check your Internet Connection", Toast.LENGTH_SHORT).show();
        } else {
            firebaseReference.child(endUserName).child("incoming").setValue(userName);
            firebaseReference.child(endUserName).child("isAvailable").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue().toString() == "true") {
                        listenForConnection();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    private void listenForConnection() {
    firebaseReference.child(endUserName).child("connId").addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(snapshot.getValue()!=null){
                switchToControllLayout();
                callJsData("javascript:startCall(\"${snapshot.value}\")");
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });
    }

    @SuppressLint("SetJavaScriptEnabled")
    void setupWebView() {
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onPermissionRequest(PermissionRequest request) {
                request.grant(request.getResources());
                super.onPermissionRequest(request);
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);

        webView.addJavascriptInterface(new javaScriptInterface(callActivity.this), "Android");

        loadVideoCall();
    }

    private void loadVideoCall() {
        String filePath = "file:android_asset/call.html";
        webView.loadUrl(filePath);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                initializePeer();
                super.onPageFinished(view, url);
            }
        });


    }

    private void initializePeer() {
        uniqueId = uniqueId();
        callJsData("javascript:init(\"${uniqueId}\")");

        firebaseReference.child(userName).child("incoming").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                onCallRquest(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onCallRquest(String caller) {
        if (caller != null) {
            callLayout.setVisibility(View.VISIBLE);
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firebaseReference.child(userName).child("connId").setValue(uniqueId);
                    firebaseReference.child("isAvailable").setValue(true);
                    callLayout.setVisibility(View.GONE);

                    switchToControllLayout();
        }
            });

            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    firebaseReference.child(userName).child("incoming").setValue(null);
                    callLayout.setVisibility(View.GONE);
                }
            });


        }

    }

    private void switchToControllLayout() {
        dataLayout.setVisibility(View.GONE);
        featureLayout.setVisibility(View.VISIBLE);

    }

    void callJsData(String string) {

        webView.evaluateJavascript(string, null);


    }

    String uniqueId() {
        return UUID.randomUUID().toString();

    }

    public void onPeerConnected() {
        isPeerConnected = true;
    }

    @Override
    public void onBackPressed() {
        finish();
//        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        firebaseReference.child(userName).setValue(null);
        webView.loadUrl("about:blank");

        super.onDestroy();
    }
}