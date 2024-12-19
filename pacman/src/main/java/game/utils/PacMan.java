package game.utils;

import java.awt.*;
// import java.awt.event.*;
// import java.util.HashSet;
// import java.util.Random;
// import java.util.Random.*;
// import javax.swing.*;

public class PacMan extends Block {
    int score;
    int lives;
    boolean isInvincible = false;// when pacman has been hit, it will be set to true
    final int INVINCIBLE_DURATION = 15;
    int invincibleTimer = 0;// invincible timer is responsible for counting
    int flashFrame = 0;
    final int FLASH_INTERVAL = 2;
    int animationSpeed = 5;

    PacMan(Image image, int x, int y, int width, int height) {
        super(image, x, y, width, height);
        lives = 3;
        score = 0;
    }

    public boolean eatFood(Block food) {
        if (collision(this, food)) {
            score += 1;
            return true;
        }
        return false;
    }

    public boolean eatFrightFruit(FrightFruit frightFruit) {
        if (collision(this, frightFruit)) {
            System.out.println("Ghost Frightening Mode activated!");
            return true;
        }
        return false;
    }

    public void collideWithGhost(Block ghost) {
        if (collision(ghost, this) && invincibleTimer == 0) {
            lives -= 1;
            invincibleTimer = INVINCIBLE_DURATION;
            isInvincible = true;
            // if (lives == 0) {
            // gameOver = true;
            // return;
            // }
        }
    }

    // reset PacMan Postion
    public void resetPositions() {
        reset();
        velocityX = 0;
        velocityY = 0;
        lives = 3;
        score = 0;
        isInvincible = false;
        invincibleTimer = 0;
    }

}