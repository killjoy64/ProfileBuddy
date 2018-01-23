package com.allendalerobotics.frc.profilebuddy;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class ProfileBuddy extends Application {
    private static Stage mainStage;

    @Override
    public void start(Stage primaryStage) throws Exception {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ui.fxml"));
            Scene scene = new Scene(root);

            mainStage = primaryStage;
            mainStage.setScene(scene);
            mainStage.setTitle("FRC Profile Buddy");
            mainStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.jpeg")));
            mainStage.setResizable(false);
            mainStage.show();
//            mainStage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getMainStage() {
        return ProfileBuddy.mainStage;
    }

}
