package ironturn.model;
import ironturn.pattern.strategy.AttackStrategy;

public class Hero extends Character{

    private AttackStrategy strategy;
    private final HeroClass heroClass;
    private int undosRemaining;
    private int scrollCount = 0;

    public Hero(String name, int hp, int hpMax, int atk, int def, AttackStrategy strategy, HeroClass heroClass) {
        super(name, hp, hpMax, atk, def);
        this.strategy = strategy;
        this.heroClass = heroClass;
        this.undosRemaining = (heroClass == HeroClass.MAGE) ? 1 : 0;
    }

    public HeroClass getHeroClass() { return heroClass; }

    public int getContraAvailable() { return undosRemaining; }
    public void useContra() { if (undosRemaining > 0) undosRemaining-- ;}
    public void resetContra() { undosRemaining = 1; }

    public boolean hasScroll()   { return scrollCount > 0; }
    public int getScrollCount()  { return scrollCount; }
    public void addScroll()      { scrollCount++; }

    @Override
    public int attack(Character target) {
        return strategy.execute(this, target);
    }

    public int useScroll() {
        if (scrollCount == 0) return 0;
        scrollCount--;
        int amount = (int)(getMaxHp() * 0.5);
        heal(amount);
        return amount;
    }
}