package ironturn.pattern.strategy;
import ironturn.model.Character;

import java.util.Random;

public class WarriorAttack implements AttackStrategy {

    private static final double CRIT_CHANCE = 0.05;
    private static final double CRIT_MULTIPLIER = 2.0;

    private final Random random;

    public WarriorAttack() {
        this.random = new Random();
    }

    @Override
    public int execute(Character attacker, Character target) {

        int base = attacker.getAtk() - target.getDef();

        if (random.nextDouble() < CRIT_CHANCE) {
            return (int)(base * CRIT_MULTIPLIER);
        } else {
            return Math.max(0, base);
        }
    }
}
