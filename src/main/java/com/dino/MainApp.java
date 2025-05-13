package com.dino;

import com.dino.commands.CommandParser;
import com.dino.engine.Game;
import com.dino.view.BottomBar;
import com.dino.view.GameBoard;
import com.dino.view.Scoreboard;
import com.dino.view.TopBar;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Alapelrendezés létrehozása
            BorderPane root = new BorderPane();
            root.setPrefSize(1200, 800);
            root.setStyle("-fx-background-color: #333333;");
            
            // Játék létrehozása és inicializálása
            Game game = Game.getInstance(); // Singleton pattern használata
            game.initBoard(); // Játéktábla generálása
            game.quickInit(); // Gyors játékinicializálás
            
            // UI komponensek létrehozása
            GameBoard gameBoard = new GameBoard();
            Scoreboard scoreboard = new Scoreboard();
            TopBar topBar = new TopBar(scoreboard);
            BottomBar bottomBar = new BottomBar();
            
            // Observer kapcsolatok beállítása
            game.addObserver(gameBoard);
            game.addObserver(topBar);
            game.addObserver(bottomBar);
            game.addObserver(scoreboard);
            
            // Manuális frissítés az inicializálás után
            gameBoard.update(game);
            
            // Scoreboard elhelyezése a jobb oldalon
            scoreboard.setMaxWidth(250);
            VBox rightPanel = new VBox(scoreboard);
            rightPanel.setPadding(new Insets(10));
            
            // GameBoard elhelyezése középen, maximális mérettel
            VBox centerPanel = new VBox(gameBoard.createNode());
            centerPanel.setPadding(new Insets(10));
            VBox.setVgrow(centerPanel, Priority.ALWAYS);
            
            // Komponensek elhelyezése
            root.setTop(topBar);
            root.setCenter(centerPanel);
            root.setBottom(bottomBar);
            root.setRight(rightPanel);
            
            // CommandParser és eseménykezelés beállítása a BottomBar-hoz
            CommandParser parser = new CommandParser(game);
            
            // BottomBar parancs eseménykezelő beállítása (ha szükséges)
            // Ezt implementálhatod később a BottomBar osztályban
            
            // Scene létrehozása és megjelenítése
            Scene scene = new Scene(root);
            scene.setFill(Color.BLACK);
            
            primaryStage.setTitle("Fungorium Game");
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();
            
            // Játék indítása
            game.notifyObservers();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}