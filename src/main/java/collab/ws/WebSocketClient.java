package collab.ws;

import javafx.application.Platform;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

public class WebSocketClient implements WebSocket.Listener {

    private WebSocket socket;
    private Consumer<String> onMessageHandler;

    public void connect(String url, Consumer<String> onMessage) {
        this.onMessageHandler = onMessage;

        HttpClient.newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(URI.create(url), this)
                .thenAccept(ws -> socket = ws);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        String message = data.toString();

        if (onMessageHandler != null) {
            Platform.runLater(() -> onMessageHandler.accept(message));
        }

        webSocket.request(1);
        return null;
    }
}
