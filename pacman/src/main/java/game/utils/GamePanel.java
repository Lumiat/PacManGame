package game.utils;

import javax.swing.*;
import java.util.concurrent.*;
import java.awt.event.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private int rowCount = 17;
    private int columnCount = 17;
    private int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    // resources related variables
    private Image frontCover;
    private Image FrightFruit;

    private HashSet<Block> walls;
    private HashSet<Block> food;
    private HashSet<FrightFruit> frightFruits;
    private HashSet<Ghost> ghosts;
    private PacMan pacman;

    // game related variables
    private Timer gameLoop;
    private int[] directions = { 0, 1, 2, 3 };// move directions , 0--U, 1--D, 2--L, 3--R
    private Level[] levels;

    // Game State Variables
    private boolean gameStarted = false;
    private boolean gameOver = false;
    private boolean gameEnded = false;
    private boolean levelPassed = false;
    private boolean ghostFrightening = false;
    private final static int FRIGHTENED_INTERVAL = 3000;// 3000 milliseconds
    private int currentFrame = 0;
    private int level = 1;

    // utilities variables
    private Color wallColor = new Color(0x1fbbf7, false);
    private Color fontColor = new Color(0x5774e4, false);

    // animation sets
    private Image[] pacManFrames = new Image[4 * 4];
    private Image[] ghostFrames = new Image[2];
    private Image[] ghostFrightenedFrames = new Image[2];

    Random random = new Random();

    // Map of Game
    // x = wall, o = skip, b = normal beans, f = frightfruit, g = ghost, p = pacman
    private char[][] tileMap = {
            { 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x' },
            { 'x', 'p', 'b', 'b', 'b', 'x', 'x', 'b', 'b', 'b', 'x', 'x', 'b', 'b', 'b', 'b', 'x' },
            { 'x', 'b', 'x', 'x', 'b', 'b', 'b', 'b', 'x', 'b', 'b', 'b', 'b', 'x', 'x', 'b', 'x' },
            { 'x', 'b', 'b', 'b', 'b', 'x', 'x', 'b', 'b', 'b', 'x', 'x', 'b', 'b', 'b', 'b', 'x' },
            { 'x', 'b', 'x', 'x', 'b', 'b', 'x', 'b', 'x', 'b', 'x', 'b', 'b', 'x', 'x', 'b', 'x' },
            { 'x', 'b', 'x', 'x', 'x', 'b', 'x', 'b', 'x', 'b', 'x', 'b', 'x', 'x', 'x', 'b', 'x' },
            { 'x', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'x', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'x' },
            { 'x', 'x', 'b', 'x', 'b', 'x', 'b', 'x', 'x', 'x', 'b', 'x', 'b', 'x', 'b', 'x', 'x' },
            { 'x', 'x', 'b', 'x', 'b', 'x', 'b', 'x', 'x', 'x', 'b', 'x', 'b', 'x', 'b', 'x', 'x' },
            { 'x', 'x', 'b', 'x', 'b', 'x', 'b', 'x', 'x', 'x', 'b', 'x', 'b', 'x', 'b', 'x', 'x' },
            { 'x', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'x' },
            { 'x', 'b', 'x', 'x', 'x', 'x', 'b', 'x', 'b', 'x', 'b', 'x', 'x', 'x', 'x', 'b', 'x' },
            { 'x', 'b', 'x', 'x', 'x', 'x', 'b', 'b', 'b', 'b', 'b', 'x', 'x', 'x', 'x', 'b', 'x' },
            { 'x', 'b', 'b', 'b', 'b', 'x', 'b', 'x', 'x', 'x', 'b', 'x', 'b', 'b', 'b', 'b', 'x' },
            { 'x', 'b', 'x', 'x', 'b', 'b', 'b', 'b', 'x', 'b', 'b', 'b', 'b', 'x', 'x', 'b', 'x' },
            { 'x', 'b', 'b', 'b', 'b', 'x', 'x', 'b', 'b', 'b', 'x', 'x', 'b', 'b', 'b', 'b', 'x' },
            { 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x' }
    };

    public GamePanel() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);
        level = 1;
        // load images
        loadImages();
        // gameStarted = true;
        walls = new HashSet<Block>();
        food = new HashSet<Block>();
        ghosts = new HashSet<Ghost>();
        frightFruits = new HashSet<FrightFruit>();
        initializeLevels();
        loadLevel(level);
        loadMap();
        for (Block ghost : ghosts) {
            int newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection, walls);
        }

        gameLoop = new Timer(50, this);// every 50 milliseconds repaint 20fps
    }

    private void initializeLevels() {
        levels = new Level[2];
        levels[0] = new Level(3, 3, tileSize / 4, tileSize / 8);
        // at level two,reduce number of frightfruit,and increse ghost speed
        levels[1] = new Level(5, 2, tileSize / 4, tileSize / 4);
    }

    private void loadLevel(int level) {
        if (level < 1 || level > levels.length)
            return; // in case level does not exist
        Level currentLevelData = levels[level - 1];
        generateGhost(currentLevelData.ghostNumber, ghosts, currentLevelData.ghostSpeed);
        generateFrightFruit(currentLevelData.frightFruitNumber, frightFruits);
        System.out.println("Level" + String.valueOf(level) + " ghosts: " + String.valueOf(currentLevelData.ghostNumber)
                + "frightfruits: " + String.valueOf(currentLevelData.frightFruitNumber));
    }

    public void loadImages() {
        // FrontCover
        frontCover = new ImageIcon(getClass().getResource("/FrontCover.png")).getImage();

        // Ghost2
        ghostFrames[0] = new ImageIcon(getClass().getResource("/Ghost2.gif")).getImage();
        // Ghost1
        ghostFrames[1] = new ImageIcon(getClass().getResource("/Ghost1.gif")).getImage();
        // GhostScared1
        ghostFrightenedFrames[0] = new ImageIcon(getClass().getResource("/GhostScared1.gif")).getImage();
        // GhostScared2
        ghostFrightenedFrames[1] = new ImageIcon(getClass().getResource("/GhostScared2.gif")).getImage();

        // PacMan1
        pacManFrames[0] = new ImageIcon(getClass().getResource("/PacMan1.gif")).getImage();
        pacManFrames[4] = new ImageIcon(getClass().getResource("/PacMan1.gif")).getImage();
        pacManFrames[8] = new ImageIcon(getClass().getResource("/PacMan1.gif")).getImage();
        pacManFrames[12] = new ImageIcon(getClass().getResource("/PacMan1.gif")).getImage();

        // PacMan2up
        pacManFrames[1] = new ImageIcon(getClass().getResource("/PacMan2up.gif")).getImage();
        // PacMan2down
        pacManFrames[5] = new ImageIcon(getClass().getResource("/PacMan2down.gif")).getImage();
        // PacMan2left
        pacManFrames[9] = new ImageIcon(getClass().getResource("/PacMan2left.gif")).getImage();
        // PacMan2right
        pacManFrames[13] = new ImageIcon(getClass().getResource("/PacMan2right.gif")).getImage();

        // PacMan3up
        pacManFrames[2] = new ImageIcon(getClass().getResource("/PacMan3up.gif")).getImage();
        // PacMan3down
        pacManFrames[6] = new ImageIcon(getClass().getResource("/PacMan3down.gif")).getImage();
        // PacMan3left
        pacManFrames[10] = new ImageIcon(getClass().getResource("/PacMan3left.gif")).getImage();
        // PacMan3right
        pacManFrames[14] = new ImageIcon(getClass().getResource("/PacMan3right.gif")).getImage();

        // PacMan4up
        pacManFrames[3] = new ImageIcon(getClass().getResource("/PacMan4up.gif")).getImage();
        // PacMan4down
        pacManFrames[7] = new ImageIcon(getClass().getResource("/PacMan4down.gif")).getImage();
        // PacMan4left
        pacManFrames[11] = new ImageIcon(getClass().getResource("/PacMan4left.gif")).getImage();
        // PacMan4right
        pacManFrames[15] = new ImageIcon(getClass().getResource("/PacMan4right.gif")).getImage();

        FrightFruit = new ImageIcon(getClass().getResource("/FrightFruit.png")).getImage();
    }

    public void loadMap() {
        // if game is just over, then clear all the ghosts in the tileMap
        if (gameOver) {
            for (Ghost ghost : ghosts) {
                tileMap[ghost.startY / tileSize][ghost.startX / tileSize] = 'b';
                System.out.println("Deleted ghost");
            }
            for (FrightFruit frightFruit : frightFruits) {
                tileMap[frightFruit.startY / tileSize][frightFruit.startX / tileSize] = 'b';
                System.out.println("Deleted frightfruit");
            }
        }

        for (int row = 0; row < tileMap.length; row++) {
            for (int col = 0; col < tileMap.length; col++) {
                int x = col * tileSize;
                int y = row * tileSize;

                char tile = tileMap[row][col];
                switch (tile) {
                    case 'x':
                        Block wall = new Block(null, x, y, tileSize, tileSize);
                        walls.add(wall);
                        break;
                    case 'b':
                        Block bean = new Block(null, x + 14, y + 14, 4, 4);
                        food.add(bean);
                        break;
                    case 'p':
                        pacman = new PacMan(pacManFrames[12], x, y, tileSize, tileSize, levels[level - 1].pacmanSpeed);
                        break;
                    case 'g':
                        Block beanG = new Block(null, x + 14, y + 14, 4, 4);
                        food.add(beanG);
                        break;

                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gameStarted) {
            drawElements(g);
            drawInformation(g);
            if (ghostFrightening) {
                drawFrightenedHint(g);
            }
        }
        drawMap(g);
        if (!gameStarted || gameOver)
            drawMessageBox(g);
    }

    public void drawElements(Graphics g) {
        // 绘制pacman
        if (!pacman.isInvincible || pacman.flashFrame < pacman.FLASH_INTERVAL / 2)
            g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        g.setColor(Color.WHITE);
        for (Block bean : food) {
            g.fillOval(bean.x, bean.y, bean.width, bean.height);
        }
        for (FrightFruit frightFruit : frightFruits) {
            g.drawImage(frightFruit.image, frightFruit.x, frightFruit.y, frightFruit.width, frightFruit.height, null);
        }
        for (Ghost ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

    }

    public void drawMap(Graphics g) {
        g.setColor(wallColor);
        for (Block wall : walls) {
            int row = wall.y / tileSize;
            // System.out.printf("row = %d\n", row);
            int col = wall.x / tileSize;
            // System.out.printf("col = %d\n", col);
            if (row > 0 && tileMap[row - 1][col] != 'x') { // 上边
                g.drawLine(wall.x, wall.y, wall.x + tileSize, wall.y);
            }
            if (row < tileMap.length - 1 && tileMap[row + 1][col] != 'x') { // 下边
                g.drawLine(wall.x, wall.y + tileSize, wall.x + tileSize, wall.y + tileSize);
            }
            if (col > 0 && tileMap[row][col - 1] != 'x') { // 左边
                g.drawLine(wall.x, wall.y, wall.x, wall.y + tileSize);
            }
            if (col < tileMap.length - 1 && tileMap[row][col + 1] != 'x') { // 右边
                g.drawLine(wall.x + tileSize, wall.y, wall.x + tileSize, wall.y + tileSize);
            }
        }
    }

    public void drawMessageBox(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        int scaledWidth = tileSize * 11;
        int scaledHeight = 80;// tileSize * 2.5
        g2d.drawImage(frontCover, tileSize * 3, 200, scaledWidth, scaledHeight, null);
        g.setColor(Color.WHITE);
        if (!gameStarted) {
            g.setFont(new Font("Rockwell", Font.BOLD, 20));
            String hintMessage = "PRESS ENTER TO PLAY >>>";
            g.drawString(hintMessage, tileSize * 4 + 8, 310);
        } else if (gameOver) {
            // g.fillRect(tileSize * 5, tileSize * 9, tileSize * 8, tileSize * 5);
            g.setFont(new Font("Rockwell", Font.BOLD, 45));
            String message = levelPassed ? "LEVEL PASSED" : "GAME    OVER";
            String subMessage = levelPassed ? "        PASSED LEVEL " : "FAILED TO PASS LEVEL ";
            g.drawString(message, tileSize * 3 + 16, tileSize * 6);
            g.setFont(new Font("Rockwell", Font.BOLD, 20));
            g.drawString(subMessage + String.valueOf(level), tileSize * 5 - 10, tileSize * 11 - 10);
            String hintMessage = levelPassed ? "PRESS ENTER TO GOTO NEXT LEVEL >>>"
                    : "        PRESS ENTER TO RESTART >>>";
            g.drawString(hintMessage, tileSize * 2 + 10, 310);
            g.drawString("SCORE: " + String.valueOf(pacman.score), tileSize * 7, tileSize * 12);
        }

    }

    public void drawInformation(Graphics g) {
        // display level
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(fontColor);
        g.drawString("Level " + String.valueOf(level), tileSize, tileSize - 10);
        // draw the lives of pacman
        for (int i = 1; i <= pacman.lives; i++) {
            g.drawImage(pacManFrames[10], tileSize * i, tileSize * 16 + 5, null);
        }
        // display score
        g.drawString(" Score: " + String.valueOf(pacman.score), tileSize * 13, tileSize * 17 - 10);
    }

    public void drawFrightenedHint(Graphics g) {
        int remainTime = 3 - (int) (System.currentTimeMillis() - pacman.frighteningStartTime) / 1000;
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        String frightenedHint = "GHOST FRIGHTENED! TIME REMAIN: ";
        g.drawString(frightenedHint + String.valueOf(remainTime) + " s", 140, tileSize - 10);
    }

    public void generateGhost(int num, HashSet<Ghost> ghosts, int velocity) {
        int counter = 0;
        while (counter < num) {
            int x = random.nextInt(columnCount);
            int y = random.nextInt(rowCount);
            if (tileMap[y][x] != 'x' && tileMap[y][x] != 'p' && tileMap[y][x] != 'g' && tileMap[y][x] != 'f') {
                counter++;
                tileMap[y][x] = 'g';
                System.out.println("Create componnet at tileMap[" + y + "][" + x + ']');
                Ghost ghost = new Ghost(ghostFrames[0], x * tileSize, y * tileSize, tileSize, tileSize, velocity);
                ghosts.add(ghost);
            }
        }
    }

    public void generateFrightFruit(int num, HashSet<FrightFruit> frightFruits) {
        int counter = 0;
        while (counter < num) {
            int x = random.nextInt(17);
            int y = random.nextInt(17);
            if (tileMap[y][x] != 'x' && tileMap[y][x] != 'p' && tileMap[y][x] != 'g' && tileMap[y][x] != 'f') {
                counter++;
                tileMap[y][x] = 'f';
                System.out.println("Create componnet at tileMap[" + y + "][" + x + ']');
                FrightFruit frightFruit = new FrightFruit(FrightFruit, x * tileSize, y * tileSize, tileSize, tileSize);
                frightFruits.add(frightFruit);
            }
        }

    }

    public void nextLevel() {
        level++;
        if (level > levels.length) {
            gameEnded = true;
            System.out.println(" All levels completed.");
        } else {
            // load next level
            loadLevel(level);
        }
    }

    public void checkLevelCompletion() {
        if (ghosts.isEmpty()) {
            gameLoop.stop();
            gameOver = true;
            levelPassed = true;
            nextLevel();
        }
    }

    public void updatePanel() {
        pacman.move(); // Pacman moves
        for (Block wall : walls) {
            pacman.collideWithWall(wall); // Check collision with wallss
        }

        Block beanEaten = null;
        for (Block bean : food) {
            if (pacman.eatFood(bean)) {
                beanEaten = bean;
                break;
            }
        }
        food.remove(beanEaten);

        Block frightFruitEaten = null;
        for (FrightFruit frightFruit : frightFruits) {
            if (pacman.eatFrightFruit(frightFruit)) {
                frightFruitEaten = frightFruit;
                ghostFrightening = true;
                break;
            }
        }
        frightFruits.remove(frightFruitEaten);
        // if frightFruit all eaten, randomly generate a new one
        if (frightFruits.isEmpty()) {
            generateFrightFruit(1, frightFruits);
        }

        Ghost exterminatedGhost = null;
        for (Ghost ghost : ghosts) {
            ghost.move(); // Ghost moves
            ghost.collideWithWall(walls); // Check collision with walls
            if (ghostFrightening) {
                if (pacman.exterminateGhost(ghost)) {
                    exterminatedGhost = ghost;
                    break;
                }
            } else {
                pacman.collideWithGhost(ghost); // Check pacman collision with ghosts
                if (pacman.lives == 0) {
                    gameOver = true;
                    return;
                }
            }
        }
        ghosts.remove(exterminatedGhost);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        // move then redraw again
        checkLevelCompletion();
        updatePanel();
        currentFrame++;

        if (pacman.isInvincible) {
            pacman.flashFrame++;
            if (pacman.flashFrame > pacman.FLASH_INTERVAL)
                pacman.flashFrame = 0;
        }
        if (ghostFrightening) {
            if (System.currentTimeMillis() - pacman.frighteningStartTime > FRIGHTENED_INTERVAL) {
                ghostFrightening = false;
                pacman.frighteningStartTime = 0;
            }

        }

        // update pacMan animation frame
        pacman.currentAnimationFrame = currentFrame % 4;// each direction has 4 frames
        pacman.image = pacManFrames[pacman.direction * 4 + pacman.currentAnimationFrame];
        // update ghost animation frame
        if (ghostFrightening) {
            for (Block ghost : ghosts) {
                // ghost.currentAnimationFrame = currentFrame % 2; // 假设每个幽灵有2帧
                ghost.image = ghostFrightenedFrames[ghost.direction % 2];
            }
        } else {
            for (Block ghost : ghosts) {
                // ghost.currentAnimationFrame = currentFrame % 2; // 假设每个幽灵有2帧
                ghost.image = ghostFrames[ghost.direction % 2];
            }
        }

        repaint();

        if (pacman.invincibleTimer > 0) {
            pacman.invincibleTimer--;
            if (pacman.invincibleTimer == 0) {
                System.out.println("无敌时间结束！");
                pacman.isInvincible = false;
            }
        }
        if (gameOver) {
            gameLoop.stop();
        }
    }

    // won't use it
    @Override
    public void keyTyped(KeyEvent e) {

    }

    // won't use it
    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
        // System.out.println("KeyEvent:" + e.getKeyCode());
        if (!gameStarted && e.getKeyCode() == KeyEvent.VK_ENTER) {
            gameStarted = true;
            gameLoop.start();
        }

        if (gameOver && e.getKeyCode() == KeyEvent.VK_ENTER) {
            ghosts.clear();
            frightFruits.clear();
            generateGhost(levels[level - 1].ghostNumber, ghosts, levels[level - 1].ghostSpeed);
            generateFrightFruit(levels[level - 1].frightFruitNumber, frightFruits);
            loadMap();

            pacman.resetPositions();
            gameOver = false;

            for (Block ghost : ghosts) {
                int newDirection = directions[random.nextInt(4)];
                ghost.updateDirection(newDirection, walls);
            }
            gameLoop.start();
        }

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            pacman.updateDirection(0, walls);
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacman.updateDirection(1, walls);
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.updateDirection(2, walls);
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.updateDirection(3, walls);
        }

    }
}
