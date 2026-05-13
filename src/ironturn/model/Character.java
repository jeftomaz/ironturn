package ironturn.model;

public abstract class Character {

    private String name;

    private int hp;
    private int maxHp;

    private int atk;
    private int def;

    public Character(String name, int hp, int maxHp, int atk, int def) {
        this.name = name;
        this.hp = hp;
        this.maxHp = maxHp;
        this.atk = atk;
        this.def = def;
    }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getAtk() { return atk; }
    public int getDef() { return def; }

    public void takeDamage(int hitTaken) {
        hp = Math.max(0, hp - hitTaken);
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public abstract int attack(Character target);

}