package com.dino.view;

import com.dino.engine.Game;
import com.dino.player.Player;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class TopBar extends HBox implements ModelObserver {
    private int currentTurn;
    private Player currentPlayer;

    private final Label roundLabel;
    private final Label playerLabel;
    private final Button scoreboardButton;

    public TopBar(Scoreboard scoreboard){
        setSpacing(10);
        setPadding(new Insets(10));
        setAlignment(Pos.CENTER_LEFT);
        setPrefHeight(50);
        setMaxWidth(Double.MAX_VALUE);

        setBackground(new Background(new BackgroundFill(
            Color.rgb(60, 60, 60, 0.5),
            new CornerRadii(10),
            Insets.EMPTY
        )));

        setBorder(new Border(new BorderStroke(
            Color.DARKGRAY,
            BorderStrokeStyle.SOLID,
            new CornerRadii(10),
            BorderWidths.DEFAULT
        )));

        roundLabel = new Label("Current Round: N/A");
        playerLabel = new Label("Current Player: N/A");

        roundLabel.setTextFill(Color.WHITE);
        roundLabel.setFont(Font.font("Georgia", FontWeight.BOLD, 16));
        playerLabel.setTextFill(Color.WHITE);
        playerLabel.setFont(Font.font("Georgia", FontWeight.BOLD, 16));

        StackPane roundWrapper = wrapLabel(roundLabel);
        StackPane playerWrapper = wrapLabel(playerLabel);

        scoreboardButton = new Button("Scoreboard");
        scoreboardButton.setFont(Font.font("Georgia", FontWeight.BOLD, 16));
        scoreboardButton.setTextFill(Color.WHITE);
        scoreboardButton.setBackground(new Background(new BackgroundFill(
            Color.rgb(60, 60, 60, 0.8),
            new CornerRadii(10),
            Insets.EMPTY
        )));
        scoreboardButton.setEffect(new DropShadow(2, Color.BLACK));
        scoreboardButton.setFocusTraversable(false);
        scoreboardButton.setOnAction(e -> scoreboard.toggleVisibility());

        Region leftSpacer = new Region();
        Region centerSpacer = new Region();
        Region rightSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        HBox.setHgrow(centerSpacer, Priority.ALWAYS);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        StackPane buttonWrapper = new StackPane(scoreboardButton);
            buttonWrapper.setPadding(new Insets(5, 15, 5, 15));
            buttonWrapper.setBackground(new Background(new BackgroundFill(
                Color.rgb(60, 60, 60, 0.8),
                new CornerRadii(10),
                Insets.EMPTY
            )));
            buttonWrapper.setEffect(new DropShadow(2, Color.BLACK));

        getChildren().addAll(roundWrapper, leftSpacer, playerWrapper, centerSpacer, buttonWrapper, rightSpacer);
    }

    private StackPane wrapLabel(Label label){
        StackPane wrapper = new StackPane(label);
        wrapper.setPadding(new Insets(5, 15, 5, 15));
        wrapper.setBackground(new Background(new BackgroundFill(
            Color.rgb(60, 60, 60, 0.8),
            new CornerRadii(10),
            Insets.EMPTY
        )));
        wrapper.setEffect(new DropShadow(2, Color.BLACK));
        return wrapper;
    }

    /**
     * Az aktuális kör frissítése
     * @param turn Az új kör száma
     */
    public void updateTurn(int turn) {
        this.currentTurn = turn;
        roundLabel.setText("Current Round: " + turn);
    }

    /**
     * Az aktuális játékos frissítése
     * @param player Az új játékos
     */
    public void updatePlayer(Player player) {
        this.currentPlayer = player;
        playerLabel.setText("Current Player: " + (player != null ? player.name : "N/A"));
    }

    /**
     * Frissíti a komponenst a játék aktuális állapota alapján
     * @param game A játék aktuális állapota
     */
    @Override
    public void update(Game game) {
        updateTurn(game.getCurrentRound());
        updatePlayer(game.getCurrentPlayer());
    }
}