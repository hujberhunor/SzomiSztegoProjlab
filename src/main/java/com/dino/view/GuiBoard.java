package com.dino.view;

import com.dino.core.Hexagon;
import com.dino.core.Spore;
import com.dino.engine.Game;
import com.dino.tecton.Tecton;
import com.dino.util.EntityRegistry;
import com.dino.util.ObjectNamer;

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.*;

public class GuiBoard implements ModelObserver {
    private Pane boardPane;
    private Map<Integer, Polygon> hexagonShapes; // id -> polygon
    private Map<Integer, Double[]> hexagonPositions; // id -> [x, y]
    private Map<Tecton, Color> tectonColors;
    private EntityRegistry registry;
    private ObjectNamer namer;
    private Set<Integer> existingHexagonIds; // Csak a létező hexagonok ID-i
    private final int GRID_SIZE = 10; // A rács mérete 10x10

    // Hexagon méret és elrendezés változók
    private final double HEX_SIZE = 35; // A hatszög oldalhossza
    private final double HEX_HORIZ_DIST = HEX_SIZE * Math.sqrt(3); // Vízszintes távolság a középpontok között
    private final double HEX_VERT_DIST = HEX_SIZE * 1.5; // Függőleges távolság a középpontok között

    public GuiBoard() {
        boardPane = new Pane();
        boardPane.setPrefSize(800, 600);
        boardPane.setStyle("-fx-background-color: #333333;");

        hexagonShapes = new HashMap<>();
        hexagonPositions = new HashMap<>();
        tectonColors = new HashMap<>();
        existingHexagonIds = new HashSet<>();
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
        existingHexagonIds.clear();

        // 1. Először azonosítsuk a létező hexagonokat a tectonok alapján
        identifyExistingHexagons(game);

        // 2. Tecton színek meghatározása
        setupTectonColors(game);

        // 3. A teljes hexagon rács pozíciójának kiszámítása
        calculateGridPositions();

        // 4. Csak a létező hexagonok kirajzolása
        drawExistingHexagons();

        // 5. Hexagonok színezése a tectonok alapján
        colorHexagonsByTecton(game);
    }

    private void identifyExistingHexagons(Game game) {
        // Csak azok a hexagonok léteznek, amelyek valamelyik tectonhoz tartoznak
        for (Tecton tecton : game.getBoard().getAllTectons()) {
            if (tecton.hexagons != null) {
                for (Hexagon hexagon : tecton.hexagons) {
                    if (hexagon != null) {
                        existingHexagonIds.add(hexagon.getId());
                    }
                }
            }
        }
    }

    private void setupTectonColors(Game game) {
        List<Color> possibleColors = Arrays.asList(
            Color.web("FFADAD"), Color.web("FFD6A5"), Color.web("FDFFBF"), 
            Color.web("CAFFBF"), Color.web("9BF6FF"), Color.web("A0C4FF"), 
            Color.web("BDB2FF"), Color.web("FFC6FF")
        );

        Color selectedColor = Color.MEDIUMVIOLETRED;
        Random rnd = new Random();

        for (Tecton tecton : game.getBoard().getAllTectons()) {
            //a tektonnak még adható színek listája
            List<Color> availableColors = new ArrayList<>(possibleColors);

            boolean isUnique = false; //egyedi-e a szín, vagyis nincs olyan szomszédja, ami ugyanilyen színű
            while(!isUnique && !(availableColors.isEmpty())) {
                isUnique = true;

                //a színlistából találomra kiválasztunk egyet
                int selectedIndex = rnd.nextInt(availableColors.size());
                selectedColor = availableColors.get(selectedIndex);

                //Leellenőrizzük, hogy van-e olyan szomszédos tekton, ami ugyanolyan színű, mint amit kiválasztottunk.
                for(Tecton neigbourTecton : tecton.getNeighbours()){
                    if(neigbourTecton.getColor() == null){
                        break;
                    }
                    if(neigbourTecton.getColor().equals(selectedColor)){
                        //Ha a szomszéd már ugyanolyan színű, eltávolítjuk a lehetséges színek listájából a színt
                        isUnique = false;
                        availableColors.remove(selectedColor);
                        break;
                    }
                }
            }

            //Beállítjuk a tekton színét
            tecton.setColor(selectedColor);
            tectonColors.put(tecton, selectedColor);
        }
    }

    private void calculateGridPositions() {
        double centerX = boardPane.getPrefWidth() / 2;
        double centerY = boardPane.getPrefHeight() / 2;

        // Számítsuk ki a rács szélességét és magasságát
        double gridWidth = GRID_SIZE * HEX_HORIZ_DIST;
        double gridHeight = GRID_SIZE * HEX_VERT_DIST + HEX_SIZE / 2;

        // Kezdőpont, hogy középre helyezzük a rácsot
        double startX = centerX - gridWidth / 2 + HEX_HORIZ_DIST / 2;
        double startY = centerY - gridHeight / 2 + HEX_SIZE;

        // Biztosítsuk, hogy a 0-ás ID-hez is legyen pozíció (ha véletlenül létezik)
        hexagonPositions.put(0, new Double[]{startX, startY});
        
        // Számítsuk ki az összes pozíciót (a létező és nem létező hexagonokét is)
        int id = 1;
        for (int row = 0; row < GRID_SIZE; row++) {
            // Páratlan sorok eltolása
            double rowOffset = (row % 2 == 1) ? HEX_HORIZ_DIST / 2 : 0;

            for (int col = 0; col < GRID_SIZE; col++) {
                // Hexagon középpontjának kiszámítása
                double x = startX + col * HEX_HORIZ_DIST + rowOffset;
                double y = startY + row * HEX_VERT_DIST;

                // Csak elmentjük a pozíciókat, nem rajzolunk még
                hexagonPositions.put(id, new Double[]{x, y});
                id++;
            }
        }
    }

    private void drawExistingHexagons() {
        // Csak a létező hexagonokat rajzoljuk ki
        for (Integer hexId : existingHexagonIds) {
            Double[] position = hexagonPositions.get(hexId);
            if (position != null) {
                double x = position[0];
                double y = position[1];

                // Hexagon létrehozása a pontos pozícióban
                Polygon hexagon = createHexagon(x, y);
                hexagon.setFill(Color.WHITE);
                hexagon.setStroke(Color.BLACK);
                hexagon.setStrokeWidth(1);

                // Hexagon tárolása
                hexagonShapes.put(hexId, hexagon);

                // Hexagon és azonosító hozzáadása a képernyőhöz
                boardPane.getChildren().add(hexagon);

                // ID kiírása
                Text idText = new Text(x - 5, y + 5, String.valueOf(hexId));
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
                StringBuilder hexIds = new StringBuilder();
                
                // Ellenőrizzük, hogy van-e spóra a tectonon
                boolean hasSpores = tecton.spores != null && !tecton.spores.isEmpty();

                for (Hexagon hexagon : tecton.hexagons) {
                    int hexId = hexagon.getId();
                    hexIds.append(hexId).append(",");

                    Polygon hexShape = hexagonShapes.get(hexId);
                    if (hexShape != null) {
                        // Alap szín beállítása
                        if (hasSpores) {
                            // Ha van spóra, speciális mintát használunk
                            hexShape.setFill(createSporePatternFill(color));
                            hexShape.setStroke(Color.YELLOW);
                            hexShape.setStrokeWidth(2);
                        } else {
                            // Ha nincs spóra, normál színezés
                            hexShape.setFill(color);
                        }
                    }
                }

                // Tecton információinak kiírása
                if (!tecton.hexagons.isEmpty()) {
                    // Keressünk egy hexagont, ahova kiírhatjuk az infót
                    for (Hexagon hex : tecton.hexagons) {
                        int hexId = hex.getId();
                        Double[] pos = hexagonPositions.get(hexId);

                        if (pos != null) {
                            String tectonName = registry.getNameOf(tecton);
                            String tectonType = tecton.getClass().getSimpleName();

                            Text nameText = new Text(pos[0] - 30, pos[1] - 10, tectonName);
                            nameText.setFont(Font.font(8));

                            Text typeText = new Text(pos[0] - 30, pos[1] + 10, tectonType);
                            typeText.setFont(Font.font(8));
                            
                            // Ha van spóra, azt is kiírjuk
                            if (hasSpores) {
                                Text sporeText = new Text(pos[0] - 30, pos[1] + 20, "Spores: " + tecton.spores.size());
                                sporeText.setFont(Font.font(8));
                                sporeText.setFill(Color.YELLOW);
                                boardPane.getChildren().add(sporeText);
                            }

                            boardPane.getChildren().addAll(nameText, typeText);
                            break;
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

    /**
     * Spóra mintázatú kitöltés létrehozása
     * @param baseColor Az alap szín
     * @return A spóra mintázatú kitöltés
     */
    private Paint createSporePatternFill(Color baseColor) {
        // Világosabb variációja az alapszínnek a spóra pöttyökhöz
        Color spotColor = baseColor.brighter().brighter();
        
        // Kép létrehozása a textúrához
        Canvas canvas = new Canvas(20, 20);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        // Alapszín kitöltése
        gc.setFill(baseColor);
        gc.fillRect(0, 0, 20, 20);
        
        // Spóra pöttyök rajzolása
        gc.setFill(spotColor);
        gc.fillOval(5, 5, 3, 3);
        gc.fillOval(12, 7, 2, 2);
        gc.fillOval(8, 14, 3, 3);
        
        // Kép készítése a mintázathoz
        WritableImage image = new WritableImage(20, 20);
        canvas.snapshot(null, image);
        
        // Mintázat visszaadása
        return new ImagePattern(image);
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