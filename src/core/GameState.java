package core;

import java.util.ArrayList;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;


/**
 * Main class representing the game state.
 * This class handles the game logic, rendering, and user input.
 */

class GameState extends JPanel implements MouseListener, KeyListener {

/**
 * {@link #frameSetter()}
 * 
     *   **Pane Concept NOTE:** :
     * 
     *       Main (this) : The game position that Mapped (Overlayed)
     *                     Will be cloned for aligning a Bot and top zone (Border 0)
     * 
     *   --TOP-- Border2
     * 
     *       HP Zone        :   Align Top-LEFT!     nest(1)
     *       
     *       Tip & Timer    :   Align Top-RIGHT!    nest(1)
     *
     *       Label Zone     :   Align CENTER!       nest(2)
     * 
     *   --CENTER--
     * 
     *       Game end Label
     *       
     *   --BOT-- Border1
     *      
     *       Button Zone    :   Align Bot >> with (Nest Border 1.1)
     *          But 1       :   "Flow layout" Align LEFT  (WEST)
     *          But 2       :   Single        Align RIGHT (EAST)
 *
*/

    /** 
     * @see Generate
     * Main Content of these Game ( Map Setter and regenerating ) 
     * Beware! this one might be called in self package 
    */
    static public Mapping<ArrayList<ArrayList<Integer>>, ArrayList<ArrayList<Character>>> Map;

    /**
     * @see TurnManager
     * The main core of specific turn
     */
    private TurnManager turnManager;


    // Hard code locate - Entities
    private Sprite<GameState> P1;
    private Sprite<GameState> P2;
    private Sprite<GameState> nowTurn;


    // Collection of Entittes and Sprites thread
    private ArrayList<Sprite<GameState>> sprites;


    // private Image backGround;
    private Theme theme;
    private Color backgroundColor     = new Color(33, 26, 40); // Prevent Theme error

    // Hard code locate - Images
    private Image tileImage;
    private Image tileTrapImage;
    private Image tilePoisonedImage;
    private ArrayList<TileObjectHover> tileObjects = new ArrayList<>();


    // Hard code locate - JPanel (For re-draw and UI)
    private HPBar  jpnHPBarP1;
    private HPBar  jpnHPBarP2;
    private JLabel jlbTurn;
    private JLabel jlbMoveTurn;
    private JLabel jlbNotify; // Public for setting at logic
    private JLabel jlbEndGame;
    private JLabel jlbTimer;


    // Hard code locate - Event Button , Event Listener
    private GameButton attackButton;
    private GameButton shootButton;
    private GameButton trapButton;
    private GameButton fillButton;
    private GameButton endTurnButton;


    // Hard code locate - Font
    private Fonts fontsNormal         = new Fonts(Setting.WINDOWS_WIDTH > 1200 ? 32.0 : 24.0);
    private Font defaultNormal        = fontsNormal.getFontAt(0);
    private Font articleNormal        = fontsNormal.getFontAt(3);

    private Fonts fontsTitle          = new Fonts(144.0);
    private Font defaultTitle         = fontsTitle.getFontAt(0);

    // For logic & toggle button
    private int buttonDrawRange;
    private String actionKeyword;
    private Color buttonKeyColor;
    private boolean endGameFlag;
    private boolean buttonDrawFlag;
    private boolean toggleButton;
    private boolean keyboardInputEnabled;
    private boolean mouseInputEnabled;


    // Calling default constructor is not permitted
    public GameState() {}

    /**
     *  @param characterInfo
     *  GameState MUST get selection info
     *  for set the property of P1 & P2
     *  which Mapping pairs collection (<P1>, <P2>)
     */
    public GameState(Mapping<Entities, Entities> characterInfo) {

        /* Initial */
            // Generating a map by randomize
            do {
                Map = Generate.generateMap();
                // Generate.printGridGenerated();
                Generate.randomCharacterPosition(characterInfo.getFirst());
                Generate.randomCharacterPosition(characterInfo.getSecond());
            } while ( !Logic.pathfinderChecker(characterInfo.getFirst(), characterInfo.getSecond()) );

            // init entities list
            sprites = new ArrayList<>();

            // Thread for animated
                P1 = new Sprite<>(this, characterInfo.getFirst());
                P2 = new Sprite<>(this, characterInfo.getSecond());
                
                sprites.add(P1);
                sprites.add(P2);

            // Hard Locate ****DO RANDOMIZE
            nowTurn = Generate.randomIntWithIndicator(1) == 0 ? P1 : P2;

            // Generate ID of Theme
            theme = new Theme();
            tileImage           = Tiles.getTile(0, theme);
            tileTrapImage       = Tiles.getTile(1, theme);
            tilePoisonedImage   = Tiles.getTile(2, theme);
            
        /* */

        // Label Setter Zone
        jlbTurn         = new TextDialog(defaultNormal, "Round : 1");
        jpnHPBarP1      = new HPBar(P1.getSelf().getHealthPoint(), new Color(211, 30, 69));
        jpnHPBarP2      = new HPBar(P2.getSelf().getHealthPoint(), new Color(30, 159, 211));
        jlbMoveTurn     = new TextDialog(articleNormal, "Move Turn : " + nowTurn.getSelf().getTurn());
        jlbNotify       = new TextDialog(articleNormal, (nowTurn == P1 ? "Red" : "Blue") + " Got start!"); // Space for Layout position
        jlbEndGame      = new TextDialog(defaultTitle, "", new Color(245, 249, 6));
        jlbTimer        = new TextDialog(defaultNormal, "");
        jlbTurn.setAlignmentX(Component.CENTER_ALIGNMENT);
        jlbNotify.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Initialize Components
        turnManager = new TurnManager(this, sprites, jlbTurn, jlbNotify, jlbTimer);
        frameSetter();
        initVariant();
        updateButtons();
        runThread(); // Initial start thread

        // Focus and key listener
            setBackground(backgroundColor);
            setFocusable(true);
            requestFocusInWindow();
            addMouseListener(this);
            addKeyListener(this);
    }

    // for setting self Boolean and Initial variant
    private void initVariant() {
        buttonDrawRange         = -1;
        actionKeyword           = "";
        endGameFlag             = false;
        buttonDrawFlag          = false;
        toggleButton            = true;
        keyboardInputEnabled    = true;     
        mouseInputEnabled       = false; // For toggling button
        buttonKeyColor          = null;

        // Suitable button visibilities for player
        if (nowTurn.getSelf() instanceof Pathfinder){
            shootButton.setVisible(false);            
            trapButton.setVisible(false);
            fillButton.setVisible(true);
        }
        else if (nowTurn.getSelf() instanceof Caster){
            shootButton.setVisible(true);                
            trapButton.setVisible(true);
            fillButton.setVisible(false);
        }
    }

    private void frameSetter() {
    // Button Setter Zone ( Click Action )
        attackButton = new GameButton(defaultNormal, "", new ButtonImage("ATTACK"),  
         e -> toggleActionState("ATTACK", Setting.ATTACK_RANGE, Color.WHITE) 
        ).configureButton();
        
        shootButton  = new GameButton(defaultNormal, "", new ButtonImage("SHOOT"),
         e -> toggleActionState("SHOOT", Setting.SKILL_RANGE, Color.BLUE)
        ).configureButton();    

        trapButton  = new GameButton(defaultNormal, "", new ButtonImage("TRAP"),
         e -> toggleActionState("TRAP", Setting.TRAP_RANGE, Color.RED) 
        ).configureButton();

        fillButton = new GameButton(defaultNormal, "", new ButtonImage("FILL"),
         e -> toggleActionState("FILL", Setting.FILL_RANGE, Color.YELLOW) 
        ).configureButton();

        endTurnButton = new GameButton(defaultNormal, "END TURN", new ButtonImage("ENDTURN"),
         e -> {
            // System.out.println("______________________________________________");

            // Async Prevention
            endTurnButton.setEnabled(false);
            try {

                turnManager.endTurn();
            }
            finally {
                endTurnButton.setEnabled(true);
            }
            
            // System.out.println(P1.getSelf().getTurn());
            // System.out.println(P1.getSelf().getAttackTurn());
            // System.out.println(P1.getSelf().getSkillTurn());
            // System.out.println(P1.getSelf().getTrapTurn());
            // System.out.println("-----------------------");
            // System.out.println(P2.getSelf().getTurn());
            // System.out.println(P2.getSelf().getAttackTurn());
            // System.out.println(P2.getSelf().getSkillTurn());
            // System.out.println(P2.getSelf().getTrapTurn());
            
            jlbMoveTurn.setText("Move Turn : " + nowTurn.getSelf().getTurn());
            Logic.clearSprites(sprites);
        });

        // Set a text turn as a TOP-RIGHT
        attackButton.setTextAlignButton();
        shootButton.setTextAlignButton();
        trapButton.setTextAlignButton();
        fillButton.setTextAlignButton();
        
        // Set fixed button size
        attackButton.setPreferredSize(new Dimension(Setting.WINDOWS_WIDTH / 15, Setting.WINDOWS_WIDTH / 15));
        shootButton.setPreferredSize(new Dimension(Setting.WINDOWS_WIDTH / 15, Setting.WINDOWS_WIDTH / 15));
        trapButton.setPreferredSize(new Dimension(Setting.WINDOWS_WIDTH / 15, Setting.WINDOWS_WIDTH / 15));
        fillButton.setPreferredSize(new Dimension(Setting.WINDOWS_WIDTH / 15, Setting.WINDOWS_WIDTH / 15));
        endTurnButton.setPreferredSize(new Dimension(Setting.WINDOWS_WIDTH / 15 * 3, Setting.WINDOWS_WIDTH / 15));
        
    // Add to frame
    
        setLayout(new OverlayLayout(this));
        // Clone Border
        JPanel jpnMainBorder = new JPanel(new BorderLayout());

        // Top Zone
        JPanel jpnTopZone   = new JPanel(new BorderLayout());
        JPanel jpnBarFlow   = new JPanel();
        JPanel jpnTextFlow  = new JPanel();

            jpnBarFlow.setLayout(new BoxLayout(jpnBarFlow, BoxLayout.Y_AXIS));
            jpnTextFlow.setLayout(new BoxLayout(jpnTextFlow, BoxLayout.Y_AXIS));

            jpnBarFlow.setPreferredSize(new Dimension(Setting.WINDOWS_WIDTH / 3, Setting.WINDOWS_HEIGHT / 8));
            jpnTextFlow.setPreferredSize(new Dimension(Setting.WINDOWS_WIDTH / 3, Setting.WINDOWS_HEIGHT / 8));

            jpnTopZone.setOpaque(false);
            jpnBarFlow.setOpaque(false);
            jpnTextFlow.setOpaque(false);

            jpnTopZone.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));

            jpnBarFlow.add(jpnHPBarP1);
            jpnBarFlow.add(Box.createVerticalStrut(10)); // Making gap
            jpnBarFlow.add(jpnHPBarP2);
            jpnTextFlow.add(jlbTurn);
            jpnTextFlow.add(jlbNotify);
            

            JPanel jpnTips = new JPanel();
            JPanel jpnMinibox = new JPanel();
            jpnTips.setLayout(new BorderLayout());
            jpnMinibox.setLayout(new BoxLayout(jpnMinibox, BoxLayout.Y_AXIS));

            jpnTips.setPreferredSize(new Dimension(Setting.WINDOWS_WIDTH / 3, Setting.WINDOWS_HEIGHT / 8));
        
            jpnTips.setOpaque(false);
            jpnMinibox.setOpaque(false);

            ImageIcon gridTip = new ImageIcon(getClass().getResource("/resources/img/grid/GC2.png"));
            Image scaledGridTip = gridTip.getImage().getScaledInstance(Setting.WINDOWS_WIDTH / 18, Setting.WINDOWS_WIDTH / 18, Image.SCALE_SMOOTH);
            JLabel tipPics = new JLabel(new ImageIcon(scaledGridTip));

            tipPics.setAlignmentX(Component.RIGHT_ALIGNMENT);
            jlbTimer.setAlignmentX(Component.RIGHT_ALIGNMENT);
            
            jpnMinibox.add(tipPics);
            jpnMinibox.add(jlbTimer);

            jpnTips.add(jpnMinibox, BorderLayout.EAST);
            
            jpnTopZone.add(jpnBarFlow, BorderLayout.WEST);
            jpnTopZone.add(jpnTextFlow, BorderLayout.CENTER);
            jpnTopZone.add(jpnTips, BorderLayout.EAST);

        
        // Center Zone
        JPanel jpnMidZone = new JPanel(new BorderLayout());
        JPanel jpnFlowMid = new JPanel(new FlowLayout(FlowLayout.LEFT));
            jpnMidZone.setOpaque(false);  
            jpnFlowMid.setOpaque(false);

            jlbMoveTurn.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 0));
            jpnFlowMid.add(jlbMoveTurn);
            jpnMidZone.add(jpnFlowMid, BorderLayout.NORTH);
            jpnMidZone.add(jlbEndGame, BorderLayout.CENTER);

        // Bottom Zone
        JPanel jpnBottomZone = new JPanel(new BorderLayout());
        JPanel jpnBtmFlow    = new JPanel(new FlowLayout(FlowLayout.LEFT));
            jpnBottomZone.setOpaque(false);
            jpnBtmFlow.setOpaque(false);

            jpnBottomZone.setBorder(BorderFactory.createEmptyBorder(Setting.WINDOWS_WIDTH / 20, Setting.WINDOWS_HEIGHT / 20, Setting.WINDOWS_HEIGHT / 20, Setting.WINDOWS_WIDTH / 20));

            jpnBtmFlow.add(attackButton);
            jpnBtmFlow.add(shootButton);
            jpnBtmFlow.add(trapButton);
            jpnBtmFlow.add(fillButton);
            jpnBottomZone.add(jpnBtmFlow, BorderLayout.WEST);
            jpnBottomZone.add(endTurnButton, BorderLayout.EAST); // Single Button

        // Main adder zone
        jpnMainBorder.add(jpnTopZone, BorderLayout.NORTH);
        jpnMainBorder.add(jpnMidZone, BorderLayout.CENTER);
        jpnMainBorder.add(jpnBottomZone, BorderLayout.SOUTH);
        jpnMainBorder.setOpaque(false);

        this.add(jpnMainBorder);
    }

    // frameSetter Called this for toggle button properties
    private void toggleActionState(String keyword, int range, Color color) {
        toggleButton         = !toggleButton;
        keyboardInputEnabled = toggleButton;
        mouseInputEnabled    = !toggleButton;
        endTurnButton.setEnabled(toggleButton);
        
        if (toggleButton) { // Disabled toggle
            runThread();
            buttonDrawFlag   = false;
            actionKeyword    = "";
        } 
        
        else { // Pause for Selection
            stopThread();
            buttonDrawFlag   = true;
            buttonKeyColor   = color;
            actionKeyword    = keyword;
            buttonDrawRange  = range;
        }
        repaint();
    }
    
    @Override 
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g.drawImage(theme.getBackgroundImage(), 0, 0, getWidth(), getHeight(), this);

        // Always detection HP
        jpnHPBarP1.setCurrentHP(P1.getSelf().getHealthPoint());
        jpnHPBarP2.setCurrentHP(P2.getSelf().getHealthPoint());

        // Grid isometric position calculation 
        for (int x = 0; x < Setting.GRIDSIZE; x++) {
            for (int y = 0; y < Setting.GRIDSIZE; y++){
                int drawXPosition = Setting.TILE_STARTX + (x - y) * Setting.TILE_HEIGHT / 2;
                int drawYPosition = Setting.TILE_STARTY + (x + y) * Setting.TILE_HEIGHT / 4;
                Image keyImage;
                
                Mapping <Integer, Character> temp = new Mapping<>(Map.getFirst().get(x).get(y), Map.getSecond().get(x).get(y));

                // Draw Grid
                if (temp.getFirst() == 1) { continue; }
                switch (temp.getSecond()) {
                    case 'E' -> keyImage = tileImage;
                    case 'T' -> keyImage = tileTrapImage;
                    case 'P' -> keyImage = tilePoisonedImage;
                    default  -> keyImage = tileImage; // Where position is not be blanked
                }

                if (keyImage == null) { continue; }
                Drawer.drawTile(g2d, keyImage, tileImage, drawXPosition, drawYPosition);
            }
        }

        // Detection Attacking areas by from grid calculation
        if (buttonDrawFlag) {
            tileObjects.clear();
            int positionX          = nowTurn.getSelf().getXPosition();
            int positionY          = nowTurn.getSelf().getYPosition();
            Boolean[] positionFlag = { true, true, true, true };
            int[][] directions = {
                { 0, -1},
                { 1,  0},
                { 0,  1},
                {-1,  0}
            };
        
            for (int i = 0; i <= buttonDrawRange; i++) {
                for (int j = 0; j < 4; j++) {
                    if (!positionFlag[j]) { continue; }
        
                    int xDirection = positionX + directions[j][0] * i;
                    int yDirection = positionY + directions[j][1] * i;
        
                    if (!Logic.isValidBound(xDirection, yDirection)) {
                        positionFlag[j] = false;
                        continue;
                    }
        
                    if (!Logic.isValidRange(xDirection, yDirection) && !actionKeyword.equals("FILL")) {
                        positionFlag[j] = false;
                        continue;
                    }
        
                    // Trap cannot replace positioned tiles
                    if (actionKeyword.equals("TRAP") && Map.getSecond().get(xDirection).get(yDirection) != 'E') {
                        continue;
                    }
        
                    // Fill action positions in Map.getFirst()
                    if (actionKeyword.equals("FILL") && Map.getFirst().get(xDirection).get(yDirection) == 0) {
                        continue;
                    }
        
                    // Draw tile object
                    int drawXPosition = Setting.TILE_STARTX + (xDirection - yDirection) * Setting.TILE_HEIGHT / 2;
                    int drawYPosition = Setting.TILE_STARTY + (xDirection + yDirection) * Setting.TILE_HEIGHT / 4;
                    TileObjectHover temp = Drawer.drawCoverTile(g2d, buttonKeyColor, tileImage, drawXPosition, drawYPosition); 
                    
                    if (i != 0) {
                        assert temp != null; // assert the temp is not be null;
                        temp.setPosition(xDirection, yDirection);
                        tileObjects.add(temp);
                    }
                }
            }
        }
     
        // Draw Entities
        Drawer.sortingEntitiesLayers(sprites);
        for (Sprite<GameState> sprite : sprites) {
            Drawer.drawEntities(g2d, sprite.getCurrentImage(), tileImage, sprite.getSelf().getxVisualPosition(), sprite.getSelf().getyVisualPosition());
        }

        // Draw End-Game Activate When Someone is deaded
        if (!P1.getSelf().isAlive() || !P2.getSelf().isAlive()){
            
            endGameActions();

            if (!P1.getSelf().isAlive() && !P2.getSelf().isAlive()){
                jlbEndGame.setText("DRAW");
                return;
            }
            else if (!P1.getSelf().isAlive()) {
                jlbEndGame.setText("P2 WINS");
                return;
            }
            else if (!P2.getSelf().isAlive()) {
                jlbEndGame.setText("P1 WINS");
                return;
            }
        }
    }

    // Method for checking every entities standing at
    private Entities getEntityAtPosition(int x, int y) {
        for (Sprite<GameState> s : sprites) {
            Entities temp = s.getSelf();
            if (temp.equalsPosition(x, y)) {
                return temp;
            }
        }
        return null;
    }

    @Override
    public void keyTyped(KeyEvent e) {}     // not implemented

    @Override
    public void keyReleased(KeyEvent e) {}  // not implemented

    @Override
    public void keyPressed(KeyEvent e) {
        if (endGameFlag) {
            restart();
        }
        
        keyPerformed(e);
    }

    // Key - Self events
    private void keyPerformed(KeyEvent event) {
        int keyCode = event.getKeyCode();
        
        
        // The actionKeyword will defined at button event
        Entities actor = nowTurn.getSelf();

        // Ignore disabled keyboard
        switch (keyCode) {
            case KeyEvent.VK_1, KeyEvent.VK_NUMPAD1 -> {
                actionHandler(attackButton, actor instanceof Attackable);
            }
            case KeyEvent.VK_2, KeyEvent.VK_NUMPAD2 -> {
                actionHandler(fillButton, actor instanceof Fillable);
                actionHandler(shootButton, actor instanceof Shootable);
                
            }
            case KeyEvent.VK_3, KeyEvent.VK_NUMPAD3 -> {
                actionHandler(trapButton, actor instanceof Trapable);
            }
        }
        
        if (keyboardInputEnabled){
            switch (keyCode) {
                case KeyEvent.VK_W, KeyEvent.VK_UP      -> {Logic.doWalk(nowTurn, Direction.UP, jlbNotify, jlbMoveTurn);}
                case KeyEvent.VK_D, KeyEvent.VK_RIGHT   -> {Logic.doWalk(nowTurn, Direction.RIGHT, jlbNotify, jlbMoveTurn);}
                case KeyEvent.VK_S, KeyEvent.VK_DOWN    -> {Logic.doWalk(nowTurn, Direction.DOWN, jlbNotify, jlbMoveTurn);}
                case KeyEvent.VK_A, KeyEvent.VK_LEFT    -> {Logic.doWalk(nowTurn, Direction.LEFT, jlbNotify, jlbMoveTurn);}
                case KeyEvent.VK_1, KeyEvent.VK_NUMPAD1, KeyEvent.VK_2, KeyEvent.VK_NUMPAD2, KeyEvent.VK_3, KeyEvent.VK_NUMPAD3 -> {} // Ignore default case
                default -> { jlbNotify.setText("Not a key");}
            }
        }

        revalidate();
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {} // not implemented

    @Override
    public void mouseReleased(MouseEvent e) {} // not implemented

    @Override
    public void mouseEntered(MouseEvent e) {} // not implemented

    @Override
    public void mouseExited(MouseEvent e) {} // not implemented

    @Override
    public void mouseClicked(MouseEvent e) {
        if (endGameFlag) {
            restart();
        }
        else if (mouseInputEnabled) {
            mousePerformed(e);
        }
    }

    // Mouse - Self event
    private void mousePerformed(MouseEvent e) {
        
        TileObjectHover t = TileObjectHover.tilesContain(tileObjects, e.getX(), e.getY());
        if (t == null){ return; } // Objects is null
        
        int positionX = t.getPositionX();
        int positionY = t.getPositionY();

        // Not checking the wall grid got checked while generate positioned
        // The actionKeyword will defined at button event
        Entities actor = nowTurn.getSelf();
        String nofAct  = getWhoTurn();

        switch (actionKeyword) {
            case "ATTACK"-> {
                if (actor instanceof Attackable attackableEntity) {
                    // System.out.println(nowTurn.getSelf().getAttackTurn());
                    
                    Entities target = getEntityAtPosition(positionX, positionY);
                    if (attackableEntity.attacking(target)) {
                        this.add(new ActionFrame(actionKeyword, positionX, positionY));
                        jlbNotify.setText(nofAct + " use ATTACK!");
                        jlbMoveTurn.setText("Move Turn : " + actor.getTurn());
                    }
                    else{
                        jlbNotify.setText("There is no Object");
                    }
                }
            }
            case "SHOOT" -> {
                if (actor instanceof Shootable shootableEntity) {

                    if (!shootableEntity.checkShoot()) break;

                    char direction = Logic.checkShootDirection(nowTurn, positionX, positionY);
                    int SpositionX = nowTurn.getSelf().getXPosition();  
                    int SpositionY = nowTurn.getSelf().getYPosition(); 
                    int dx = 0, dy = 0;
                    jlbNotify.setText(nofAct + " use SKILL");
                    
                    switch (direction) {
                        case 'N' -> dy =  1;
                        case 'S' -> dy = -1;
                        case 'E' -> dx = -1;
                        case 'W' -> dx =  1;
                    }
            
                    for (int i = 0; i < Setting.SKILL_RANGE; i++) {
                        int targetX = SpositionX + dx * (i + 1);
                        int targetY = SpositionY + dy * (i + 1);
                        // System.out.println(targetX + " " + targetY);

                        if (!Logic.isValidRange(targetX, targetY)) {
                            break;
                        }

                        if (actor.getSkillTurn() == Setting.CASTER_SKILLTURN){
                            shootableEntity.shooting(getEntityAtPosition(targetX, targetY));
                            actor.setSkillTurn(Setting.CASTER_SKILLTURN); // Do not count if its not ended
                            this.add(new ActionFrame(actionKeyword, targetX, targetY));
                        }

                    }

                    actor.setSkillTurn(0); // Count the skill as used

                }
            }
            
            case "TRAP"  -> {
                if (actor instanceof Trapable trappableEntity) {
                    if (trappableEntity.trapping()) {
                        Map.getSecond().get(positionX).set(positionY, 'T');
                        jlbNotify.setText(nofAct + " use TRAP");
                    }
                }
            }
            case "FILL"  -> {
                if (actor instanceof Fillable fillableEntity) {
                    if (fillableEntity.filling()) {
                        Map.getFirst().get(positionX).set(positionY, 0);
                        jlbNotify.setText(nofAct + " use FILL");
                    }
                }
            }
            default -> {}
        }

        // Reset Input and button setter unable thread
        resetMouseAction();

    }

    public Sprite<GameState> getP1() {
        return this.P1;
    }

    public Sprite<GameState> getP2() {
        return this.P2;
    }

    public Sprite<GameState> getNowTurn() {
        return this.nowTurn;
    }

    public String getWhoTurn() {
        return nowTurn == P1 ? "Red" : "Blue";
    }

    public Theme getTheme() {
        return theme;
    }

    public void setNowTurn (Sprite<GameState> s) {
        this.nowTurn = s;
    }

    public void updateButtons() {
        // Suitable button visibilities for player
        Entities actor = nowTurn.getSelf();
    
        boolean attackEnable = false;
        boolean shootEnable  = false;
        boolean trapEnable   = false;
        boolean fillEnable  = false;
    
        if (actor instanceof Pathfinder p) {
            attackEnable = p.getAttackTurn() > 0;
            fillEnable   = p.getFillCollection() > 0;

            attackButton.setText(String.valueOf(p.getAttackTurn()));
            fillButton.setText(String.valueOf(p.getFillCollection()));

            shootButton.setVisible(false);
            trapButton.setVisible(false);
            fillButton.setVisible(true);

        } else if (actor instanceof Caster c) {
            attackEnable = c.getAttackTurn() > 0;
            shootEnable  = c.checkShoot();
            trapEnable   = c.getTrapCollection() > 0;
    
            attackButton.setText(String.valueOf(c.getAttackTurn()));
            shootButton.setText(shootEnable ? "1" : "0");
            trapButton.setText(String.valueOf(c.getTrapCollection()));
    
            shootButton.setVisible(true);
            trapButton.setVisible(true);
            fillButton.setVisible(false);
        }
    
        attackButton.setEnabled(attackEnable);
        shootButton.setEnabled(shootEnable);
        trapButton.setEnabled(trapEnable);
        fillButton.setEnabled(fillEnable);
    }
    
    public void resetMouseAction(){
        toggleButton            = true;
        keyboardInputEnabled    = true;
        mouseInputEnabled       = false;
        endTurnButton.setEnabled(true);

        buttonDrawFlag          = false;
        runThread();
        updateButtons();
        Logic.clearSprites(sprites);
    }

    private void actionHandler(JButton jbt, boolean instancable) {

        if (!jbt.isEnabled()){
            // jlbNotify.setText("No Object usage");
            return;
        }

        if (instancable){
            jbt.getActionListeners()[0].actionPerformed(new ActionEvent(jbt, ActionEvent.ACTION_PERFORMED, null));
            return;
        }
        else {
            jlbNotify.setText("");
            return;
        }
    }

    private void stopThread() {
        for (Sprite<GameState> s : sprites) {
            s.stop();
        }
    }

    private void runThread() {
        for (Sprite<GameState> s : sprites) {
            if (s.isActive()) {continue; } //Skipped Active thread

            s.setThread(new Thread(s));
            s.start();
            s.getThread().start();
        }
    }

    private void endGameActions(){
        stopThread();
        turnManager.endGame();
        
        attackButton.setEnabled(false);
        shootButton.setEnabled(false);
        trapButton.setEnabled(false);
        fillButton.setEnabled(false);
        endTurnButton.setEnabled(false);
        
        mouseInputEnabled = true;
        keyboardInputEnabled = true;
        endGameFlag = true;
        
    }

    private void restart() {
        // Replace a new frame by get this size from ancestor
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.remove(this);

        InitialState initialState = new InitialState();
        frame.add(initialState);
        frame.revalidate();
        frame.repaint();

        initialState.requestFocusInWindow();
    }
}