package au.com.softclient.webrtc_screenshare_1.service;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import au.com.softclient.webrtc_screenshare_1.R;
import au.com.softclient.webrtc_screenshare_1.repository.MainRepository;

import org.webrtc.MediaStream;
import org.webrtc.SurfaceViewRenderer;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

//@AndroidEntryPoint
//public class WebrtcService extends Service implements MainRepository.Listener {
//
//    public static Intent screenPermissionIntent = null;
//    public static SurfaceViewRenderer surfaceView = null;
//    public static MainRepository.Listener listener = null;
//
//    @Inject
//    MainRepository mainRepository;
//
//    private NotificationManager notificationManager;
//    private String username;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        notificationManager = getSystemService(NotificationManager.class);
//        mainRepository.setListener(this);
//    }
//
//    @Override
//    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
//        if (intent != null) {
//            String action = intent.getAction();
//            if (action != null) {
//                switch (action) {
//                    case "StartIntent":
//                        this.username = intent.getStringExtra("username");
//                        if (username != null && surfaceView != null) {
//                            mainRepository.init(username, surfaceView);
//                            startServiceWithNotification();
//                        }
//                        break;
//                    case "StopIntent":
//                        stopMyService();
//                        break;
//                    case "EndCallIntent":
//                        mainRepository.sendCallEndedToOtherPeer();
//                        mainRepository.onDestroy();
//                        stopMyService();
//                        break;
//                    case "AcceptCallIntent":
//                        String target = intent.getStringExtra("target");
//                        if (target != null) {
//                            mainRepository.startCall(target);
//                        }
//                        break;
//                    case "RequestConnectionIntent":
//                        target = intent.getStringExtra("target");
//                        if (target != null && screenPermissionIntent != null && surfaceView != null) {
//                            mainRepository.setPermissionIntentToWebrtcClient(screenPermissionIntent);
//                            mainRepository.startScreenCapturing(surfaceView);
//                            mainRepository.sendScreenShareConnection(target);
//                        }
//                        break;
//                }
//            }
//        }
//        return START_STICKY;
//    }
//
//    private void stopMyService() {
//        mainRepository.onDestroy();
//        stopSelf();
//        notificationManager.cancelAll();
//    }
//
//    @SuppressLint("ForegroundServiceType")
//    private void startServiceWithNotification() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel notificationChannel = new NotificationChannel(
//                    "channel1", "foreground", NotificationManager.IMPORTANCE_HIGH
//            );
//            notificationManager.createNotificationChannel(notificationChannel);
//            Notification notification = new NotificationCompat.Builder(this, "channel1")
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .build();
//
//            startForeground(1, notification);
//        }
//    }
//
//    @Override
//    public void onConnectionRequestReceived(String target) {
//        if (listener != null) {
//            listener.onConnectionRequestReceived(target);
//        }
//    }
//
//    @Override
//    public void onConnectionConnected() {
//        if (listener != null) {
//            listener.onConnectionConnected();
//        }
//    }
//
//    @Override
//    public void onCallEndReceived() {
//        if (listener != null) {
//            listener.onCallEndReceived();
//        }
//        stopMyService();
//    }
//
//    @Override
//    public void onRemoteStreamAdded(MediaStream stream) {
//        if (listener != null) {
//            listener.onRemoteStreamAdded(stream);
//        }
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//}
//




@AndroidEntryPoint
public class WebrtcService extends Service implements MainRepository.Listener {

    public static Intent screenPermissionIntent = null;
    public static SurfaceViewRenderer surfaceView = null;
    public static MainRepository.Listener listener = null;

    @Inject
    MainRepository mainRepository;

    private NotificationManager notificationManager;
    private String username;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = getSystemService(NotificationManager.class);
        mainRepository.setListener(this);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case "StartIntent":
                        this.username = intent.getStringExtra("username");
                        if (username != null && surfaceView != null) {
                            mainRepository.init(username, surfaceView);
                            startServiceWithNotification();
                        }
                        break;
                    case "StopIntent":
                        stopMyService();
                        break;
                    case "EndCallIntent":
                        mainRepository.sendCallEndedToOtherPeer();
                        mainRepository.onDestroy();
                        stopMyService();
                        break;
                    case "AcceptCallIntent":
                        String target = intent.getStringExtra("target");
                        if (target != null) {
                            mainRepository.startCall(target);
                        }
                        break;
                    case "RequestConnectionIntent":
                        target = intent.getStringExtra("target");
                        if (target != null && screenPermissionIntent != null && surfaceView != null) {
                            mainRepository.setPermissionIntentToWebrtcClient(screenPermissionIntent);
                            mainRepository.startScreenCapturing(surfaceView);
                            mainRepository.sendScreenShareConnection(target);
                        }
                        break;
                }
            }
        }
        return START_STICKY;
    }

    private void stopMyService() {
        mainRepository.onDestroy();
        stopSelf();
        notificationManager.cancelAll();
    }

    @SuppressLint({"MissingPermission", "ForegroundServiceType"})
    private void startServiceWithNotification0() {
        String channelId = "media_projection_channel";
        String channelName = "Media Projection Service";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Screen Sharing Active")
                .setContentText("Your screen is being shared")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)  // Make the notification persistent
                .build();

        // Specify the type of foreground service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION);
        }
    }

    @SuppressLint({"MissingPermission", "ForegroundServiceType"})
    private void startServiceWithNotification() {
        String channelId = "media_projection_channel";
        String channelName = "Media Projection Service";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Screen Sharing Active")
                .setContentText("Your screen is being shared")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)  // Make the notification persistent
                .build();

        // For API level 29 and above, use the media projection foreground service type
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {  // Q is Android 10, API level 29
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION);
        } else {
            // For devices running below Android 10, just start foreground without the specific type
            startForeground(1, notification);
        }
    }


    @Override
    public void onConnectionRequestReceived(String target) {
        if (listener != null) {
            listener.onConnectionRequestReceived(target);
        }
    }

    @Override
    public void onConnectionConnected() {
        if (listener != null) {
            listener.onConnectionConnected();
        }
    }

    @Override
    public void onCallEndReceived() {
        if (listener != null) {
            listener.onCallEndReceived();
        }
        stopMyService();
    }

    @Override
    public void onRemoteStreamAdded(MediaStream stream) {
        if (listener != null) {
            listener.onRemoteStreamAdded(stream);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}