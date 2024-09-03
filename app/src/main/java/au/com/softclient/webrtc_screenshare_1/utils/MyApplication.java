package au.com.softclient.webrtc_screenshare_1.utils;


import android.app.Application;

import org.webrtc.PeerConnectionFactory;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MyApplication extends Application {
    // No additional implementation is needed here.
    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize PeerConnectionFactory
        PeerConnectionFactory.InitializationOptions initializationOptions =
                PeerConnectionFactory.InitializationOptions.builder(this)
                        .setEnableInternalTracer(true)
                        .createInitializationOptions();
        PeerConnectionFactory.initialize(initializationOptions);
    }
}
