# IronTurn — Plano de Construção

## Ordem de Desenvolvimento

A regra é simples: **cada fase só depende do que já foi feito antes.**

---

### ✅ Fase 1 — Camada `model/`
Fundação do jogo. Sem isso, nada mais funciona.

- [x] `Character.java` — classe base abstrata (hp, atk, def, takeDamage)
- [x] `Hero.java` — personagem jogável com Strategy injetada
- [x] `Enemy.java` — inimigo com IA simples

---

### ✅ Fase 2 — Padrão `strategy/`
Define *como* cada classe ataca.

- [x] `AttackStrategy.java` — interface
- [x] `WarriorAttack.java` — golpe físico com chance de crítico
- [x] `MageAttack.java` — magia que ignora defesa

---

### ✅ Fase 3 — Padrão `observer/`
Define *quem é notificado* quando algo acontece na batalha.

- [x] `BattleObserver.java` — interface
- [x] `BattleEvent.java` — objeto imutável de evento
- [x] `BattleLogger.java` — registra histórico completo
- [x] `StatusDisplay.java` — exibe barras de HP em tempo real

---

### ✅ Fase 4 — Padrão `decorator/`
Adiciona atributos via equipamentos sem herança.

- [x] `CharacterDecorator.java` — base abstrata
- [x] `SwordDecorator.java` — +10 ataque
- [x] `ShieldDecorator.java` — +8 defesa
- [x] `AmuletDecorator.java` — +5 ataque, +20 HP máximo

---

### ✅ Fase 5 — Padrão `command/`
Encapsula ações do turno como objetos (com undo).

- [x] `TurnCommand.java` — interface com `execute()` e `undo()`
- [x] `AttackCommand.java` — ataque do herói + notifica observers
- [x] `CommandHistory.java` — pilha LIFO para undo

---

### ✅ Fase 6 — `battle/BattleEngine.java`
Orquestra o loop de turnos. Depende de tudo acima.

- [x] Loop principal: jogador age → inimigo age → repete
- [x] Menu de ação (atacar / undo / status)
- [ ] Seleção de herói no início (Guerreiro ou Mago)
- [ ] Sequência de 2–3 inimigos em ordem

---

### ✅ Fase 7 — `Main.java`
Ponto de entrada. Monta os objetos e inicia a batalha.

- [ ] Criação dos heróis com equipamentos via Decorator
- [ ] Criação dos inimigos
- [ ] Instancia e chama `BattleEngine.start()`

---

### ✅ Fase 8 — Documentação
- [ ] `README.md` com instruções de compilação/execução
- [ ] Diagrama UML simplificado em texto (ASCII)
- [ ] `LICENSE` (MIT)

---

## Compilação (referência rápida)

```bash
# A partir da raiz do projeto
java -cp out Main
```

---

## O que está fora do escopo
- Persistência (save/load)
- Mapa ou movimentação
- Inventário ou progressão de nível
- Frameworks externos