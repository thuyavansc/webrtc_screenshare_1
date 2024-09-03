package au.com.softclient.webrtc_screenshare_1.webrtc;


import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.RtpReceiver;

public class MyPeerObserver implements PeerConnection.Observer {

    @Override
    public void onSignalingChange(PeerConnection.SignalingState signalingState) {
        // Override this method if needed
    }

    @Override
    public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
        // Override this method if needed
    }

    @Override
    public void onIceConnectionReceivingChange(boolean receiving) {
        // Override this method if needed
    }

    @Override
    public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
        // Override this method if needed
    }

    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {
        // Override this method if needed
    }

    @Override
    public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
        // Override this method if needed
    }

    @Override
    public void onAddStream(MediaStream mediaStream) {
        // Override this method if needed
    }

    @Override
    public void onRemoveStream(MediaStream mediaStream) {
        // Override this method if needed
    }

    @Override
    public void onDataChannel(DataChannel dataChannel) {
        // Override this method if needed
    }

    @Override
    public void onRenegotiationNeeded() {
        // Override this method if needed
    }

    @Override
    public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
        // Override this method if needed
    }
}
