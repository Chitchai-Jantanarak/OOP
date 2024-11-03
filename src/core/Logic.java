package core;

import java.util.ArrayList;

import javax.swing.JLabel;

class Logic {

    // Turn checker for moving
    private static boolean isMovableTurn(int turn) {
        return turn > 0 ? true : false;
    }

    // Safe-Move for Slime moving
    private static boolean isSafeMove(int positionX, int positionY) {
        return positionX >= 0 && positionY >= 0 && positionX < Setting.GRIDSIZE && positionY < Setting.GRIDSIZE
            && GameState.Map.getFirst().get(positionX).get(positionY) == 0
            && GameState.Map.getSecond().get(positionX).get(positionY) == 'E' ;
    }

    private static boolean isValidMove(int positionX, int positionY) {
        return positionX >= 0 && positionY >= 0 && positionX < Setting.GRIDSIZE && positionY < Setting.GRIDSIZE
            && GameState.Map.getFirst().get(positionX).get(positionY) == 0
            && (GameState.Map.getSecond().get(positionX).get(positionY) == 'E'
                || GameState.Map.getSecond().get(positionX).get(positionY) == 'P'
                    || GameState.Map.getSecond().get(positionX).get(positionY) == 'T');
            
    }

    // Outbound & wall checker
    public static boolean isValidRange(int positionX, int positionY) {
        return positionX >= 0 && positionY >= 0 && positionX < Setting.GRIDSIZE && positionY < Setting.GRIDSIZE
            && GameState.Map.getFirst().get(positionX).get(positionY) == 0;
    }
    
    // Outbound checker
    public static boolean isValidBound(int positionX, int positionY) {
        return positionX >= 0 && positionY >= 0 && positionX < Setting.GRIDSIZE && positionY < Setting.GRIDSIZE;
    }

    // Manhattan distance calculator
    private static int findDistance(int x1, int x2, int y1, int y2) {
        return Math.abs(x1-x2) + Math.abs(y1-y2);
    }

    // Walk-UP after checked
    public static void doWalk(Sprite<GameState> s, Direction direction, JLabel jlbNoftify, JLabel jlbMoveTurn) {
        Entities e = s.getSelf();
        int startX  = e.getXPosition();
        int startY  = e.getYPosition();
        int targetX = startX;
        int targetY = startY;
        boolean jlbSetter = jlbNoftify != null && jlbMoveTurn != null;

        switch (direction) {
            case UP    -> { targetX +=  0; targetY += -1;} 
            case RIGHT -> { targetX +=  1; targetY +=  0;}
            case DOWN  -> { targetX +=  0; targetY +=  1;}
            case LEFT  -> { targetX += -1; targetY +=  0;}
        }

        if (!isMovableTurn(e.getTurn())){
            if (jlbSetter){
                jlbNoftify.setText("move turn has ended");
            }
            return;
        } 
        if (!isValidMove(targetX, targetY)){
            if (jlbSetter){
                jlbNoftify.setText(direction + " is blocked");
            }
            return;
        }
        if (s.isTransitionThreadWorking()){
            if (jlbSetter){
                jlbNoftify.setText("Do not SPAM >:(");
            }
            return;
        }
        walkSetter(e, targetX, targetY);
        s.startMove(startX, startY);

        // Show as end Move
        if (jlbSetter){
            jlbNoftify.setText("MOVE " + direction);
            jlbMoveTurn.setText("Move Turn : " + e.getTurn());
        }
    }

    // Position-Walk Setter
    public static void walkSetter(Entities e, int targetX, int targetY) {
        int startX = e.getXPosition();
        int startY = e.getYPosition();
   
        e.setPosition(targetX, targetY);
        e.setVisualPosition();

        char newPositionChar = GameState.Map.getSecond().get(targetX).get(targetY);
        
        if (newPositionChar == 'T') {
            e.setHealthPoint(e.getHealthPoint() - Setting.TRAP_DAMAGE);
        }

        if (newPositionChar == 'P') {
            e.setHealthPoint(e.getHealthPoint() - Setting.POISON_DAMAGE);
        }

        // Update the map with the new character representation
        GameState.Map.getSecond().get(startX).set(startY, 'E'); // Clear old position
        GameState.Map.getSecond().get(targetX).set(targetY, e.getCharRepresented()); // Set new position

        // Handle trapping skill
        if (e.getTrapTurn() == Setting.MOBS_SKILLTURN && e instanceof Trapable trapable && e instanceof Slime) {
            if (trapable.trapping() && newPositionChar == 'E') {
                GameState.Map.getSecond().get(startX).set(startY, 'P');
            }
        }

        // Decrease turn
        e.setTurn(e.getTurn() - 1);
    
    }
    
    // Get Choice shoot Selection
    public static char checkShootDirection(Sprite<GameState> s, int positionX, int positionY) {
        // s is where the caster standing at
        int selfX = s.getSelf().getXPosition();
        int selfY = s.getSelf().getYPosition();

        // This case is hold all case ( The caster can't click self )

        // N & S
        if (selfX == positionX){
            if (selfY > positionY){
                return 'S';
            }
            else if (selfY < positionY){
                return 'N';
            }
        }
        // E & W
        else if (selfY == positionY){
            if (selfX > positionX){
                return 'E';
            }
            else if (selfX < positionX){
                return 'W';
            }
        }
        return '-';
    }

    // Mob Spawner
    public static boolean isGeneratable() {
        
        // This medthod is using for checking is there possible to generate a Mob
        ArrayList<ArrayList<Integer>>   tempBlocked = GameState.Map.getFirst();
        ArrayList<ArrayList<Character>> tempChar    = GameState.Map.getSecond();

        for (int i = 0; i < Setting.GRIDSIZE; i++) {
            for (int j = 0; j < Setting.GRIDSIZE; j++) {
                if (tempChar.get(i).get(j) == 'E' && tempBlocked.get(i).get(j) == 0) {
                    return true;
                }
            }
        }

        return false;
    }

    // All Slime moving logic
    /* 
       -- Self Method Dependecies --
        isValidRange , isValidMove , Pathfinding
        findDistance , doWalk
    */ 
    public static void slimeMove(Sprite<GameState> s, Sprite<GameState> P1, Sprite<GameState> P2) {
        // Finding who is Nearest for moving
        int nearestPositionX;
        int nearestPositionY;
        int positionX  = s.getSelf().getXPosition();
        int positionY  = s.getSelf().getYPosition();
        int positionXR = P1.getSelf().getXPosition(); 
        int positionYR = P1.getSelf().getYPosition();
        int positionXB = P2.getSelf().getXPosition(); 
        int positionYB = P2.getSelf().getYPosition();
    
        boolean p1SurroundedFlag = false;
        boolean p2SurroundedFlag = false;
        boolean movedFlag        = false;
        
        ArrayList<ArrayList<Character>> tempChar    = GameState.Map.getSecond();
        ArrayList<Direction> directionTo            = new ArrayList<>();  // Use Direction enum
    
        // Check Attacking Surround
        if (
            (isValidRange(positionX, positionY - 1) && tempChar.get(positionX).get(positionY - 1) == 'R')
            || (isValidRange(positionX + 1, positionY) && tempChar.get(positionX + 1).get(positionY) == 'R')
            || (isValidRange(positionX, positionY + 1) && tempChar.get(positionX).get(positionY + 1) == 'R')
            || (isValidRange(positionX - 1, positionY) && tempChar.get(positionX - 1).get(positionY) == 'R')
        ) {
            p1SurroundedFlag = true;
        }
    
        if (
            (isValidRange(positionX, positionY - 1) && tempChar.get(positionX).get(positionY - 1) == 'B')
            || (isValidRange(positionX + 1, positionY) && tempChar.get(positionX + 1).get(positionY) == 'B')
            || (isValidRange(positionX, positionY + 1) && tempChar.get(positionX).get(positionY + 1) == 'B')
            || (isValidRange(positionX - 1, positionY) && tempChar.get(positionX - 1).get(positionY) == 'B')
        ) {
            p2SurroundedFlag = true;
        }
    
        // Considered Least Player HP
        if (p1SurroundedFlag && p2SurroundedFlag) {
            if (P1.getSelf().getHealthPoint() > P2.getSelf().getHealthPoint()) {
                p2SurroundedFlag = false;
            } else {
                p1SurroundedFlag = false;
            }
        }
        
        // Player Position nearby do ATTACK!!
        if (p1SurroundedFlag) {
            if (s.getSelf() instanceof Slime slime) {
                slime.attacking(P1.getSelf());
                return;
            }
        } else if (p2SurroundedFlag) {
            if (s.getSelf() instanceof Slime slime) {
                slime.attacking(P2.getSelf());
                return;
            }
        }
    
        // Straight to Position (don't care about walls)
        if (findDistance(positionX, positionXR, positionY, positionYR) < findDistance(positionX, positionXB, positionY, positionYB)) {
            nearestPositionX = positionXR;
            nearestPositionY = positionYR;
            // System.out.println("case1: Moving towards 'R'");
        } else {
            nearestPositionX = positionXB;
            nearestPositionY = positionYB;
            // System.out.println("case2: Moving towards 'B'");
        }
    
        // Find possible directions
        if (positionY > nearestPositionY && isValidMove(positionX, positionY-1)) { // Move Up
            directionTo.add(Direction.UP);
        }
        if (positionX < nearestPositionX && isValidMove(positionX+1, positionY)) { // Move Right
            directionTo.add(Direction.RIGHT);
        }
        if (positionY < nearestPositionY && isValidMove(positionX, positionY+1)) { // Move Down
            directionTo.add(Direction.DOWN);
        }
        if (positionX > nearestPositionX && isValidMove(positionX-1, positionY)) { // Move Left
            directionTo.add(Direction.LEFT);
        }
        
        // Perform movement based on direction
        for (Direction direction : directionTo) {
            switch (direction) {
                case UP -> {
                    if (isSafeMove(positionX, positionY - 1)) {
                        doWalk(s, Direction.UP, null, null);
                    }
                    movedFlag = true;
                }
                case RIGHT -> {
                    if (isSafeMove(positionX + 1, positionY)) {
                        doWalk(s, Direction.RIGHT, null, null);
                    }
                    movedFlag = true;
                }
                case DOWN -> {
                    if (isSafeMove(positionX, positionY + 1)) {
                        doWalk(s, Direction.DOWN, null, null);
                    }
                    movedFlag = true;
                }
                case LEFT -> {
                    if (isSafeMove(positionX - 1, positionY)) {
                        doWalk(s, Direction.LEFT, null, null);
                    }
                    movedFlag = true;
                }
            }
        }
    
        // Alternative path if no move was performed
        if (!movedFlag) {
            if (isSafeMove(positionX, positionY - 1)) {
                doWalk(s, Direction.UP, null, null);
            } else if (isSafeMove(positionX + 1, positionY)) {
                doWalk(s, Direction.RIGHT, null, null);
            } else if (isSafeMove(positionX, positionY + 1)) {
                doWalk(s, Direction.DOWN, null, null);
            } else if (isSafeMove(positionX - 1, positionY)) {
                doWalk(s, Direction.LEFT, null, null);
            }
        }
    }
    
    // Normal Pathfinding by BFS
    private static void pathfinding(
        Boolean[] endFlag, int currX, int currY, int endX, int endY, 
        ArrayList<ArrayList<Boolean>> visitedFlag, Mapping<int[], int[]> direction) {

        /*
         *      For generating the map by using BFS
         */
           // Outbound
           if (currX < 0 || currX >= Setting.GRIDSIZE || currY < 0 || currY >= Setting.GRIDSIZE ||
               visitedFlag.get(currX).get(currY) || endFlag[0]) {
               return;
           }
   
           // Wall
           if (GameState.Map.getFirst().get(currX).get(currY) == 1) {
               return;
           }
   
           if (currX == endX && currY == endY) {
               endFlag[0] = true;
               return;
           }
   
           visitedFlag.get(currX).set(currY, true);
   
           // Direction recursion
           for (int i = 0; i<4; i++) {
               int positionX = currX + direction.getFirst()[i];
               int positionY = currY + direction.getSecond()[i];
               pathfinding(endFlag, positionX, positionY, endX, endY, visitedFlag, direction);
           }
   
           visitedFlag.get(currX).set(currY, false);
       }
   
    // Pathfinding for nearest player position
    public static boolean pathfinderChecker(Entities a, Entities b) {
        /*
        *  This medthod using for checking by tracing the Path
        *  Is there possible ways between Entity A and Entity B
        *  are able to go between return true flag when is possible
        *  otherwise false when not found any path for LOOPING
        */
        int currentX = a.getXPosition();
        int currentY = a.getYPosition();
        int endX     = b.getXPosition();
        int endY     = b.getYPosition();

        // Set Node to false
        ArrayList<ArrayList<Boolean>> visitNode = new ArrayList<>();

        for (int i = 0; i < Setting.GRIDSIZE; i++) {
            ArrayList<Boolean> temp = new ArrayList<>();
            for (int j = 0; j < Setting.GRIDSIZE; j++) {
                temp.add(false);
            }
            visitNode.add(temp);
        }
        
        // For Referencing
        Boolean[] returnFlag = {false}; // initialize

        Mapping<int[], int[]> direction = new Mapping<>();
        int[] rowDirection = {  0,  1,  0, -1 }; // x
        int[] colDirection = { -1,  0,  1,  0 }; // y
        direction.set(rowDirection, colDirection);
        
        pathfinding(returnFlag, currentX, currentY, endX, endY, visitNode, direction);
        return returnFlag[0];
    }

    // Remove list of Sprites with out of HP
    public static void clearSprites ( ArrayList<Sprite<GameState>> sprites ) {
        // Checking Entities HP
        for (int i = sprites.size() - 1; i >= 0; i--) {
            Sprite<GameState> a = sprites.get(i);
            Entities temp = a.getSelf();
            if (!temp.isAlive()) {
                GameState.Map.getSecond().get(temp.getXPosition()).set(temp.getYPosition(), 'E'); // Clear that position
                sprites.remove(i);
            }
        }
    }
}