package collab.controller;

import collab.ws.WebSocketClient;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class WebSocketLogController {

    @FXML
    private TextArea logArea;

    private WebSocketClient client = new WebSocketClient();

    @FXML
    public void initialize() {
        logArea.setEditable(false);

        client.connect("ws://localhost:8080/ws", msg -> {
            appendMessage(format(msg));
        });
    }

    private void appendMessage(String text) {
        logArea.appendText(text + "\n");
    }

    private String format(String json) {

        if (json.contains("\"type\": \"CHAT_MESSAGE\""))
            return "[CHAT] " + json;

        if (json.contains("\"type\": \"TASK_CREATED\""))
            return "[TASK CREATED] " + json;

        if (json.contains("\"type\": \"GROUP_CREATED\""))
            return "[GROUP CREATED] " + json;

        if (json.contains("\"type\": \"GROUP_LEFT\""))
            return "[GROUP LEFT] " + json;

        if (json.contains("\"type\": \"ACTIVITY_LOG\""))
            return "[ACTIVITY] " + json;

        return "[OTHER] " + json;
    }

    @FXML
    public void onClear() {
        logArea.clear();
    }

    @FXML
    public void onClose() {
        logArea.getScene().getWindow().hide();
    }
}
