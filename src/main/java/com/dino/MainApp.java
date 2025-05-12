package com.dino;

import com.dino.view.Scoreboard;
import com.dino.view.TopBar;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPrefSize(800, 600);

        Scoreboard scoreboard = new Scoreboard();
        TopBar topBar = new TopBar(scoreboard);

        VBox topContainer = new VBox(10);
        topContainer.setPadding(new Insets(20, 20, 0, 20));
        
        HBox scoreboardContainer = new HBox();
        scoreboardContainer.setAlignment(Pos.TOP_RIGHT);
        scoreboardContainer.getChildren().add(scoreboard);

        HBox.setMargin(scoreboard, new Insets(0, 0, 0, 0));
        scoreboard.setMaxWidth(250);

        topContainer.getChildren().addAll(topBar, scoreboardContainer);

        root.setTop(topContainer);
        
        Scene scene = new Scene(root);
        primaryStage.setTitle("Fungorium GUI");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
