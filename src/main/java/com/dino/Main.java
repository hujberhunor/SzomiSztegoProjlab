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

    public static void insectCutSeq(){
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
        skeleton.log("2. eset: Vágás megkísérlése olyan tektonra, amin nincs fonál.");
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
