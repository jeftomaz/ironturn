package ironturn.battle;

import ironturn.model.Character;
import ironturn.model.Enemy;
import ironturn.model.Hero;
import ironturn.model.HeroClass;
import ironturn.model.item.Item;
import ironturn.pattern.command.AttackCommand;
import ironturn.pattern.command.CommandHistory;
import ironturn.pattern.command.GuardCommand;
import ironturn.pattern.decorator.AmuletDecorator;
import ironturn.pattern.decorator.CharacterDecorator;
import ironturn.pattern.decorator.ShieldDecorator;
import ironturn.pattern.decorator.SwordDecorator;
import ironturn.pattern.observer.BattleLogger;
import ironturn.pattern.observer.BattleObserver;
import ironturn.pattern.observer.StatusDisplay;
import ironturn.pattern.strategy.MageAttack;
import ironturn.pattern.strategy.WarriorAttack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import static ironturn.battle.UI.buildBar;

public class BattleEngine {

    private record MenuEntry(String label, Runnable action, boolean takesTurn) {}

    private Hero hero;
    private Character equipped;
    private List<Enemy> enemies;
    private Enemy currentEnemy;
    private CommandHistory history;
    private List<BattleObserver> observers;
    private Scanner scanner;
    private int heroHpSnapshot;
    private boolean enemyHasAttacked;
    private GuardCommand activeGuard;

    public BattleEngine() {
        this.history     = new CommandHistory();
        this.observers   = new ArrayList<>();
        this.scanner     = new Scanner(System.in);
        this.enemies     = new ArrayList<>();
        this.activeGuard = null;
    }

    // Setup helpers

    private int vary(int base) {
        double factor = 0.85 + new Random().nextDouble() * 0.30;
        return (int)(base * factor);
    }

    private Hero createHero(String name, int type) {
        Hero h;
        switch (type) {
            case 1 -> {
                h = new Hero(name, 120, 120, 20, 15, new WarriorAttack(), HeroClass.WARRIOR);
                equipped = new ShieldDecorator(new SwordDecorator(h));
            }
            case 2 -> {
                h = new Hero(name, 80, 80, 15, 5, new MageAttack(), HeroClass.MAGE);
                equipped = new AmuletDecorator(h);
                h.setHp(equipped.getMaxHp());
            }
            default -> {
                System.out.println("Escolha inválida! Guerreiro selecionado por padrão.");
                h = new Hero(name, 120, 120, 20, 15, new WarriorAttack(), HeroClass.WARRIOR);
                equipped = new ShieldDecorator(new SwordDecorator(h));
            }
        }
        return h;
    }

    private Enemy createEnemy(String name, int baseHp, int baseAtk, int baseDef) {
        int hp = vary(baseHp);
        return new Enemy(name, hp, hp, vary(baseAtk), vary(baseDef));
    }

    private List<Enemy> createEnemies() {
        List<Enemy> list = new ArrayList<>();
        list.add(createEnemy("Goblin",          40, 12,  3));
        list.add(createEnemy("Esqueleto",       58, 17,  5));
        list.add(createEnemy("Lobisomen",       75, 22,  8));
        list.add(createEnemy("Cavaleiro",       88, 25, 10));
        list.add(createEnemy("Vampiro",        100, 28, 12));
        list.add(createEnemy("Necromante",     115, 31, 14));
        list.add(createEnemy("Rei Demônio",    145, 36, 16));
        return list;
    }

    // UI helpers

    private void showEquipment() {
        System.out.println("  Equipamentos:");
        Character c = equipped;
        while (c instanceof CharacterDecorator dec) {
            if      (c instanceof SwordDecorator)  System.out.println("    • Espada     +10 ATK");
            else if (c instanceof ShieldDecorator) System.out.println("    • Escudo      +8 DEF");
            else if (c instanceof AmuletDecorator) System.out.println("    • Amuleto     +20 ATK  +30 HP máx");
            c = dec.getWrapped();
        }
    }

    private void showStatus() {
        System.out.println("\n--- STATUS ---");
        System.out.println("  " + buildBar(equipped));
        System.out.printf("  ATK: %d | DEF: %d%n", equipped.getAtk(), equipped.getDef());
        showEquipment();
        System.out.println();
        System.out.println("  " + buildBar(currentEnemy));
        System.out.printf("  ATK: %d | DEF: %d%n", currentEnemy.getAtk(), currentEnemy.getDef());
        System.out.println();
        if (hero.getHeroClass() == HeroClass.MAGE)
            System.out.println("  ✦ Feitiços de reversão: " + hero.getContraAvailable());
    }

    // Menu dinâmico

    private List<MenuEntry> buildMenu() {
        List<MenuEntry> menu = new ArrayList<>();

        menu.add(new MenuEntry("Atacar", () -> {
            AttackCommand cmd = new AttackCommand(equipped, currentEnemy, observers);
            cmd.execute();
            history.push(cmd);
        }, true));

        if (hero.getHeroClass() == HeroClass.WARRIOR) {
            menu.add(new MenuEntry("Defender (levantar escudo)", () -> {
                activeGuard = new GuardCommand(equipped, currentEnemy, observers);
                activeGuard.execute();
            }, true));
        }

        if (hero.getHeroClass() == HeroClass.MAGE && hero.getContraAvailable() > 0) {
            if (enemyHasAttacked) {
                menu.add(new MenuEntry("Reverter Turno", () -> {
                    history.undo();
                    history.undo();
                    hero.useContra();
                    enemyHasAttacked = false;
                }, false));
            }
            menu.add(new MenuEntry("Reverter Batalha", () -> {
                hero.setHp(heroHpSnapshot);
                currentEnemy.setHp(currentEnemy.getMaxHp());
                history.clear();
                hero.useContra();
                enemyHasAttacked = false;
            }, false));
        }

        menu.add(new MenuEntry("Ver Status (não gasta turno)", this::showStatus, false));

        return menu;
    }

    // Turn logic

    private void playerTurn() {
        boolean actionTaken = false;

        System.out.println();
        System.out.println("Faça seu movimento!");
        UI.pause(2000);

        while (!actionTaken) {
            try {
                UI.section(">> SEU TURNO :: " + hero.getName() + " vs " + currentEnemy.getName());
                System.out.println();
                System.out.println("  " + buildBar(equipped));
                System.out.println("  " + buildBar(currentEnemy));
                System.out.println();
                UI.separator();

                List<MenuEntry> menu = buildMenu();
                for (int i = 0; i < menu.size(); i++)
                    System.out.println("  [" + (i + 1) + "] " + menu.get(i).label());

                UI.separator();
                System.out.print("  > ");

                int choice = Integer.parseInt(scanner.nextLine()) - 1;
                if (choice < 0 || choice >= menu.size()) {
                    System.out.println("  Comando inválido. Tente novamente.");
                    continue;
                }

                MenuEntry entry = menu.get(choice);
                entry.action().run();
                if (entry.takesTurn()) actionTaken = true;

            } catch (NumberFormatException e) {
                System.out.println("  Digite um número válido.");
            }
        }
    }

    private void enemyTurn() {
        UI.pause(1000);
        UI.sectionEnemy("<<  TURNO DO INIMIGO  —  " + currentEnemy.getName());

        System.out.println();
        System.out.println("…O inimigo está se preparando para atacar!");
        UI.pause(3000);

        AttackCommand cmd = new AttackCommand(currentEnemy, equipped, observers);
        cmd.execute();
        history.push(cmd);
        enemyHasAttacked = true;
    }

    // Game flow

    private void setup() {
        UI.titleScreen();

        System.out.println("  Digite o nome do seu personagem:");
        System.out.print("  > ");
        String name = scanner.nextLine();

        UI.section("ESCOLHA SUA CLASSE");
        System.out.println();
        System.out.println("  [1]  ∆ Guerreiro ∆ -- HP 120 | ATK 20 | DEF 15");
        System.out.println("       Espada (+10 ATK) e Escudo (+8 DEF)");
        System.out.println("       Habilidade: crítico e penetração de armadura");
        System.out.println();
        System.out.println("  [2]  ≈ Mago ≈      -- HP 80  | ATK 15 | DEF 5");
        System.out.println("       Amuleto (+20 ATK, +30 HP máx)");
        System.out.println("       Habilidade: reverter 1 turno por inimigo");
        System.out.println();
        System.out.print("  > ");
        int choice = Integer.parseInt(scanner.nextLine());

        hero = createHero(name, choice);
        this.enemies     = createEnemies();
        currentEnemy     = enemies.get(0);
        heroHpSnapshot   = hero.getHp();
        enemyHasAttacked = false;

        observers.add(new BattleLogger());
        observers.add(new StatusDisplay());

        UI.separator();
        System.out.println();
        System.out.printf("  Bem-vindo, %s! Prepare-se para a batalha.%n", hero.getName());
        System.out.printf("  Primeiro inimigo: %s%n", currentEnemy.getName());
        UI.separator();
    }

    private Item showDropMenu(List<Item> drops) {
        UI.sectionDrop(">> SAQUE");
        System.out.println("O inimigo deixou cair alguns itens:\n");
        System.out.println(" [1] " + drops.get(0).getName() + " -- " + drops.get(0).getDescription());
        System.out.println(" [2] " + drops.get(1).getName() + " -- " + drops.get(1).getDescription());
        System.out.println();
        UI.separator();

        int choice = -1;
        while (choice != 1 && choice != 2) {
            try {
                System.out.print(" > ");
                choice = Integer.parseInt(scanner.nextLine());
                if (choice != 1 && choice != 2)
                    System.out.println(" Escolha [1] ou [2].");
            } catch (NumberFormatException e) {
                System.out.println(" Digite um número válido");
            }
        }
        return drops.get(choice - 1);
    }

    private void loop() {
        while (hero.isAlive() && !enemies.isEmpty()) {
            playerTurn();

            if (!currentEnemy.isAlive()) {
                UI.enemyDefeated(currentEnemy.getName());

                List<Item> drops = DropTable.roll();
                Item chosen = showDropMenu(drops);
                chosen.apply(hero);
                System.out.println();
                System.out.println(UI.GREEN + " Você obteve: " + chosen.getName() + "!" + UI.RESET);
                UI.separator();

                enemies.remove(currentEnemy);
                if (!enemies.isEmpty()) {
                    currentEnemy = enemies.get(0);
                    heroHpSnapshot   = hero.getHp();
                    enemyHasAttacked = false;
                    activeGuard      = null;
                    hero.resetContra();
                    UI.separator();
                    System.out.println("  [!] Um novo inimigo surge das sombras: " + currentEnemy.getName() + "!");
                    UI.pause(3000);
                    UI.separator();
                }
                continue;
            }

            enemyTurn();

            if (activeGuard != null) {
                activeGuard.revert();
                activeGuard = null;
            }

            UI.turnDivider();
        }
    }

    private void ending() {
        UI.blank();
        UI.section(hero.isAlive() ? "  VITÓRIA!" : "  FIM DE JOGO");
        UI.blank();
        if (hero.isAlive())
            System.out.printf("  Parabéns, %s! Todos os inimigos foram derrotados.%n", hero.getName());
        else
            System.out.printf("  %s foi derrotado. A escuridão vence desta vez.%n", hero.getName());
        UI.blank();
        scanner.close();
    }

    public void start() {
        setup();
        loop();
        ending();
    }
}