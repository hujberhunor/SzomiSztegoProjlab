package com.dino.view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class UIFactory {
    /**
     * Gomb létrehozása
     *
     * @param text A gomb szövege
     * @return Az új gomb objektum
     */
    public Button createButton(String text) {
        return new Button(text);
    }

    /**
     * Címke létrehozása
     *
     * @param text A címke szövege
     * @return Az új címke objektum
     */
    public Label createLabel(String text) {
        return new Label(text);
    }

    /**
     * Szövegmező létrehozása
     *
     * @return Az új szövegmező objektum
     */
    public TextField createTextField() {
        return new TextField();
    }

    /**
     * Panel létrehozása
     *
     * @return Az új panel objektum
     */
    public Pane createPanel() {
        return new Pane();
    }
}