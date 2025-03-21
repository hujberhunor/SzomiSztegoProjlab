package com.dino;

public abstract class Spore {
    protected Mycologist species;
    protected int effectDuration;
    protected int nutrientValue;

    //public boolean move(Tecton t)?
    public void move() {
        System.out.println("Spore.move()");
    }
    //public boolean eat(Entomologist e)?
    public void eat() {
        System.out.println("Spore.eat()");
    }
    //public boolean cut(Tecton t, Hypha h)?
    public void cut() {
        System.out.println("Spore.cut()");
    }
    public void update() { }
    public abstract void applyTo(Insect i);
}

