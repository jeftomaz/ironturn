package ironturn.battle;

import ironturn.model.Character;

public class UI {
    private static final int WIDTH = 40;
    private static final String SEP  = "═".repeat(WIDTH);
    private static final String THIN = "─".repeat(WIDTH);

    // ANSI
    public static final String RESET  = "\u001B[0m";
    public static final String BLUE   = "\u001B[94m";  // turno do herói
    public static final String RED    = "\u001B[91m";  // turno do inimigo / HP baixo / morte
    public static final String YELLOW = "\u001B[93m";  // transição / crítico
    public static final String GREEN  = "\u001B[92m";  // drop / HP normal
    public static final String DIM    = "\u001B[2m";   // separadores

    public static void titleScreen() {
        blank();
        System.out.println("  ╔" + SEP + "╗");
        System.out.println("  ║" + sp(WIDTH) + "║");
        System.out.println("  ║" + pad("=[ I R O N   T U R N ]=", WIDTH) + "║");
        System.out.println("  ║" + sp(WIDTH) + "║");
        System.out.println("  ║" + pad("RPG por turnos · Java puro", WIDTH) + "║");
        System.out.println("  ║" + sp(WIDTH) + "║");
        System.out.println("  ╚" + SEP + "╝");
        blank();
    }

    public static void section(String title) {
        blank();
        System.out.println(BLUE +
                "  ╔" + SEP + "╗\n" +
                "  ║  " + title + sp(WIDTH - 2 - visLen(title)) + "║\n" +
                "  ╚" + SEP + "╝" + RESET);
    }

    public static void sectionEnemy(String title) {
        blank();
        System.out.println(RED +
                "  ╔" + SEP + "╗\n" +
                "  ║  " + title + sp(WIDTH - 2 - visLen(title)) + "║\n" +
                "  ╚" + SEP + "╝" + RESET);
    }

    public static void sectionDrop(String title) {
        blank();
        System.out.println(GREEN +
                "  ╔" + SEP + "╗\n" +
                "  ║  " + title + sp(WIDTH - 2 - visLen(title)) + "║\n" +
                "  ╚" + SEP + "╝" + RESET);
    }

    public static void turnDivider() {
        blank();
        System.out.println(YELLOW + "  " + "━".repeat(WIDTH) + RESET);
        blank();
    }

    public static void separator() {
        System.out.println(DIM + "  " + THIN + RESET);
    }

    public static void enemyDefeated(String name) {
        blank();
        System.out.println(RED + "  [+] " + name + " foi derrotado!" + RESET);
    }

    public static void blank() { System.out.println(); }

    private static String pad(String text, int width) {
        String s = "   " + text;
        return s + sp(Math.max(0, width - visLen(s)));
    }

    private static String sp(int n) { return n > 0 ? " ".repeat(n) : ""; }

    private static int visLen(String s) {
        int len = 0;
        for (int i = 0; i < s.length(); ) {
            int cp = s.codePointAt(i);
            len += (cp > 0xFFFF) ? 2 : 1;
            i += java.lang.Character.charCount(cp);
        }
        return len;
    }

    public static void pause(int ms) {
        try {
            Thread.sleep(ms);
            int avail = System.in.available();
            if (avail > 0) System.in.read(new byte[avail]);
        } catch (Exception ignored) {}
    }

    public static String buildBar(Character c, int damage) {
        int barSize = 10;
        int filled  = (int)((double) c.getHp() / c.getMaxHp() * barSize);
        int empty   = barSize - filled;
        double pct  = (double) c.getHp() / c.getMaxHp();

        String barColor = pct <= 0.30 ? RED : GREEN;
        String dmgStr   = damage > 0 ? RED + "  (-" + damage + ")" + RESET : "";

        return String.format("%s [%s%s%s%s] %d/%d HP%s",
                c.getName(),
                barColor, "█".repeat(filled), "░".repeat(empty), RESET,
                c.getHp(), c.getMaxHp(),
                dmgStr);
    }

    public static String buildBar(Character c) { return buildBar(c, 0); }
}