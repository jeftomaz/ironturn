package ironturn.battle;

import ironturn.model.HeroClass;
import ironturn.model.item.*;

import java.util.*;

public class DropTable {

    private static final List<Item> POOL;

    static {
        Map<Item, Integer> weights = new LinkedHashMap<>();
        weights.put(new HealingPotion(), 3);
        weights.put(new AttackGem(),     3);
        weights.put(new DefenseRune(),   3);
        weights.put(new PowerCrystal(),  2);
        weights.put(new LifeElixir(),    1);
        weights.put(new DivineDefense(), 1);

        List<Item> pool = new ArrayList<>();
        weights.forEach((item, w) -> Collections.nCopies(w, item).forEach(pool::add));
        POOL = Collections.unmodifiableList(pool);
    }

    public static List<Item> roll(HeroClass heroClass) {
        List<Item> pool = new ArrayList<>(POOL);

        if (heroClass == HeroClass.WARRIOR) {
            pool.add(new HopeScroll());
            pool.add(new HopeScroll());   // peso 2, mesmo que PowerCrystal
        }

        Collections.shuffle(pool);
        Item first = pool.get(0);
        Item second = null;
        for (Item item : pool) {
            if (item.getClass() != first.getClass()) { second = item; break; }
        }
        if (second == null) second = first;
        return List.of(first, second);
    }
}
