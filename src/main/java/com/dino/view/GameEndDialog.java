package com.dino.view;

import com.dino.player.Player;
import com.dino.player.Mycologist;
import com.dino.player.Entomologist;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GameEndDialog extends Alert {

    public GameEndDialog(List<Player> rankedPlayers) {
        super(AlertType.NONE);

        setTitle("Game Over");

        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20));

        Label titleLabel = new Label("A játéknak vége!");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setMaxWidth(Double.MAX_VALUE); // Teljes szélességre kiterjesztés
        titleLabel.setTextAlignment(TextAlignment.CENTER);

        content.getChildren().add(titleLabel);
        content.getChildren().add(new javafx.scene.control.Separator());

        // Különválogotjuk a gombászokat és rovarászokat
        List<Player> mycologists = new ArrayList<>();
        List<Player> entomologists = new ArrayList<>();

        for (Player player : rankedPlayers) {
            if (player instanceof Mycologist) {
                mycologists.add(player);
            } else if (player instanceof Entomologist) {
                entomologists.add(player);
            }
        }

        // Külön-külön sorba rendezzük a két csoportot
        mycologists.sort(Comparator.comparingInt((Player p) -> p.score).reversed());
        entomologists.sort(Comparator.comparingInt((Player p) -> p.score).reversed());

        // Gombász győztes
        if (!mycologists.isEmpty()) {
            Label mycologistHeader = new Label("Gombász győztes:");
            mycologistHeader.setFont(Font.font("System", FontWeight.BOLD, 16));
            mycologistHeader.setTextFill(Color.DARKGREEN);
            content.getChildren().add(mycologistHeader);

            Player winner = mycologists.get(0);
            Label winnerLabel = new Label(winner.name + " - " + winner.score + " pont");
            winnerLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
            winnerLabel.setTextAlignment(TextAlignment.CENTER);
            content.getChildren().add(winnerLabel);
            content.getChildren().add(new javafx.scene.control.Separator());
        }

        // Rovarász győztes
        if (!entomologists.isEmpty()) {
            Label entomologistHeader = new Label("Rovarász győztes:");
            entomologistHeader.setFont(Font.font("System", FontWeight.BOLD, 16));
            entomologistHeader.setTextFill(Color.DARKORANGE);
            content.getChildren().add(entomologistHeader);

            Player winner = entomologists.get(0);
            Label winnerLabel = new Label(winner.name + " - " + winner.score + " pont");
            winnerLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
            winnerLabel.setTextAlignment(TextAlignment.CENTER);
            content.getChildren().add(winnerLabel);
            content.getChildren().add(new javafx.scene.control.Separator());
        }

        // Végeredmény táblázat - Gombászok
        if (!mycologists.isEmpty()) {
            Label mycologistsHeader = new Label("Gombászok pontjai:");
            mycologistsHeader.setFont(Font.font("System", FontWeight.BOLD, 14));
            mycologistsHeader.setTextFill(Color.DARKGREEN);
            content.getChildren().add(mycologistsHeader);

            for (int i = 0; i < mycologists.size(); i++) {
                Player player = mycologists.get(i);
                Label playerLabel = new Label((i + 1) + ". " + player.name + ": " + player.score + " pont");
                playerLabel.setFont(Font.font("System", 14));
                content.getChildren().add(playerLabel);
            }
        }

        // Végeredmény táblázat - Rovarászok
        if (!entomologists.isEmpty()) {
            Label entomologistsHeader = new Label("Rovarászok pontjai:");
            entomologistsHeader.setFont(Font.font("System", FontWeight.BOLD, 14));
            entomologistsHeader.setTextFill(Color.DARKORANGE);
            content.getChildren().add(entomologistsHeader);

            for (int i = 0; i < entomologists.size(); i++) {
                Player player = entomologists.get(i);
                Label playerLabel = new Label((i + 1) + ". " + player.name + ": " + player.score + " pont");
                playerLabel.setFont(Font.font("System", 14));
                content.getChildren().add(playerLabel);
            }
        }

        getDialogPane().setContent(content);
        getButtonTypes().setAll(ButtonType.OK);
    }
}