package game.utils;

public class Level {
    int ghostNumber;
    int frightFruitNumber;
    int pacmanSpeed;
    int ghostSpeed;

    public Level(int ghostN, int frightfruitN, int pacmanS, int ghostS) {
        this.ghostNumber = ghostN;
        this.frightFruitNumber = frightfruitN;
        this.pacmanSpeed = pacmanS;
        this.ghostSpeed = ghostS;
    }
}
