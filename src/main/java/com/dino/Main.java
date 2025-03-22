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


    public static void insectEatSeq(){
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

    public static void insectCutSeq(){
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

    public static void tectonSplitSeq(){
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
        
        skeleton.log("3. eset: Tekton törése második alkalommal (kisebb esély)");
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


    public static void main(String[] args) {
        boolean menuActive = true;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter 0 to exit\n");

        while(menuActive){
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
