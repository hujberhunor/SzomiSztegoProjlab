package com.dino.view;

import com.dino.engine.Game;
import com.dino.util.Logger;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class BottomBar extends VBox implements ModelObserver {
    private TextField commandInput;
    private TextArea logDisplay;

    public BottomBar() {
        setSpacing(5);
        setPadding(new Insets(10));
        setAlignment(Pos.BOTTOM_LEFT);
        setMaxWidth(Double.MAX_VALUE);

        setBackground(new Background(new BackgroundFill(
            Color.rgb(30, 30, 30, 0.7),
            new CornerRadii(10),
            Insets.EMPTY
        )));
        setEffect(new DropShadow(2, Color.BLACK));

        commandInput = new TextField();
        commandInput.setFont(Font.font("Consolas", FontWeight.BOLD, 12));
        commandInput.setPromptText("Enter command...");
        commandInput.setStyle("-fx-text-fill: white; -fx-background-color: rgba(20, 20, 20, 0.8);");

        commandInput.setOnAction(e -> {
            String command = getCommand();
            Logger.getInstance().logOk("COMMAND", "USER", "EXECUTE", "", command);
            clearInput();
        });

        logDisplay = new TextArea();
        logDisplay.setEditable(false);
        logDisplay.setWrapText(true);
        logDisplay.setFont(Font.font("Consolas", 12));
        logDisplay.setPrefHeight(75);
        logDisplay.setStyle("-fx-text-fill: white; -fx-control-inner-background: rgba(0, 0, 0, 0.5);");
        VBox.setVgrow(logDisplay, Priority.ALWAYS);

        getChildren().addAll(logDisplay, commandInput);

        Logger.getInstance().addListener(this::appendLog);
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