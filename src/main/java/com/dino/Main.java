package com.dino;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void insectMoveSeq() {
        Skeleton skeleton = Skeleton.getInstance();

        // Inicializáljuk az entomológust és a rovarát
        Entomologist player = new Entomologist(3); // 3 action
        Tecton startTecton = new InfiniteHyphaTecton();
        Tecton targetTecton = new InfiniteHyphaTecton();
        Tecton accelTecton = new InfiniteHyphaTecton();
        Tecton nonNeighbour = new InfiniteHyphaTecton();
        Insect insect = new Insect(player, startTecton); //start tectonon indul a rovar

        // A két tekton legyen szomszédos és kötődjenek össze fonallal
        startTecton.neighbours.add(targetTecton);
        targetTecton.neighbours.add(startTecton);
        targetTecton.neighbours.add(accelTecton);
        accelTecton.neighbours.add(targetTecton);

        // TOOD: Kétirányú asszociáció miatt mind a két helye össze kell kötni őket.
        Hypha hypha = new Hypha();
        hypha.getTectons().add(startTecton);
        hypha.getTectons().add(targetTecton);
        hypha.getTectons().add(accelTecton);

        startTecton.getHyphas().add(hypha);
        targetTecton.getHyphas().add(hypha);
        accelTecton.getHyphas().add(hypha);

        // Log: teszt kezdése
        skeleton.log("Teszt: rovar mozgása sikeresen.");
        insect.move(targetTecton); // Össze vannak kötve
        skeleton.log("\n");

        // Mychologit init az effektekhez
        Mycologist emilia = new Mycologist();

        skeleton.log("Rovar mozgása accel effekt alatt.");
        insect = new Insect(player, startTecton); // resetelem az insectet
        player.decreaseActions(); // 1 actionja maradjon
        AcceleratingEffect accel = new AcceleratingEffect(emilia);
        accel.applyTo(insect);
        insect.move(targetTecton);
        insect.move(accelTecton);
        skeleton.log("\n");

        // ACTIONT Kell hozzáadni
        player.increaseActions();
        player.increaseActions();
        player.increaseActions();
        skeleton.log("\n");

        skeleton.log("Rovar mozgása paralyzed effekt alatt.");
        ParalyzingEffect paralyze = new ParalyzingEffect(new Mycologist());
        paralyze.applyTo(insect);
        // Mozgás próbálkozás hatás alatt
        insect.move(targetTecton);
        skeleton.log("\n");

        skeleton.log("Teszt: rovar mozgása nem szomszédos tektonra.");
        // Isecet helyzetének resetelése
        insect = new Insect(player, startTecton); // resetelem az insectet
        insect.move(nonNeighbour); // Nem szomszédosak
        skeleton.log("\n");

        // initelem hogy ne legyenek összekötve
        targetTecton.getHyphas().remove(hypha);
        hypha.getTectons().remove(startTecton);
        insect = new Insect(player, targetTecton); // resetelem az insectet
        skeleton.log(
            "Teszt: rovar mozgása szomszédos de fonallal nem összekötött tektonra."
        );
        insect.move(startTecton);
        skeleton.log("\n");

        player = new Entomologist(0);
        insect = new Insect(player, startTecton);
        skeleton.log("Rovar mozgása: Nem maradt akció.");
        skeleton.log("Akciók száma: " + player.getRemainingActions());
        insect.move(targetTecton);
        skeleton.log("\n");

        // Log: teszt vége
        skeleton.log("Teszt befejezve.");
    }

    public static void insectEatSeq() {
        Skeleton skeleton = Skeleton.getInstance();

        // Log: teszt kezdése
        skeleton.log("Teszt: rovar megpróbál spórát enni.");

        // Inicializáljuk az entomológust, a rovarát, illetve egy gombászt
        Entomologist player = new Entomologist(3);
        Tecton tecton = new InfiniteHyphaTecton(); // Jelenlegi tekton
        Insect insect = new Insect(player, tecton);
        Mycologist mycologist = new Mycologist();

        // Sikertelen táplálkozási kísérlet (a tektonon nincs spóra)
        skeleton.log("1. eset: Táplálkozás megkísérlése spóra nélkül.");
        insect.consumeSpores(player);

        // Sikeres táplálkozási kísérlet
        skeleton.log("2. eset: Táplálkozás sikeres megkísérlése.");

        // A tektonon elhelyezünk egy, a gombásztól származó spórát
        tecton.addSpores(mycologist);

        insect.consumeSpores(player);

        // Log: teszt vége
        skeleton.log("Teszt befejezve.");
    }

    public static void insectCutSeq() {
        Skeleton skeleton = Skeleton.getInstance();

        // Log: teszt kezdése
        skeleton.log("Teszt: Fonál vágása.");

        // Inicializáljuk az entomológust és a rovarát
        Entomologist player = new Entomologist(3);
        Tecton firstTecton = new InfiniteHyphaTecton(); //Az a tekton, ahol a rovar tartózkodik
        skeleton.log("Első tekton azonosítója: " + firstTecton.toString());
        Tecton secondTecton = new InfiniteHyphaTecton(); //Tekton a kettő között
        skeleton.log("Második tekton azonosítója: " + secondTecton.toString());
        Tecton thirdTecton = new InfiniteHyphaTecton(); // Utolsó tekton, ahonnan a fonál indul
        skeleton.log("Harmadik tekton azonosítója: " + thirdTecton.toString());
        Insect insect = new Insect(player, firstTecton);

        //Inicializálunk három tekton, amik közül az első és a harmadik nem szomszédosak egymással
        firstTecton.neighbours.add(secondTecton);
        secondTecton.neighbours.add(firstTecton);
        secondTecton.neighbours.add(thirdTecton);
        thirdTecton.neighbours.add(secondTecton);

        // Első teszt (nem szomszédos tekton) előkészítése
        Hypha hypha = new Hypha();
        thirdTecton.hyphas.add(hypha);
        hypha.getTectons().add(thirdTecton);

        // Sikertelen vágás (tekton nem szomszédos)
        skeleton.log("1. eset: Vágás megkísérlése nem szomszédos tektonra.");
        insect.cutHypha(hypha, thirdTecton);

        // Sikertelen vágás (a tektonon nincs fonál)
        skeleton.log(
            "2. eset: Vágás megkísérlése olyan tektonra, amin nincs fonál."
        );
        insect.cutHypha(hypha, secondTecton);

        // Létrehozunk egy gombafonalat, ami az első tektonról indulva a harmadikig tart
        secondTecton.hyphas.add(hypha);
        firstTecton.hyphas.add(hypha);

        hypha.getTectons().add(secondTecton);
        hypha.getTectons().add(firstTecton);

        // Sikertelen vágás (a rovar kábítva van)
        skeleton.log("3. eset: Vágás megkísérlése kábító hatás alatt.");

        // A rovar kábító spóra hatása alá kerül
        Mycologist mycologist = new Mycologist();
        StunningEffect stunningSpore = new StunningEffect(mycologist);
        stunningSpore.applyTo(insect);

        insect.cutHypha(hypha, secondTecton);

        // A rovarat kigyógyítjuk a kábultságból
        stunningSpore.decreaseEffectDuration();
        stunningSpore.decreaseEffectDuration();
        insect.removeExpiredEffects();

        // Sikeres vágás
        skeleton.log("4. eset: Vágás sikeres megkísérlése.");
        insect.cutHypha(hypha, secondTecton);

        // Log: teszt vége
        skeleton.log("Teszt befejezve.");
    }

    public static void tectonSplitSeq() {
        Skeleton skeleton = Skeleton.getInstance();
        Scanner scanner = new Scanner(System.in);

        // Hexagonok számának bekérése
        System.out.print("Hexagonok száma a tektonban (1 vagy több): ");
        int hexagonCount = scanner.nextInt();

        // Eredeti törési valószínűség bekérése (Teszt miatt lehet 1-100 között, de rendes implementálásnál generálásnál 5-40 között lesz)
        //Ha már volt törés, akkor érdemes 100-200 között megadni az értéket, ha látni is akarunk törést a Skeleton tesztelésnél
        System.out.print("Eredeti törési valószínűség(1-100 között): ");
        double originalBreakChance = scanner.nextDouble();

        // Előző törések számának bekérése
        System.out.print("Előző törések száma (0, 1 vagy 2): ");
        int breakCount = scanner.nextInt();

        // Jelenlegi törésnek valószínűségének kiszámolása "előző" törésektől függően
        double currentBreakChance;
        if (breakCount == 0) {
            currentBreakChance = originalBreakChance;
        } else if (breakCount == 1) {
            currentBreakChance = originalBreakChance / 2;
        } else { // breakCount >= 2
            currentBreakChance = 0;
        }

        //Tectonon tartózkodik rovar vagy sem
        System.out.print("Van rajta rovar? (0 - nincs, 1 - van): ");
        int hasInsect = scanner.nextInt();

        // Létrehozzuk a tektont a felhasználó által megadott értékekkel
        Tecton tecton = new InfiniteHyphaTecton();

        // Hexagonok hozzáadása
        for (int i = 1; i <= hexagonCount; i++) {
            tecton.hexagons.add(new Hexagon(i));
        }

        // Törési valószínűség beállítása
        tecton.breakChance = currentBreakChance;

        // Előző törések számának beállítása
        tecton.breakCount = breakCount;

        // Ha megadták, rovar hozzáadása
        if (hasInsect == 1) {
            Entomologist player = new Entomologist(3);
            Insect insect = new Insect(player, tecton);
            tecton.insect = insect;
        }

        // Teszt elkezdése

        skeleton.log("Tekton konfiguráció:");
        skeleton.log("- Hexagonok száma: " + hexagonCount);
        skeleton.log(
            "- Eredeti törési valószínűség: " + originalBreakChance + "%"
        );
        skeleton.log("- Előző törések száma: " + breakCount);
        skeleton.log(
            "- Aktuális törési valószínűség: " + currentBreakChance + "%"
        );
        skeleton.log(
            "- Van-e rajta rovar: " + (hasInsect == 1 ? "Igen" : "Nem")
        );

        //split lefutása
        List<Tecton> result = tecton.split(tecton.breakChance);

        // Végeredmény
        if (result.isEmpty()) {
            skeleton.log("A törés nem történt meg.");
        } else {
            skeleton.log(
                "A törés sikeresen megtörtént, " +
                result.size() +
                " új tekton jött létre."
            );
        }

        // Teszt vége
        skeleton.log("Teszt befejezve.");
    }

    public static void placeFungusSeq() {
        Skeleton skeleton = Skeleton.getInstance();
        Tecton tecton1 = new InfiniteHyphaTecton();
        Mycologist mycologist = new Mycologist();

        // 1. Teszteset
        skeleton.log("1. Teszt eset: Sikeres gombatest elhelyezés");
        Hypha hypha = new Hypha();
        hypha.setMychologist(mycologist);

        List<Hypha> hyphas = new ArrayList<>();
        hyphas.add(hypha);
        tecton1.setHyphas(hyphas);

        tecton1.addSpores(mycologist);
        tecton1.addSpores(mycologist);
        mycologist.placeFungus(tecton1);

        // 2. Teszteset
        skeleton.log(
            "2. Teszt eset: Sikertelen elhelyezés - Nincs megfelelő gombafonál"
        );
        Tecton tecton2 = new InfiniteHyphaTecton();
        tecton2.addSpores(mycologist);
        tecton2.addSpores(mycologist);
        mycologist.placeFungus(tecton2);

        // 3. Teszteset
        skeleton.log(
            "3. Teszt eset: Sikertelen elhelyezés - Nincs elegendő spóra"
        );
        Tecton tecton3 = new InfiniteHyphaTecton();
        tecton3.setHyphas(hyphas);
        mycologist.placeFungus(tecton3);

        skeleton.endMethod();
    }

    public static void growHyphaSeq() {
        Skeleton skeleton = Skeleton.getInstance();

        Fungus fungus = new Fungus();
        Tecton tecton1 = new InfiniteHyphaTecton();
        Tecton tecton2 = new InfiniteHyphaTecton();

        // 1. Teszteset
        skeleton.log(
            "1. Teszt eset: Sikeres gombafonál növesztés egy tektonra"
        );
        List<Tecton> singleTectonList = new ArrayList<>();
        singleTectonList.add(tecton1);
        fungus.growHypha(singleTectonList);

        // 2. Teszteset
        skeleton.log(
            "2. Teszt eset: Sikeres gombafonál növesztés két tektonra"
        );
        Tecton tecton3 = new InfiniteHyphaTecton();
        List<Tecton> neighborsTectonList = new ArrayList<>();
        tecton3.setNeighbours(neighborsTectonList);
        neighborsTectonList.add(tecton1);
        neighborsTectonList.add(tecton3);
        fungus.growHypha(neighborsTectonList);

        // 3. Teszteset
        skeleton.log(
            "3. Teszt eset: Sikertelen gombafonál növesztés - üres lista"
        );
        List<Tecton> emptyList = new ArrayList<>();
        fungus.growHypha(emptyList);

        // 4. Teszteset
        skeleton.log(
            "4. Teszt eset: Sikertelen gombafonál növesztés - nem szomszédos tektonok"
        );
        List<Tecton> doubleTectonList = new ArrayList<>();
        doubleTectonList.add(tecton1);
        doubleTectonList.add(tecton2);
        fungus.growHypha(doubleTectonList);
    }

    public static void spreadSporeSeq() {
        Skeleton skeleton = Skeleton.getInstance();

        Mycologist mycologist = new Mycologist();
        Tecton tecton1 = new InfiniteHyphaTecton();
        Tecton tecton2 = new InfiniteHyphaTecton();
        Tecton tecton3 = new InfiniteHyphaTecton();
        Tecton tecton4 = new InfiniteHyphaTecton();

        int originalCharge = 0;
        int newCharge = 0;

        List<Tecton> t1Neighbors = new ArrayList<>();
        t1Neighbors.add(tecton2);
        t1Neighbors.add(tecton4);
        tecton1.setNeighbours(t1Neighbors);

        List<Tecton> t2Neighbors = new ArrayList<>();
        t2Neighbors.add(tecton1);
        t2Neighbors.add(tecton3);
        tecton2.setNeighbours(t2Neighbors);

        List<Tecton> t3Neighbors = new ArrayList<>();
        t3Neighbors.add(tecton2);
        tecton3.setNeighbours(t3Neighbors);

        // 1. Teszteset
        skeleton.log("1. Teszteset: Töltöttség < 2 - Nem történik spóraszórás");
        Fungus fungus1 = new Fungus();
        fungus1.setSpecies(mycologist);
        fungus1.setTecton(tecton1);
        fungus1.setCharge(1);
        originalCharge = fungus1.getCharge();
        fungus1.spreadSpores();
        newCharge = fungus1.getCharge();
        skeleton.log(
            "Töltöttség a spóraszórás előtt: " +
            originalCharge +
            ", után: " +
            newCharge
        );

        // 2. Teszteset
        skeleton.log(
            "2. Teszteset: Töltöttség == 2 - Spóraszórás a szomszédos tektonokra"
        );
        Fungus fungus2 = new Fungus();
        fungus2.setSpecies(mycologist);
        fungus2.setTecton(tecton1);
        fungus2.setCharge(2);
        originalCharge = fungus2.getCharge();
        fungus2.spreadSpores();
        newCharge = fungus2.getCharge();
        skeleton.log(
            "Töltöttség a spóraszórás előtt: " +
            originalCharge +
            ", után: " +
            newCharge
        );

        // 3. Teszteset
        skeleton.log(
            "3. Teszteset: Töltöttség == 3 - Spóraszórás a szomszédos tektonok szomszédjaira"
        );
        Fungus fungus3 = new Fungus();
        fungus3.setSpecies(mycologist);
        fungus3.setTecton(tecton1);
        fungus3.setCharge(3);
        originalCharge = fungus3.getCharge();
        fungus3.spreadSpores();
        newCharge = fungus3.getCharge();
        skeleton.log(
            "Töltöttség a spóraszórás előtt: " +
            originalCharge +
            ", után: " +
            newCharge
        );

        skeleton.endMethod();
    }

    public static void main(String[] args) {
        boolean menuActive = true;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter 0 to exit\n");

        while (menuActive) {
            System.out.println("-----------------------\nUse case list:");
            System.out.println("1. Insect movement");
            System.out.println("2. Insect eating");
            System.out.println("3. Insect cutting");
            System.out.println("4. Place fungus");
            System.out.println("5. Spread spore");
            System.out.println("6. Grow hypha");
            System.out.println("7. Tecton splitting");
            System.out.println("-----------------------");
            System.out.print("Select use case (e.g. 1, 2...): ");
            int useCase = scanner.nextInt();
            switch (useCase) {
                case 0:
                    menuActive = false;
                    scanner.close();
                    break;
                case 1:
                    insectMoveSeq();
                    break;
                case 2:
                    insectEatSeq();
                    break;
                case 3:
                    insectCutSeq();
                    break;
                case 4:
                    placeFungusSeq();
                    break;
                case 5:
                    spreadSporeSeq();
                    break;
                case 6:
                    growHyphaSeq();
                    break;
                case 7:
                    tectonSplitSeq();
                    break;
                default:
                    System.out.println("Invalid input");
                    break;
            }
            System.out.println("");
        }
    }
}
