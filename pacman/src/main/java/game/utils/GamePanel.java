package game.utils;

import javax.swing.*;

import java.awt.event.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private int rowCount = 17;
    private int columnCount = 17;
    private int tileSize = 35;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;

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

    // private int score = 0;
    // private int lives = 3;

    private boolean gameStarted = false;
    private boolean gameOver = false;

    private int currentFrame = 0;

    private Color wallColor = new Color(0x1fbbf7, false);
    private Color fontColor = new Color(0x5774e4, false);

    // int frameCount = 4;// 4 directions
    private Image[] pacManFrames = new Image[4 * 4];
    private Image[] ghostFrames = new Image[2];
    private Image[] ghostScaredFrames = new Image[2];

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
        // load images
        loadImages();
        // gameStarted = true;
        walls = new HashSet<Block>();
        food = new HashSet<Block>();
        ghosts = new HashSet<Ghost>();
        frightFruits = new HashSet<FrightFruit>();
        generateGhost();
        loadMap();
        for (Block ghost : ghosts) {
            int newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection, walls);
        }

        gameLoop = new Timer(50, this);// every 50 milliseconds repaint 20fps
        gameLoop.start();

    }

    public void loadImages() {
        // FrontCover
        frontCover = new ImageIcon(getClass().getResource("/FrontCover.png")).getImage();

        // Ghost2
        ghostFrames[0] = new ImageIcon(getClass().getResource("/Ghost2.gif")).getImage();
        // Ghost1
        ghostFrames[1] = new ImageIcon(getClass().getResource("/Ghost1.gif")).getImage();
        // GhostScared1
        ghostScaredFrames[0] = new ImageIcon(getClass().getResource("/GhostScared1.gif")).getImage();
        // GhostScared2
        ghostScaredFrames[1] = new ImageIcon(getClass().getResource("/GhostScared2.gif")).getImage();

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
            for (Block ghost : ghosts) {
                tileMap[ghost.startY / tileSize][ghost.startX / tileSize] = 'b';
                System.out.println("Deleted ghost");
            }
        }
        // clear old elements
        walls.clear();
        food.clear();
        ghosts.clear();
        frightFruits.clear();
        // System.out.println("Game State:" + gameOver);

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
                    // 添加FrightFruit
                    case 'f':
                        FrightFruit frightFruit = new FrightFruit(FrightFruit, x, y, tileSize, tileSize);
                        frightFruits.add(frightFruit);
                        break;
                    case 'g':
                        Ghost ghost = new Ghost(ghostFrames[0], x, y, tileSize, tileSize);
                        ghosts.add(ghost);
                        Block beanG = new PacMan(null, x + 14, y + 14, 4, 4);
                        food.add(beanG);
                        break;
                    case 'p':
                        pacman = new PacMan(pacManFrames[12], x, y, tileSize, tileSize);
                        break;
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gameStarted) {
            drawElements(g);

            drawLives(g);
        }
        drawMap(g);
        if (!gameStarted) {
            drawStartHintBox(g);
        }
    }

    public void drawElements(Graphics g) {

        // 绘制pacman
        if (!pacman.isInvincible || pacman.flashFrame < pacman.FLASH_INTERVAL / 2)
            g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        g.setColor(Color.WHITE);
        for (Block bean : food) {
            g.fillOval(bean.x, bean.y, bean.width, bean.height);
        }

        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        // display score
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(fontColor);

        g.drawString(" Score: " + String.valueOf(pacman.score), tileSize * 13, tileSize * 17 - 10);
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

    public void drawStartHintBox(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        int scaledWidth = 500;
        int scaledHeight = 100;
        g2d.drawImage(frontCover, 22, 200, scaledWidth, scaledHeight, null);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Rockwell", Font.BOLD, 20));
        String hintMessage = "PRESS ENTER TO PLAY >>>";
        g.drawString(hintMessage, 145, 330);
    }

    // draw the lives of pacman
    public void drawLives(Graphics g) {
        for (int i = 1; i <= pacman.lives; i++) {
            g.drawImage(pacManFrames[10], tileSize * i, tileSize * 16 + 5, null);
        }

    }

    public void generateGhost() {
        int counter = 0;
        while (counter < 5) {
            int x = random.nextInt(17);
            int y = random.nextInt(17);
            if (tileMap[y][x] != 'x' && tileMap[y][x] != 'p') {
                counter++;
                tileMap[y][x] = 'g';
                System.out.println("Create ghost at tileMap[" + y + "][" + x + ']');
            }
        }
    }

    public void move() {
        pacman.move(); // Pacman moves
        for (Block wall : walls) {
            pacman.collideWithWall(wall); // Check collision with walls
        }

        for (Block bean : food) {
            pacman.eatFood(bean);
        }

        for (Ghost ghost : ghosts) {
            ghost.move(); // Ghost moves
            ghost.collideWithWall(walls); // Check collision with walls
            pacman.collideWithGhost(ghost); // Check pacman collision with ghosts
            if (pacman.lives == 0) {
                gameOver = true;
                return;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        // move then redraw again
        move();
        currentFrame++;

        if (pacman.isInvincible) {
            pacman.flashFrame++;
            if (pacman.flashFrame > pacman.FLASH_INTERVAL)
                pacman.flashFrame = 0;
        }

        // update pacMan animation frame
        pacman.currentAnimationFrame = currentFrame % 4;// each direction has 4 frames
        pacman.image = pacManFrames[pacman.direction * 4 + pacman.currentAnimationFrame];
        // update ghost animation frame
        for (Block ghost : ghosts) {
            // ghost.currentAnimationFrame = currentFrame % 2; // 假设每个幽灵有2帧
            ghost.image = ghostFrames[ghost.direction % 2];
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
            generateGhost();
            loadMap();

            // resetPositions();
            pacman.lives = 3;
            pacman.score = 0;
            pacman.isInvincible = false;
            pacman.invincibleTimer = 0;
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