package com.dino.util;

import java.util.Stack;

/**
 * Skeleton osztály a szekvencia diagramok megvalósításához.
 * Egy objektumhívás stack segítségével követi a metódushívásokat,
 * és naplózza az eseményeket.
 */
public class Skeleton {

    /** Stack az objektumhívások követésére */
    private Stack<String> callStack;

    /** Singleton példány */
    private static Skeleton instance = null;

    /**
     * Privát konstruktor, hogy Singletonként működjön.
     */
    private Skeleton() {
        callStack = new Stack<>();
    }

    /**
     * Singleton példány lekérése.
     * @return Az egyetlen Skeleton példány.
     */
    public static Skeleton getInstance() {
        if (instance == null) {
            instance = new Skeleton();
        }
        return instance;
    }

    /**
     * Egy metódushívás kezdetét naplózza és hozzáadja a stackhez.
     * @param objectName Az objektum neve, amelyen a metódus hívódik.
     * @param methodName A hívott metódus neve.
     */
    public void startMethod(String objectName, String methodName) {
        String call = objectName + "." + methodName + "()";
        callStack.push(call);
        System.out.println("[Skeleton] --> " + call);
    }

    /**
     * Egy metódushívás befejezését naplózza és eltávolítja a stackből.
     */
    public void endMethod() {
        if (!callStack.isEmpty()) {
            String call = callStack.pop();
            System.out.println("[Skeleton] <-- " + call + " visszatért");
        }
    }

    /**
     * Egy adott esemény naplózása.
     * @param message Az üzenet, amelyet naplózni kell.
     */
    public void log(String message) {
        System.out.println("[Skeleton Log]: " + message);
    }

    /**
     * Stack aktuális állapotának kiírása (debug célokra).
     */
    public void printCallStack() {
        System.out.println("Aktuális metódushívások:");
        for (String call : callStack) {
            System.out.println("   " + call);
        }
    }
}
