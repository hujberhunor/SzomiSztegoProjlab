package com.dino.tecton;

import com.dino.core.Hypha;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;

/**
 * Egy olyan konkrét, példányosítható tektontípus, amely életben tartja a
 * gombafonalakat még akkor is, ha máshogy elpusztulnának.
 */
public class KeepHyphaTecton extends Tecton {
    private int hyphaLifespan; // Korlátlan (-1)
    private int hyphaLimit;    // Alapesetben 1

    /**
     * Konstruktor
     */
    public KeepHyphaTecton() {
        super();
        this.hyphaLifespan = -1; // A fonalak korlátlan ideig élnek
        this.hyphaLimit = 1;    // Alapértelmezetten 1 fonál lehet rajta
    }

    /**
     * A fonalakat kezelő virtuális függvény felülírása.
     * Ellenőrzi, hogy van-e már gombafonál a tektonon, és ha a limit engedi,
     * hozzáadja az újat. Minden hozzáadott fonalat "örök életűvé" tesz.
     *
     * @param h A kezelendő gombafonál
     */
    @Override
    public void handleHypha(Hypha h) {
        EntityRegistry registry = new EntityRegistry();
        Logger logger = new Logger(registry);
        
        String hyphaName = registry.getNameOf(h);
        String tectonName = registry.getNameOf(this);
        
        // Ha van limit és már elértük, nem adhatunk hozzá újat
        if (hyphaLimit > 0 && hyphas.size() >= hyphaLimit) {
            logger.logError("TECTON", tectonName, "Nem lehet több gombafonalat hozzáadni: elérte a limitet");
            return;
        }
        
        // Lementjük az eredeti élettartamot
        int originalLifespan = h.getLifespan();
        
        // Hozzáadjuk a fonalat és beállítjuk "örök" élettartamra
        hyphas.add(h);
        h.setLifespan(-1); // Beállítjuk végtelenre az élettartamot
        
        // Logoljuk a változásokat
        logger.logChange("TECTON", this, "ADD_HYPHA", "-", hyphaName);
        logger.logChange("HYPHA", h, "LIFESPAN", String.valueOf(originalLifespan), "-1");
    }

    /**
     * Létrehoz egy új, ugyanolyan típusú tektont
     * 
     * @return Új KeepHyphaTecton példány
     */
    @Override
    public Tecton createCopy() {
        return new KeepHyphaTecton();
    }
}