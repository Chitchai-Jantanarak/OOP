package core;

class TransitionThread extends Thread {
    private Entities refEntities; // for referencing Entity properties
    private int startX;
    private int startY;
    private int endX;
    private int endY;
    private static final int STEPS = 20;
    private static final int DELAY_MS = 5;

    public TransitionThread(Entities e, int virX, int virY) {
        this.refEntities = e;
        this.startX      = virX;
        this.startY      = virY;
        this.endX        = e.getXPosition();
        this.endY        = e.getYPosition();
    }

    private double easeOutCirc(double x) {
        return Math.sqrt(1 - Math.pow(x - 1, 2));
    }

    @Override
    public void run() {
        for (int i = 0; i <= STEPS; i++) {
            double t = (double) i / STEPS;
            double easedT = easeOutCirc(t);

            double newX = (startX + (endX - startX) * easedT);
            double newY = (startY + (endY - startY) * easedT);
            
            refEntities.setVisualPosition(newX, newY);

            try {
                Thread.sleep(DELAY_MS);
            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
            }
        }

        refEntities.setVisualPosition();
        
    }
}
