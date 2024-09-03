package au.com.softclient.webrtc_screenshare_1.socket;


import android.util.Log;

import au.com.softclient.webrtc_screenshare_1.utils.DataModel;
import au.com.softclient.webrtc_screenshare_1.utils.DataModelType;

import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Singleton
public class SocketClient {

    private final Gson gson;
    private String username;

    private static WebSocketClient webSocket;

    private Listener listener;
    //private String webSocketUrl = "ws://10.0.2.2:3000";
    private String webSocketUrl = "ws://192.168.8.105:3000";

    @Inject
    public SocketClient(Gson gson) {
        this.gson = gson;
    }

    public void init(String username) {
        this.username = username;

        try {
            webSocket = new WebSocketClient(new URI(webSocketUrl)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    sendMessageToSocket(new DataModel(DataModelType.SignIn, username, null, null));
                }

                @Override
                public void onMessage(String message) {
                    DataModel model = null;
                    try {
                        model = gson.fromJson(message, DataModel.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.d("TAG", "onMessage: " + model);
                    if (model != null && listener != null) {
                        listener.onNewMessageReceived(model);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                    scheduler.schedule(() -> init(username), 5, TimeUnit.SECONDS);
                }

                @Override
                public void onError(Exception ex) {
                    ex.printStackTrace();
                }
            };
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if (webSocket != null) {
            webSocket.connect();
        }
    }

    public void sendMessageToSocket(Object message) {
        try {
            if (webSocket != null) {
                webSocket.send(gson.toJson(message));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        if (webSocket != null) {
            webSocket.close();
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onNewMessageReceived(DataModel model);
    }
}
