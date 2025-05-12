package com.dino.view;

import com.dino.engine.Game;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class BottomBar implements ModelObserver {
    private TextField commandInput;
    private TextArea logDisplay;

    public BottomBar() {
        commandInput = new TextField();
        logDisplay = new TextArea();
        logDisplay.setEditable(false);
    }

    /**
     * Új log bejegyzés hozzáadása
     * @param entry Az új log bejegyzés szövege
     */
    public void appendLog(String entry) {
        logDisplay.appendText(entry + "\n");
        logDisplay.setScrollTop(Double.MAX_VALUE);
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

    }
}