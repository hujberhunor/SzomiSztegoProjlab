package com.dino.view;

import com.dino.engine.Game;
import com.dino.util.Logger;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class BottomBar implements ModelObserver {
    private TextField commandInput;
    private TextArea logDisplay;
    private VBox bottomBarBox;
    private Logger logger;

    public BottomBar() {
        bottomBarBox = new VBox(10);
        bottomBarBox.setStyle("-fx-padding: 10;");
        
        logDisplay = new TextArea();
        logDisplay.setEditable(false);
        logDisplay.setPrefHeight(150);
        
        commandInput = new TextField();
        commandInput.setPromptText("Írj parancsot (pl. MOVE_INSECT insect_0 tecton_B)");
        
        Button nextTurnButton = new Button("Következő kör");
        
        HBox inputBox = new HBox(10);
        inputBox.getChildren().addAll(commandInput, nextTurnButton);
        HBox.setHgrow(commandInput, Priority.ALWAYS);
        
        bottomBarBox.getChildren().addAll(logDisplay, inputBox);
        VBox.setVgrow(logDisplay, Priority.ALWAYS);
        
        logger = Logger.getInstance();
    }
    
    /**
     * UI komponens létrehozása
     */
    public Node createNode(GameController controller) {
        // Parancs bevitel eseménykezelése
        commandInput.setOnAction(e -> {
            String command = getCommand();
            appendLog("> " + command);
            controller.handleCommand(command);
            clearInput();
        });
        
        return bottomBarBox;
    }

    /**
     * Egyszerűbb verzió controller nélkül
     */
    public Node createNode() {
        return createNode(null);
    }

    /**
     * Új log bejegyzés hozzáadása
     */
    public void appendLog(String entry) {
        logDisplay.appendText(entry + "\n");
        logDisplay.setScrollTop(Double.MAX_VALUE);
    }

    /**
     * Az aktuális parancs lekérése
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
     * Frissítés a játék állapota alapján
     */
    @Override
    public void update(Game game) {
        // Logger tartalmának frissítése
        String log = logger.getLog();
        logDisplay.setText(log);
        logDisplay.setScrollTop(Double.MAX_VALUE);
    }
}