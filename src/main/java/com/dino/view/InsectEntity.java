package com.dino.view;

import com.dino.core.*;
import com.dino.player.Entomologist;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;

public class InsectEntity extends Entity {
    protected Insect insect;
    /*
    protected int movementPoints;
    protected List<Spore> effects;
    protected Entomologist entomologist;
    */
    public InsectEntity(Insect i) {
        insect = i;
        // effects = i.getEffects();
        // entomologist = i.getEntomologist();
    }

    /**
     * A rovar kirajzolása
     * @return A kirajzolt rovart tartalmazó Node objektum
     */
    @Override
    public Node draw() {
        int playerId = insect.getEntomologist().getIdForDrawing();
        String imagePath = "/images/insect" + playerId + ".png";
        Image image;
        try {
            image = new Image(getClass().getResourceAsStream(imagePath));
        } catch (Exception e){
            System.err.println("Could not load image for Insect: " + imagePath);
            return null;
        }
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(40);
        imageView.setFitHeight(40);
        imageView.setPreserveRatio(true);
        
        if (location != null) {
            imageView.setTranslateX(location.getX() - imageView.getFitWidth() / 2);
            imageView.setTranslateY(location.getY() - imageView.getFitWidth() / 2);
        }

        return imageView;
    }
}