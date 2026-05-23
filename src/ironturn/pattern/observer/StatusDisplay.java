package ironturn.pattern.observer;

import ironturn.battle.UI;
import ironturn.model.Character;

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
    public void onEvent(BattleEvent battleEvent) {
        System.out.println();
        System.out.printf("  💥 %s ataca %s por %d de dano!%n",
                battleEvent.getAttacker().getName(),
                battleEvent.getTarget().getName(),
                battleEvent.getHitTaken());
        UI.separator();
        System.out.println("  " + buildBar(battleEvent.getAttacker()));
        System.out.println("  " + buildBar(battleEvent.getTarget()));
    }
}
