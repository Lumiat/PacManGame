package game.utils;

import java.awt.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Ghost extends Block {

    Random random = new Random();

    Ghost(Image image, int x, int y, int width, int height, int velocity) {
        super(image, x, y, width, height);
        VELOCITY = velocity;
    }

    // input the walls set
    // check then handle the collision
    public void collideWithWall(PacMan pacman, HashSet<Block> walls, int rows, int columns) {
        for (Block wall : walls) {
            if (collision(this, wall)) {
                x -= velocityX;
                y -= velocityY;
                int newDirection = trackPacman(pacman, walls, rows, columns);
                updateGhostDirection(newDirection, pacman, walls, rows, columns);
            }
        }

    }

    public int trackPacman(PacMan pacman, HashSet<Block> walls, int rows, int columns) {
        int[] dx = { -1, 1, 0, 0 }; // Directions: Up, Down, Left, Right
        int[] dy = { 0, 0, -1, 1 };
        int directions[] = { 1, 0, 3, 2 };
        // 使用BFS计算Pacman到Ghost目标位置的最短路径
        int[][] dist = new int[rows][columns]; // 记录每个位置到pacman的最短距离
        // int[][] parent = new int[rows][columns]; // 记录路径的来源方向
        int bestDirection = -1;
        // 初始化BFS的dist和parent
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                dist[i][j] = Integer.MAX_VALUE; // 默认距离为无穷大
                // parent[i][j] = -1; // 没有父节点
            }
        }

        // 设置Pacman的初始位置
        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(pacman.y / tileSize, pacman.x / tileSize)); // 使用tile大小进行坐标转换
        dist[pacman.y / tileSize][pacman.x / tileSize] = 0; // Pacman的起始位置，距离为0

        // 获取鬼的位置，作为目的地
        int ghostX = this.y / tileSize;
        int ghostY = this.x / tileSize;
        boolean flag = false;
        // 进行BFS，找到从Pacman到目标位置的最短路径
        while (!queue.isEmpty()) {
            Point current = queue.poll();

            // 检查四个方向的邻居,执行上-下-左-右
            for (int i = 0; i < 4; i++) {
                int newX = current.x + dx[i];
                int newY = current.y + dy[i];

                // 检查新位置是否有效，且未被访问过
                if (newX >= 0 && newX < rows && newY >= 0 && newY < columns && dist[newX][newY] == Integer.MAX_VALUE) {
                    if (!isWall(newX, newY, walls)) { // 如果没有墙壁阻挡
                        dist[newX][newY] = dist[current.x][current.y] + 1;
                        bestDirection = i; // 记录路径方向
                        queue.add(new Point(newX, newY));
                        if (newX == ghostX && newY == ghostY) {
                            flag = true;
                            break;
                        }
                    }
                }
            }
            if (flag)
                break;
        }

        // 返回最优方向（鬼应该朝哪个方向走）
        if (bestDirection != -1) {
            return directions[bestDirection];
        } else
            return this.direction;
    }

    // Helper: check if the next place is wall
    private boolean isWall(int x, int y, HashSet<Block> walls) {
        for (Block wall : walls) {
            if (x == wall.y / tileSize && y == wall.x / tileSize) {
                return true;
            }
        }
        return false;
    }

    public void updateGhostDirection(int direction, PacMan pacman, HashSet<Block> walls, int rows, int columns) {
        // int prevDirection = this.direction;
        this.direction = direction;
        updateVelocity();

        // 计算新的位置
        int newX = this.x + this.velocityX;
        int newY = this.y + this.velocityY;

        // 如果没有碰撞，更新位置
        boolean canMove = true;
        for (Block wall : walls) {
            if (collision(this, wall)) {
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
            this.direction = trackPacman(pacman, walls, rows, columns);
            updateVelocity();
        }
    }
}
