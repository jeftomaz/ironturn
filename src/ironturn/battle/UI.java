package ironturn.battle;

public class UI {
    private static final int WIDTH = 40;
    private static final String SEP = "═".repeat(WIDTH);
    private static final String THIN = "─".repeat(WIDTH);

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
        System.out.println("  ╔" + SEP + "╗");
        System.out.println("  ║  " + title + sp(WIDTH - 2 - visLen(title)) + "║");
        System.out.println("  ╚" + SEP + "╝");
    }

    public static void separator() {
        System.out.println("  " + THIN);
    }

    public static void blank() {
        System.out.println();
    }

    private static String pad(String text, int width) {
        String s = "   " + text;
        return s + sp(Math.max(0, width - visLen(s)));
    }

    private static String sp(int n) {
        return n > 0 ? " ".repeat(n) : "";
    }

    private static int visLen(String s) {
        int len = 0 ;
        for (int i = 0; i < s.length(); ) {
            int cp = s.codePointAt(i);
            len += (cp > 0xFFFF) ? 2 : 1;
            i += Character.charCount(cp);
        }
        return len;
    }

    public static void sectionEnemy(String title) {
        String wave = "v^".repeat(WIDTH / 2);
        blank();
        System.out.println("  " + wave);
        System.out.println("  | " + title + sp(WIDTH - 1 - visLen(title)) + "|");
        System.out.println("  " + wave);
    }

    public static void pause(int ms) {
        try { Thread.sleep(ms); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
 }
