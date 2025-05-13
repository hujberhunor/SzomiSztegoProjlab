package com.dino.view;

import com.dino.core.Hexagon;
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
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.Map;

public class GameBoard implements ModelObserver {
    private Pane boardPane;
    private Map<Integer, Polygon> hexagonShapes; // id -> polygon
    private Map<Tecton, Color> tectonColors;
    private EntityRegistry registry;
    private ObjectNamer namer;
    
    // Hexagon méret és elrendezés változók
    private final double HEX_SIZE = 30;
    private final double HEX_WIDTH = HEX_SIZE * Math.sqrt(3);
    private final double HEX_HEIGHT = HEX_SIZE * 2;
    
    public GameBoard() {
        boardPane = new Pane();
        boardPane.setPrefSize(800, 600);
        boardPane.setStyle("-fx-background-color: lightgreen;");
        
        hexagonShapes = new HashMap<>();
        tectonColors = new HashMap<>();
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
        hexagonShapes.clear();
        tectonColors.clear();
        
        // 1. Először határozzuk meg a tecton színeket
        setupTectonColors(game);
        
        // 2. Hexagon rács kirajzolása
        drawHexagonGrid(10, 10);
        
        // 3. Hexagonok színezése a tectonok alapján (később csináljuk)
        colorHexagonsByTecton(game);
    }
    
    private void setupTectonColors(Game game) {
        for (Tecton tecton : game.getBoard().getAllTectons()) {
            Color color;
            
            if (tecton instanceof SingleHyphaTecton) {
                color = Color.LIGHTBLUE;
            } else if (tecton instanceof KeepHyphaTecton) {
                color = Color.LIGHTYELLOW;
            } else if (tecton instanceof NoFungiTecton) {
                color = Color.LIGHTPINK;
            } else if (tecton instanceof ShortHyphaTecton) {
                color = Color.LIGHTGRAY;
            } else if (tecton instanceof InfiniteHyphaTecton) {
                color = Color.LIGHTGREEN;
            } else {
                color = Color.WHITE;
            }
            
            tectonColors.put(tecton, color);
        }
    }
    
    private void drawHexagonGrid(int rows, int cols) {
        double centerX = boardPane.getPrefWidth() / 2;
        double centerY = boardPane.getPrefHeight() / 2;
        
        // Rács kezdőpontja, hogy középre helyezzük
        double startX = centerX - (cols * HEX_WIDTH * 0.75) / 2;
        double startY = centerY - (rows * HEX_HEIGHT * 0.75) / 2;
        
        // Minden sorban és oszlopban létrehozunk egy hexagont
        int id = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                // Hexagon pozíciójának kiszámítása
                double x = startX + col * HEX_WIDTH * 0.75;
                double y = startY + row * HEX_HEIGHT * 0.75;
                
                // Páratlan sorokban eltoljuk a hexagonokat
                if (row % 2 == 1) {
                    x += HEX_WIDTH * 0.375;
                }
                
                // Hexagon létrehozása
                Polygon hexagon = createHexagon(x, y);
                hexagon.setFill(Color.WHITE); // Alapértelmezett színezés
                hexagon.setStroke(Color.BLACK);
                hexagon.setStrokeWidth(1);
                
                // Hexagon azonosító (sorszám)
                id++;
                hexagonShapes.put(id, hexagon);
                
                // Hexagon hozzáadása a pane-hez
                boardPane.getChildren().add(hexagon);
                
                // ID kiírása (később törölhető)
                Text idText = new Text(x - 5, y, String.valueOf(id));
                idText.setFont(Font.font(10));
                boardPane.getChildren().add(idText);
            }
        }
    }
    
    private void colorHexagonsByTecton(Game game) {
        // Minden tecton minden hexagonját színezzük
        for (Tecton tecton : game.getBoard().getAllTectons()) {
            Color color = tectonColors.get(tecton);
            
            if (color != null && tecton.hexagons != null) {
                for (Hexagon hexagon : tecton.hexagons) {
                    Polygon hexShape = hexagonShapes.get(hexagon.getId());
                    
                    if (hexShape != null) {
                        hexShape.setFill(color);
                        
                        // A hexagon közepét megkeressük, hogy a szöveget oda írjuk
                        double centerX = 0, centerY = 0;
                        for (int i = 0; i < 6; i++) {
                            centerX += hexShape.getPoints().get(i*2);
                            centerY += hexShape.getPoints().get(i*2+1);
                        }
                        centerX /= 6;
                        centerY /= 6;
                        
                        // Tecton neve és típusa
                        String tectonName = registry.getNameOf(tecton);
                        String tectonType = tecton.getClass().getSimpleName();
                        
                        // Csak egyszer írjuk ki tecton információit minden tectonra
                        if (tecton.hexagons.indexOf(hexagon) == 0) {
                            Text nameText = new Text(centerX - 30, centerY - 10, tectonName);
                            nameText.setFont(Font.font(8));
                            
                            Text typeText = new Text(centerX - 30, centerY + 10, tectonType);
                            typeText.setFont(Font.font(8));
                            
                            boardPane.getChildren().addAll(nameText, typeText);
                        }
                    }
                }
            }
        }
    }
    
    private Polygon createHexagon(double centerX, double centerY) {
        Polygon hexagon = new Polygon();
        
        for (int i = 0; i < 6; i++) {
            double angle = 2 * Math.PI / 6 * i;
            double xPoint = centerX + HEX_SIZE * Math.cos(angle);
            double yPoint = centerY + HEX_SIZE * Math.sin(angle);
            hexagon.getPoints().addAll(xPoint, yPoint);
        }
        
        return hexagon;
    }
    
    public void highlightTecton(Tecton t) {
        if (t.hexagons != null) {
            for (Hexagon hex : t.hexagons) {
                Polygon hexShape = hexagonShapes.get(hex.getId());
                if (hexShape != null) {
                    hexShape.setStroke(Color.RED);
                    hexShape.setStrokeWidth(3);
                }
            }
        }
    }
}