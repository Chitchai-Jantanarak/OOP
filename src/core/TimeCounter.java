package core;

import javax.swing.JLabel;

class TimeCounter extends Thread {
    private int counter_MS;
    private JLabel jlbTimer;
    private boolean running;

    private TurnManager turnManager;
    

    public TimeCounter(JLabel jlbTimer, TurnManager turnManager) {
        this.counter_MS  = Setting.TIMER;
        this.jlbTimer    = jlbTimer;
        this.turnManager = turnManager;
        this.running     = true;
    }

    @Override
    public void run() {
        while (counter_MS > 0 && running) {
            try {
                jlbTimer.setText("Timer : " + String.valueOf(this.counter_MS));
                this.counter_MS--;
                sleep(1000); // Sleep for 1 second
            } catch (InterruptedException e) {
                running = false;
                Thread.currentThread().interrupt();
            }
        }
        

        if (running) {
            turnManager.nextTurn();
        }

        running = false;
    }

    public boolean isRunning(){
        return running;
    }
}
