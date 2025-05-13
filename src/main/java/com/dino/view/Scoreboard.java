package com.dino.view;

import com.dino.engine.Game;
import com.dino.player.Player;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scoreboard implements ModelObserver {
    private Map<Player, Integer> scores;
    private boolean isVisible;
    private ListView<String> scoreListView;
    private VBox scoreboardBox;

    public Scoreboard() {
        scores = new HashMap<>();
        isVisible = true;
        
        scoreboardBox = new VBox(10);
        scoreboardBox.setStyle("-fx-padding: 10; -fx-background-color: #d0d0d0; -fx-border-color: #909090;");
        scoreboardBox.setPrefWidth(200);
        
        Label title = new Label("Pontszámok");
        scoreListView = new ListView<>();
        scoreListView.setPrefHeight(400);
        
        scoreboardBox.getChildren().addAll(title, scoreListView);
    }
    
    /**
     * UI komponens létrehozása
     */
    public Node createNode() {
        return scoreboardBox;
    }

    /**
     * A pontszámok frissítése
     */
    public void updateScores(Map<Player, Integer> scores) {
        this.scores = scores;
        
        scoreListView.getItems().clear();
        for (Map.Entry<Player, Integer> entry : scores.entrySet()) {
            Player player = entry.getKey();
            Integer score = entry.getValue();
            
            if (player != null) {
                String playerName = player.name != null ? player.name : "Játékos " + player.hashCode();
                scoreListView.getItems().add(playerName + ": " + score);
            }
        }
    }

    /**
     * A láthatóság átváltása
     */
    public void toggleVisibility() {
        isVisible = !isVisible;
        scoreboardBox.setVisible(isVisible);
    }

    /**
     * Frissítés a játék állapota alapján
     */
    @Override
    public void update(Game game) {
        List<Player> players = game.getPlayers();
        Map<Player, Integer> currentScores = new HashMap<>();
        
        for (Player player : players) {
            currentScores.put(player, player.score);
        }
        
        updateScores(currentScores);
    }
}