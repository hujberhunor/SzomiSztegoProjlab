package com.dino;

import java.util.ArrayList;
import java.util.List;

/**
 * Skeleton osztály, amely a szekvencia diagramok szerinti tesztelést segíti.
 * Tárolja az összes objektumot, és naplózza az eseményeket.
 */
public class Skeleton {

    /** A Skeleton által kezelt objektumok listája */
    private List<Object> objects;

    /** Naplózott események listája */
    private List<String> log;

    /** Singleton példány */
    private static Skeleton instance = null;

    /**
     * Privát konstruktor, hogy Singletonként működjön.
     */
    private Skeleton() {
        objects = new ArrayList<>();
        log = new ArrayList<>();
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
     * Új objektum regisztrálása a Skeleton rendszerbe.
     * @param obj Az objektum, amelyet tesztelni szeretnénk.
     */
    public void registerObject(Object obj) {
        objects.add(obj);
    }

    /**
     * Esemény naplózása a konzolra és a belső naplóba.
     * @param message A naplózandó üzenet.
     */
    public void log(String message) {
        log.add(message);
        System.out.println("[Skeleton Log]: " + message);
    }

    /**
     * Egy adott teszteset futtatása.
     * @param testName A teszteset neve.
     */
    public void runTest(String testName) {
        log("Running test: " + testName);
        // Ide jön majd a tesztesetek szimulációja a szekvencia diagramok alapján.
    }

    /**
     * Visszaadja az eseménynaplót.
     * @return A naplózott események listája.
     */
    public List<String> getLog() {
        return log;
    }
}
