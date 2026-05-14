package ironturn.pattern.observer;

import java.util.List;
import java.util.ArrayList;

public class BattleLogger implements BattleObserver {

    private List<String> log;

    public BattleLogger() {
        this.log = new ArrayList<>();
    }

    @Override
    public void onEvent(BattleEvent event) {
        String msg = String.format("%s atacou %s por %d de dano",
                event.getAttacker().getName(),
                event.getTarget().getName(),
                event.getHitTaken());
        log.add(msg);
    }

    public List<String> getLog() {
        return log;
    }
}
