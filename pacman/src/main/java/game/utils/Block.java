package game.utils;

import java.awt.*;
import java.util.HashSet;

public class Block {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected Image image;

    protected int startX;
    protected int startY;
    // 0: Up, 1: Down, 2: Left, 3: Right
    protected int direction = 3;
    protected int velocityX = 0;
    protected int velocityY = 0;

    protected int currentAnimationFrame = 0;
    public static final int tileSize = 32;
    protected int VELOCITY = tileSize / 8;

    Block(Image image, int x, int y, int width, int height) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.startX = x;
        this.startY = y;
    }

    public void updateDirection(int direction, HashSet<Block> walls) {
        int prevDirection = this.direction;
        this.direction = direction;
        updateVelocity();

        // 计算新的位置
        int newX = this.x + this.velocityX;
        int newY = this.y + this.velocityY;

        // 如果没有碰撞，更新位置
        boolean canMove = true;
        for (Block wall : walls) {
            if (collision(new Block(null, newX, newY, this.width, this.height), wall)) {
                canMove = false;
                break;
            }
        }

        // 如果没有碰撞，更新位置
        if (canMove) {
            this.x = newX;
            this.y = newY;
        } else {
            // 如果有碰撞，恢复到原来的位置，方向不改变
            this.direction = prevDirection;
            updateVelocity();
        }
    }

    public void updateVelocity() {
        switch (this.direction) {
            case 0: // Up
                this.velocityX = 0;
                this.velocityY = -VELOCITY;
                break;
            case 1: // Down
                this.velocityX = 0;
                this.velocityY = VELOCITY;
                break;
            case 2: // Left
                this.velocityX = -VELOCITY;
                this.velocityY = 0;
                break;
            case 3: // Right
                this.velocityX = VELOCITY;
                this.velocityY = 0;
                break;
            default:
                this.velocityX = 0;
                this.velocityY = 0;
        }
    }

    public void reset() {
        this.x = this.startX;
        this.y = this.startY;
        this.direction = 3;
        // updateVelocity();
    }

    public void move() {
        x += velocityX;
        y += velocityY;
    }

    public boolean collision(Block a, Block b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public void collideWithWall(Block wall) {
        if (collision(this, wall)) {
            x -= velocityX;
            y -= velocityY;
            // System.out.println("happend at x:" + this.x + " y:" + this.y + "block
            // attribute: height" + this.height
            // + ",width" + this.width);
            // System.out.println("wall x: " + wall.x + " wall y: " + wall.y + "block
            // attribute: height" + wall.height
            // + ",width" + wall.width);
        }
    }

}
