package com.dino.view;

import com.dino.commands.Command;
import com.dino.commands.CommandParser;
import com.dino.engine.Game;
import com.dino.util.Logger;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class BottomBar extends VBox implements ModelObserver {
    private final TextField commandInput;
    private final TextArea logDisplay;
    private final Game game;
    private final Logger logger;
    private final CommandParser parser;

    public BottomBar(Game game) {
        this.game = game;
        this.logger = game.getLogger(); // vagy Logger.getInstance()
        this.parser = new CommandParser(game);

        // GUI init
        setSpacing(5);
        setPadding(new Insets(10));
        setAlignment(Pos.BOTTOM_LEFT);
        setMaxWidth(Double.MAX_VALUE);
        setMinHeight(80);
        setBackground(new Background(new BackgroundFill(Color.rgb(30, 30, 30, 0.7), new CornerRadii(10), Insets.EMPTY)));
        setEffect(new DropShadow(2, Color.BLACK));

        commandInput = new TextField();
        commandInput.setFont(Font.font("Consolas", FontWeight.BOLD, 12));
        commandInput.setPromptText("Enter command...");
        commandInput.setStyle("-fx-text-fill: white; -fx-background-color: rgba(20, 20, 20, 0.8);");

        logDisplay = new TextArea();
        logDisplay.setEditable(false);
        logDisplay.setWrapText(true);
        logDisplay.setFont(Font.font("Consolas", 12));
        logDisplay.setPrefHeight(200);
        logDisplay.setStyle("-fx-text-fill: white; -fx-control-inner-background: rgba(0, 0, 0, 0.5);");
        VBox.setVgrow(logDisplay, Priority.SOMETIMES);

        VBox contentBox = new VBox(5, logDisplay, commandInput);
        contentBox.setPadding(new Insets(10));
        contentBox.setAlignment(Pos.BOTTOM_LEFT);
        VBox.setVgrow(contentBox, Priority.ALWAYS);

        // Ablak méretének állítgatása
        Region resizeHandle = new Region();
        resizeHandle.setMaxHeight(5);
        resizeHandle.setMaxWidth(Double.MAX_VALUE);
        resizeHandle.setCursor(Cursor.N_RESIZE);
        resizeHandle.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05);");
        resizeHandle.setMouseTransparent(false);

        final double[] startY = new double[1];
        final double[] startHeight = new double[1];

        resizeHandle.setOnMousePressed(e -> {
            startY[0] = e.getScreenY();
            startHeight[0] = getHeight();
        });

        resizeHandle.setOnMouseDragged(e -> {
            double delta = startY[0] - e.getScreenY();
            double newHeight = startHeight[0] + delta;
            if (newHeight >= 100) {
                setPrefHeight(newHeight);
            }
        });

        StackPane overlayPane = new StackPane(contentBox, resizeHandle);
        StackPane.setAlignment(resizeHandle, Pos.TOP_CENTER);
        StackPane.setMargin(resizeHandle, new Insets(0, 0, 0, 0));
        VBox.setVgrow(overlayPane, Priority.ALWAYS);

        getChildren().add(overlayPane);

        // ESEMÉNY: ENTER billentyű
        commandInput.setOnAction(e -> processCommand());

        // opcionális: logger visszajelzés
        logger.addListener(this::appendLog);
    }

private void processCommand() {
    String input = getCommand().trim();
    clearInput();

    if (input.isBlank()) return;

    try {
        Command command = parser.parse(input);
        if (command.validate(game)) {
            command.execute(game, logger);
            appendLog("[OK] Executed: " + input);
        } else {
            appendLog("[ERROR] Invalid command: " + input);
        }
    } catch (Exception ex) {
        appendLog("[ERROR] " + ex.getMessage());
    }
game.notifyObservers();
game.autoAdvanceIfNeeded();

}



    /**
     * Új log bejegyzés hozzáadása
     * @param entry Az új log bejegyzés szövege
     */
    public void appendLog(String entry) {
        Platform.runLater(() -> {
            logDisplay.appendText(entry + "\n");
            logDisplay.setScrollTop(Double.MAX_VALUE);
        });
    }

    /**
     * Az aktuális parancs lekérése
     * @return A beviteli mezőben található parancs
     */
    public String getCommand() {
        return commandInput.getText();
    }

    /**
     * A beviteli mező törlése
     */
    public void clearInput() {
        commandInput.clear();
    }

    /**
     * Frissíti a komponenst a játék aktuális állapota alapján
     * @param game A játék aktuális állapota
     */
    @Override
    public void update(Game game) {
        // TODO
    }
}