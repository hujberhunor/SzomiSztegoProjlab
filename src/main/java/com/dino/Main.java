package com.dino;

public class Main {

    public static Skeleton skeleton = Skeleton.getInstance();

    public static void Init() {
        skeleton.log("Játék indítása...");
        skeleton.runTest("Játék inicializálása");

        Fungus fungus = new Fungus(new Mycologist(), new NoFungiTecton());
        skeleton.registerObject(fungus);

        skeleton.log("Gombatest elhelyezve");
        skeleton.runTest("Spóra szórás teszt");
    }

    public static void main(String[] args) {
        Init();

        System.out.println("Naplózott események:");
        for (String entry : skeleton.getLog()) {
            System.out.println(entry);
        }
    }
}
