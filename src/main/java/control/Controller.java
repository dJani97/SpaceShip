/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import view.ScorePanel;
import view.GamePanel;
import model.Ship;
import model.GameObject;
import database.*;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import model.Player;

/**
 *
 * @author djani
 *
 */
public class Controller implements Runnable {

    //int score = 0;
    //int lives = 3;
    int asteroidNum = 10;
    Ship ship;
    GamePanel gamePanel;
    ScorePanel scorePanel;
    List<GameObject> asteroids = new CopyOnWriteArrayList<>();
    Image imgAsteroid;
    Thread controllerThread;
    int timeSurvived;
    Player player;
    Dao dao;
    List<Player> playerList;

    public Controller(GamePanel gamePanel, ScorePanel scorePanel) {
        this.gamePanel = gamePanel;
        this.scorePanel = scorePanel;
        focusOnPanel();
        setup();
    }

    private void setup() {
        gamePanel.setController(this);
        scorePanel.setController(this);
        Image background = new ImageIcon(this.getClass().
                getResource(Globals.IMG_BACKGROUND)).getImage();
        gamePanel.setBackground(background);
        gamePanel.setPreferredSize(new Dimension(
                Globals.PANEL_WIDTH, Globals.PANEL_HEIGHT));

        GameObject.setSleepTime(Globals.SLEEP_TIME);
        GameObject.setPanelWidth(gamePanel.getPreferredSize().width);
        GameObject.setPanelHeight(gamePanel.getPreferredSize().height);
        Ship.setMaxTurnVelocity(Globals.SHIP_TURN_VELOCITY);
        Ship.setTurnFrameCount(Globals.SHIP_TURN_FRAME_COUNT);
        Ship.setAcceleration(Globals.SHIP_ACCELERATION);
        Ship.setMaxVelocity(Globals.SHIP_MAX_VELOCITY);

        imgAsteroid = new ImageIcon(this.getClass().
                getResource(Globals.IMG_ASTEROID)).getImage();

        establishConnection();

        startGame();
    }

    public void startGame() {
        loadPlayerScores();
        player = new Player(askForName());
        scorePanel.setPlayerName(player.getName());
        

        Image img = new ImageIcon(this.getClass().
                getResource(Globals.IMG_SHIP_STILL)).getImage();

        Image imgThrusting = new ImageIcon(this.getClass().
                getResource(Globals.IMG_SHIP_THRUSTING)).getImage();

        ship = new Ship(gamePanel.getWidth() / 2, gamePanel.getHeight() / 2, 0, 0, 0, 0, img, imgThrusting, this);
        Thread t = new Thread(ship);
        t.start();

        controllerThread = new Thread(this);
        controllerThread.start();
    }

    private void endGame() {
        player.setScore(timeSurvived / 1000);
        writeToDatabase(player);
        asteroids.clear();
        player = null;
        ship = null;
        timeSurvived = 0;

        int answer = JOptionPane.showConfirmDialog(gamePanel, "Your score is saved. Do you want to restart the game?", "GAME OVER", JOptionPane.YES_NO_OPTION);
        
        if(answer == 0) {
            startGame();
        } else {
            System.exit(0);
        }
    }

    public void draw(Graphics g) {
        if (ship != null) {
            ship.draw(g);
        }
        for (GameObject asteroid : asteroids) {
            asteroid.draw(g);
        }
    }

    public void repaint() {
        gamePanel.repaint();
    }

    public void keyEvent(KeyEvent evt, boolean pressed) {
        if (evt.getKeyChar() == 'w') {
            ship.setThrust(pressed);
        }
        if (evt.getKeyChar() == 'a') {
            ship.setTurnLeft(pressed);
        }
        if (evt.getKeyChar() == 'd') {
            ship.setTurnRight(pressed);
        }
    }

    private void createNewAsteroid() {
        GameObject asteroid = null;

        do {
            float posX = (float) (Math.random() * gamePanel.getPreferredSize().width);
            float posY = (float) (Math.random() * gamePanel.getPreferredSize().height);
            float dX = (float) (Math.random() * Globals.ASTEROID_MAX_VELOCITY) - Globals.ASTEROID_MAX_VELOCITY / 2;
            float dY = (float) (Math.random() * Globals.ASTEROID_MAX_VELOCITY) - Globals.ASTEROID_MAX_VELOCITY / 2;
            float angle = (float) (Math.random() * 360);
            float angVel = (float) (Math.random() * Globals.ASTEROID_MAX_ROTATION);

            asteroid = new GameObject(posX, posY, dX, dY, angle, angVel, imgAsteroid, this);

        } while (ship.collides(asteroid, 3));

        asteroids.add(asteroid);
        Thread asteroidThread = new Thread(asteroid);
        asteroidThread.start();
    }

    @Override
    public void run() {
        while (ship.isAlive()) {

            if (asteroids.size() < Globals.ASTEROIDS + timeSurvived / 1000) {
                createNewAsteroid();
            }

            shipCollision();

            try {
                Thread.sleep(Globals.SLEEP_TIME);
                timeSurvived += Globals.SLEEP_TIME;
                scorePanel.setTimeSurvived(timeSurvived);
            } catch (InterruptedException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        endGame();
    }

    private void shipCollision() {
        for (GameObject asteroid : asteroids) {
            if (ship.collides(asteroid)) {
                asteroid.setAlive(false);
                asteroids.remove(asteroid);

                ship.setAlive(false);
            }
        }
    }

    private String askForName() {
        while (true) {
            String s = (String) JOptionPane.showInputDialog("Enter your name:");
            if ((s != null) && (s.length() > 0) && (!s.contains(" "))) {
                return s;
            }
        }
    }

    private void establishConnection() {
        Connection connection = DB_Connector.getInstance().getConnection();
        dao = new PlayerDao(connection);
    }

    private void loadPlayerScores() {
        if(playerList != null) playerList.clear();
        playerList = dao.listAllRecords();
        System.out.println(playerList);
        
        scorePanel.clearPlayers();
        scorePanel.addPlayers(playerList);
    }

    private void writeToDatabase(Player player) {
        boolean needsUpdate = false;
        if (playerList.contains(player)) {
            for (Player previousP : playerList) {
                if (previousP.equals(player)) {
                    needsUpdate = player.getScore() > previousP.getScore();
                }
            }
            if (needsUpdate) {
                dao.updateRecord(player);
            }
        } else {
            dao.createRecaord(player);
            playerList.add(player);
        }
    }

    public void focusOnPanel() {
        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();
    }

}
