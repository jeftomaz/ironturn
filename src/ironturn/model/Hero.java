package ironturn.model;
import ironturn.pattern.strategy.AttackStrategy;

public class Hero extends Character{

    private AttackStrategy strategy;

    public Hero(String name, int hp, int hpMax, int atk, int def, AttackStrategy strategy) {
        super(name, hp, hpMax, atk, def);
        this.strategy = strategy;
    }

    @Override
    public int attack(Character target) {
        return strategy.execute(this, target);
    }
}