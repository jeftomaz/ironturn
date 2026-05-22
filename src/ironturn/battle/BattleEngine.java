package ironturn.battle;

import ironturn.model.Enemy;
import ironturn.model.Hero;
import ironturn.model.HeroClass;
import ironturn.pattern.command.AttackCommand;
import ironturn.pattern.command.CommandHistory;
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
        return switch (type) {
            case 1 -> new Hero(name, 120, 120, 20, 15, new WarriorAttack(), HeroClass.WARRIOR);
            case 2-> new Hero(name, 80, 80, 30, 5, new MageAttack(), HeroClass.MAGE);

            default -> {
                System.out.println("Escolha inválida! Guerreiro selecionado por padrão.");
                yield new Hero(name, 120, 120, 20, 15, new WarriorAttack(), HeroClass.WARRIOR);
            }
        };
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
        int choice = -1;

        do {
            try {
                System.out.println("\n--- SEU TURNO ---");
                System.out.println(hero.getName() + " vs "  + currentEnemy.getName() + "\n");

                System.out.println("[1] Atacar");
                if(hero.getHeroClass() == HeroClass.MAGE && hero.getUndosRemaining() > 0) {
                    System.out.println("[2] Desfazer ataque sofrido");
                    System.out.println("[3] Desfazer turno completo");
                }
                System.out.println("[4] Ver status");
                System.out.print("> ");
                choice = Integer.parseInt(scanner.nextLine());

                if(choice < 1 || choice > 4) {
                    System.out.println("Comando inválido. Tente novamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido");
            }
        } while (choice < 1 || choice > 4);

        switch (choice) {
            case 1 -> {
                AttackCommand cmd = new AttackCommand(hero, currentEnemy, observers);
                cmd.execute();
                history.push(cmd);
            }
            case 2 -> {
                history.undo();
                hero.useUndo();
            }
            case 3 -> {
                history.undo();
                history.undo();
                hero.useUndo();
            }
            case 4 -> showStatus();
        }
    }

    private void enemyTurn() {
        System.out.println("\n--- TURNO DO INIMIGO ---");
        AttackCommand cmd = new AttackCommand(currentEnemy, hero, observers);
        cmd.execute();
        history.push(cmd);
    }

    private void setup() {
        System.out.println("Escreva o nome do personagem: ");
        String name = scanner.nextLine();

        System.out.println("\nEscolha sua classe:");
        System.out.println("[1] Guerreiro");
        System.out.println("[2] Mago");
        int choice = Integer.parseInt(scanner.nextLine());

        hero = createHero(name, choice);
        this.enemies = createEnemies();
        currentEnemy = enemies.get(0);

        observers.add(new BattleLogger());
        observers.add(new StatusDisplay());
    }

    private void loop() {
        while (hero.isAlive() && !enemies.isEmpty()) {
            playerTurn();

            if (!currentEnemy.isAlive()) {
                System.out.println("\n" + currentEnemy.getName() + " foi derrotado!");
                enemies.remove(currentEnemy);

                if (!enemies.isEmpty()) {
                    currentEnemy = enemies.get(0);
                    hero.resetUndos();
                    System.out.println("Um novo inimigo aparece: " + currentEnemy.getName() + "!");
                }

                continue;
            }

            enemyTurn();
        }
    }

    private void ending() {
        if (hero.isAlive()) {
            System.out.println("\n⚔ Parabéns, " + hero.getName() + "! Você derrotou todos os inimigos!");
        } else {
            System.out.println("\n💀 " + hero.getName() + " foi derrotado. Fim de jogo.");
        }
        scanner.close();
    }

    public void start() {
        setup();
        loop();
        ending();
    }
}
