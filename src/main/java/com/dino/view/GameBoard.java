package com.dino.view;

import com.dino.engine.Game;
import com.dino.tecton.Tecton;
import com.dino.tecton.SingleHyphaTecton;
import com.dino.tecton.InfiniteHyphaTecton;
import com.dino.tecton.KeepHyphaTecton;
import com.dino.tecton.NoFungiTecton;
import com.dino.tecton.ShortHyphaTecton;
import com.dino.util.EntityRegistry;
import com.dino.util.ObjectNamer;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameBoard implements ModelObserver {
    private Pane boardPane;
    private Map<Tecton, Polygon> tectonShapes;
    private EntityRegistry registry;
    private ObjectNamer namer;
    
    // Hexagon méret és elrendezés változók
    private final double HEX_SIZE = 40; // Hatszögek mérete
    private final double WIDTH_FACTOR = Math.sqrt(3);
    private final double HEX_WIDTH = HEX_SIZE * WIDTH_FACTOR;
    private final double HEX_HEIGHT = HEX_SIZE * 2;
    
    public GameBoard() {
        boardPane = new Pane();
        boardPane.setPrefSize(800, 600);
        boardPane.setStyle("-fx-background-color: lightgreen;");
        
        tectonShapes = new HashMap<>();
        registry = EntityRegistry.getInstance();
        namer = ObjectNamer.getInstance();
    }
    
    public Node createNode() {
        return boardPane;
    }
    
    @Override
    public void update(Game game) {
        render(game);
    }
    
    public void render(Game game) {
        boardPane.getChildren().clear();
        tectonShapes.clear();
        
        List<Tecton> tectons = game.getBoard().getAllTectons();
        drawTectons(tectons);
    }
    
    private void drawTectons(List<Tecton> tectons) {
        int rows = (int)Math.sqrt(tectons.size() * 2);
        int cols = (int)Math.ceil(tectons.size() / (double)rows);
        
        // Középpont kiszámítása
        double centerX = boardPane.getPrefWidth() / 2;
        double centerY = boardPane.getPrefHeight() / 2;
        
        // Eltolás kiszámítása, hogy középre helyezzük a méhsejt mintát
        double startX = centerX - (cols * 0.75 * HEX_WIDTH) / 2;
        double startY = centerY - (rows * HEX_HEIGHT * 0.75) / 2;
        
        int index = 0;
        for (int row = 0; row < rows && index < tectons.size(); row++) {
            int colsInRow = (row % 2 == 0) ? cols : cols - 1;
            
            for (int col = 0; col < colsInRow && index < tectons.size(); col++) {
                Tecton tecton = tectons.get(index);
                
                // Hexagon pozíciójának kiszámítása
                double x = startX + col * HEX_WIDTH * 0.75;
                if (row % 2 == 1) {
                    x += HEX_WIDTH * 0.375; // Páratlan sorok eltolása
                }
                double y = startY + row * HEX_HEIGHT * 0.75;
                
                // Hexagon kirajzolása
                Polygon hexagon = createHexagon(x, y);
                styleHexagon(hexagon, tecton);
                
                boardPane.getChildren().add(hexagon);
                tectonShapes.put(tecton, hexagon);
                
                // Szöveg hozzáadása
                String tectonName = registry.getNameOf(tecton);
                String tectonType = tecton.getClass().getSimpleName();
                
                Text nameText = new Text(x - HEX_SIZE * 0.5, y - 5, tectonName);
                nameText.setFont(Font.font(10));
                nameText.setTextAlignment(TextAlignment.CENTER);
                
                Text typeText = new Text(x - HEX_SIZE * 0.5, y + 10, tectonType);
                typeText.setFont(Font.font(8));
                typeText.setTextAlignment(TextAlignment.CENTER);
                
                boardPane.getChildren().addAll(nameText, typeText);
                
                index++;
            }
        }
        
        // Szomszédok közötti kapcsolatok kirajzolása
        drawConnections(tectons);
    }
    
    private Polygon createHexagon(double x, double y) {
        Polygon hexagon = new Polygon();
        
        for (int i = 0; i < 6; i++) {
            double angle = 2 * Math.PI / 6 * i;
            double xPoint = x + HEX_SIZE * Math.cos(angle);
            double yPoint = y + HEX_SIZE * Math.sin(angle);
            hexagon.getPoints().addAll(xPoint, yPoint);
        }
        
        return hexagon;
    }
    
    private void styleHexagon(Polygon hexagon, Tecton tecton) {
        // Tecton típusa alapján színezés
        if (tecton instanceof SingleHyphaTecton) {
            hexagon.setFill(Color.LIGHTBLUE);
        } else if (tecton instanceof KeepHyphaTecton) {
            hexagon.setFill(Color.LIGHTYELLOW);
        } else if (tecton instanceof NoFungiTecton) {
            hexagon.setFill(Color.LIGHTPINK);
        } else if (tecton instanceof ShortHyphaTecton) {
            hexagon.setFill(Color.LIGHTGRAY);
        } else if (tecton instanceof InfiniteHyphaTecton) {
            hexagon.setFill(Color.LIGHTGREEN);
        } else {
            hexagon.setFill(Color.WHITE);
        }
        
        hexagon.setStroke(Color.BLACK);
        hexagon.setStrokeWidth(1.5);
    }
    
    private void drawConnections(List<Tecton> tectons) {
        for (Tecton tecton : tectons) {
            if (tecton.getNeighbours() == null) continue;
            
            Polygon p1 = tectonShapes.get(tecton);
            if (p1 == null) continue;
            
            // A hexagon középpontjának kiszámítása
            double centerX1 = p1.getPoints().get(0);
            double centerY1 = p1.getPoints().get(1);
            for (int i = 1; i < 6; i++) {
                centerX1 += p1.getPoints().get(i*2);
                centerY1 += p1.getPoints().get(i*2+1);
            }
            centerX1 /= 6;
            centerY1 /= 6;
            
            for (Tecton neighbour : tecton.getNeighbours()) {
                Polygon p2 = tectonShapes.get(neighbour);
                if (p2 == null) continue;
                
                // A szomszéd hexagon középpontjának kiszámítása
                double centerX2 = p2.getPoints().get(0);
                double centerY2 = p2.getPoints().get(1);
                for (int i = 1; i < 6; i++) {
                    centerX2 += p2.getPoints().get(i*2);
                    centerY2 += p2.getPoints().get(i*2+1);
                }
                centerX2 /= 6;
                centerY2 /= 6;
                
                // Összekötő vonal rajzolása
                Line line = new Line(centerX1, centerY1, centerX2, centerY2);
                line.setStroke(Color.GRAY);
                line.setStrokeWidth(1.0);
                
                // Vonalak háttérbe helyezése
                boardPane.getChildren().add(0, line);
            }
        }
    }
    
    public void highlightTecton(Tecton t) {
        Polygon hexagon = tectonShapes.get(t);
        if (hexagon != null) {
            hexagon.setStroke(Color.RED);
            hexagon.setStrokeWidth(3);
        }
    }
}