package com.dino.view;

import com.dino.core.Hexagon;
import com.dino.core.Fungus;
import com.dino.engine.Game;
import com.dino.player.Mycologist;
import com.dino.tecton.Tecton;
import com.dino.tecton.SingleHyphaTecton;
import com.dino.tecton.InfiniteHyphaTecton;
import com.dino.tecton.KeepHyphaTecton;
import com.dino.tecton.NoFungiTecton;
import com.dino.tecton.ShortHyphaTecton;
import com.dino.util.EntityRegistry;
import com.dino.util.ObjectNamer;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class GuiBoard implements ModelObserver {
    private Pane boardPane;
    private Map<Integer, Polygon> hexagonShapes; // id -> polygon
    private Map<Integer, Double[]> hexagonPositions; // id -> [x, y]
    private Map<Tecton, Color> tectonColors;
    private EntityRegistry registry;
    private ObjectNamer namer;
    private Set<Integer> removedHexagons; // Az eltávolított hexagonok ID-i
    private Random random = new Random();

    // Hexagon méret és elrendezés változók - nagyobb értékek a jobb láthatóság
    // érdekében
    private final double HEX_SIZE = 35; // A hatszög oldalhossza - növelve a jobb láthatóságért
    private final double HEX_HORIZ_DIST = HEX_SIZE * Math.sqrt(3); // Vízszintes távolság a középpontok között
    private final double HEX_VERT_DIST = HEX_SIZE * 1.5; // Függőleges távolság a középpontok között

    // Hézagok beállítása
    private final double GAP_PERCENTAGE = 0.1; // A hexagonok 10%-a lesz hézag

    public GuiBoard() {
        boardPane = new Pane();
        boardPane.setPrefSize(800, 600);
        boardPane.setStyle("-fx-background-color: #333333;");

        hexagonShapes = new HashMap<>();
        hexagonPositions = new HashMap<>();
        tectonColors = new HashMap<>();
        removedHexagons = new HashSet<>();
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
        hexagonPositions.clear();
        tectonColors.clear();
        removedHexagons.clear();

        // 1. Először határozzuk meg a tecton színeket
        setupTectonColors(game);

        // 2. Hexagon rács kirajzolása - tökéletes méhsejt mintával, hézagokkal
        createHexagonGrid(10, 10);

        // 3. Hexagonok színezése a tectonok alapján
        colorHexagonsByTecton(game);

        drawFungi(game);
    }

    // TODO 
    // Nem típusonként hanem tektononként színezünk
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

    private void createHexagonGrid(int rows, int cols) {
        // TODO ablak középén legyen a map
        double centerX = boardPane.getPrefWidth() / 2;
        double centerY = boardPane.getPrefHeight() / 2;

        // Számítsuk ki a rács szélességét és magasságát
        double gridWidth = cols * HEX_HORIZ_DIST;
        double gridHeight = rows * HEX_VERT_DIST + HEX_SIZE / 2;

        // Kezdőpont, hogy középre helyezzük a rácsot
        double startX = centerX - gridWidth / 2 + HEX_HORIZ_DIST / 2;
        double startY = centerY - gridHeight / 2 + HEX_SIZE;

        // Létrehozzuk az összes lehetséges hexagon ID-t
        List<Integer> allHexIds = new ArrayList<>();
        int totalHexagons = rows * cols;
        for (int i = 1; i <= totalHexagons; i++) {
            allHexIds.add(i);
        }

        // Véletlenszerűen kiválasztjuk a hézagokat (a hexagonok GAP_PERCENTAGE %-a)
        int gapCount = (int) (totalHexagons * GAP_PERCENTAGE);
        for (int i = 0; i < gapCount; i++) {
            if (!allHexIds.isEmpty()) {
                int randomIndex = random.nextInt(allHexIds.size());
                int hexId = allHexIds.get(randomIndex);
                removedHexagons.add(hexId);
                allHexIds.remove(randomIndex);
            }
        }

        // Minden sorban és oszlopban létrehozunk egy hatszöget
        int id = 1;
        for (int row = 0; row < rows; row++) {
            // Páratlan sorok eltolása
            double rowOffset = (row % 2 == 1) ? HEX_HORIZ_DIST / 2 : 0;

            for (int col = 0; col < cols; col++) {
                // Hexagon középpontjának kiszámítása
                double x = startX + col * HEX_HORIZ_DIST + rowOffset;
                double y = startY + row * HEX_VERT_DIST;

                // Hexagon létrehozása a pontos pozícióban
                Polygon hexagon = createHexagon(x, y);

                // Hézag esetén feketére színezzük
                if (removedHexagons.contains(id)) {
                    hexagon.setFill(Color.BLACK);
                } else {
                    hexagon.setFill(Color.WHITE);
                    // TODO
                }

                hexagon.setStroke(Color.BLACK);
                hexagon.setStrokeWidth(1);

                // Hexagon tárolása
                hexagonShapes.put(id, hexagon);
                hexagonPositions.put(id, new Double[] { x, y });

                // Hexagon és azonosító hozzáadása a képernyőhöz
                boardPane.getChildren().add(hexagon);

                // ID kiírása (kivéve a hézagokon)
                if (!removedHexagons.contains(id)) {
                    Text idText = new Text(x - 5, y + 5, String.valueOf(id));
                    idText.setFont(Font.font(10));
                    boardPane.getChildren().add(idText);
                }

                id++;
            }
        }
    }

    private void colorHexagonsByTecton(Game game) {
        // Minden tecton minden hexagonját színezzük
        for (Tecton tecton : game.getBoard().getAllTectons()) {
            Color color = tectonColors.get(tecton);

            if (color != null && tecton.hexagons != null) {
                StringBuilder hexIds = new StringBuilder();

                for (Hexagon hexagon : tecton.hexagons) {
                    int hexId = hexagon.getId();
                    hexIds.append(hexId).append(",");

                    // Csak akkor színezzük, ha nem hézag
                    if (!removedHexagons.contains(hexId)) {
                        Polygon hexShape = hexagonShapes.get(hexId);

                        if (hexShape != null) {
                            hexShape.setFill(color);
                        }
                    }
                }

                // Tecton információinak kiírása
                if (!tecton.hexagons.isEmpty()) {
                    // Keressünk egy nem-hézag hexagont, ahova kiírhatjuk az infót
                    for (Hexagon hex : tecton.hexagons) {
                        int hexId = hex.getId();
                        if (!removedHexagons.contains(hexId)) {
                            Double[] pos = hexagonPositions.get(hexId);

                            if (pos != null) {
                                String tectonName = registry.getNameOf(tecton);
                                String tectonType = tecton.getClass().getSimpleName();

                                Text nameText = new Text(pos[0] - 30, pos[1] - 10, tectonName);
                                nameText.setFont(Font.font(8));

                                Text typeText = new Text(pos[0] - 30, pos[1] + 10, tectonType);
                                typeText.setFont(Font.font(8));

                                boardPane.getChildren().addAll(nameText, typeText);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private Polygon createHexagon(double centerX, double centerY) {
        Polygon hexagon = new Polygon();

        // 6 pont, minden 60 fokkal - pontos szabályos hatszög
        for (int i = 0; i < 6; i++) {
            double angleDeg = 60 * i - 30; // -30 fok kezdőszög a vízszintes hexagonhoz
            double angleRad = Math.PI / 180 * angleDeg;

            double x = centerX + HEX_SIZE * Math.cos(angleRad);
            double y = centerY + HEX_SIZE * Math.sin(angleRad);

            hexagon.getPoints().addAll(x, y);
        }

        return hexagon;
    }

    public void highlightTecton(Tecton t) {
        if (t.hexagons != null) {
            for (Hexagon hex : t.hexagons) {
                // Csak a nem-hézag hexagonokat emeljük ki
                if (!removedHexagons.contains(hex.getId())) {
                    Polygon hexShape = hexagonShapes.get(hex.getId());
                    if (hexShape != null) {
                        hexShape.setStroke(Color.RED);
                        hexShape.setStrokeWidth(3);
                    }
                }
            }
        }
    }

    private final Map<Integer, Integer> fungiCountPerHex = new HashMap<>();

    public void drawFungi(Game game){
        fungiCountPerHex.clear();

        for (Mycologist m: game.getAllMycologists()){
            for (Fungus f : m.getMushrooms()){
                Tecton tecton = f.getTecton();

                List<Hexagon> hexagons = new ArrayList<>(tecton.hexagons);
                Collections.shuffle(hexagons);

                Hexagon chosenHexagon = null;
                for (Hexagon h : hexagons) {
                    if (!removedHexagons.contains(h.getId())) {
                        chosenHexagon = h;
                        break;
                    }
                }
                if (chosenHexagon == null) break;

                int hexagonId = chosenHexagon.getId();
                Double[] position = hexagonPositions.get(hexagonId);
                int indexInHexagon = fungiCountPerHex.getOrDefault(hexagonId, 0);
                fungiCountPerHex.put(hexagonId, indexInHexagon + 1);

                //double offsetX = (indexInHexagon % 2.0) * 15.0 - 7.5;
                //double offsetY = (indexInHexagon / 2.0) * 15.0;

                //Point2D location = new Point2D(position[0] + offsetX, position[1] + offsetY);
                Point2D location = new Point2D(position[0], position[1]);
                FungusEntity fEntity = new FungusEntity(f);
                fEntity.location = location;

                Node fungusNode = fEntity.draw();
                if (fungusNode != null) {
                    boardPane.getChildren().add(fungusNode);
                }
            }
        }
    }
}