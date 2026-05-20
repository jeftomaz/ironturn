package ironturn.pattern.command;

import ironturn.model.Enemy;
import ironturn.model.Hero;
import ironturn.model.HeroClass;
import ironturn.pattern.observer.BattleEvent;
import ironturn.pattern.observer.BattleObserver;

import java.util.List;

public class AttackCommand implements TurnCommand {

    private final Hero attacker;
    private Enemy target;
    private final List<BattleObserver> observers;
    private int damageDealt;

    public AttackCommand(Hero attacker, Enemy target, List<BattleObserver> observers) {
        this.attacker = attacker;
        this.target = target;
        this.observers = observers;
        this.damageDealt = 0;
    }

    @Override
    public void execute() {
        damageDealt = attacker.attack(target);
        target.takeDamage(damageDealt);

        BattleEvent event = new BattleEvent(attacker, target, damageDealt);
        for (BattleObserver observer : observers) {
            observer.onEvent(event);
        }
    }

    @Override
    public void undo() {
        if (attacker.getHeroClass() != HeroClass.MAGE) {
            System.out.println("Somente um mago pode voltar no tempo!");
            return;
        }

        if (attacker.getUndosRemaining() <= 0) {
           System.out.println(attacker.getName() + " não tem mais feitiços de reversão!");
           return;
        }

        target.heal(damageDealt);
        attacker.useUndo();

        System.out.println("✦ " + attacker.getName() + " reverte o tempo - o dano foi desfeito.");
    }
}
