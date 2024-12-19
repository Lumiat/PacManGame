package game.utils;

import java.awt.*;
import java.util.Random;

public class FrightFruit extends Block {
    Random random = new Random();

    FrightFruit(Image image, int x, int y, int width, int height) {
        super(image, x, y, width, height);
    }

}
