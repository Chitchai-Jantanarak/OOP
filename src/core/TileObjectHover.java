package core;

import java.util.ArrayList;

class TileObjectHover {
    private int positionX;
    private int positionY;
    private int[] xPoints;
    private int[] yPoints;

    public TileObjectHover(int[] xPoints, int[] yPoints) {
        this.positionX = 0;
        this.positionY = 0;
        this.xPoints   = xPoints.clone();
        this.yPoints   = yPoints.clone();
    }

    public TileObjectHover(int positionX, int positionY, int[] xPoints, int[] yPoints) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.xPoints   = xPoints.clone();
        this.yPoints   = yPoints.clone();
    }

    public boolean isContain(int x, int y) {
        
        boolean flag = false;
        int j = xPoints.length - 1; // last vertex

        for (int i = 0; i < xPoints.length; i++) {
            
            if ((yPoints[i] > y) != (yPoints[j] > y) && 
                (x < (xPoints[j] - xPoints[i]) * (y - yPoints[i]) / (yPoints[j] - yPoints[i]) + xPoints[i])) {
                flag = !flag; // Toggle the result
            }
            j = i; //next vertex
        }

        return flag;
    }

    public int[] getxPoints() {
        return xPoints;
    }

    public int[] getyPoints() {
        return yPoints;
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPosition(int positionX, int positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public static TileObjectHover tilesContain(ArrayList<TileObjectHover> tileObjects, int x, int y) {
        
        if (tileObjects == null) {return null;}

        for (TileObjectHover t : tileObjects) {
            if(t.isContain(x, y)){
                return t;
            }
        }
        return null;
    }
    
    public void print() {
        System.out.println(this);
    }
}
