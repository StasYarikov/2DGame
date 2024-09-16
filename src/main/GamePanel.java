package main;

import entity.Player;
import object.*;
import tile.TileManager;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {

    // SCREEN SETTINGS
    final int originalTileSize = 16; // 16x16 tile
    public final int scale = 3;

    public int tileSize = originalTileSize * scale; // 48x48 tile
    public double multiplier = 1.0;
    public int maxScreenCol = 16;
    public int maxScreenRow = 12;
    public int screenWidth = tileSize * maxScreenCol; // 768 pixels
    public int screenHeight = tileSize * maxScreenRow; // 576 pixels

    //WORLD SETTINGS
    public final int maxWorldCol = 60;
    public final int maxWorldRow = 60;
    public int worldWidth = tileSize * maxWorldCol;
    public int worldHeight = tileSize * maxWorldRow;

    // FPS
    int FPS = 60;

    TileManager tileM = new TileManager(this);
    KeyHandler keyH = new KeyHandler(this);
    Sound music = new Sound();
    Sound se = new Sound();
    public CollisionChecker checker = new CollisionChecker(this);
    public AssetSetter aSetter = new AssetSetter(this);
    public UI ui = new UI(this);
    Thread gameThread;

    // ENTITY AND OBJECT
    public Player player = new Player(this, keyH);
    public SuperObject[] obj = new SuperObject[10];

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    public void zoomInOut(int i) {
        if (tileSize == 38 && i < 0 || tileSize == 100 && i > 0) {
            System.out.println("The max is reached");
        }
        else {
            int oldWorldWidth = tileSize * maxWorldCol;
            tileSize += i;
            int newWorldWidth = tileSize * maxWorldCol;

            multiplier = (double) newWorldWidth / oldWorldWidth;

            double newPlayerWorldX = player.worldX * multiplier;
            double newPlayerWorldY = player.worldY * multiplier;

            player.worldX = newPlayerWorldX;
            player.worldY = newPlayerWorldY;
            player.speed = player.speed + i * maxWorldCol / 720.0;
            SuperObject.changeScale(this);
            player.changeScale(this);

            if (this.obj[0] != null) {
                this.obj[0].worldX = 28 * this.tileSize;
                this.obj[0].worldY = 12 * this.tileSize;
            }

            if (this.obj[1] != null) {
                this.obj[1].worldX = 28 * this.tileSize;
                this.obj[1].worldY = 45 * this.tileSize;
            }

            if (this.obj[2] != null) {
                this.obj[2].worldX = 43 * this.tileSize;
                this.obj[2].worldY = 13 * this.tileSize;
            }

            if (this.obj[3] != null) {
                this.obj[3].worldX = 15 * this.tileSize;
                this.obj[3].worldY = 16 * this.tileSize;
            }

            if (this.obj[4] != null) {
                this.obj[4].worldX = 13 * this.tileSize;
                this.obj[4].worldY = 33 * this.tileSize;
            }

            if (this.obj[5] != null) {
                this.obj[5].worldX = 17 * this.tileSize;
                this.obj[5].worldY = 27 * this.tileSize;
            }

            if (this.obj[6] != null) {
                this.obj[6].worldX = 15 * this.tileSize;
                this.obj[6].worldY = 12 * this.tileSize;
            }

            if (this.obj[7] != null) {
                this.obj[7].worldX = 42 * this.tileSize;
                this.obj[7].worldY = 47 * this.tileSize;
            }
        }
    }

    public void setupGame() {

        aSetter.setObject();
        playMusic(0);

    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

//    @Override
//    public void run() {
//        double drawInterval = (double) 1000000000 / FPS; // 0.01666 seconds
//        double nextDrawTime = System.nanoTime() + drawInterval;
//
//        while (gameThread != null) {
//
//            // 1 UPDATE: update information such as character position
//            update();
//            // 2 DRAW: draw the screen with the updated information
//            repaint();
//
//            try {
//                double remainingTime = nextDrawTime - System.nanoTime();
//                remainingTime = remainingTime/1000000;
//
//                if (remainingTime < 0) {
//                    remainingTime = 0;
//                }
//
//                Thread.sleep((long) remainingTime);
//
//                nextDrawTime += drawInterval;
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }

    @Override
    public void run() {

        double drawInterval = (double) 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        long drawCount = 0;

        while (gameThread != null) {
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            timer += currentTime - lastTime;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
                drawCount++;
            }

            if (timer >= 1000000000) {
                System.out.println("FPS: " + drawCount);
                timer = 0;
                drawCount = 0;
            }
        }
    }

    public void update() {
        player.update();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // DEBUG
        long drawStart = 0;
        if (keyH.checkDrawTime) {
            drawStart = System.nanoTime();
        }
        // TILE
        tileM.draw(g2);

        // OBJECT
        for (int i = 0; i < obj.length; i++) {
            if (obj[i] != null) {
                obj[i].draw(g2, this);
            }
        }

        // PLAYER
        player.draw(g2);
        ui.draw(g2);

        //DEBUG
        if (keyH.checkDrawTime) {
            long drawEnd = System.nanoTime();
            long passed = drawEnd - drawStart;
            g2.setColor(Color.white);
            g2.drawString("Draw Time: " + passed, 10, 400);
            System.out.println("Draw Time: " + passed);
        }

        g2.dispose();
    }

    public void playMusic(int i) {

        music.setFile(i);
        music.play();
        music.loop();

    }

    public void stopMusic() {

        music.stop();

    }

    public void playSE(int i) {

        se.setFile(i);
        se.play();

    }
}
