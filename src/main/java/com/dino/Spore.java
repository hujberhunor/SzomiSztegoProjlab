public abstract class Spore {
    protected Mycologist species;
    protected int effectDuration;
    protected int nutrientValue;

    public void move() { }
    public void eat() { }
    public void cut() { }
    public void update() { }
    public abstract void applyTo(Insect i);
}

