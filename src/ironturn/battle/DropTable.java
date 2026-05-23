package ironturn.battle;

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

    public static List<Item> roll () {
        List<Item> shuffled = new ArrayList<>(POOL);
        Collections.shuffle(shuffled);

        Item first = shuffled.get(0);
        Item second = null;
        for (Item item : shuffled) {
            if (item.getClass() != first.getClass()) {
                second = item;
                break;
            }
        }
        return List.of(shuffled.get(0), shuffled.get(1));
    }
}
