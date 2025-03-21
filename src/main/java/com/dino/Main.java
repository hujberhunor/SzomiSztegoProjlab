package com.dino;

public class Main {

    public static void main(String[] args) {
        Skeleton skeleton = Skeleton.getInstance();

        skeleton.log("Játék indítása...");
        skeleton.runTest("Játék inicializálása");

        Fungus fungus = new Fungus(new Mycologist(), new Tecton());
        skeleton.registerObject(fungus);

        skeleton.log("Gombatest elhelyezve");
        skeleton.runTest("Spóra szórás teszt");

        System.out.println("Naplózott események:");
        for (String entry : skeleton.getLog()) {
            System.out.println(entry);
        }
    }
}
