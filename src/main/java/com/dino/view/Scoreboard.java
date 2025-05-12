package com.dino.view;

import com.dino.engine.Game;
import com.dino.player.Entomologist;
import com.dino.player.Player;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scoreboard extends VBox implements ModelObserver {
    private final Map<Player, Integer> scores;
    private final VBox scoreList;
    private boolean isVisible;

    public Scoreboard() {
        this.scores = new HashMap<>();
        this.isVisible = false;

        setPadding(new Insets(10));
        setSpacing(8);
        setAlignment(Pos.TOP_CENTER);

        setBackground(new Background(new BackgroundFill(
            Color.rgb(40, 40, 40, 0.9),
            new CornerRadii(10),
            Insets.EMPTY
        )));
        setEffect(new DropShadow(1, Color.BLACK));

        Label title = new Label("Scoreboard");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 18));
        title.setTextFill(Color.WHITE);

        scoreList = new VBox(5);
        scoreList.setAlignment(Pos.CENTER_LEFT);

        getChildren().addAll(title, scoreList);

        setVisible(false);
        setManaged(false);

    }

    /**
     * A pontszámok frissítése
     * @param newScores Az új pontszámok
     */
    public void updateScores(Map<Player, Integer> newScores) {
        this.scores.clear();
        this.scores.putAll(newScores);
        refreshDisplay();
    }

    /**
     * A láthatóság átváltása
     */
    public void toggleVisibility() {
        isVisible = !isVisible;
        setVisible(isVisible);
        setManaged(isVisible);
    }

    private void refreshDisplay(){
        scoreList.getChildren().clear();

        List<Map.Entry<Player, Integer>> entomologists = new ArrayList<>();
        List<Map.Entry<Player, Integer>> mycologists = new ArrayList<>();

        for (Map.Entry<Player, Integer> entry : scores.entrySet()){
            if (entry.getKey() instanceof Entomologist){
                entomologists.add(entry);
            }
            else mycologists.add(entry);
        }

        Comparator<Map.Entry<Player, Integer>> byScoreDescending = Map.Entry.<Player, Integer>comparingByValue().reversed();
        entomologists.sort(byScoreDescending);
        mycologists.sort(byScoreDescending);

        Label entomologistHeader = new Label("Entomologists:");
        Label mycologistHeader = new Label("Mycologists:");

        entomologistHeader.setTextFill(Color.ORANGE);
        entomologistHeader.setFont(Font.font("Georgia", FontWeight.BOLD, 14));

        mycologistHeader.setTextFill(Color.CORNFLOWERBLUE);
        mycologistHeader.setFont(Font.font("Georgia", FontWeight.BOLD, 14));

        scoreList.getChildren().add(entomologistHeader);
        for (Map.Entry<Player, Integer> entry : entomologists) {
            scoreList.getChildren().add(makePlayerLabel(entry.getKey(), entry.getValue(), Color.ORANGE));
        }

        scoreList.getChildren().add(new Separator());

        scoreList.getChildren().add(mycologistHeader);
        for (Map.Entry<Player, Integer> entry : mycologists) {
            scoreList.getChildren().add(makePlayerLabel(entry.getKey(), entry.getValue(), Color.CORNFLOWERBLUE));
        }
    }

    private Label makePlayerLabel(Player player, int score, Color color){
        Label label = new Label(player.name + " : " + score);
        label.setTextFill(color);
        label.setFont(Font.font("Georgia", 14));
        return label;
    }

    /**
     * Frissíti a komponenst a játék aktuális állapota alapján
     * @param game A játék aktuális állapota
     */
    @Override
    public void update(Game game) {
        Map<Player, Integer> newScores = new HashMap<>();

        for (Player player : game.getPlayers()) {
            newScores.put(player, player.score);
        }

        updateScores(newScores);
    }
}