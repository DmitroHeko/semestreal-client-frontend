package collab.controller;

import collab.ApiClient;
import collab.MainApp;
import collab.dto.*;
import collab.ws.WebSocketClient;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;

public class GroupViewController {

    @FXML private Label groupTitleLabel;

    // MEMBERS
    @FXML private TableView<UserDto.UserResponse> memberTable;
    @FXML private TableColumn<UserDto.UserResponse, String> memberNameCol;
    @FXML private TableColumn<UserDto.UserResponse, String> memberEmailCol;

    // CHAT
    @FXML private ListView<String> chatList;
    @FXML private TextField chatInput;

    // TASKS
    @FXML private TableView<TaskDto.TaskResponse> taskTable;
    @FXML private TableColumn<TaskDto.TaskResponse, String> taskTitleCol;
    @FXML private TableColumn<TaskDto.TaskResponse, String> taskStatusCol;
    @FXML private TableColumn<TaskDto.TaskResponse, String> taskDeadlineCol;
    @FXML private TextField taskTitleField;
    @FXML private DatePicker taskDeadlineField;

    // RESOURCES
    @FXML private TableView<ResourceDto.ResourceResponse> resourceTable;
    @FXML private TableColumn<ResourceDto.ResourceResponse, String> resourceTitleCol;
    @FXML private TableColumn<ResourceDto.ResourceResponse, String> resourceTypeCol;
    @FXML private TableColumn<ResourceDto.ResourceResponse, String> resourceUrlCol;
    @FXML private TextField resTitleField;
    @FXML private TextField resUrlField;

    private ApiClient api;
    private GroupDto.GroupResponse currentGroup;
    private AuthDto.UserResponse currentUser;

    // ⭐ Один WebSocket клиент для чата и задач
    private final WebSocketClient wsClient = new WebSocketClient();

    @FXML
    public void initialize() {
        api = MainApp.getApiClient();
        currentUser = MainApp.getCurrentUser();

        // MEMBERS
        memberNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        memberEmailCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));

        // TASKS
        taskTitleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle()));
        taskStatusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));
        taskDeadlineCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDeadline()));

        // RESOURCES
        resourceTitleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle()));
        resourceTypeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType()));
        resourceUrlCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPathOrUrl()));
    }

    // Задаётся из MainController
    public void setGroup(GroupDto.GroupResponse group) {
        this.currentGroup = group;
        groupTitleLabel.setText(group.getName());

        refreshMembers();
        refreshTasks();
        refreshResources();
        refreshChat();

        connectWebSocket();  // ⭐ включаем real-time
    }

    // ================================
    // ⭐ WebSocket — чат и задачи
    // ================================
    private void connectWebSocket() {

        wsClient.connect("ws://localhost:8080/ws", msg -> {

            if (!msg.contains("\"groupId\": " + currentGroup.getId())) return;

            // --- CHAT MESSAGE ---
            if (msg.contains("\"type\": \"CHAT_MESSAGE\"")) {
                Platform.runLater(() -> chatList.getItems().add(parseChat(msg)));
            }

            // --- TASK CREATED ---
            if (msg.contains("\"type\": \"TASK_CREATED\"")) {
                Platform.runLater(this::refreshTasks);
            }

            // --- TASK UPDATED ---
            if (msg.contains("\"type\": \"TASK_UPDATED\"")) {
                Platform.runLater(this::refreshTasks);
            }
        });
    }

    // Разбор {"user": "...", "message": "..."}
    private String parseChat(String json) {
        try {
            String user = json.split("\"user\": \"")[1].split("\"")[0];
            String msg = json.split("\"message\": \"")[1].split("\"")[0];
            return user + ": " + msg;
        } catch (Exception e) {
            return json;
        }
    }

    // =======================================
    // MEMBERS
    // =======================================
    @FXML
    public void refreshMembers() {
        if (currentGroup == null) return;

        try {
            var arr = api.getList("/api/groups/" + currentGroup.getId() + "/members",
                    UserDto.UserResponse[].class);

            memberTable.setItems(FXCollections.observableArrayList(Arrays.asList(arr)));

        } catch (Exception e) {
            showError("Chyba pri načítaní členov: " + e.getMessage());
        }
    }

    @FXML
    public void onInviteUser() {
        if (currentGroup == null) return;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Zadajte email používateľa:");
        var result = dialog.showAndWait();

        if (result.isEmpty()) return;

        try {
            api.postNoResponse(
                    "/api/groups/" + currentGroup.getId() + "/invite?email=" + result.get(),
                    null
            );

            refreshMembers();

        } catch (Exception e) {
            showError("Nepodarilo sa pridať používateľa: " + e.getMessage());
        }
    }

    @FXML
    public void onLeaveGroup() {
        if (currentGroup == null) return;

        try {
            api.postNoResponse(
                    "/api/groups/" + currentGroup.getId() + "/leave?userId=" + currentUser.getId(),
                    null
            );
            MainApp.showMain();

        } catch (Exception e) {
            showError("Chyba pri odchode zo skupiny: " + e.getMessage());
        }
    }

    // =======================================
    // CHAT
    // =======================================
    @FXML
    public void refreshChat() {
        if (currentGroup == null) return;

        try {
            ChatDtos.ChatMessageResponse[] arr =
                    api.getList("/api/chat/" + currentGroup.getId(),
                            ChatDtos.ChatMessageResponse[].class);

            chatList.getItems().clear();

            for (var m : arr) {
                chatList.getItems().add(m.userName() + ": " + m.message());
            }

        } catch (Exception e) {
            showError("Chyba pri načítaní chatu: " + e.getMessage());
        }
    }

    @FXML
    public void onSendMessage() {
        if (currentGroup == null) return;

        String msg = chatInput.getText();
        if (msg.isBlank()) return;

        try {
            ChatDtos.SendMessageRequest req = new ChatDtos.SendMessageRequest(
                    currentGroup.getId(),
                    currentUser.getId(),
                    msg
            );

            ChatDtos.ChatMessageResponse resp =
                    api.post("/api/chat", req, ChatDtos.ChatMessageResponse.class);

            chatList.getItems().add(resp.userName() + ": " + resp.message());
            chatInput.clear();

        } catch (Exception e) {
            showError("Správu sa nepodarilo odoslať: " + e.getMessage());
        }
    }

    // =======================================
    // TASKS
    // =======================================
    @FXML
    public void refreshTasks() {
        if (currentGroup == null) return;

        try {
            var arr = api.getList("/api/tasks/group/" + currentGroup.getId(),
                    TaskDto.TaskResponse[].class);

            taskTable.setItems(FXCollections.observableArrayList(Arrays.asList(arr)));

        } catch (Exception e) {
            showError("Chyba pri načítaní úloh: " + e.getMessage());
        }
    }

    @FXML
    public void onAddTask() {
        if (currentGroup == null) return;

        try {
            String title = taskTitleField.getText();
            LocalDate date = taskDeadlineField.getValue();

            TaskDto.CreateTaskRequest req = new TaskDto.CreateTaskRequest(
                    currentGroup.getId(),
                    currentUser.getId(),
                    title,
                    "",
                    date == null ? null : date.atStartOfDay(ZoneId.systemDefault()).toInstant().toString()
            );

            api.post("/api/tasks", req, TaskDto.TaskResponse.class);

            taskTitleField.clear();
            taskDeadlineField.setValue(null);

            refreshTasks();

        } catch (Exception e) {
            showError("Chyba pri vytváraní úlohy: " + e.getMessage());
        }
    }

    // =======================================
    // RESOURCES
    // =======================================
    @FXML
    public void refreshResources() {
        if (currentGroup == null) return;

        try {
            var arr = api.getList("/api/resources/group/" + currentGroup.getId(),
                    ResourceDto.ResourceResponse[].class);

            resourceTable.setItems(FXCollections.observableArrayList(Arrays.asList(arr)));

        } catch (Exception e) {
            showError("Chyba pri načítaní materiálov: " + e.getMessage());
        }
    }

    @FXML
    public void onAddResource() {
        if (currentGroup == null) return;

        try {
            ResourceDto.CreateResourceRequest req =
                    new ResourceDto.CreateResourceRequest(
                            currentGroup.getId(),
                            currentUser.getId(),
                            resTitleField.getText(),
                            "LINK",
                            resUrlField.getText()
                    );

            api.post("/api/resources", req, ResourceDto.ResourceResponse.class);

            resTitleField.clear();
            resUrlField.clear();

            refreshResources();

        } catch (Exception e) {
            showError("Chyba pri vytváraní materiálu: " + e.getMessage());
        }
    }

    @FXML
    public void onBack() {
        MainApp.showMain();
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.showAndWait();
    }
}
