# IronTurn

RPG por turnos via console, desenvolvido em Java puro, sem frameworks externos.

---

## Compilação e Execução

```bash
# A partir da raiz do projeto
find src -name "*.java" > sources.txt
javac -d out @sources.txt
java -cp out ironturn.Main
```

Requisito: Java 17 ou superior.

---

## Como Jogar

1. Digite o nome do seu personagem
2. Escolha sua classe: **Guerreiro** ou **Mago**
3. Enfrente os inimigos em sequência: Goblin → Lobisomem → Vampiro
4. A cada turno, escolha uma ação:
    - `[1]` Atacar
    - `[2]` Desfazer ataque sofrido _(apenas Mago, 1 uso por inimigo)_
    - `[3]` Desfazer turno completo _(apenas Mago, 1 uso por inimigo)_
    - `[4]` Ver status

---

## Classes

| Classe | HP | ATK | DEF | Equipamentos | Habilidade |
|--------|-----|-----|-----|--------------|------------|
| Guerreiro | 120 | 20 | 15 | Espada (+10 ATK), Escudo (+8 DEF) | Crítico (5% chance, 2x dano) |
| Mago | 80 | 30 | 5 | Amuleto (+5 ATK, +30 HP máx) | Reverter tempo (undo) |

---

## Inimigos

Os stats variam ±15% a cada partida.

| Inimigo | HP base | ATK base | DEF base |
|---------|---------|----------|----------|
| Goblin | 40 | 12 | 3 |
| Lobisomem | 75 | 22 | 8 |
| Vampiro | 100 | 28 | 12 |

---

## Padrões de Projeto

### Strategy
Define o comportamento de ataque de cada classe. Injetado no `Hero` na criação.

```
AttackStrategy
├── WarriorAttack  — dano físico com chance de crítico
└── MageAttack     — dano fixo ignorando defesa
```

### Decorator
Adiciona bônus de atributos via equipamentos, sem herança.

```
CharacterDecorator
├── SwordDecorator   — +10 ATK
├── ShieldDecorator  — +8 DEF
└── AmuletDecorator  — +5 ATK, +30 HP máximo
```

### Command
Encapsula ações do turno como objetos, permitindo undo.

```
TurnCommand
└── AttackCommand  — executa ataque e suporta reversão
CommandHistory     — pilha LIFO dos comandos executados
```

### Observer
Notifica componentes sobre eventos de batalha.

```
BattleObserver
├── BattleLogger    — registra histórico completo
└── StatusDisplay   — exibe barras de HP após cada evento
```

---

## Diagrama UML (simplificado)

```
                    ┌─────────────┐
                    │  Character  │ (abstract)
                    │  - name     │
                    │  - hp       │
                    │  - atk      │
                    │  - def      │
                    │  + attack() │
                    └──────┬──────┘
               ┌───────────┴───────────┐
        ┌──────┴──────┐         ┌──────┴──────┐
        │    Hero     │         │    Enemy    │
        │ - strategy  │         │ - variance  │
        │ - heroClass │         └─────────────┘
        │ - undos     │
        └──────┬──────┘
               │ wrapped by
        ┌──────┴──────────────┐
        │  CharacterDecorator │ (abstract)
        └──────┬──────────────┘
    ┌──────────┼──────────┐
┌───┴───┐ ┌───┴────┐ ┌───┴────┐
│ Sword │ │ Shield │ │ Amulet │
└───────┘ └────────┘ └────────┘

        ┌──────────────────┐
        │  AttackStrategy  │ (interface)
        │  + execute()     │
        └──────┬───────────┘
    ┌──────────┴──────────┐
┌───┴────────┐   ┌────────┴───┐
│WarriorAtk  │   │  MageAtk   │
└────────────┘   └────────────┘

        ┌──────────────┐
        │  TurnCommand │ (interface)
        │  + execute() │
        │  + undo()    │
        └──────┬───────┘
               │
        ┌──────┴───────┐
        │AttackCommand │
        │- attacker    │
        │- target      │
        │- damageDealt │
        └──────────────┘

        ┌─────────────────┐
        │ CommandHistory  │
        │ - Stack<Cmd>    │
        │ + push()        │
        │ + undo()        │
        └─────────────────┘

        ┌──────────────────┐
        │  BattleObserver  │ (interface)
        │  + onEvent()     │
        └──────┬───────────┘
    ┌──────────┴──────────┐
┌───┴────────┐   ┌────────┴──────┐
│BattleLogger│   │StatusDisplay  │
└────────────┘   └───────────────┘

        ┌──────────────────┐
        │  BattleEngine    │
        │  - hero          │
        │  - equipped      │
        │  - enemies       │
        │  - history       │
        │  - observers     │
        │  + start()       │
        └──────────────────┘
```

---

## Estrutura de Pacotes

```
ironturn/
├── battle/
│   └── BattleEngine.java
├── model/
│   ├── Character.java
│   ├── Enemy.java
│   ├── Hero.java
│   └── HeroClass.java
├── pattern/
│   ├── command/
│   │   ├── AttackCommand.java
│   │   ├── CommandHistory.java
│   │   └── TurnCommand.java
│   ├── decorator/
│   │   ├── AmuletDecorator.java
│   │   ├── CharacterDecorator.java
│   │   ├── ShieldDecorator.java
│   │   └── SwordDecorator.java
│   ├── observer/
│   │   ├── BattleEvent.java
│   │   ├── BattleLogger.java
│   │   ├── BattleObserver.java
│   │   └── StatusDisplay.java
│   └── strategy/
│       ├── AttackStrategy.java
│       ├── MageAttack.java
│       └── WarriorAttack.java
└── Main.java
```

---

## Fora do Escopo

- Persistência (save/load)
- Mapa ou movimentação
- Inventário ou progressão de nível
- Frameworks externos