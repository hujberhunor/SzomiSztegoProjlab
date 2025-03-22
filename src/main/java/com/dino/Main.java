package com.dino;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void insectMoveSeq(){
        Skeleton skeleton = Skeleton.getInstance();

        // Inicializáljuk az entomológust és a rovarát
        Entomologist player = new Entomologist(3);
        Tecton startTecton = new InfiniteHyphaTecton(); // Kezdő tekton
        Tecton targetTecton = new InfiniteHyphaTecton(); // Cél tekton
        Insect insect = new Insect(player, startTecton);

        // A két tekton legyen szomszédos és kötődjenek össze fonallal
        startTecton.neighbours.add(targetTecton);
        targetTecton.neighbours.add(startTecton);

        Hypha hypha = new Hypha();
        startTecton.hyphas.add(hypha);
        targetTecton.hyphas.add(hypha);

        // Log: teszt kezdése
        skeleton.log("Teszt: rovar mozgása egyik tektonról a másikra.");

        // A rovar megpróbál mozogni
        insect.move(targetTecton);

        // Log: teszt vége
        skeleton.log("Teszt befejezve.");
    }

    public static void tectonSplitSeq(){
    Skeleton skeleton = Skeleton.getInstance();
    
    // Inicializáljuk a tektont
    Tecton tecton = new InfiniteHyphaTecton();
    
    // Helyezzünk el néhány hexagont a tektonban, hogy több hexagonból álljon
    tecton.hexagons.add(new Hexagon(1));
    tecton.hexagons.add(new Hexagon(2));
    tecton.hexagons.add(new Hexagon(3));
    
    // Állítsuk be a törési valószínűséget elég magasra, hogy biztosan törjön
    tecton.breakChance = 90.0;
    
    // Log: teszt kezdése
    skeleton.log("Teszt: Tekton kettétörése");
    
    // Teszteljük a sikeres törést - nincs rajta rovar
    skeleton.log("1. eset: Tekton törése rovar nélkül");
    List<Tecton> result1 = tecton.split(tecton.breakChance);
    
    // Most helyezzünk el egy rovart a tektonon és próbáljuk újra
    Entomologist player = new Entomologist(3);
    Insect insect = new Insect(player, tecton);
    tecton.insect = insect;
    
    // Log: második teszt
    skeleton.log("2. eset: Tekton törése rovarral (nem törhet)");
    List<Tecton> result2 = tecton.split(tecton.breakChance);
    
    // Most állítsuk be, hogy már egyszer tört, és teszteljük a csökkentett valószínűséget
    tecton.insect = null; // Távolítsuk el a rovart
    tecton.breakCount = 1; // Már egyszer tört
    
    // Log: harmadik teszt
    skeleton.log("3. eset: Tekton törése második alkalommal (csökkentett esély)");
    List<Tecton> result3 = tecton.split(tecton.breakChance);
    
    // Most teszteljük az egy hexagonból álló tektont
    Tecton smallTecton = new InfiniteHyphaTecton();
    smallTecton.hexagons.add(new Hexagon(4)); // Csak egy hexagon
    smallTecton.breakChance = 90.0;
    
    // Log: negyedik teszt
    skeleton.log("4. eset: Egy hexagonból álló tekton törése (nem törhet)");
    List<Tecton> result4 = smallTecton.split(smallTecton.breakChance);
    
    // Log: teszt vége
    skeleton.log("Teszt befejezve.");
}

    public static void main(String[] args) {
        boolean menuActive = true;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter 0 to exit");

        while(menuActive){
            System.out.println("\n-----------------------\nUse case list:");
            System.out.println("1. Insect movement");
            System.out.println("2. Tecton splitting");
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
                    tectonSplitSeq();
                    break;
                default:
                    System.out.println("Invalid input");
            }
            System.out.println("");
        }
    }
}
