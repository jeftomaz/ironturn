package ironturn.pattern.observer;

import ironturn.model.Character;

public class BattleEvent {

    private final Character attacker;
    private final Character target;
    private final int hitTaken;

    public BattleEvent(Character attacker, Character target, int hitTaken) {
        this.attacker = attacker;
        this.target = target;
        this.hitTaken = hitTaken;
    }

    public Character getAttacker() { return attacker; }
    public Character getTarget() { return target; }
    public int getHitTaken() { return hitTaken; }
}
