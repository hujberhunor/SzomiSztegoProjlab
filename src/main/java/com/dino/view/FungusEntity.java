package com.dino.view;

import com.dino.core.Fungus;
import com.dino.player.Mycologist;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FungusEntity extends Entity {
    protected Fungus fungus;
    /*
    protected Mycologist mycologist;
    protected int charge;
    protected int lifespan;
    */
    public FungusEntity(){
        this.fungus = new Fungus();
        fungus.setSpecies(null);
        fungus.setCharge(0);
        fungus.setLifespan(0);
    }

    public FungusEntity(Fungus f){
        this.fungus = f;
    }

    /**
     * A gomba kirajzolása
     * @return A kirajzolt gombát tartalmazó Node objektum
     */
    @Override
    public Node draw() {
        int playerId = fungus.getSpecies().getIdForDrawing();
        String imagePath = "images/fungus" + playerId + ".png";
        Image image;
        try {
            image = new Image(getClass().getResourceAsStream(imagePath));
        } catch (Exception e){
            System.err.println("Could not load image for Fungus: " + imagePath);
            return null;
        }
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(20);
        imageView.setFitHeight(20);
        imageView.setPreserveRatio(true);
        
        if (location != null) {
            imageView.setTranslateX(location.getX());
            imageView.setTranslateY(location.getY());
        }

        return imageView;
    }
}