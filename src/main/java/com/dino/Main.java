package com.dino;

import java.lang.annotation.Target;
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

        // Inicializáljuk az entomológust, a rovarát, illetve egy gombászt
        Entomologist player = new Entomologist(3);
        Tecton tecton = new InfiniteHyphaTecton(); // Jelenlegi tekton
        Insect insect = new Insect(player, tecton);
        Mycologist mycologist = new Mycologist();

        tecton.addSpores(mycologist);

        // Log: teszt kezdése
        skeleton.log("Teszt: rovar megpróbál spórát enni.");

        // A rovar megpróbál mozogni
        insect.consumeSpores(player);

        // Log: teszt vége
        skeleton.log("Teszt befejezve.");
    }

    public static void insectCutSeq() {
        Skeleton skeleton = Skeleton.getInstance();
        // Inicializáljuk az entomológust és a rovarát
        Entomologist player = new Entomologist(3);
        Tecton firstTecton = new InfiniteHyphaTecton(); //Az a tekton, ahonnal a fonál indul és ahol a rovar tartózkodik
        Tecton secondTecton = new InfiniteHyphaTecton(); //Tekton a kettő között
        Tecton thirdTecton = new InfiniteHyphaTecton(); // Utolsó tekton
        Insect insect = new Insect(player, firstTecton);

        //Inicializálunk három tekton, amik közül az első és a harmadik nem szomszédosak egymással
        firstTecton.neighbours.add(secondTecton);
        secondTecton.neighbours.add(firstTecton);
        secondTecton.neighbours.add(thirdTecton);
        thirdTecton.neighbours.add(secondTecton);

        //Inicializálunk egy gombafonalat, ami az első tektonról indulva a harmadikig tart
        Hypha hypha = new Hypha();
        firstTecton.hyphas.add(hypha);
        secondTecton.hyphas.add(hypha);
        thirdTecton.hyphas.add(hypha);

        hypha.getTectons().add(firstTecton);
        hypha.getTectons().add(secondTecton);
        hypha.getTectons().add(thirdTecton);

        // Log: teszt kezdése
        skeleton.log("Teszt: rovar megpróbál fonalat vágni.");

        // A rovar megpróbál fonalat vágni
        insect.cutHypha(hypha, secondTecton);

        // Log: teszt vége
        skeleton.log("Teszt befejezve.");
    }

    public static void tectonSplitSeq() {
        Skeleton skeleton = Skeleton.getInstance();

        // Inicializáljuk a tektont
        Tecton tecton = new InfiniteHyphaTecton();

        // Hexagonok elhelyezése a tektonon
        tecton.hexagons.add(new Hexagon(1));
        tecton.hexagons.add(new Hexagon(2));
        tecton.hexagons.add(new Hexagon(3));

        // Teszteléshez magas valószínűség
        tecton.breakChance = 90.0;

        skeleton.log("Teszt: Tekton kettétörése");

        // Sikeres törés(nincs rajta rovar)
        skeleton.log("1. eset: Tekton törése rovar nélkül");
        List<Tecton> result1 = tecton.split(tecton.breakChance);

        // Törés rovarral
        Entomologist player = new Entomologist(3);
        Insect insect = new Insect(player, tecton);
        tecton.insect = insect;

        skeleton.log("2. eset: Tekton törése rovarral (nem törhet)");
        List<Tecton> result2 = tecton.split(tecton.breakChance);

        // Most állítsuk be, hogy már egyszer tört, és teszteljük a csökkentett valószínűséget
        tecton.insect = null;
        tecton.breakCount = 1;

        skeleton.log(
            "3. eset: Tekton törése második alkalommal (kisebb esély)"
        );
        List<Tecton> result3 = tecton.split(tecton.breakChance);

        // Teszt amiben csak egy hexagonból áll
        Tecton smallTecton = new InfiniteHyphaTecton();
        smallTecton.hexagons.add(new Hexagon(4));
        smallTecton.breakChance = 90.0;

        skeleton.log("4. eset: Egy hexagonból álló tekton törése (nem törhet)");
        List<Tecton> result4 = smallTecton.split(smallTecton.breakChance);

        // Log: teszt vége
        skeleton.log("Teszt befejezve.");
    }

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
        skeleton.log("- Eredeti törési valószínűség: " + originalBreakChance + "%");
        skeleton.log("- Előző törések száma: " + breakCount);
        skeleton.log("- Aktuális törési valószínűség: " + currentBreakChance + "%");
        skeleton.log("- Van-e rajta rovar: " + (hasInsect == 1 ? "Igen" : "Nem"));
        
        //split lefutása
        List<Tecton> result = tecton.split(tecton.breakChance);
        
        // Végeredmény
        if (result.isEmpty()) {
            skeleton.log("A törés nem történt meg.");
        } else {
            skeleton.log("A törés sikeresen megtörtént, " + result.size() + " új tekton jött létre.");
        }
        
        // Teszt vége
        skeleton.log("Teszt befejezve.");
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
            System.out.println("7. Tecton splitting");
            System.out.println("-----------------------");
            System.out.print("Select use case (e.g. 1,2...): ");
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
// WORKFLOW COMMENT
