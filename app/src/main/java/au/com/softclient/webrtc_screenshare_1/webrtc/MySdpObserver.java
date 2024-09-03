package au.com.softclient.webrtc_screenshare_1.webrtc;


import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

public class MySdpObserver implements SdpObserver {

    @Override
    public void onCreateSuccess(SessionDescription sessionDescription) {
        // Override this method if needed
    }

    @Override
    public void onSetSuccess() {
        // Override this method if needed
    }

    @Override
    public void onCreateFailure(String error) {
        // Override this method if needed
    }

    @Override
    public void onSetFailure(String error) {
        // Override this method if needed
    }
}
