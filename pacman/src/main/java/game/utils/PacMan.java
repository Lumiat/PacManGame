package game.utils;

import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Random.*;
import javax.swing.*;

public class PacMan extends JPanel implements ActionListener, KeyListener {
    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;
        int direction = 3;
        int velocityX = 0;
        int velocityY = 0;

        int currentAnimationFrame = 0;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(int direction) {
            int prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.currentAnimationFrame = 0;
            this.x += this.velocityX;
            this.y += this.velocityY;
            // boolean collisionOccured = false;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                    // collisionOccured = true;
                    // break;
                }
            }
        }

        void updateVelocity() {
            if (this.direction == 0) {
                this.velocityX = 0;
                this.velocityY = -VELOCITY;// go up in 8px
            } else if (this.direction == 1) {
                this.velocityX = 0;
                this.velocityY = VELOCITY;// go down in 8px
            } else if (this.direction == 2) {
                this.velocityX = -VELOCITY;
                this.velocityY = 0;// go left in 8px
            } else if (this.direction == 3) {
                this.velocityX = VELOCITY;
                this.velocityY = 0;// go right in 8px
            }
        }

        public void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }

    }

    private int rowCount = 17;
    private int columnCount = 17;
    private int tileSize = 35;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    private Image frontCover;
    private Image FrightFriut;

    HashSet<Block> walls;
    HashSet<Block> food;
    HashSet<Block> frightFruits;
    HashSet<Block> ghosts;
    Block pacman;
    // game related variables
    Timer gameLoop;
    int[] directions = { 0, 1, 2, 3 };// move directions , 0--U, 1--D, 2--L, 3--R
    Random random = new Random();
    int score = 0;
    int lives = 3;
    final int INVINCIBLE_DURATION = 10;
    final int FLASH_INTERVAL = 2;
    final int VELOCITY = tileSize / 6;
    boolean gameStarted = false;
    boolean gameOver = false;
    boolean isInvincible = false;// when pacman has been hit, it will be set to true
    int invincibleTimer = 0;// invincible timer is responsible for counting
    int flashFrame = 0;
    int animationSpeed = 5;
    int currentFrame = 0;

    // int frameCount = 4;// 4 directions
    Image[] pacManFrames = new Image[4 * 4];
    Image[] ghostFrames = new Image[2];
    Image[] ghostScaredFrames = new Image[2];

    private Color wallColor = new Color(0x1fbbf7, false);
    private Color fontColor = new Color(0x5774e4, false);

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
                        Block frightFruit = new Block(FrightFriut, x, y, tileSize, tileSize);
                        frightFruits.add(frightFruit);
                        break;
                    case 'g':
                        Block ghost = new Block(ghostFrames[0], x, y, tileSize, tileSize);
                        ghosts.add(ghost);
                        Block beanG = new Block(null, x + 14, y + 14, 4, 4);
                        food.add(beanG);
                        break;
                    case 'p':
                        pacman = new Block(pacManFrames[12], x, y, tileSize, tileSize);
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
        if (!isInvincible || flashFrame < FLASH_INTERVAL / 2)
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

        g.drawString(" Score: " + String.valueOf(score), tileSize * 13, tileSize * 17 - 10);
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
        for (int i = 1; i <= lives; i++) {
            g.drawImage(pacManFrames[10], tileSize * i, tileSize * 16 + 5, null);
        }

    }

    public void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        // check collision
        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }
        for (Block ghost : ghosts) {
            if (collision(ghost, pacman) && invincibleTimer == 0) {
                lives -= 1;
                invincibleTimer = INVINCIBLE_DURATION;
                isInvincible = true;
                if (lives == 0) {
                    gameOver = true;
                    return;
                }
            }

            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            for (Block wall : walls) {
                if (collision(ghost, wall)) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    int newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }
        }
        // check food eaten
        Block foodEaten = null;
        for (Block bean : food) {
            if (collision(pacman, bean)) {
                foodEaten = bean;
                score += 1;
            }
        }
        food.remove(foodEaten);
    }

    public boolean collision(Block a, Block b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;

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

        FrightFriut = new ImageIcon(getClass().getResource("/FrightFruit.png")).getImage();
    }

    PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);
        // load images
        loadImages();
        // gameStarted = true;
        walls = new HashSet<Block>();
        food = new HashSet<Block>();
        ghosts = new HashSet<Block>();
        frightFruits = new HashSet<Block>();
        generateGhost();
        loadMap();

        for (Block ghost : ghosts) {
            int newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
        gameLoop = new Timer(50, this);// every 50 milliseconds repaint 20fps
        gameLoop.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        // move then redraw again
        move();
        currentFrame++;

        if (isInvincible) {
            flashFrame++;
            if (flashFrame > FLASH_INTERVAL)
                flashFrame = 0;
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

        if (invincibleTimer > 0) {
            invincibleTimer--;
            if (invincibleTimer == 0) {
                System.out.println("无敌时间结束！");
                isInvincible = false;
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
            lives = 3;
            score = 0;
            isInvincible = false;
            invincibleTimer = 0;
            gameOver = false;

            for (Block ghost : ghosts) {
                int newDirection = directions[random.nextInt(4)];
                ghost.updateDirection(newDirection);
            }
            gameLoop.start();
        }

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            pacman.updateDirection(0);
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacman.updateDirection(1);
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.updateDirection(2);
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.updateDirection(3);
        }

        // if (pacman.direction == 'U') {
        // pacman.image = PacMan2up;
        // } else if (pacman.direction == 'D') {
        // pacman.image = PacMan2down;
        // } else if (pacman.direction == 'L') {
        // pacman.image = PacMan2left;
        // } else if (pacman.direction == 'R') {
        // pacman.image = PacMan2right;
        // }
    }
}