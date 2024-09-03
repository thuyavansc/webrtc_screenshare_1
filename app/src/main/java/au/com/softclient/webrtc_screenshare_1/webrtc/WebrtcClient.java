package au.com.softclient.webrtc_screenshare_1.webrtc;


import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import au.com.softclient.webrtc_screenshare_1.utils.DataModel;
import au.com.softclient.webrtc_screenshare_1.utils.DataModelType;

import com.google.gson.Gson;

import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.ScreenCapturerAndroid;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import javax.inject.Inject;
import java.util.List;

public class WebrtcClient {

    private final Context context;
    private final Gson gson;

    private String username;
    private PeerConnection.Observer observer;
    private SurfaceViewRenderer localSurfaceView;
    private Listener listener;
    private Intent permissionIntent;

    private PeerConnection peerConnection;
    private final EglBase.Context eglBaseContext = EglBase.create().getEglBaseContext();
    private final PeerConnectionFactory peerConnectionFactory;
    private final MediaConstraints mediaConstraint;
    private final List<PeerConnection.IceServer> iceServer;
    private VideoCapturer screenCapturer;
    private final VideoTrack localVideoTrack;
    private final String localTrackId = "local_track";
    private final String localStreamId = "local_stream";
    private MediaStream localStream;

    private int screenWidth;
    private int screenHeight;
    private int frameRate = 30; // Set a default frame rate, e.g., 30 frames per second


    @Inject
    public WebrtcClient(Context context, Gson gson) {
        this.context = context;
        this.gson = gson;

        mediaConstraint = new MediaConstraints();
        mediaConstraint.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));

        iceServer = List.of(
                PeerConnection.IceServer.builder("turn:openrelay.metered.ca:443?transport=tcp")
                        .setUsername("openrelayproject")
                        .setPassword("openrelayproject")
                        .createIceServer()
        );

        peerConnectionFactory = createPeerConnectionFactory();
        localVideoTrack = peerConnectionFactory.createVideoTrack(localTrackId + "_video", peerConnectionFactory.createVideoSource(false));

        initPeerConnectionFactory(context);
    }

    public void initializeWebrtcClient(String username, SurfaceViewRenderer view, PeerConnection.Observer observer) {
        this.username = username;
        this.observer = observer;
        peerConnection = createPeerConnection(observer);
        initSurfaceView(view);
    }

    public void setPermissionIntent(Intent intent) {
        this.permissionIntent = intent;
    }

    private void initSurfaceView(SurfaceViewRenderer view) {
        this.localSurfaceView = view;
        view.setMirror(false);
        view.setEnableHardwareScaler(true);
        view.init(eglBaseContext, null);
    }

//    public void startScreenCapturing0(SurfaceViewRenderer view) {
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        WindowManager windowsManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        if (windowsManager != null) {
//            windowsManager.getDefaultDisplay().getMetrics(displayMetrics);
//        }
//
//        int screenWidthPixels = displayMetrics.widthPixels;
//        int screenHeightPixels = displayMetrics.heightPixels;
//
//        SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create(Thread.currentThread().getName(), eglBaseContext);
//
//        screenCapturer = createScreenCapturer();
//        if (screenCapturer != null) {
//            screenCapturer.initialize(surfaceTextureHelper, context, peerConnectionFactory.createVideoSource(false).getCapturerObserver());
//            screenCapturer.startCapture(screenWidthPixels, screenHeightPixels, 15);
//
//            localVideoTrack.addSink(view);
//            localStream = peerConnectionFactory.createLocalMediaStream(localStreamId);
//            localStream.addTrack(localVideoTrack);
//            peerConnection.addStream(localStream);
//        }
//    }

    private void initializeScreenDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
    }


    public void startScreenCapturing(SurfaceViewRenderer surfaceView) {
        initializeScreenDimensions(); // Initialize screen dimensions

        VideoCapturer videoCapturer = createScreenCapturer();
        VideoSource videoSource = peerConnectionFactory.createVideoSource(false);

        SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBaseContext);
        videoCapturer.initialize(surfaceTextureHelper, context, videoSource.getCapturerObserver());
        videoCapturer.startCapture(screenWidth, screenHeight, frameRate);

        VideoTrack videoTrack = peerConnectionFactory.createVideoTrack("videoTrack", videoSource);
        MediaStream mediaStream = peerConnectionFactory.createLocalMediaStream("localStream");
        mediaStream.addTrack(videoTrack);

        if (peerConnection != null) {
            peerConnection.addStream(mediaStream);
        }

        videoTrack.addSink(surfaceView);
    }



    private VideoCapturer createScreenCapturer() {
        return new ScreenCapturerAndroid(permissionIntent, new MediaProjection.Callback() {
            @Override
            public void onStop() {
                super.onStop();
                Log.d("TAG", "onStop: stopped screen casting permission");
            }
        });
    }

    private void initPeerConnectionFactory(Context application) {
        PeerConnectionFactory.InitializationOptions options = PeerConnectionFactory.InitializationOptions.builder(application)
                .setEnableInternalTracer(true)
                .setFieldTrials("WebRTC-H264HighProfile/Enabled/")
                .createInitializationOptions();
        PeerConnectionFactory.initialize(options);
    }

    private PeerConnectionFactory createPeerConnectionFactory() {
        return PeerConnectionFactory.builder()
                .setVideoDecoderFactory(new DefaultVideoDecoderFactory(eglBaseContext))
                .setVideoEncoderFactory(new DefaultVideoEncoderFactory(eglBaseContext, true, true))
                .setOptions(new PeerConnectionFactory.Options())
                .createPeerConnectionFactory();
    }

    private PeerConnection createPeerConnection(PeerConnection.Observer observer) {
        return peerConnectionFactory.createPeerConnection(iceServer, observer);
    }

    public void call(String target) {
        peerConnection.createOffer(new MySdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription desc) {
                super.onCreateSuccess(desc);
                peerConnection.setLocalDescription(new MySdpObserver() {
                    @Override
                    public void onSetSuccess() {
                        super.onSetSuccess();
                        if (listener != null) {
                            listener.onTransferEventToSocket(new DataModel(DataModelType.Offer, username, target, desc.description));
                        }
                    }
                }, desc);
            }
        }, mediaConstraint);
    }

    public void answer(String target) {
        peerConnection.createAnswer(new MySdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription desc) {
                super.onCreateSuccess(desc);
                peerConnection.setLocalDescription(new MySdpObserver() {
                    @Override
                    public void onSetSuccess() {
                        super.onSetSuccess();
                        if (listener != null) {
                            listener.onTransferEventToSocket(new DataModel(DataModelType.Answer, username, target, desc.description));
                        }
                    }
                }, desc);
            }
        }, mediaConstraint);
    }

    public void onRemoteSessionReceived(SessionDescription sessionDescription) {
        peerConnection.setRemoteDescription(new MySdpObserver(), sessionDescription);
    }

    public void addIceCandidate(IceCandidate iceCandidate) {
        peerConnection.addIceCandidate(iceCandidate);
    }

    public void sendIceCandidate(IceCandidate candidate, String target) {
        addIceCandidate(candidate);
        if (listener != null) {
            listener.onTransferEventToSocket(new DataModel(DataModelType.IceCandidates, username, target, gson.toJson(candidate)));
        }
    }

    public void closeConnection() {
        try {
            if (screenCapturer != null) {
                screenCapturer.stopCapture();
                screenCapturer.dispose();
            }
            if (localStream != null) {
                localStream.dispose();
            }
            if (peerConnection != null) {
                peerConnection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void restart() {
        closeConnection();
        if (localSurfaceView != null) {
            localSurfaceView.clearImage();
            localSurfaceView.release();
            initializeWebrtcClient(username, localSurfaceView, observer);
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onTransferEventToSocket(DataModel data);
    }
}
