package collab.controller;

import collab.ApiClient;
import collab.MainApp;
import collab.dto.AuthDto;
import collab.dto.GroupDto;
import collab.dto.TaskDto;
import collab.dto.ResourceDto;
import collab.dto.UserDto;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class MainController {

    @FXML private Label currentUserLabel;

    // GROUPS TAB
    @FXML private TableView<GroupDto.GroupResponse> groupsTable;
    @FXML private TableColumn<GroupDto.GroupResponse, String> groupNameColumn;
    @FXML private TableColumn<GroupDto.GroupResponse, String> groupDescColumn;
    @FXML private TextField newGroupNameField;
    @FXML private TextField newGroupDescField;

    // MEMBERS
    @FXML private TableView<UserDto.UserResponse> membersTable;
    @FXML private TableColumn<UserDto.UserResponse, String> memberNameColumn;
    @FXML private TableColumn<UserDto.UserResponse, String> memberEmailColumn;

    // TASKS TAB
    @FXML private TableView<TaskDto.TaskResponse> tasksTable;
    @FXML private TableColumn<TaskDto.TaskResponse, String> taskTitleColumn;
    @FXML private TableColumn<TaskDto.TaskResponse, String> taskStatusColumn;
    @FXML private TableColumn<TaskDto.TaskResponse, String> taskDeadlineColumn;

    @FXML private ComboBox<GroupDto.GroupResponse> tasksGroupCombo;
    @FXML private TextField newTaskTitleField;
    @FXML private TextField newTaskDescField;
    @FXML private DatePicker newTaskDeadlinePicker;

    // RESOURCES TAB
    @FXML private TableView<ResourceDto.ResourceResponse> resourcesTable;
    @FXML private TableColumn<ResourceDto.ResourceResponse, String> resTitleColumn;
    @FXML private TableColumn<ResourceDto.ResourceResponse, String> resTypeColumn;
    @FXML private TableColumn<ResourceDto.ResourceResponse, String> resUrlColumn;

    @FXML private ComboBox<GroupDto.GroupResponse> resourcesGroupCombo;
    @FXML private TextField newResTitleField;
    @FXML private TextField newResTypeField;
    @FXML private TextField newResUrlField;

    // STATS
    @FXML private PieChart statsPieChart;

    private ApiClient api;
    private AuthDto.UserResponse currentUser;

    // ==========================================================
    // INITIALIZE
    // ==========================================================
    @FXML
    public void initialize() {
        this.api = MainApp.getApiClient();
        this.currentUser = MainApp.getCurrentUser();

        if (currentUser != null) {
            currentUserLabel.setText(currentUser.getName() + " (" + currentUser.getEmail() + ")");
        }

        // GROUPS
        groupNameColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getName()));
        groupDescColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getDescription()));

        // MEMBERS
        memberNameColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getName()));
        memberEmailColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getEmail()));

        // TASKS
        taskTitleColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getTitle()));

        taskStatusColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getStatus()));

        taskDeadlineColumn.setCellValueFactory(c -> {
            String raw = c.getValue().getDeadline();
            if (raw == null || raw.isBlank()) return new SimpleStringProperty("");

            try {
                Instant inst = Instant.parse(raw);
                LocalDate date = inst.atZone(ZoneId.systemDefault()).toLocalDate();
                return new SimpleStringProperty(date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            } catch (Exception e) {
                return new SimpleStringProperty(raw);
            }
        });

        // RESOURCES
        resTitleColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getTitle()));
        resTypeColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getType()));

        // URL column with clickable hyperlink
        resUrlColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getPathOrUrl()));

        resUrlColumn.setCellFactory(col -> new TableCell<>() {
            private final Hyperlink link = new Hyperlink();

            {
                link.setOnAction(e -> {
                    String url = getItem();
                    if (url == null || url.isBlank()) return;

                    try {
                        java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
                    } catch (Exception ex) {
                        showError("Nepodarilo sa otvoriť odkaz: " + ex.getMessage());
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isBlank()) {
                    setGraphic(null);
                } else {
                    link.setText(item);
                    setGraphic(link);
                }
            }
        });

        // ON GROUP SELECT — reload all
        groupsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldG, newG) -> {
            if (newG != null) {
                tasksGroupCombo.setValue(newG);
                resourcesGroupCombo.setValue(newG);
                refreshMembers();
                refreshTasks();
                refreshResources();
            }
        });

        refreshGroups();
        refreshTasks();
        refreshResources();
    }

    // ==========================================================
    // GROUPS
    // ==========================================================
    @FXML
    public void refreshGroups() {
        try {
            Long userId = currentUser.getId();

            GroupDto.GroupResponse[] arr =
                    api.getList("/api/groups?userId=" + userId, GroupDto.GroupResponse[].class);

            ObservableList<GroupDto.GroupResponse> list =
                    FXCollections.observableArrayList(Arrays.asList(arr));

            groupsTable.setItems(list);
            tasksGroupCombo.setItems(list);
            resourcesGroupCombo.setItems(list);

            if (!list.isEmpty()) {
                groupsTable.getSelectionModel().selectFirst();
            }

            updateStats(list, tasksTable.getItems());

        } catch (Exception e) {
            showError("Nepodarilo sa načítať skupiny: " + e.getMessage());
        }
    }


    @FXML
    public void onCreateGroup() {
        String name = newGroupNameField.getText();
        String desc = newGroupDescField.getText();

        if (name.isBlank()) {
            showError("Názov skupiny je povinný.");
            return;
        }

        try {
            GroupDto.CreateGroupRequest req =
                    new GroupDto.CreateGroupRequest(name, desc, currentUser.getId());

            api.post("/api/groups", req, GroupDto.GroupResponse.class);

            newGroupNameField.clear();
            newGroupDescField.clear();
            refreshGroups();

        } catch (Exception e) {
            showError("Vytvorenie skupiny zlyhalo: " + e.getMessage());
        }
    }

    @FXML
    public void refreshMembers() {
        GroupDto.GroupResponse grp = groupsTable.getSelectionModel().getSelectedItem();
        if (grp == null) return;

        try {
            UserDto.UserResponse[] arr =
                    api.getList("/api/groups/" + grp.getId() + "/members",
                            UserDto.UserResponse[].class);

            membersTable.setItems(FXCollections.observableArrayList(Arrays.asList(arr)));

        } catch (Exception e) {
            showError("Nepodarilo sa načítať členov: " + e.getMessage());
        }
    }

    // ==========================================================
    // TASKS
    // ==========================================================
    @FXML
    public void refreshTasks() {
        try {
            GroupDto.GroupResponse grp = tasksGroupCombo.getValue();
            if (grp == null) {
                tasksTable.setItems(FXCollections.observableArrayList());
                updateStats(groupsTable.getItems(), FXCollections.observableArrayList());
                return;
            }

            TaskDto.TaskResponse[] arr =
                    api.getList("/api/tasks/group/" + grp.getId(), TaskDto.TaskResponse[].class);

            ObservableList<TaskDto.TaskResponse> list =
                    FXCollections.observableArrayList(Arrays.asList(arr));

            tasksTable.setItems(list);
            updateStats(groupsTable.getItems(), list);

        } catch (Exception e) {
            showError("Nepodarilo sa načítať úlohy: " + e.getMessage());
        }
    }

    @FXML
    public void onCreateTask() {
        GroupDto.GroupResponse grp = tasksGroupCombo.getValue();
        if (grp == null) grp = groupsTable.getSelectionModel().getSelectedItem();
        if (grp == null) {
            showError("Vyberte skupину.");
            return;
        }

        String title = newTaskTitleField.getText();
        String desc = newTaskDescField.getText();
        LocalDate ld = newTaskDeadlinePicker.getValue();

        if (title.isBlank()) {
            showError("Názov úlohy je povinný.");
            return;
        }

        try {
            String deadline = ld == null ? null :
                    ld.atStartOfDay(ZoneId.systemDefault()).toInstant().toString();

            TaskDto.CreateTaskRequest req =
                    new TaskDto.CreateTaskRequest(
                            grp.getId(),
                            currentUser.getId(),
                            title,
                            desc,
                            deadline
                    );

            api.post("/api/tasks", req, TaskDto.TaskResponse.class);

            newTaskTitleField.clear();
            newTaskDescField.clear();
            newTaskDeadlinePicker.setValue(null);

            refreshTasks();

        } catch (Exception e) {
            showError("Vytvorenie úlohy zlyhalo: " + e.getMessage());
        }
    }

    private void changeSelectedTaskStatus(String newStatus) {
        TaskDto.TaskResponse task = tasksTable.getSelectionModel().getSelectedItem();
        if (task == null) {
            showError("Vyberte úlohu.");
            return;
        }

        try {
            api.putNoResponse("/api/tasks/" + task.getId() + "/status?status=" + newStatus, null);
            refreshTasks();
        } catch (Exception e) {
            showError("Zmena stavu úlohy zlyhala: " + e.getMessage());
        }
    }

    @FXML public void onChangeTaskStatusToOpen() { changeSelectedTaskStatus("OPEN"); }
    @FXML public void onChangeTaskStatusToInProgress() { changeSelectedTaskStatus("IN_PROGRESS"); }
    @FXML public void onChangeTaskStatusToDone() { changeSelectedTaskStatus("DONE"); }

    // ==========================================================
    // RESOURCES
    // ==========================================================
    @FXML
    public void refreshResources() {
        try {
            GroupDto.GroupResponse grp = resourcesGroupCombo.getValue();
            if (grp == null) {
                resourcesTable.setItems(FXCollections.observableArrayList());
                return;
            }

            ResourceDto.ResourceResponse[] arr =
                    api.getList("/api/resources/group/" + grp.getId(),
                            ResourceDto.ResourceResponse[].class);

            resourcesTable.setItems(FXCollections.observableArrayList(Arrays.asList(arr)));

        } catch (Exception e) {
            showError("Nepodarilo sa načítať materiály: " + e.getMessage());
        }
    }

    @FXML
    public void onCreateResource() {
        GroupDto.GroupResponse grp = resourcesGroupCombo.getValue();
        if (grp == null) grp = groupsTable.getSelectionModel().getSelectedItem();
        if (grp == null) {
            showError("Vyberte skupinu.");
            return;
        }

        String title = newResTitleField.getText();
        String type = newResTypeField.getText();
        String url = newResUrlField.getText();

        if (title.isBlank() || url.isBlank()) {
            showError("Názov a URL sú povinné.");
            return;
        }

        try {
            ResourceDto.CreateResourceRequest req =
                    new ResourceDto.CreateResourceRequest(
                            grp.getId(),
                            currentUser.getId(),
                            title,
                            type,
                            url
                    );

            api.post("/api/resources", req, ResourceDto.ResourceResponse.class);

            newResTitleField.clear();
            newResTypeField.clear();
            newResUrlField.clear();

            refreshResources();

        } catch (Exception e) {
            showError("Vytvorenie materiálu zlyhalo: " + e.getMessage());
        }
    }

    // ==========================================================
    // STATS
    // ==========================================================
    private void updateStats(ObservableList<GroupDto.GroupResponse> groups,
                             ObservableList<TaskDto.TaskResponse> tasks) {

        long total = tasks == null ? 0 : tasks.size();
        long open = 0, inProgress = 0, done = 0;

        if (tasks != null) {
            for (TaskDto.TaskResponse t : tasks) {
                if (t.getStatus() == null) continue;
                switch (t.getStatus()) {
                    case "OPEN" -> open++;
                    case "IN_PROGRESS" -> inProgress++;
                    case "DONE" -> done++;
                }
            }
        }

        ObservableList<PieChart.Data> pie = FXCollections.observableArrayList();

        if (total > 0) {
            if (open > 0) pie.add(new PieChart.Data("OPEN (" + open + ")", open));
            if (inProgress > 0) pie.add(new PieChart.Data("IN PROGRESS (" + inProgress + ")", inProgress));
            if (done > 0) pie.add(new PieChart.Data("DONE (" + done + ")", done));
        }

        statsPieChart.setData(pie);
    }

    // ==========================================================
    // HELPERS
    // ==========================================================
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }

    // ===== ОТКРЫТИЕ ОКНА ГРУППЫ =====
    @FXML
    public void onOpenGroup() {
        var g = groupsTable.getSelectionModel().getSelectedItem();
        if (g == null) {
            showError("Vyberte skupinu.");
            return;
        }
        // ВАЖНО: передаём ВСЮ группу, не только id
        MainApp.showGroupView(g);
    }

    @FXML
    public void onLogout() {
        MainApp.setCurrentUser(null);
        MainApp.showLogin();
    }

    @FXML
    public void onOpenWebSocketLog() {
        MainApp.showWebSocketLog();
    }

}
