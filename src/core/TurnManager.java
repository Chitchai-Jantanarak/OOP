package core;

import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.text.Position;


// Link with TimeCounter
class TurnManager {
    private int turn;
    private int round;

    private GameState game;

    /**
     *  @see #GameState
     *  Main Components for turn manager setter
     */
    private ArrayList<Sprite<GameState>> sprites;
    private Sprite<GameState> P1;
    private Sprite<GameState> P2;
    private Sprite<GameState> nowTurn;
    private JLabel jlbTurn;
    private JLabel jlbNotify;
    private JLabel jlbTimer;
    private boolean startWithP1;

    private int currentSpawnRate;
    private int mobImplCounter;

    /**
     *  @see #TimeCounter
     *  A timer in every turn 
     */
    private TimeCounter timer;
    private boolean turnProgresses; // Flag to prevent multiple execution (thread & button)

    public TurnManager(GameState game, ArrayList<Sprite<GameState>> sprites, JLabel jlbTurn, JLabel jlbNotify, JLabel jlbTimer) {
        this.turn = 0;
        this.round = 1;

        this.game      = game;
        this.sprites   = sprites;
        this.jlbTurn   = jlbTurn;
        this.jlbNotify = jlbNotify;
        this.jlbTimer  = jlbTimer;

        this.P1 = game.getP1();
        this.P2 = game.getP2();
        this.nowTurn = game.getNowTurn();
        this.startWithP1 = nowTurn == P1;

        this.currentSpawnRate  = 0;
        this.mobImplCounter    = 1;

        this.turnProgresses = false;

        prepareTraceCaller(); // Prevent asynchronous calling
        startTimer();
    }

    public synchronized void nextTurn() {
        // System.out.println("nextTurn called");
    
        // Prevent multiple executions and ensure timer is not running
        if (turnProgresses) return;
        turnProgresses = true;
    
        if (turn > 0) {
            turn = 0; // Re-Counter
            round++;
            jlbTurn.setText("Round : " + round);
    
            resetTurnEntities();
            mobsMove();
            increaseSpawnRate();
            spawnMobsByTurns();
        } else {
            turn++;
        }
    
        // System.out.println("BEFORE : " + nowTurn.getSelf().getCharRepresented());

        updateNowTurn();
        game.updateButtons();
        game.resetMouseAction();

        // System.out.println("AFTER : " + nowTurn.getSelf().getCharRepresented());
        // System.out.println("______________________________________________");
    
        startTimer();
    
        turnProgresses = false;
    }
    

    private void startTimer() {
        if (timer != null && timer.isAlive()) {
            timer.interrupt();
        }

        timer = new TimeCounter(jlbTimer, this);
        timer.start();
    }

    /**
     * Usage by end turn button and re-counter (interrupt)
     * @see GameButton#endTurnButton
     */
    public synchronized void endTurn() {
        // System.out.println("endTurn called");
        if (timer != null && timer.isAlive()) {
            timer.interrupt();
        }
        nextTurn();
    }

    /**
     * Usage for destroying the timer thread
     * Handle exception thread null loop
     */
    public void endGame() {
        if (timer != null && timer.isAlive()) {
            timer.interrupt();
        }
        timer = null;
        P1    = null;
        P2    = null;
        nowTurn = null;
    }
    
    // Mobs will spawn indicate by rounds
    private synchronized void spawnMobsByTurns() {

        // Generating new Slimes
        // Will be change the function for freely generating (Random with indicator?)
        boolean isSpawned = false;

        while (currentSpawnRate > Setting.MOB_SPAWNBOUND) {
            if (!Logic.isGeneratable()) break;

            Sprite<GameState> slime = new Sprite<>(game, new Slime(game.getTheme()));
            Generate.randomCharacterPosition(slime.getSelf());
            sprites.add(slime);

            currentSpawnRate -= Setting.MOB_SPAWNBOUND;
            // System.out.println(currentSpawnRate);
            isSpawned = true;
        }

        if (isSpawned) {
            mobImplCounter = 1;
        }
        else {
            mobImplCounter += (int) Math.ceil(round * Setting.MOB_SPAWNRATE);
        }

        reRunThread();
    }

    private synchronized void increaseSpawnRate() {
        int adjustor = Setting.MOB_SPAWNRATE * mobImplCounter;

        currentSpawnRate += Generate.randomIntWithIndicator(adjustor);
    }

    public int getTurn() {
        return turn;
    }

    public int getRound() {
        return round;
    }

    public synchronized boolean isTurnProgessing() {
        return turnProgresses;
    }

    /**
     * Iterated all collection of sprites
     * @see #sprites
     */
    private synchronized void resetTurnEntities() {
        for ( Sprite<GameState> s : sprites){
            Entities e = s.getSelf();
            e.resetTurn();
            e.resetAttackTurn();
            e.resetSkillTurn();
            e.resetSkillTurn();
            e.resetTrapTurn();
            e.resetFillTurn();
        }
    } 

    /**
     * Moves all slime mobs in the game.
     * Iterated all collection of sprites
     * the slime movement logic defined in 
     * {@link Logic#slimeMove(Sprite, Position, Position)}
     */
    private synchronized void mobsMove () {
        for (Sprite<GameState> a : sprites) {
            if (a.getSelf() instanceof Slime){
                Logic.slimeMove(a, P1, P2);
            }
        }
    }

    // start new Thread whereas new Sprite was added 
    private void reRunThread() {
        for (Sprite<GameState> s : sprites) {
            if (s.isActive()) {continue; } //Skipped Active thread

            s.setThread(new Thread(s));
            s.start();
            s.getThread().start();
        }
    }

    // Update nowTurn Sprite
    private void updateNowTurn() {
       
        if (P1 == null || P2 == null) return;
        // System.out.println();

        if (startWithP1) {
            nowTurn = (this.turn == 0) ? P1 : P2; // P1 starts first
        } else {
            nowTurn = (this.turn == 0) ? P2 : P1; // P2 starts first
        }
        // System.out.println("/// SETTER");
        // System.out.println(nowTurn.getSelf().getCharRepresented());
        game.setNowTurn(nowTurn);
        jlbNotify.setText(game.getWhoTurn() + " Turn");
        // System.out.println(game.getNowTurn().getSelf().getCharRepresented());
        // System.out.println("///");

    }

    /**
     * NOTE :
     * Using for thread prevention by using interruption 
     * while loading initial slime got appear!!
     * An interruption may occur if the loading process is unexpectedly delayed.
     * {@link javax.swing.ImageIcon}
    */ 
    private void prepareTraceCaller() {
        new Slime();
    }

}
