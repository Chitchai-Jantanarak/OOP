package core;

import java.awt.Image;

import javax.swing.ImageIcon;

public class Theme {

    private static String BACKGROUND_PATH = "/resources/img/background/";;
    private static int MAX_ID = 4; // In this files has 4 collections of theme

    private Image backgroundImage;
    private int themeID;
    
    public Theme (){

        this.themeID = Math.abs(Generate.randomInt() % MAX_ID);

        String fullPath = BACKGROUND_PATH + "bg" + this.themeID + ".png";
        try {
            this.backgroundImage = new ImageIcon(getClass().getResource(fullPath)).getImage();
        } catch (Exception e) {
            this.backgroundImage = null;
        }

    }

    public Image getBackgroundImage() {
        return backgroundImage;
    }

    public int getThemeID() {
        return themeID;
    }
}
