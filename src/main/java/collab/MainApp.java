package collab;

import collab.controller.GroupViewController;
import collab.dto.AuthDto;
import collab.dto.GroupDto;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static Stage primaryStage;
    private static ApiClient apiClient;
    private static AuthDto.UserResponse currentUser;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        apiClient = new ApiClient("http://localhost:8080"); // при необходимости поменяй URL
        showLogin();
    }

    // ===== Геттеры / сеттеры =====
    public static ApiClient getApiClient() {
        return apiClient;
    }

    public static AuthDto.UserResponse getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(AuthDto.UserResponse user) {
        currentUser = user;
    }

    // ===== Экраны =====
    public static void showLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load());
            primaryStage.setTitle("Login - Study Collab");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(600);
            primaryStage.setMinHeight(400);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/register.fxml"));
            Scene scene = new Scene(loader.load());
            primaryStage.setTitle("Register - Study Collab");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showMain() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/main.fxml"));
            Scene scene = new Scene(loader.load());
            primaryStage.setTitle("Study Collab - Client");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showWebSocketLog() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/websocket_log.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("WebSocket Activity Log");
            stage.setScene(scene);
            stage.setWidth(600);
            stage.setHeight(500);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Открыть подробный экран группы (+чат внутри).
     * ВАЖНО: сюда передаём САМ ОБЪЕКТ GroupResponse, не id.
     */
    public static void showGroupView(GroupDto.GroupResponse group) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/group_view.fxml"));
            Scene scene = new Scene(loader.load());

            // достаём контроллер и передаём ему текущую группу
            GroupViewController controller = loader.getController();
            controller.setGroup(group);

            primaryStage.setTitle("Skupina – " + group.getName());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
