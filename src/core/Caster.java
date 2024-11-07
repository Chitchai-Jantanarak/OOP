package core;

import java.awt.Image;
import javax.swing.ImageIcon;

class Caster extends Entities implements Attackable, Shootable, Trapable {
    private final String IMAGE_PATH = "/resources/img/cast/";

    public Caster(){
        super(-1,-1, Setting.HEALTHPOINT, Setting.CASTER_ATTACKPOINT, Setting.CASTER_MOVETURN,
         Setting.CASTER_ATTACKTIMES, Setting.CASTER_SKILLTURN, Setting.CASTER_TRAPTURN, -1, 'C');
        setTrapCollection(1);
    }

    public Caster(char side){
        super(1, 1, Setting.HEALTHPOINT, Setting.CASTER_ATTACKPOINT, Setting.CASTER_MOVETURN,
         Setting.CASTER_ATTACKTIMES, Setting.CASTER_SKILLTURN, Setting.CASTER_TRAPTURN, -1, side);
        setTrapCollection(1);

        collectImage(side);
    }

	protected void collectImage(char side){
        images = new Image[8]; // Hard Code Size

        if (side != 'R' && side != 'B'){          
            Setting.RUNNABLE = false;
            System.out.println("Side selection Failed");
        }

        for (int i = 0; i < images.length; i++) {
            String imageName = "C" + side + i + ".png";
            String fullPath = this.IMAGE_PATH + imageName;
            
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
        this.moveTurn = Setting.CASTER_MOVETURN;
    }

    @Override
    protected void resetAttackTurn() {
        this.attackTurn = Setting.CASTER_ATTACKTIMES;
    }
    
    @Override
    protected void resetSkillTurn() {
        if (this.skillTurn == Setting.CASTER_SKILLTURN) { return; }
        this.skillTurn++;
    }

    @Override
    protected void resetTrapTurn() {
        // Always added the trap of caster will add as collection
        this.trapTurn++;
        setTrapCollection(trapTurn / Setting.CASTER_TRAPTURN);
    }

    @Override
    protected void resetFillTurn() {} // Not implemented for Pathfinder

    @Override
    public boolean attacking(Entities e) {
        if (e == null || attackTurn == 0) { return false; }
        e.setHealthPoint(e.getHealthPoint() - this.attackPoint);
        this.setAttackTurn(this.getAttackTurn() - this.attackPoint);
        return true;
    }

    @Override
    public void shooting(Entities e) {
        if (e == null || skillTurn != Setting.CASTER_SKILLTURN) { return; }
        e.setHealthPoint(e.getHealthPoint() - Setting.SKILL_DAMAGE);
    }

    @Override
    public boolean checkShoot() {
        return this.skillTurn == Setting.CASTER_SKILLTURN;
    }

    @Override
    public boolean trapping() {
        if (trapTurn < Setting.CASTER_TRAPTURN) { return false; }
        this.setTrapTurn(this.trapTurn -= Setting.CASTER_TRAPTURN);
        setTrapCollection(trapTurn / Setting.CASTER_TRAPTURN);
        return true;
    }
}
