package com.dino.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.dino.core.Fungus;
import com.dino.core.Hexagon;
import com.dino.core.Hypha;
import com.dino.core.Insect;
import com.dino.core.Spore;
import com.dino.engine.Game;
import com.dino.player.Entomologist;
import com.dino.player.Mycologist;
import com.dino.tecton.Tecton;
import com.dino.util.EntityRegistry;
import com.dino.util.ObjectNamer;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Popup;

public class GuiBoard implements ModelObserver {
    private Pane boardPane;
    private Map<Integer, Polygon> hexagonShapes; // id -> polygon
    private Map<Integer, Double[]> hexagonPositions; // id -> [x, y]
    private Map<Tecton, Color> tectonColors;
    private Map<String, Color> persistentTectonColors; // Tárolja a tectonok színeit név alapján
    private EntityRegistry registry;
    private ObjectNamer namer;
    private Set<Integer> existingHexagonIds; // Csak a létező hexagonok ID-i
    private final int GRID_SIZE = 10; // A rács mérete 10x10

    // Hexagon méret és elrendezés változók
    private final double HEX_SIZE = 35; // A hatszög oldalhossza
    private final double HEX_HORIZ_DIST = HEX_SIZE * Math.sqrt(3); // Vízszintes távolság a középpontok között
    private final double HEX_VERT_DIST = HEX_SIZE * 1.5; // Függőleges távolság a középpontok közötta

    private Popup tectonInfoPopup;
    private Label tectonInfoLabel;

    public GuiBoard() {
        boardPane = new Pane();
        boardPane.setPrefSize(800, 600);
        boardPane.setStyle("-fx-background-color: #333333;");

        hexagonShapes = new HashMap<>();
        hexagonPositions = new HashMap<>();
        tectonColors = new HashMap<>();
        persistentTectonColors = new HashMap<>(); // Inicializáljuk a perzisztens színtárolót
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

        // 2. Tecton színek meghatározása (most már perzisztens módon)
        setupTectonColors(game);

        // 3. A teljes hexagon rács pozíciójának kiszámítása
        calculateGridPositions();

        // 4. Csak a létező hexagonok kirajzolása
        drawExistingHexagons();

        // 5. Hexagonok színezése a tectonok alapján
        colorHexagonsByTecton(game);

        drawFungi(game);
        drawInsects(game);
        drawHypha(game);
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
        // List<Color> possibleColors = Arrays.asList(
        // Color.web("FFADAD"), Color.web("FFD6A5"), Color.web("FDFFBF"),
        // Color.web("CAFFBF"), Color.web("9BF6FF"), Color.web("A0C4FF"),
        // Color.web("BDB2FF"), Color.web("FFC6FF")
        // );

        // PETI
        List<Color> possibleColors = Arrays.asList(Color.web("#557174"), Color.web("#798f7a"), Color.web("#9dad7f"),
                Color.web("#b2be9b"), Color.web("#c7cfb7"), Color.web("#f7f7e8"));

        Random rnd = new Random();

        for (Tecton tecton : game.getBoard().getAllTectons()) {
            String tectonName = registry.getNameOf(tecton);

            // Ellenőrizzük, hogy a tectonnak már van-e színe
            if (persistentTectonColors.containsKey(tectonName)) {
                // Ha már van színe, használjuk azt
                Color savedColor = persistentTectonColors.get(tectonName);
                tectonColors.put(tecton, savedColor);
                tecton.setColor(savedColor);
                continue;
            }

            // Ha nincs még színe, új színt választunk neki
            List<Color> availableColors = new ArrayList<>(possibleColors);

            boolean isUnique = false; // egyedi-e a szín, vagyis nincs olyan szomszédja, ami ugyanilyen színű
            Color selectedColor = possibleColors.get(0); // Alapértelmezett

            while (!isUnique && !(availableColors.isEmpty())) {
                isUnique = true;

                // a színlistából találomra kiválasztunk egyet
                int selectedIndex = rnd.nextInt(availableColors.size());
                selectedColor = availableColors.get(selectedIndex);

                // Leellenőrizzük, hogy van-e olyan szomszédos tekton, ami ugyanolyan színű,
                // mint amit kiválasztottunk.
                for (Tecton neigbourTecton : tecton.getNeighbours()) {
                    if (neigbourTecton.getColor() == null) {
                        break;
                    }
                    if (neigbourTecton.getColor().equals(selectedColor)) {
                        // Ha a szomszéd már ugyanolyan színű, eltávolítjuk a lehetséges színek
                        // listájából a színt
                        isUnique = false;
                        availableColors.remove(selectedColor);
                        break;
                    }
                }
            }

            // Beállítjuk a tecton színét és eltároljuk a perzisztens térképben is
            tectonColors.put(tecton, selectedColor);
            tecton.setColor(selectedColor);
            persistentTectonColors.put(tectonName, selectedColor);
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
        hexagonPositions.put(0, new Double[] { startX, startY });

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
                hexagonPositions.put(id, new Double[] { x, y });
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

                // Ellenőrizzük, hogy van-e spóra a tectonon
                boolean hasSpores = tecton.spores != null && !tecton.spores.isEmpty();
                int sporeCount = 0;
                if (hasSpores) {
                    for (Integer count : tecton.spores.values()) {
                        sporeCount += count;
                    }
                }

                for (Hexagon hexagon : tecton.hexagons) {
                    int hexId = hexagon.getId();
                    hexIds.append(hexId).append(",");

                    Polygon hexShape = hexagonShapes.get(hexId);
                    if (hexShape != null) {
                        // Alap szín beállítása
                        if (hasSpores) {
                            // Ha van spóra, speciális mintát használunk
                            hexShape.setFill(createSporePatternFill(color, sporeCount));

                            // Enyhe árnyékhatás hozzáadása a spórákhoz
                            hexShape.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.5)));
                        } else {
                            // Ha nincs spóra, normál színezés
                            hexShape.setFill(color);
                        }
                    }
                }

                // Tecton információinak kiírása
                // if (!tecton.hexagons.isEmpty()) {
                // // Keressünk egy hexagont, ahova kiírhatjuk az infót
                // for (Hexagon hex : tecton.hexagons) {
                // int hexId = hex.getId();
                // Double[] pos = hexagonPositions.get(hexId);

                // if (pos != null) {
                // String tectonName = registry.getNameOf(tecton);
                // String tectonType = tecton.getClass().getSimpleName();

                // Text nameText = new Text(pos[0] - 30, pos[1] - 10, tectonName);
                // nameText.setFont(Font.font(8));

                // Text typeText = new Text(pos[0] - 30, pos[1] + 10, tectonType);
                // typeText.setFont(Font.font(8));

                // boardPane.getChildren().addAll(nameText, typeText);
                // break;
                // }
                // }
                // }
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
     * Spóra mintázatú kitöltés létrehozása - Látványosabb, sötétebb foltokkal
     * 
     * @param baseColor  Az alap szín
     * @param sporeCount A spórák száma a tectonon
     * @return A spóra mintázatú kitöltés
     */
    private Paint createSporePatternFill(Color baseColor, int sporeCount) {
        // Kép létrehozása a textúrához (nagyobb méretű a részletesebb mintázathoz)
        int canvasSize = 50;
        Canvas canvas = new Canvas(canvasSize, canvasSize);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Alapszín kitöltése
        gc.setFill(baseColor);
        gc.fillRect(0, 0, canvasSize, canvasSize);

        // Sötétebb színvariációk létrehozása
        Color darkSpotColor = baseColor.darker().darker(); // Jelentősen sötétebb
        Color mediumSpotColor = baseColor.darker(); // Közepesen sötét

        // Véletlen generátor a spórák elhelyezéséhez
        Random random = new Random(sporeCount); // A spórák száma lesz a seed

        // Spórák számától függően állítjuk a sűrűséget
        int spotCount = Math.min(5 + sporeCount * 3, 30); // Maximum 30 folt

        // Spóra foltok rajzolása
        for (int i = 0; i < spotCount; i++) {
            // Véletlenszerű pozíció
            double x = random.nextDouble() * canvasSize;
            double y = random.nextDouble() * canvasSize;

            // Véletlenszerű méret (de nagyobb, mint eddig volt)
            double size = 3 + random.nextDouble() * 8;

            // Váltakozva használjuk a nagyon sötét és középsötét színeket
            if (i % 3 == 0) {
                gc.setFill(darkSpotColor);
            } else {
                gc.setFill(mediumSpotColor);
            }

            // Folt rajzolása (oválisok és körök)
            if (random.nextBoolean()) {
                // Kör
                gc.fillOval(x, y, size, size);
            } else {
                // Ovális
                gc.fillOval(x, y, size, size * 0.7);
            }
        }

        // Kép készítése a mintázathoz
        WritableImage image = new WritableImage(canvasSize, canvasSize);
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
            if (i > 0)
                info.append(", ");
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
        }

        // Add hypha information
        if (!tecton.getHyphas().isEmpty()) {
            info.append("Connected by ").append(tecton.getHyphas().size()).append(" hyphae\n");
        } else {
            info.append("No hypha connections\n");
        }

        Map<Spore, Integer> sporeMap = tecton.getSporeMap();
        if (!sporeMap.isEmpty()) {
            int totalSpores = 0;
            for (Integer count : sporeMap.values()) {
                totalSpores += count;
            }

            info.append("Spores: ").append(totalSpores).append("\n");

            for (Map.Entry<Spore, Integer> entry : sporeMap.entrySet()) {
                Spore spore = entry.getKey();
                Integer count = entry.getValue();

                String sporeName = registry.getNameOf(spore);
                try {
                    Mycologist owner = spore.getSpecies();
                    if (owner != null) {
                        String ownerName = registry.getNameOf(owner);
                        info.append(" - ").append(count).append("× ").append(sporeName)
                                .append(" (Owner: ").append(ownerName).append(")\n");
                    } else {
                        info.append(" - ").append(count).append("× ").append(sporeName).append("\n");
                    }
                } catch (Exception e) {
                    info.append(" - ").append(count).append("× ").append(sporeName).append("\n");
                }
            }
        } else {
            info.append("Spores: None\n");
        }

        return info.toString();
    }

    // private final Map<Integer, Integer> fungusCountPerHexagon = new HashMap<>();
    // private final Map<Integer, Integer> insectCountPerHexagon = new HashMap<>();
    private final Map<Integer, Integer> entityCountPerHexagon = new HashMap<>();

    public void drawFungi(Game game) {
        // fungusCountPerHexagon.clear();
        entityCountPerHexagon.clear();

        for (Mycologist m : game.getAllMycologists()) {
            for (Fungus f : m.getMushrooms()) {
                Tecton tecton = f.getTecton();

                List<Hexagon> hexagons = new ArrayList<>(tecton.hexagons);
                // Collections.shuffle(hexagons);

                Hexagon chosenHexagon = null;
                for (Hexagon h : hexagons) {
                    if (existingHexagonIds.contains(h.getId())) {
                        chosenHexagon = h;
                        break;
                    }
                }
                if (chosenHexagon == null)
                    break;

                int hexagonId = chosenHexagon.getId();
                Double[] position = hexagonPositions.get(hexagonId);
                int indexInHexagon = entityCountPerHexagon.getOrDefault(hexagonId, 0);
                entityCountPerHexagon.put(hexagonId, indexInHexagon + 1);

                double offsetX = (indexInHexagon % 2.0) * 15.0 - 7.5;
                double offsetY = (indexInHexagon / 2.0) * 15.0;

                Point2D location = new Point2D(position[0] + offsetX, position[1] + offsetY);
                // Point2D location = new Point2D(position[0], position[1]);
                FungusEntity fEntity = new FungusEntity(f);
                fEntity.location = location;

                Node fungusNode = fEntity.draw();
                if (fungusNode != null) {
                    boardPane.getChildren().add(fungusNode);
                    fungusNode.setOnMouseClicked(event -> handleFungusClick(event, f));
                }
            }
        }
    }

    public void drawInsects(Game game) {
        // insectCountPerHexagon.clear();

        for (Entomologist e : game.getAllEntomologists()) {
            for (Insect i : e.getInsects()) {
                Tecton tecton = i.getTecton();

                List<Hexagon> hexagons = new ArrayList<>(tecton.hexagons);
                // Collections.shuffle(hexagons);

                Hexagon chosenHexagon = null;
                for (Hexagon h : hexagons) {
                    if (existingHexagonIds.contains(h.getId())) {
                        chosenHexagon = h;
                        break;
                    }
                }
                if (chosenHexagon == null)
                    break;

                int hexagonId = chosenHexagon.getId();
                Double[] position = hexagonPositions.get(hexagonId);
                int indexInHexagon = entityCountPerHexagon.getOrDefault(hexagonId, 0);
                entityCountPerHexagon.put(hexagonId, indexInHexagon + 1);

                double offsetX = (indexInHexagon % 2.0) * 15.0 - 7.5;
                double offsetY = (indexInHexagon / 2.0) * 15.0;

                Point2D location = new Point2D(position[0] + offsetX, position[1] + offsetY);
                InsectEntity iEntity = new InsectEntity(i);
                iEntity.location = location;

                Node insectNode = iEntity.draw();
                if (insectNode != null) {
                    boardPane.getChildren().add(insectNode);
                    insectNode.setOnMouseClicked(event -> handleInsectClick(event, i));
                }
            }
        }
    }

    public void drawHypha(Game game) {
        Set<Hypha> alreadyDrawn = new HashSet<>();

        for (Tecton tecton : game.getBoard().getAllTectons()) {
            for (Hypha h : tecton.getHyphas()) {
                if (alreadyDrawn.contains(h))
                    continue;

                alreadyDrawn.add(h);

                // Ellenőrizzük, hogy van-e legalább két tecton a hypha-ban
                if (h.getTectons().size() < 2)
                    continue;

                Tecton start = h.getTectons().get(0);
                Tecton end = h.getTectons().get(1);

                Hexagon startHex = null, endHex = null;

                for (Hexagon hex : start.hexagons) {
                    if (existingHexagonIds.contains(hex.getId())) {
                        startHex = hex;
                        break;
                    }
                }

                for (Hexagon hex : end.hexagons) {
                    if (existingHexagonIds.contains(hex.getId())) {
                        endHex = hex;
                        break;
                    }
                }

                if (startHex == null || endHex == null)
                    continue;

                Double[] startPosArray = hexagonPositions.get(startHex.getId());
                Double[] endPosArray = hexagonPositions.get(endHex.getId());
                if (startPosArray == null || endPosArray == null)
                    continue;

                Point2D startPos = new Point2D(startPosArray[0], startPosArray[1]);
                Point2D endPos = new Point2D(endPosArray[0], endPosArray[1]);

                HyphaEntity entity = new HyphaEntity(h);
                entity.setStartPos(startPos);
                entity.setEndPos(endPos);

                Node hyphaNode = entity.draw();
                if (hyphaNode != null) {
                    boardPane.getChildren().add(hyphaNode);
                }
            }
        }
    }

    private void handleFungusClick(MouseEvent event, Fungus fungus) {
        // Prepare the fungus information
        String fungusInfo = buildFungusInfo(fungus);

        // Update the label and show the popup
        tectonInfoLabel.setText(fungusInfo);
        tectonInfoPopup.show(boardPane.getScene().getWindow(),
                event.getScreenX() + 10,
                event.getScreenY() + 10);
    }

    private void handleInsectClick(MouseEvent event, Insect insect) {
        // Prepare the insect information
        String insectInfo = buildInsectInfo(insect);

        // Update the label and show the popup
        tectonInfoLabel.setText(insectInfo);
        tectonInfoPopup.show(boardPane.getScene().getWindow(),
                event.getScreenX() + 10,
                event.getScreenY() + 10);
    }

    private String buildFungusInfo(Fungus fungus) {
        StringBuilder info = new StringBuilder();

        // Add fungus name
        String fungusName = registry.getNameOf(fungus);
        info.append("Name: ").append(fungusName).append("\n");

        // Add species information
        Mycologist species = fungus.getSpecies();
        if (species != null) {
            info.append("Species: ").append(species.name).append("\n");
        } else {
            info.append("Species: Unknown\n");
        }

        // Add location information
        Tecton tecton = fungus.getTecton();
        if (tecton != null) {
            info.append("Location: ").append(registry.getNameOf(tecton)).append("\n");
        }

        // Add charge and lifespan information
        int charge = fungus.getCharge();
        int lifespan = fungus.getLifespan();
        info.append("Charge: ").append(charge).append("\n");
        info.append("Lifespan: ").append(lifespan).append("\n");

        // Add hypha information
        List<Hypha> hyphas = fungus.getHyphas();
        if (hyphas != null && !hyphas.isEmpty()) {
            info.append("Connected Hyphae: ").append(hyphas.size()).append("\n");
        } else {
            info.append("Connected Hyphae: None\n");
        }

        // Add spore information
        List<Spore> spores = fungus.getSpores();
        if (spores != null && !spores.isEmpty()) {
            info.append("Spores: ").append(spores.size()).append("\n");
        } else {
            info.append("Spores: None\n");
        }

        return info.toString();
    }

    private String buildInsectInfo(Insect insect) {
        StringBuilder info = new StringBuilder();

        // Add insect name and owner
        String insectName = registry.getNameOf(insect);
        info.append("Name: ").append(insectName).append("\n");

        // Add owner information
        Entomologist owner = insect.getEntomologist();
        if (owner != null) {
            info.append("Owner: ").append(owner.name).append("\n");
        } else {
            info.append("Owner: Unknown\n");
        }

        // Add location information
        Tecton currentTecton = insect.getTecton();
        if (currentTecton != null) {
            info.append("Location: ").append(registry.getNameOf(currentTecton)).append("\n");
        }

        // Add effects information
        List<Spore> effects = insect.getEffects();
        if (effects != null && !effects.isEmpty()) {
            info.append("Active Effects: \n");
            for (Spore effect : effects) {
                info.append(" - ").append(effect.getClass().getSimpleName());

                // If it's a timed effect, add duration
                int duration = effect.getEffectDuration();
                if (duration > 0) {
                    info.append(" (").append(duration).append(" turns remaining)");
                }
                info.append("\n");
            }
        } else {
            info.append("Active Effects: None\n");
        }

        return info.toString();
    }
}
