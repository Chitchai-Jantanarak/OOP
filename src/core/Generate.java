package core;

import java.util.Random;
import java.util.ArrayList;

import java.lang.Character;

// Arraylist Mapping Concept
/*         GRID POSITION              ENTITY POSITION 
 *         0 1 0 1 0 1 0               P E R E E T E              
 *         1 1 0 0 0 0 1               E T E E M E E
 *         0 0 1 0 0 1 0               E T E P E T E
 *         0 1 0 1 0 0 0               E M E P E B E
 *         0 0 0 0 0 0 0               E E E M E E E
 *         1:Wall 0:Blank                 E:Empty
 * 
 *      ENTITY POSITION EXPLANATION
 *  - R Red color position (P1)
 *  - B Blue color position (P2)
 *  - T Trap grid area position (Players generater)
 *  - P Poison grid area position (Mobs generater)
 *  - E Empty grid
*/         

class Generate {

    public static ArrayList<ArrayList<Integer>> generateGrid (int size) {
        ArrayList<ArrayList<Integer>> grid = new ArrayList<> (size);
        Random rand = new Random();

        for (int i = 0; i < size; i++){
            ArrayList<Integer> row = new ArrayList<> (size);
            for (int j = 0; j < size; j++){
                //[0,1]
                int num = rand.nextInt(0, 10);

                if (num >= 7) {
                    row.add(1);
                }
                else row.add(0);
            }
            grid.add(row);
        }

        return grid;
    }

    public static ArrayList<ArrayList<Character>> generatePosition (int size) {
        ArrayList<ArrayList<Character>> grid = new ArrayList<> (size);

        for (int i = 0; i < size; i++){
            ArrayList<Character> row = new ArrayList<> (size);
            for (int j = 0; j < size; j++){
                row.add('E');
            }
            grid.add(row);
        }

        return grid;
    }

    public static ArrayList<ArrayList<Character>> testGeneratekey (int size) {
        ArrayList<ArrayList<Character>> grid = new ArrayList<> (size);
        Random a = new Random();
        for (int i = 0; i < size; i++){ 
            ArrayList<Character> row = new ArrayList<> (size);
            for (int j = 0; j < size; j++){
                int rand = a.nextInt(0, 100);
                
                if (rand >= 90) {
                    rand = 2;
                }
                else if (rand >= 70) {
                    rand = 1;
                }
                else {
                    rand = 0;
                }

                switch (rand) {
                    case 0:
                        row.add('E');
                        break;
                    
                    case 1:
                        row.add('T');
                        break;

                    case 2:
                        row.add('P');
                        break;

                    default:
                        row.add('|');
                        break;
                }
            }
            grid.add(row);
        }

        return grid;
    }

    // Hard Type collection template 
    // Using tester now
    public static Mapping<ArrayList<ArrayList<Integer>>, ArrayList<ArrayList<Character>>> generateMap () { 
        
        ArrayList<ArrayList<Integer>> randomGrid  = generateGrid(Setting.GRIDSIZE);
        ArrayList<ArrayList<Character>> blankGrid = generatePosition(Setting.GRIDSIZE);
        return new Mapping<>(randomGrid, blankGrid); 
    }

    public static int randomInt () {
        return new Random().nextInt();
    }

    public static int randomIntWithIndicator (int ind) {
        return new Random().nextInt(0, ind+1);
    }

    private static Mapping<Integer,Integer> randomPositionWithMap (){
        Random rand  = new Random();
        Mapping<Integer, Integer> map  = new Mapping<>();

        do {
            Integer positionX = rand.nextInt(0, Setting.GRIDSIZE);
            Integer positionY = rand.nextInt(0, Setting.GRIDSIZE);
            map.set(positionX, positionY);
        } while (GameState.Map.getFirst().get(map.getFirst()).get(map.getSecond()) == 1 ||
         GameState.Map.getSecond().get(map.getFirst()).get(map.getSecond()) != 'E'); // regenerate

        return map;
    }

    public static void randomCharacterPosition (Entities e) {
        Mapping<Integer, Integer> position = randomPositionWithMap();

        //position
        e.setPosition(position.getFirst(), position.getSecond());
        e.setVisualPosition();
        GameState.Map.getSecond().get(position.getFirst()).set(position.getSecond(), e.getCharRepresented());
    }

    public static int randomIntWithSettingGridBound () {
        return new Random().nextInt(0,Setting.GRIDSIZE);
    }

    public static void printGridGenerated () {
        for (ArrayList<Integer> a : GameState.Map.getFirst()){
            for (int num : a){
                System.out.print(num + " ");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();
        for (ArrayList<Character> a : GameState.Map.getSecond()){
            for (char num : a){
                System.out.print(num + " ");
            }
            System.out.println();
        }
        System.out.println("----------------------");
    }

    public static Entities randomEntity () {
        Random rand = new Random();
        int num = rand.nextInt();
        
        Entities e;
        switch (num % 5) {
            case 0 -> e = new Pathfinder('R');
            case 1 -> e = new Pathfinder('B');
            case 2 -> e = new Caster('R');
            case 3 -> e = new Caster('B');
            case 4 -> e = new Slime();
            default -> e = new Pathfinder('R');
        }

        return e;
    }
}
