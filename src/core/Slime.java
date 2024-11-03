package core;

import java.awt.Image;
import javax.swing.ImageIcon;

class Slime extends Entities implements Attackable, Trapable {
    private static final String IMAGE_PATH = "/resources/img/mob/";
    private Theme themeRef;

    public Slime(){
        super(0, 0, Setting.MOB_HEALTHPOINT, Setting.MOB_ATTACKPOINT, 1,
         1, -1, -1, Setting.MOBS_SKILLTURN, 'M');
        collectImage();
    }

    public Slime(Theme theme){
        this();
        this.themeRef = theme;
        collectImage();
    }

    protected void collectImage(){
        images = new Image[8]; // Hard Code Size

        String themeName;
        if (themeRef != null){
            themeName = "T" + themeRef.getThemeID();
        }
        else {
            themeName = "T0";
        }

        for (int i = 0; i < images.length; i++) {
            String imageName = "S" + i;
            String fullPath = IMAGE_PATH + imageName + themeName  + ".png";
            
            try {
                images[i] = new ImageIcon(getClass().getResource(fullPath)).getImage();
                if (images[i] == null) System.out.println("Image get Failed"); 
            }
            catch (Exception e) {
                System.out.println("Image load Failed");
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void resetTurn() {
        this.moveTurn = 1;
    }

    @Override
    protected void resetAttackTurn() {
        this.attackTurn = 1;
    }
    
    @Override
    protected void resetSkillTurn() {} // Not implement for Slime

    @Override
    protected void resetFillTurn() {} // Not implemented for Pathfinder

    @Override
    protected void resetTrapTurn() {
        if (this.trapTurn == Setting.MOBS_SKILLTURN) { return; }
        this.trapTurn++;
    }

    @Override
    public boolean attacking(Entities e) {
        if (e == null || attackTurn == 0) { return false; }

        e.setHealthPoint(e.getHealthPoint() - this.attackPoint);
        
        this.setAttackTurn(this.getAttackTurn() - 1);

        return true;
    }

	@Override
	public boolean trapping() {
		if (trapTurn != Setting.MOBS_SKILLTURN) { return false; }
        this.setTrapTurn(0);
        return true;
	}
}
