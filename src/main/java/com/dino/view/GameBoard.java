package com.dino.view;

import com.dino.engine.Game;
import com.dino.tecton.Tecton;
import com.dino.util.EntityRegistry;
import com.dino.util.ObjectNamer;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameBoard implements ModelObserver {
    private Pane boardPane;
    private Canvas canvas;
    private GameController controller;
    private Map<Tecton, Polygon> tectonShapes;
    private Map<Object, Node> entityNodes;
    private final ObjectNamer namer = ObjectNamer.getInstance();
    private final EntityRegistry registry = EntityRegistry.getInstance();

    public GameBoard() {
        boardPane = new Pane();
        canvas = new Canvas(800, 600);
        tectonShapes = new HashMap<>();
        entityNodes = new HashMap<>();
        
        boardPane.getChildren().add(canvas);
        boardPane.setStyle("-fx-background-color: #e0e0e0;");
    }

    /**
     * UI komponens létrehozása
     */
    public Node createNode() {
        return boardPane;
    }

    /**
     * A teljes játéktér kirajzolása
     */
    public void render(Game game) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        // Háttér
        gc.setFill(Color.LIGHTGREEN);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        // Tektonok kirajzolása
        List<Tecton> tectons = game.getBoard().getAllTectons();
        drawTectons(gc, tectons);
    }

    /**
     * Tectonok kirajzolása
     */
    private void drawTectons(GraphicsContext gc, List<Tecton> tectons) {
        // Pozícionálás és méretezés
        int centerX = (int) (canvas.getWidth() / 2);
        int centerY = (int) (canvas.getHeight() / 2);
        int hexRadius = 50;
        
        // Középpont eltolás az első tekton helyzetéhez
        int startX = centerX - hexRadius * 3;
        int startY = centerY - hexRadius * 3;
        
        tectonShapes.clear();
        
        for (int i = 0; i < tectons.size(); i++) {
            Tecton tecton = tectons.get(i);
            
            // Egyszerű grid elrendezés (valós játékban érdemes a szomszédsági viszonyokat figyelembe venni)
            int row = i / 3;
            int col = i % 3;
            
            // Méhsejt elrendezés, páros sorok eltolva
            int posX = startX + col * (int)(hexRadius * 1.75);
            if (row % 2 == 1) {
                posX += hexRadius;
            }
            int posY = startY + row * (int)(hexRadius * 1.5);
            
            // Hatszög kirajzolása
            drawHexagon(gc, posX, posY, hexRadius, tecton);
            
            // Szomszédsági kapcsolatok rajzolása
            drawConnections(gc, tecton, tectons);
        }
    }
    
    /**
     * Szomszédsági kapcsolatok rajzolása
     */
    private void drawConnections(GraphicsContext gc, Tecton tecton, List<Tecton> allTectons) {
        if (tecton.getNeighbours() == null || tecton.getNeighbours().isEmpty()) return;
        
        String tectonName = registry.getNameOf(tecton);
        if (tectonName == null) return;
        
        int tectonIndex = allTectons.indexOf(tecton);
        int row1 = tectonIndex / 3;
        int col1 = tectonIndex % 3;
        
        // Középpont kiszámítása
        int centerX = (int) (canvas.getWidth() / 2);
        int centerY = (int) (canvas.getHeight() / 2);
        int hexRadius = 50;
        int startX = centerX - hexRadius * 3;
        int startY = centerY - hexRadius * 3;
        
        int x1 = startX + col1 * (int)(hexRadius * 1.75);
        if (row1 % 2 == 1) {
            x1 += hexRadius;
        }
        int y1 = startY + row1 * (int)(hexRadius * 1.5);
        
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(3);
        
        for (Tecton neighbour : tecton.getNeighbours()) {
            int neighbourIndex = allTectons.indexOf(neighbour);
            if (neighbourIndex >= 0) {
                int row2 = neighbourIndex / 3;
                int col2 = neighbourIndex % 3;
                
                int x2 = startX + col2 * (int)(hexRadius * 1.75);
                if (row2 % 2 == 1) {
                    x2 += hexRadius;
                }
                int y2 = startY + row2 * (int)(hexRadius * 1.5);
                
                gc.strokeLine(x1, y1, x2, y2);
            }
        }
    }
    
    /**
     * Egy hatszög kirajzolása
     */
    private void drawHexagon(GraphicsContext gc, int centerX, int centerY, int radius, Tecton tecton) {
        // Hatszög pontjainak kiszámítása
        double[] xPoints = new double[6];
        double[] yPoints = new double[6];
        
        for (int i = 0; i < 6; i++) {
            double angle = Math.PI / 3 * i;
            xPoints[i] = centerX + radius * Math.cos(angle);
            yPoints[i] = centerY + radius * Math.sin(angle);
        }
        
        // Tekton típusa alapján színezés
        String tectonType = tecton.getClass().getSimpleName();
        
        switch (tectonType) {
            case "SingleHyphaTecton":
                gc.setFill(Color.LIGHTBLUE);
                break;
            case "InfiniteHyphaTecton":
                gc.setFill(Color.LIGHTGREEN);
                break;
            case "KeepHyphaTecton":
                gc.setFill(Color.LIGHTYELLOW);
                break;
            case "NoFungiTecton":
                gc.setFill(Color.LIGHTPINK);
                break;
            case "ShortHyphaTecton":
                gc.setFill(Color.LIGHTGRAY);
                break;
            default:
                gc.setFill(Color.WHITE);
                break;
        }
        
        // Hatszög kitöltése és körvonala
        gc.fillPolygon(xPoints, yPoints, 6);
        
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokePolygon(xPoints, yPoints, 6);
        
        // Tecton nevének és típusának kiírása
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font(10));
        
        String tectonName = registry.getNameOf(tecton);
        String displayName = tectonName != null ? tectonName : "?";
        
        gc.fillText(displayName, centerX - 20, centerY - 5);
        gc.fillText(tectonType, centerX - 30, centerY + 10);
        
        // Gomba és rovarok megjelenítése (egyszerűsített)
        if (tecton.getFungus() != null) {
            gc.setFill(Color.DARKGREEN);
            gc.fillOval(centerX - 10, centerY - 25, 20, 20);
        }
        
        if (tecton.getInsects() != null && !tecton.getInsects().isEmpty()) {
            gc.setFill(Color.RED);
            gc.fillOval(centerX + 15, centerY - 15, 10, 10);
        }
    }

    /**
     * A játéktér frissítése
     */
    @Override
    public void update(Game game) {
        render(game);
    }

    /**
     * Egy adott tecton kiemelése
     */
    public void highlightTecton(Tecton t) {
        // Későbbi implementáció
    }
}