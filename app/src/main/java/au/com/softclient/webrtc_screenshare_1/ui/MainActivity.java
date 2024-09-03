package au.com.softclient.webrtc_screenshare_1.ui;

import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import au.com.softclient.webrtc_screenshare_1.databinding.ActivityMainBinding;
import au.com.softclient.webrtc_screenshare_1.repository.MainRepository;
import au.com.softclient.webrtc_screenshare_1.service.WebrtcService;
import au.com.softclient.webrtc_screenshare_1.service.WebrtcServiceRepository;

import org.webrtc.MediaStream;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

//@AndroidEntryPoint
//public class MainActivity extends AppCompatActivity implements MainRepository.Listener {
//
//    private String username;
//    private ActivityMainBinding views;
//
//    @Inject
//    WebrtcServiceRepository webrtcServiceRepository;
//
//    private final int capturePermissionRequestCode = 1;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        views = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(views.getRoot());
//        init();
//    }
//
//    private void init() {
//        username = getIntent().getStringExtra("username");
//        if (username == null || username.isEmpty()) {
//            finish();
//            return;
//        }
//
//        WebrtcService.surfaceView = views.surfaceView;
//        WebrtcService.listener = this;
//        webrtcServiceRepository.startIntent(username);
//
//        views.requestBtn.setOnClickListener(v -> startScreenCapture());
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode != capturePermissionRequestCode) return;
//
//        WebrtcService.screenPermissionIntent = data;
//        webrtcServiceRepository.requestConnection(views.targetEt.getText().toString());
//    }
//
//    private void startScreenCapture() {
//        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getApplicationContext()
//                .getSystemService(Context.MEDIA_PROJECTION_SERVICE);
//
//        if (mediaProjectionManager != null) {
//            startActivityForResult(
//                    mediaProjectionManager.createScreenCaptureIntent(),
//                    capturePermissionRequestCode
//            );
//        }
//    }
//
//    @Override
//    public void onConnectionRequestReceived(String target) {
//        runOnUiThread(() -> views.apply {
//            views.notificationTitle.setText(target + " is requesting for connection");
//            ViewCompat.setIsVisible(views.notificationLayout, true);
//
//            views.notificationAcceptBtn.setOnClickListener(v -> {
//                webrtcServiceRepository.acceptCall(target);
//                ViewCompat.setIsVisible(views.notificationLayout, false);
//            });
//
//            views.notificationDeclineBtn.setOnClickListener(v -> ViewCompat.setIsVisible(views.notificationLayout, false));
//        });
//    }
//
//    @Override
//    public void onConnectionConnected() {
//        runOnUiThread(() -> views.apply {
//            ViewCompat.setIsVisible(views.requestLayout, false);
//            ViewCompat.setIsVisible(views.disconnectBtn, true);
//
//            views.disconnectBtn.setOnClickListener(v -> {
//                webrtcServiceRepository.endCallIntent();
//                restartUi();
//            });
//        });
//    }
//
//    @Override
//    public void onCallEndReceived() {
//        runOnUiThread(this::restartUi);
//    }
//
//    @Override
//    public void onRemoteStreamAdded(MediaStream stream) {
//        runOnUiThread(() -> {
//            ViewCompat.setIsVisible(views.surfaceView, true);
//            stream.videoTracks.get(0).addSink(views.surfaceView);
//        });
//    }
//
//    private void restartUi() {
//        views.apply {
//            ViewCompat.setIsVisible(views.disconnectBtn, false);
//            ViewCompat.setIsVisible(views.requestLayout, true);
//            ViewCompat.setIsVisible(views.notificationLayout, false);
//            ViewCompat.setIsVisible(views.surfaceView, false);
//        });
//    }
//}


@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements MainRepository.Listener {

    private String username;
    private ActivityMainBinding views;

    @Inject
    WebrtcServiceRepository webrtcServiceRepository;

    private ActivityResultLauncher<Intent> screenCaptureLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());

        // Register the ActivityResultLauncher
        screenCaptureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        WebrtcService.screenPermissionIntent = result.getData();
                        webrtcServiceRepository.requestConnection(views.targetEt.getText().toString());
                    }
                }
        );

        init();
    }

    private void init() {
        username = getIntent().getStringExtra("username");
        if (username == null || username.isEmpty()) {
            finish();
            return;
        }

        WebrtcService.surfaceView = views.surfaceView;
        WebrtcService.listener = this;
        webrtcServiceRepository.startIntent(username);

        views.requestBtn.setOnClickListener(v -> startScreenCapture());
    }

    private void startScreenCapture() {
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getApplicationContext()
                .getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        if (mediaProjectionManager != null) {
            Intent screenCaptureIntent = mediaProjectionManager.createScreenCaptureIntent();
            screenCaptureLauncher.launch(screenCaptureIntent);
        }
    }

    @Override
    public void onConnectionRequestReceived(String target) {
        runOnUiThread(() -> {
            views.notificationTitle.setText(target + " is requesting for connection");
            views.notificationLayout.setVisibility(View.VISIBLE); // Make it visible

            views.notificationAcceptBtn.setOnClickListener(v -> {
                webrtcServiceRepository.acceptCall(target);
                views.notificationLayout.setVisibility(View.GONE); // Hide the layout
            });

            views.notificationDeclineBtn.setOnClickListener(v -> views.notificationLayout.setVisibility(View.GONE));
        });
    }

    @Override
    public void onConnectionConnected() {
        runOnUiThread(() -> {
            views.requestLayout.setVisibility(View.GONE); // Hide the request layout
            views.disconnectBtn.setVisibility(View.VISIBLE); // Show the disconnect button

            views.disconnectBtn.setOnClickListener(v -> {
                webrtcServiceRepository.endCallIntent();
                restartUi();
            });
        });
    }

    @Override
    public void onCallEndReceived() {
        runOnUiThread(this::restartUi);
    }

    @Override
    public void onRemoteStreamAdded(MediaStream stream) {
        runOnUiThread(() -> {
            views.surfaceView.setVisibility(View.VISIBLE); // Make the surface view visible
            stream.videoTracks.get(0).addSink(views.surfaceView);
        });
    }

    private void restartUi() {
        views.disconnectBtn.setVisibility(View.GONE); // Hide the disconnect button
        views.requestLayout.setVisibility(View.VISIBLE); // Show the request layout
        views.notificationLayout.setVisibility(View.GONE); // Hide the notification layout
        views.surfaceView.setVisibility(View.GONE); // Hide the surface view
    }
}