/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import control.Controller;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author djani
 */
public class GameObject implements Runnable {

    // logic
    boolean alive = true;
    float posX, posY;
    float dX, dY;
    float angle, angVel; // = angular velocity
    static int sleepTime = 1000;
    
    // gui
    Image img;
    static int panelWidth, panelHeight;
    Controller controller;

    public GameObject(float posX, float posY, float dX, float dY, float angle, float angVel, Image img, Controller controller) {
        this.posX = posX;
        this.posY = posY;
        this.dX = dX;
        this.dY = dY;
        this.angle = angle;
        this.angVel = angVel;
        this.img = img;
        this.controller = controller;
    }
    
    
    @Override
    public void run() {
        while (alive) {
            try{
                move();

                Thread.sleep(sleepTime);  
                
                
            } catch (InterruptedException ex) {
                Logger.getLogger(GameObject.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }

    protected void move() {
        this.posX += dX;
        this.posY += dY;
        this.angle += angVel;
        
        // edge controll
        int halfImgSize = this.img.getWidth(null) / 4;
        //System.out.println(panelHeight + " " + panelWidth);
        
        if      (this.getCenterX()-halfImgSize > panelWidth) this.setCenterX(0-halfImgSize);
        else if (this.getCenterY()-halfImgSize > panelHeight) this.setCenterY(0-halfImgSize);
        else if (this.getCenterX()+halfImgSize < 0) this.setCenterX(panelWidth+halfImgSize);
        else if (this.getCenterY()+halfImgSize < 0) this.setCenterY(panelHeight+halfImgSize);
    }

    public void draw(Graphics g) {
        //System.out.println("" + (this instanceof Ship ? "ship" : "asteroid"));
        Image imageToDraw = this.getImg();
        Image rotatedImage = rotateImage(imageToDraw , this.angle);
        g.drawImage(rotatedImage, (int)posX, (int)posY, imageToDraw.getWidth(null), imageToDraw.getHeight(null), null);
    }
    
    public static BufferedImage rotateImage(Image srcImage, double degrees) {
        /*
        forrás / source:
            https://udojava.com/tag/awt-image-rotation/
        */
        
        BufferedImage srcBfrdImg = new BufferedImage(srcImage.getWidth(null), srcImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = srcBfrdImg.createGraphics();
        bGr.drawImage(srcImage, 0, 0, null);
        bGr.dispose();
        
        double radians = Math.toRadians(degrees);

        int width = srcBfrdImg.getWidth();
        int height = srcBfrdImg.getHeight();
        
        BufferedImage result = new BufferedImage(width, height, srcBfrdImg.getType());
        Graphics2D g = result.createGraphics();
        g.rotate(radians, width / 2, height / 2);
        g.drawRenderedImage(srcBfrdImg, null);
 
        return result;
    }

    protected float[] angle_to_vector(float ang){
        double rads = Math.toRadians(angle);
        return new float[] {(float)Math.cos(rads), (float)Math.sin(rads)};
    }
        
    public boolean collides(GameObject o) {
        return posX - img.getWidth(null)/2 <= o.posX && o.posX <= posX + img.getWidth(null)/2 &&
               posY - img.getHeight(null)/2 <= o.posY && o.posY <= posY + img.getHeight(null)/2;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /*
    getters & setters
    */

    public static void setSleepTime(int sleepTime) {
        GameObject.sleepTime = sleepTime;
    }

    public static void setPanelWidth(int panelWidth) {
        GameObject.panelWidth = panelWidth;
    }

    public static void setPanelHeight(int panelHeight) {
        GameObject.panelHeight = panelHeight;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public Image getImg() {
        return img;
    }

    public boolean isAlive() {
        return alive;
    }
    
    public float getCenterX(){
        return this.posX + this.img.getWidth(null)/2;
    }
    
    public float getCenterY(){
        return this.posY + this.img.getHeight(null)/2;
    }
    
    public void setCenterX(float x){
        this.posX = x - this.img.getWidth(null)/2;
    }
    
    public void setCenterY(float y){
        this.posY = y - this.img.getHeight(null)/2;
    }
    
    
}
