package ironturn.model;
import java.util.Random;

public class Enemy extends Character{

    private static final double ATTACK_VARIANCE = 0.2;

    private Random random;

    public Enemy(String name, int hp, int hpMax, int atk, int def) {
        super(name, hp, hpMax, atk, def);
        this.random = new Random();
    }

    @Override
    public int attack() {
        int min = (int)(getAtk() * (1 - ATTACK_VARIANCE));
        int max = (int)(getAtk() * (1 + ATTACK_VARIANCE));
        return random.nextInt(min, max + 1);
    }
}