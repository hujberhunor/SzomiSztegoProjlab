package com.dino;

import com.dino.commands.CommandParser;
import com.dino.engine.Game;
import com.dino.view.BottomBar;
import com.dino.view.GuiBoard;
import com.dino.view.Scoreboard;
import com.dino.view.TopBar;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
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
            //game.quickInit(); // Gyors játékinicializálás
            Game.getInstance().initGame();
            Game.getInstance().startGame();

            // UI komponensek létrehozása
            GuiBoard gameBoard = new GuiBoard();
            Scoreboard scoreboard = new Scoreboard();
            TopBar topBar = new TopBar(scoreboard);
            BottomBar bottomBar = new BottomBar(Game.getInstance());

            // Observer kapcsolatok beállítása
            game.addObserver(gameBoard);
            game.addObserver(topBar);
            game.addObserver(bottomBar);
            game.addObserver(scoreboard);

            // Manuális frissítés az inicializálás után
            gameBoard.update(game);

            // Scoreboard elhelyezése jobboldalon, fix szélességgel
            scoreboard.setMaxWidth(230);
            scoreboard.setPrefWidth(230);

            // GameBoard középre igazítása egy StackPane-ben
            StackPane centerContainer = new StackPane(gameBoard.createNode());
            centerContainer.setAlignment(Pos.CENTER);
            centerContainer.setPadding(new Insets(20));

            // Scoreboard elhelyezése a jobb oldalon
            VBox rightPanel = new VBox(scoreboard);
            rightPanel.setPadding(new Insets(20, 20, 20, 0));
            rightPanel.setAlignment(Pos.TOP_CENTER);

            // Komponensek elhelyezése
            root.setTop(topBar);
            root.setCenter(centerContainer);
            root.setBottom(bottomBar);
            root.setRight(rightPanel);

            // CommandParser és eseménykezelés beállítása a BottomBar-hoz
            CommandParser parser = new CommandParser(game);

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