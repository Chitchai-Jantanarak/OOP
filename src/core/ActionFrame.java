package core;

import java.awt.Image;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import static java.lang.Thread.sleep;


/*
 *  A class of Action list
 *  e.g. Attacked, Shoot
 */
public class ActionFrame extends JPanel implements Runnable {

    private final String IMAGE_PATH = "/resources/img/action/";
    private final String attackKeywordPath = "ATTACK";
    private final String shootKeywordPath  = "SHOOT";
    private String mergePath;

    private Image[] images;
    private int currentFrameImage;
    private int frameCount;
    private Boolean running;

    private int positionX;
    private int positionY;

    public ActionFrame(String keyword, int positionX, int positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.running   = Setting.RUNNABLE;

        switch (keyword) {
            case "ATTACK" -> mergePath = IMAGE_PATH + attackKeywordPath;
            case "SHOOT"  -> mergePath = IMAGE_PATH + shootKeywordPath;
        }
        setOpaque(false);
        collectImage();
        new Thread(this).start();
    }

    private void collectImage(){
        images = new Image[8]; // Hard Code Size

        for (int i = 0; i < images.length; i++) {
            String imageName = i + ".png";
            String fullPath = this.mergePath + imageName;
            
            try {
                images[i] = new ImageIcon(getClass().getResource(fullPath)).getImage();
                if (images[i] == null) System.out.println("Image get Failed"); 
            }
            catch (Exception e) {
                System.out.println("Image load Failed");
                e.printStackTrace();
            }
        }

        this.frameCount = images.length;
        this.currentFrameImage = 0;
    }

    public void stop() {
        this.running = false;
    }

    public void start() {
        this.running = true;
    }

    private void animate() {
        if (frameCount == 0) { 
            stop();
            System.out.println( "Action get image failed" );
            return;
        }
        currentFrameImage = (currentFrameImage + 1);
        if (currentFrameImage >= frameCount) {
            stop();
            currentFrameImage = frameCount - 1;
            clearSelf();
        }
    }

    private void clearSelf() {
        SwingUtilities.invokeLater(() -> {
            if (getParent() != null) {
                getParent().remove(this);
            }
        });
    }

    @Override
    public void run() {
        while (running) {
            animate();

            if (!running){
                break;
            }

            revalidate();
            repaint();

            try {
                sleep(1000 / 20);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
       }
    }

    @Override
    public void paintComponent (Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        if (images != null){
            Drawer.drawAction(g2d, images[currentFrameImage], positionX, positionY);
        }
        
    }

}