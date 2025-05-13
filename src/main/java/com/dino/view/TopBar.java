package com.dino.view;

import com.dino.engine.Game;
import com.dino.player.Player;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class TopBar implements ModelObserver {
    private int currentTurn;
    private Player currentPlayer;
    private Label turnLabel;
    private Label playerLabel;
    private Label actionsLabel;
    private HBox topBarBox;

    public TopBar() {
        topBarBox = new HBox(20);
        topBarBox.setStyle("-fx-padding: 10; -fx-background-color: #d0d0d0;");
        
        turnLabel = new Label("Kör: 0");
        playerLabel = new Label("Játékos: -");
        actionsLabel = new Label("Akciók: 0");
        
        turnLabel.setFont(Font.font(14));
        playerLabel.setFont(Font.font(14));
        actionsLabel.setFont(Font.font(14));
        
        topBarBox.getChildren().addAll(turnLabel, playerLabel, actionsLabel);
    }
    
    /**
     * UI komponens létrehozása
     */
    public Node createNode() {
        return topBarBox;
    }

    /**
     * Az aktuális kör frissítése
     */
    public void updateTurn(int turn) {
        this.currentTurn = turn;
        turnLabel.setText("Kör: " + turn);
    }

    /**
     * Az aktuális játékos frissítése
     */
    public void updatePlayer(Player player) {
        this.currentPlayer = player;
        if (player != null) {
            playerLabel.setText("Játékos: " + player.name);
            actionsLabel.setText("Akciók: " + player.remainingActions);
        } else {
            playerLabel.setText("Játékos: -");
            actionsLabel.setText("Akciók: 0");
        }
    }

    /**
     * Frissítés a játék állapota alapján
     */
    @Override
    public void update(Game game) {
        updateTurn(game.getCurrentRound());
        updatePlayer(game.getCurrentPlayer());
    }
}