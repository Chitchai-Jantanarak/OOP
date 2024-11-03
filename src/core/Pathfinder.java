package core;

import java.awt.Image;
import javax.swing.ImageIcon;

class Pathfinder extends Entities implements Attackable, Fillable {
    private String IMAGE_PATH = "/resources/img/path/";

    public Pathfinder(){
        super(-1,-1, Setting.HEALTHPOINT, Setting.PATHFINDER_ATTACKPOINT, Setting.PATHFINDER_MOVETURN,
         Setting.PATHFINDER_ATTACKTIMES, 0, 0, Setting.PATHFINDER_FILLTURN, 'C');
        setFillCollection(1);
    };

    public Pathfinder(char side){
        super(0,0, Setting.HEALTHPOINT, Setting.PATHFINDER_ATTACKPOINT, Setting.PATHFINDER_MOVETURN,
         Setting.PATHFINDER_ATTACKTIMES, 0, 0, Setting.PATHFINDER_FILLTURN, side);
        setFillCollection(1);

        collectImage(side);
    }

	protected void collectImage(char side){
        images = new Image[8]; // Hard Code Size

        if (side != 'R' && side != 'B'){          
            Setting.RUNNABLE = false;
            System.out.println("Side selection Failed");
        }

        for (int i = 0; i < images.length; i++) {
            String imageName = "P" + side + i + ".png";
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
        this.moveTurn = Setting.PATHFINDER_MOVETURN;
    }

    @Override
    protected void resetAttackTurn() {
        this.attackTurn = Setting.PATHFINDER_ATTACKTIMES;
    }
    
    @Override
    protected void resetSkillTurn() {} // Not implemented for Pathfinder

    @Override
    protected void resetTrapTurn() {} // Not implemented for Pathfinder

    @Override
    protected void resetFillTurn() {
        this.fillTurn++;
        setFillCollection(fillTurn / Setting.PATHFINDER_FILLTURN);
    }

    @Override
    public boolean attacking(Entities e) {
        if (e == null || attackTurn == 0) { return false; }
        e.setHealthPoint(e.getHealthPoint() - this.attackPoint);

        // Pathfinder Ability for more turn
        if (e instanceof Slime){
            this.setTurn(this.getTurn() + 1);
        }
        else {
            this.setAttackTurn(this.getAttackTurn() - 1);
        }

        // System.out.println(e.getHealthPoint());
        return true;
    }

    @Override
    public boolean filling() {
        if (fillTurn < Setting.PATHFINDER_FILLTURN) { return false; }
        this.setFillTurn(this.fillTurn -= Setting.PATHFINDER_FILLTURN);
        setFillCollection((int)(fillTurn / Setting.PATHFINDER_FILLTURN));
        return true;
    }

}