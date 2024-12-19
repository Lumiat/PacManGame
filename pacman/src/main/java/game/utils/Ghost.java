package game.utils;

import java.awt.*;
import java.util.HashSet;
import java.util.Random;

public class Ghost extends Block {
    private int directions[] = { 0, 1, 2, 3 };
    Random random = new Random();

    Ghost(Image image, int x, int y, int width, int height) {
        super(image, x, y, width, height);

    }

    // input the walls set
    // check then handle the collision
    public void collideWithWall(HashSet<Block> walls) {
        for (Block wall : walls) {
            if (collision(this, wall)) {
                x -= velocityX;
                y -= velocityY;
                // Update direction randomly or based on some AI logic
                int newDirection = directions[random.nextInt(4)];
                updateDirection(newDirection, walls);
            }
        }

    }

}
