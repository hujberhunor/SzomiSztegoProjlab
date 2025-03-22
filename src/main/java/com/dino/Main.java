package com.dino;
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

        hypha.tectons.add(firstTecton);
        hypha.tectons.add(secondTecton);
        hypha.tectons.add(thirdTecton);

        // Log: teszt kezdése
        skeleton.log("Teszt: rovar megpróbál fonalat vágni.");

        // A rovar megpróbál fonalat vágni
        insect.cutHypha(hypha, secondTecton);

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
            System.out.println("-----------------------");
            System.out.print("Select use case (e.g. 1): ");
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
                default:
                    System.out.println("Invalid input");
            }
            System.out.println("");
        }
    }
}
