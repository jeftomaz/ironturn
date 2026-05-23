package ironturn.pattern.observer;

import ironturn.battle.UI;
import ironturn.model.Character;
import ironturn.model.Hero;

public class StatusDisplay implements BattleObserver {

    private static final int BAR_SIZE = 10;

    private String buildBar(Character c) {
        int filled = (int) ((double) c.getHp() / c.getMaxHp() * BAR_SIZE);
        int empty = BAR_SIZE - filled;
        return String.format("%s: [%s%s] %d/%d HP",
                c.getName(),
                "█".repeat(filled),
                "░".repeat(empty),
                c.getHp(),
                c.getMaxHp());
    }

    @Override
    public void onEvent(BattleEvent event) {
        Character hero  = event.getAttacker() instanceof Hero
                ? event.getAttacker() : event.getTarget();
        Character enemy = event.getAttacker() instanceof Hero
                ? event.getTarget()   : event.getAttacker();

        System.out.println();
        System.out.printf("  ** %s ataca %s por %d de dano!%n",
                event.getAttacker().getName(),
                event.getTarget().getName(),
                event.getHitTaken());
        UI.separator();
        System.out.println("  " + buildBar(hero));
        System.out.println("  " + buildBar(enemy));
    }
}
