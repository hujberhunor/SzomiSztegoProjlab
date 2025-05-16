package com.dino.view;

import com.dino.core.Hexagon;
import com.dino.core.Insect;
import com.dino.core.Spore;
import com.dino.engine.Game;
import com.dino.tecton.Tecton;
import com.dino.util.EntityRegistry;
import com.dino.util.ObjectNamer;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Popup;

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

    private Popup tectonInfoPopup;
    private Label tectonInfoLabel;

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

        initializeTectonInfoPopup();
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

    /**
     * A tektonok színezéséért felelős függvény
     * @param game - a játékot megkapja paraméterként, hogy hozzá tudjon férni
     */
    private void setupTectonColors (Game game) {
        //List<Color> possibleColors = Arrays.asList(Color.LIGHTBLUE, Color.LIGHTYELLOW, Color.LIGHTPINK, Color.LIGHTGRAY, Color.LIGHTGREEN);
        List<Color> possibleColors = Arrays.asList(Color.web("FFADAD"), Color.web("FFD6A5"), Color.web("FDFFBF"), Color.web("CAFFBF"), Color.web("9BF6FF"), Color.web("A0C4FF"), Color.web("BDB2FF"), Color.web("FFC6FF"));
        //List<Color> possibleColors = Arrays.asList(Color.web("#557174"), Color.web("#798f7a"), Color.web("#9dad7f"), Color.web("#b2be9b"), Color.web("#c7cfb7"), Color.web("#f7f7e8"));

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
                        //Ha a szomszéd már ugyanolyan színű, eltávolítjuk a lehetséges színek listájából a színt, és újra lefut a ciklus, vagyis új színt választunk.
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

                hexagon.setUserData(hexId);
                hexagon.setOnMouseClicked(event -> handleHexagonClick(event, hexId));

                // Hexagon tárolása
                hexagonShapes.put(hexId, hexagon);

                // Hexagon és azonosító hozzáadása a képernyőhöz
                boardPane.getChildren().add(hexagon);

                // ID kiírása
                Text idText = new Text(x - 5, y + 5, String.valueOf(hexId));
                idText.setFont(Font.font(10));
                idText.setOnMouseClicked(event -> handleHexagonClick(event, hexId));
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

                for (Hexagon hexagon : tecton.hexagons) {
                    int hexId = hexagon.getId();
                    hexIds.append(hexId).append(",");

                    Polygon hexShape = hexagonShapes.get(hexId);
                    if (hexShape != null) {
                        hexShape.setFill(color);
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

    private void initializeTectonInfoPopup() {
        tectonInfoPopup = new Popup();
        tectonInfoPopup.setAutoHide(true);

        tectonInfoLabel = new Label();
        tectonInfoLabel.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); " +
                "-fx-padding: 10; " +
                "-fx-border-color: #aaaaaa; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5;");

        tectonInfoPopup.getContent().add(tectonInfoLabel);
    }

    private void handleHexagonClick(MouseEvent event, int hexId) {
        // Find the tecton that contains this hexagon
        Tecton clickedTecton = findTectonByHexagonId(hexId);

        if (clickedTecton != null) {
            // Prepare the tecton information
            String tectonInfo = buildTectonInfo(clickedTecton);

            // Update the label and show the popup
            tectonInfoLabel.setText(tectonInfo);
            tectonInfoPopup.show(boardPane.getScene().getWindow(),
                    event.getScreenX() + 10,
                    event.getScreenY() + 10);
        }
    }

    private Tecton findTectonByHexagonId(int hexId) {
        Game game = Game.getInstance();

        for (Tecton tecton : game.getBoard().getAllTectons()) {
            for (Hexagon hex : tecton.getHexagons()) {
                if (hex.getId() == hexId) {
                    return tecton;
                }
            }
        }

        return null;
    }

    private String buildTectonInfo(Tecton tecton) {
        StringBuilder info = new StringBuilder();

        // Add tecton name and type
        String tectonName = registry.getNameOf(tecton);
        String tectonType = tecton.getClass().getSimpleName();
        info.append("Name: ").append(tectonName).append("\n");
        info.append("Type: ").append(tectonType).append("\n");

        // Add hexagon IDs
        info.append("Hexagons: ");
        for (int i = 0; i < tecton.getHexagons().size(); i++) {
            if (i > 0) info.append(", ");
            info.append(tecton.getHexagons().get(i).getId());
        }
        info.append("\n");

        // Add fungus information
        if (tecton.getFungus() != null) {
            info.append("Fungus: ").append(registry.getNameOf(tecton.getFungus())).append("\n");
        } else {
            info.append("Fungus: None\n");
        }

        // Add insect information
        if (!tecton.getInsects().isEmpty()) {
            info.append("Insects: ").append(tecton.getInsects().size()).append("\n");
            for (Insect insect : tecton.getInsects()) {
                info.append(" - ").append(registry.getNameOf(insect)).append("\n");
            }
        } else {
            info.append("Insects: None\n");
        }

        // Add spore information
        if (!tecton.getSporeMap().isEmpty()) {
            info.append("Spores: \n");
            for (Map.Entry<Spore, Integer> entry : tecton.getSporeMap().entrySet()) {
                info.append(" - ").append(registry.getNameOf(entry.getKey()))
                        .append(": ").append(entry.getValue()).append("\n");
            }
        } else {
            info.append("Spores: None\n");
        }

        // Add hypha information
        if (!tecton.getHyphas().isEmpty()) {
            info.append("Connected by ").append(tecton.getHyphas().size()).append(" hyphae\n");
        } else {
            info.append("No hypha connections\n");
        }

        return info.toString();
    }
}