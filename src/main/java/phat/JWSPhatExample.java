package phat;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class JWSPhatExample extends WebSocketClient {

    private static Logger logger = LoggerFactory.getLogger(JWSPhatExample.class);


    public JWSPhatExample(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public JWSPhatExample(URI serverURI) {
        super(serverURI);
    }

    public JWSPhatExample(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        send("Hello, it is me. Chuki :)");
        logger.info("opened connection");
        // if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
    }

    @Override
    public void onMessage(String message) {
        logger.info("received: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // The codecodes are documented in class org.java_websocket.framing.CloseFrame
        logger.info("Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
        // if the error is fatal then onClose will be called additionally
    }

    public static void main(String[] args) throws URISyntaxException {
        JWSPhatExample c = new JWSPhatExample(new URI("ws://localhost:8090")); // more about drafts here: http://github.com/TooTallNate/Java-WebSocket/wiki/Drafts
        c.connect();
    }

}
