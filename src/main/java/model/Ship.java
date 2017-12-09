/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import control.Controller;
import java.awt.Image;

/**
 *
 * @author djani
 */
public class Ship extends GameObject {

    private long printCounter = 1;
    Image imgThrusting;
    
    static float acceleration;
    static float maxVelocity;
    static float maxTurnVelocity;
    static int turnFrameCount;
    
    
    boolean thrust = false;
    boolean turnLeft = false;
    boolean turnRight = false;

    public Ship(float posX, float posY, float dX, float dY, float angle, float angVel, Image img, Image imgThrusting, Controller controller) {
        super(posX, posY, dX, dY, angle, angVel, img, controller);
        this.imgThrusting = imgThrusting;
    }

    @Override
    protected void move() {
        
        // turn
        if (turnLeft){
            if(angVel > -maxTurnVelocity) {
                angVel -= getTurnVelStep();
            }
        }
        if (turnRight){
            if(angVel < maxTurnVelocity) {
                angVel += getTurnVelStep();
            }
        } if (!(turnLeft || turnRight)) {
            angVel *= 0.9;
        }
        
        // handling thrust
        if (thrust ) {
            float[] forwardVector = angle_to_vector(this.angle);
            this.dX += forwardVector[0] * acceleration;
            this.dY += forwardVector[1] * acceleration;
        }
        
        
        // speed controll
        this.dX *= 0.99f;
        this.dY *= 0.99f;
        
        if(this.dX > maxVelocity) this.dX = maxVelocity;
        if(this.dY > maxVelocity) this.dY = maxVelocity;
        if(this.dX < -maxVelocity) this.dX = -maxVelocity;
        if(this.dY < -maxVelocity) this.dY = -maxVelocity;
        
        
        // debug
        printCounter++;
        //if(printCounter % 120 == 0)System.out.println(this);
        
        
        super.move();
        controller.repaint();
    }

    
    
    
    
    
    /*
    getters & setters
    */
    
    public void setThrust(boolean thrust) {
        this.thrust = thrust;
    }
    public void setTurnLeft(boolean turnLeft) {
        this.turnLeft = turnLeft;
    }
    public void setTurnRight(boolean turnRight) {
        this.turnRight = turnRight;
    }

    public static void setMaxTurnVelocity(float maxTurnVelocity) {
        Ship.maxTurnVelocity = maxTurnVelocity;
    }

    public static void setTurnFrameCount(int turnFrameCount) {
        Ship.turnFrameCount = turnFrameCount;
    }

    public static void setAcceleration(float acceleration) {
        Ship.acceleration = acceleration;
    }

    public static void setMaxVelocity(float maxVelocity) {
        Ship.maxVelocity = maxVelocity;
    }

    public static void setPanelWidth(int panelWidth) {
        Ship.panelWidth = panelWidth;
    }

    public static void setPanelHeight(int panelHeight) {
        Ship.panelHeight = panelHeight;
    }

    @Override
    public Image getImg() {
        if(thrust)  return imgThrusting;
        else        return super.getImg();
    }
    
    
    
    
    private float getTurnVelStep(){
        return Ship.maxTurnVelocity / Ship.turnFrameCount;
    }
    
    
    @Override
    public String toString() {
        return String.format("SHIP vel: %f, %f \n\tang: %f", this.dX, this.dX, this.angle);
    }
}
