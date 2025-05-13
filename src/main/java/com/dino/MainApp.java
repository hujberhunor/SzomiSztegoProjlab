package com.dino;

import com.dino.commands.CommandParser;
import com.dino.engine.Game;
import com.dino.view.BottomBar;
import com.dino.view.GameBoard;
import com.dino.view.GameController;
import com.dino.view.Scoreboard;
import com.dino.view.TopBar;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Játék létrehozása és inicializálása
            Game game = new Game();
            game.initBoard(); // Játéktábla generálása
            
            // UI komponensek létrehozása
            BorderPane root = new BorderPane();
            
            GameBoard gameBoard = new GameBoard();
            TopBar topBar = new TopBar();
            BottomBar bottomBar = new BottomBar();
            Scoreboard scoreboard = new Scoreboard();
            
            CommandParser parser = new CommandParser(game);
            GameController controller = new GameController(game, gameBoard, parser);
            
            // UI komponensek elhelyezése a layoutban
            root.setCenter(gameBoard.createNode());
            root.setTop(topBar.createNode());
            root.setBottom(bottomBar.createNode(controller));
            root.setRight(scoreboard.createNode());
            
            // Kezdeti frissítés
            gameBoard.update(game);
            topBar.update(game);
            bottomBar.update(game);
            scoreboard.update(game);
            
            // Ablak beállítása és megjelenítése
            Scene scene = new Scene(root, 1200, 800);
            primaryStage.setTitle("Fungorium GUI");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}