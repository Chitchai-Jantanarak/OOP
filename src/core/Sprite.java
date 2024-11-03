package core;

import java.awt.Image;

import javax.swing.JPanel;

import static java.lang.Thread.sleep;

class Sprite <T extends JPanel> implements Runnable {
    private Entities self;
    private Thread thread;
    private TransitionThread transitionThread;

    private final T gridPanel;              // Refers to Panel
    private Image[] images;                 // Paths for entity images
    private int currentFrameImage;          // Frame by Frame thread
    private int frameCount;                 // Counting for Loop
    private Boolean running;

    public Sprite(Entities entity) {
        this.self       = entity;
        this.thread     = null;
        this.transitionThread = null;

        gridPanel       = null;
        this.images     = entity.getImages();
        this.running    = false;

        if (images != null) {
            this.frameCount = this.images.length;
        }
    }

    public Sprite(T panel, Entities entity) {
        this.self       = entity;
        this.thread     = null;
        this.transitionThread = null;

        this.images     = entity.getImages();
        this.running    = false;

        if (images != null) {
            this.frameCount = this.images.length;
        }

        this.gridPanel = panel;
    }

    public void startMove(int startX, int startY) {
        if (!isTransitionThreadWorking()) {

            transitionThread = new TransitionThread(this.self, startX, startY);
            transitionThread.start();
        } else {
            System.out.println("Movement already in progress...");
        }
    }
    

    private void animate() {
        if (frameCount == 0) { 
            stop();
            System.out.println( this.self + " frame get failed" );
            return;
        }
        currentFrameImage = (currentFrameImage + 1) % images.length;
    }

    public int getCurrentFrameImage() {
        return currentFrameImage;
    }

    public Image getCurrentImage() {
        if (frameCount == 0) { 
            stop();
            System.out.println( this.self + " frame get failed" );
            return null;
        }

        return images[currentFrameImage];
    }

    public int getFrameCount() {
        return frameCount;
    }

    public Entities getSelf() {
        return self;
    }

    public Sprite<T> getAddress() {
        return this;
    }

    public Thread getThread() {
        return this.thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public void stop() {
        this.running = false;
    }

    public void start() {
        this.running = true;
    }

    public Boolean isActive() {
        return this.running;
    }

    public Boolean isTransitionThreadWorking() {
        return !(transitionThread == null || !transitionThread.isAlive());
    }

    @Override
    public void run() {
        while (running) {

            if (transitionThread != null && transitionThread.isAlive()) {
                try {
                    transitionThread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            try {
                sleep(1000 / 20);
                animate();
                gridPanel.repaint();
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
       }
    }
}
