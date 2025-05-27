import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.sound.sampled.*;
import java.io.File;

public class CandyCrush extends JFrame {
    private final int ROWS = 8;
    private final int COLS = 10;
    private final int CELL_SIZE = 64;
    private final int CANDY_TYPES = 6;
    private JLabel[][] grid = new JLabel[ROWS][COLS];
    private int[][] candyTypes = new int[ROWS][COLS];
    private ImageIcon[] candyIcons = new ImageIcon[CANDY_TYPES];
    private Point selected = null;
    private Random rand = new Random();
    private int score = 0;

    public CandyCrush() {
        setTitle("Candy Crush - Score: 0");
        setSize(COLS * CELL_SIZE + 16, ROWS * CELL_SIZE + 39);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load candy icons
        for (int i = 0; i < CANDY_TYPES; i++) {
            candyIcons[i] = new ImageIcon("images/candy" + (i + 1) + ".png");
        }

        JPanel panel = new JPanel(new GridLayout(ROWS, COLS)) {
            Image bg = new ImageIcon("images/bg.png").getImage();
            protected void paintComponent(Graphics g) {
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                super.paintComponent(g);
            }
        };
        panel.setPreferredSize(new Dimension(COLS * CELL_SIZE, ROWS * CELL_SIZE));

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                JLabel label = new JLabel();
                label.setOpaque(false);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setVerticalAlignment(SwingConstants.CENTER);
                int type = rand.nextInt(CANDY_TYPES);
                candyTypes[row][col] = type;
                label.setIcon(candyIcons[type]);
                label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                final int r = row, c = col;
                label.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        handleClick(r, c);
                    }
                });
                grid[row][col] = label;
                panel.add(label);
            }
        }

        add(panel);
        setVisible(true);
    }

    private void handleClick(int row, int col) {
        if (selected == null) {
            selected = new Point(row, col);
            grid[row][col].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
        } else {
            int r1 = selected.x, c1 = selected.y;
            if ((Math.abs(r1 - row) == 1 && c1 == col) || (Math.abs(c1 - col) == 1 && r1 == row)) {
                swap(r1, c1, row, col);
                if (!checkMatches()) {
                    swap(r1, c1, row, col); // No match, revert
                } else {
                    playSound("sound.wav");
                }
            }
            grid[r1][c1].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            selected = null;
        }
    }

    private void swap(int r1, int c1, int r2, int c2) {
        int temp = candyTypes[r1][c1];
        candyTypes[r1][c1] = candyTypes[r2][c2];
        candyTypes[r2][c2] = temp;

        grid[r1][c1].setIcon(candyIcons[candyTypes[r1][c1]]);
        grid[r2][c2].setIcon(candyIcons[candyTypes[r2][c2]]);
    }

    private boolean checkMatches() {
        boolean[][] matched = new boolean[ROWS][COLS];
        boolean found = false;

        // Horizontal check
        for (int row = 0; row < ROWS; row++) {
            int count = 1;
            for (int col = 1; col < COLS; col++) {
                if (candyTypes[row][col] == candyTypes[row][col - 1]) {
                    count++;
                } else {
                    if (count >= 3) {
                        for (int k = 0; k < count; k++) matched[row][col - 1 - k] = true;
                        score += calculateScore(count);
                        found = true;
                    }
                    count = 1;
                }
            }
            if (count >= 3) {
                for (int k = 0; k < count; k++) matched[row][COLS - 1 - k] = true;
                score += calculateScore(count);
                found = true;
            }
        }

        // Vertical check
        for (int col = 0; col < COLS; col++) {
            int count = 1;
            for (int row = 1; row < ROWS; row++) {
                if (candyTypes[row][col] == candyTypes[row - 1][col]) {
                    count++;
                } else {
                    if (count >= 3) {
                        for (int k = 0; k < count; k++) matched[row - 1 - k][col] = true;
                        score += calculateScore(count);
                        found = true;
                    }
                    count = 1;
                }
            }
            if (count >= 3) {
                for (int k = 0; k < count; k++) matched[ROWS - 1 - k][col] = true;
                score += calculateScore(count);
                found = true;
            }
        }

        if (found) {
            removeMatches(matched);
            refillCandies();
            updateIcons();
            updateTitle();
            SwingUtilities.invokeLater(() -> {
                try { Thread.sleep(300); } catch (InterruptedException e) {}
                checkMatches();
            });
        }

        return found;
    }

    private int calculateScore(int count) {
        if (count >= 5) {
           return 100;
        }
        if (count == 4) {
           return 60;
        }
        return 30;
    }

    private void removeMatches(boolean[][] matched) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (matched[row][col]) {
                    candyTypes[row][col] = -1;
                }
            }
        }
    }

    private void refillCandies() {
        for (int col = 0; col < COLS; col++) {
            int pointer = ROWS - 1;
            for (int row = ROWS - 1; row >= 0; row--) {
                if (candyTypes[row][col] != -1) {
                    candyTypes[pointer][col] = candyTypes[row][col];
                    pointer--;
                }
            }
            while (pointer >= 0) {
                candyTypes[pointer][col] = rand.nextInt(CANDY_TYPES);
                pointer--;
            }
        }
    }

    private void updateIcons() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                grid[row][col].setIcon(candyIcons[candyTypes[row][col]]);
            }
        }
    }

    private void updateTitle() {
        setTitle("Candy Crush - Score: " + score);
    }

    private void playSound(String soundFile) {
        new Thread(() -> {
            try {
                File file = new File(soundFile);
                if (!file.exists()) {
                    System.err.println("Sound file not found: " + soundFile);
                    return;
                }
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
                Thread.sleep(1000);  
                clip.stop();
                clip.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Splash screen
    public static void main(String[] args) {
        showSplash();

        try {
            Thread.sleep(2000); // 2 seconds 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(CandyCrush::new);
    }

    private static void showSplash() {
        JWindow splash = new JWindow();
        JLabel label = new JLabel(new ImageIcon("images/splash.png"));
        splash.getContentPane().add(label);

        int width = 10 * 64 + 16;
        int height = 8 * 64 + 39;

        splash.setSize(width, height);
        splash.setLocationRelativeTo(null);
        splash.setVisible(true);

        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}
            splash.setVisible(false);
            splash.dispose();
        }).start();
    }
}
