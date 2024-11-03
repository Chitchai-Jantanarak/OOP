package core;

import java.awt.Image;


/**
 * Abstract class representing a game entity with various attributes and behaviors.
 * @see Pathfinder
 * @see Caster
 * @see Slime
 */
abstract class Entities {
    protected int xPosition;
    protected int yPosition;
    protected double xVisualPosition;
    protected double yVisualPosition;
    protected int healthPoint;
    protected int attackPoint;
    protected int moveTurn;
    protected int attackTurn;
    protected int skillTurn;
    protected int trapTurn;
    protected int trapCollection;
    protected int fillTurn;
    protected int fillCollection;
    protected Image[] images;
    protected char charRepresented;

    public Entities(){}

    public Entities(int x, int y, int health, int attack, int moveTurn, int attackTurn, int skillTurn,
     int trapTurn, int fillTurn, char charRepresented){
        this.xPosition       = x;
        this.yPosition       = y;
        this.xVisualPosition = x;
        this.yVisualPosition = y;
        this.healthPoint     = health;
        this.attackPoint     = attack;
        this.moveTurn        = moveTurn;
        this.attackTurn      = attackTurn;
        this.skillTurn       = skillTurn;
        this.trapTurn        = trapTurn;
        this.trapCollection  = -1;
        this.fillTurn        = fillTurn;
        this.fillCollection  = -1;
        this.charRepresented = charRepresented;
    }
    
    // Getters and Setters  
    public int getXPosition() {
        return xPosition;
    }

    public int getYPosition() {
        return yPosition;
    }

    public double getxVisualPosition() {
        return xVisualPosition;
    }

    public double getyVisualPosition() {
        return yVisualPosition;
    }

    public int getHealthPoint() {
        return healthPoint;
    }

    public int getTurn() {
        return moveTurn;
    }

    public int getAttackTurn() {
        return attackTurn;
    }

    public int getSkillTurn() {
        return skillTurn;
    }

    public int getTrapTurn() {
        return trapTurn;
    }

    public int getTrapCollection() {
        return trapCollection;
    }

    public int getFillTurn() {
        return fillTurn;
    }

    public int getFillCollection() {
        return fillCollection;
    }

    public Image[] getImages() {
        return images;
    }

    public char getCharRepresented() {
        return charRepresented;
    }
    
    public void setXPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    public void setYPosition(int yPosition) {
        this.yPosition = yPosition;
    }

    public void setPosition(int xPosition, int yPosition) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }

    public void setVisualPosition(double newX, double newY) {
        this.xVisualPosition = newX;
        this.yVisualPosition = newY;
    }

    public void setVisualPosition() {
        this.xVisualPosition = this.xPosition;
        this.yVisualPosition = this.yPosition;
    }

    public void setHealthPoint(int healthPoint) {
        this.healthPoint = healthPoint;
    }

    public void setTurn(int moveturn) {
        this.moveTurn = moveturn;
    }

    public void setAttackTurn(int attackTurn) {
        this.attackTurn = attackTurn;
    }

    public void setSkillTurn(int skillTurn) {
        this.skillTurn = skillTurn;
    }

    public void setTrapTurn(int trapTurn) {
        this.trapTurn = trapTurn;
    }

    public void setTrapCollection(int trapCollection) {
        this.trapCollection = trapCollection;
    }

    public void setFillTurn(int fillTurn) {
        this.fillTurn = fillTurn;
    }

    public void setFillCollection(int fillCollection) {
        this.fillCollection = fillCollection;
    }

    public boolean isAlive(){
        return this.healthPoint > 0;
    }

    public boolean equalsPosition(int x, int y) {
        return this.xPosition == x && this.yPosition == y;
    }

    protected abstract void resetTurn();
    protected abstract void resetAttackTurn();
    protected abstract void resetSkillTurn();
    protected abstract void resetTrapTurn();
    protected abstract void resetFillTurn();
}


/**
 * Interface defining dependencies with the {@link Entities} class.
 * Requires an attribute to specify where interface's method applicable.
 */
interface Attackable { 
    boolean attacking(Entities e);
}

interface Shootable { 
    void shooting(Entities e);
    boolean checkShoot();
}

interface Trapable { 
    boolean trapping();
}

interface Fillable {
    boolean filling();
}




