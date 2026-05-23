package ironturn.battle;

import ironturn.model.Character;
import ironturn.model.Enemy;
import ironturn.model.Hero;
import ironturn.model.HeroClass;
import ironturn.pattern.command.AttackCommand;
import ironturn.pattern.command.CommandHistory;
import ironturn.pattern.decorator.AmuletDecorator;
import ironturn.pattern.decorator.ShieldDecorator;
import ironturn.pattern.decorator.SwordDecorator;
import ironturn.pattern.observer.BattleLogger;
import ironturn.pattern.observer.BattleObserver;
import ironturn.pattern.observer.StatusDisplay;
import ironturn.pattern.strategy.MageAttack;
import ironturn.pattern.strategy.WarriorAttack;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class BattleEngine {

    private Hero hero;
    private Character equipped;
    private List<Enemy> enemies;
    private Enemy currentEnemy;
    private CommandHistory history;
    private List<BattleObserver> observers;
    private Scanner scanner;

    public BattleEngine() {
        this.history = new CommandHistory();
        this.observers = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        this.enemies = new ArrayList<>();
    }

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
            case 2-> {
                h = new Hero(name, 80, 80, 30, 5, new MageAttack(), HeroClass.MAGE);
                equipped = new AmuletDecorator(h);
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
        int atk = vary(baseAtk);
        int def = vary(baseDef);

        return new Enemy(name, hp, hp, atk, def);
    }

    private List<Enemy> createEnemies() {
        List<Enemy> enemies = new ArrayList<Enemy>();

        enemies.add(createEnemy("Goblin", 40, 12, 3));
        enemies.add(createEnemy("Lobisomen", 75, 22, 8));
        enemies.add(createEnemy("Vampiro", 100, 28, 12));

        return enemies;
    }

    private String buildBar(ironturn.model.Character c) {
        int barSize = 10;
        int filled = (int)((double) c.getHp() / c.getMaxHp() * barSize);
        int empty = barSize - filled;
        return String.format("%s [%s%s] %d/%d HP",
                c.getName(),
                "█".repeat(filled),
                "░".repeat(empty),
                c.getHp(),
                c.getMaxHp());
    }

    private void showStatus() {
        System.out.println("\n--- STATUS ---");
        System.out.println(buildBar(hero));
        System.out.println(buildBar(currentEnemy));
        if (hero.getHeroClass() == HeroClass.MAGE) {
            System.out.println("✦ Feitiços de reversão: " + hero.getUndosRemaining());
        }
    }

    private void playerTurn() {
        boolean actionTaken = false;

        while (!actionTaken) {
            try {
                UI.section("⚔  SEU TURNO  —  " + hero.getName() + " vs " + currentEnemy.getName());
                System.out.println();
                System.out.println("  " + buildBar(hero));
                System.out.println("  " + buildBar(currentEnemy));
                System.out.println();
                UI.separator();
                System.out.println("  [1] Atacar");
                if (hero.getHeroClass() == HeroClass.MAGE && hero.getUndosRemaining() > 0) {
                    System.out.println("  [2] Desfazer ataque sofrido");
                    System.out.println("  [3] Desfazer turno completo");
                }
                System.out.println("  [4] Ver status  (não gasta turno)");
                UI.separator();
                System.out.print("  > ");

                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1 -> {
                        AttackCommand cmd = new AttackCommand(equipped, currentEnemy, observers);
                        cmd.execute();
                        history.push(cmd);
                        actionTaken = true;
                    }
                    case 2 -> {
                        if (hero.getHeroClass() == HeroClass.MAGE && hero.getUndosRemaining() > 0) {
                            history.undo();
                            hero.useUndo();
                            actionTaken = true;
                        } else {
                            System.out.println("  Ação indisponível.");
                        }
                    }
                    case 3 -> {
                        if (hero.getHeroClass() == HeroClass.MAGE && hero.getUndosRemaining() > 0) {
                            history.undo();
                            history.undo();
                            hero.useUndo();
                            actionTaken = true;
                        } else {
                            System.out.println("  Ação indisponível.");
                        }
                    }
                    case 4 -> showStatus();
                    default -> System.out.println("  Comando inválido. Tente novamente.");
                }

            } catch (NumberFormatException e) {
                System.out.println("  Digite um número válido.");
            }
        }
    }

    private void enemyTurn() {
        UI.pause(1000);
        UI.section("👹  TURNO DO INIMIGO  —  " + currentEnemy.getName());

        System.out.println();
        System.out.println("…O inimigo está se preparando para atacar!");
        UI.pause(3000);

        AttackCommand cmd = new AttackCommand(currentEnemy, equipped, observers);
        cmd.execute();
        history.push(cmd);
    }

    private void setup() {
        UI.titleScreen();

        System.out.println("  Digite o nome do seu personagem:");
        System.out.print("  > ");
        String name = scanner.nextLine();

        UI.section("ESCOLHA SUA CLASSE");
        System.out.println();
        System.out.println("  [1] ⚔  Guerreiro  — HP 120 | ATK 20 | DEF 15");
        System.out.println("       Espada (+10 ATK) e Escudo (+8 DEF)");
        System.out.println("       Habilidade: crítico com 5% de chance");
        System.out.println();
        System.out.println("  [2] 🔮 Mago       — HP 80  | ATK 30 | DEF 5");
        System.out.println("       Amuleto (+5 ATK, +30 HP máx)");
        System.out.println("       Habilidade: reverter 1 turno por inimigo");
        System.out.println();
        System.out.print("  > ");
        int choice = Integer.parseInt(scanner.nextLine());

        hero = createHero(name, choice);
        this.enemies = createEnemies();
        currentEnemy = enemies.get(0);

        observers.add(new BattleLogger());
        observers.add(new StatusDisplay());

        UI.separator();
        System.out.println();
        System.out.printf("  Bem-vindo, %s! Prepare-se para a batalha.%n", hero.getName());
        System.out.printf("  Primeiro inimigo: %s%n", currentEnemy.getName());
        UI.separator();
    }

    private void loop() {
        while (hero.isAlive() && !enemies.isEmpty()) {
            playerTurn();

            if (!currentEnemy.isAlive()) {
                UI.blank();
                System.out.println("  ✔ " + currentEnemy.getName() + " foi derrotado!");
                enemies.remove(currentEnemy);

                if (!enemies.isEmpty()) {
                    currentEnemy = enemies.get(0);
                    hero.resetUndos();
                    UI.separator();
                    System.out.println("  ⚠  Um novo inimigo surge das sombras: " + currentEnemy.getName() + "!");
                    UI.separator();
                }
                continue;
            }

            enemyTurn();
        }
    }

    private void ending() {
        UI.blank();
        UI.section(hero.isAlive() ? "  VITÓRIA!" : "  FIM DE JOGO");
        UI.blank();
        if (hero.isAlive()) {
            System.out.printf("  Parabéns, %s! Todos os inimigos foram derrotados.%n", hero.getName());
        } else {
            System.out.printf("  %s foi derrotado. A escuridão vence desta vez.%n", hero.getName());
        }
        UI.blank();
        scanner.close();
    }

    public void start() {
        setup();
        loop();
        ending();
    }
}
