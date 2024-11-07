package core;

public class Setting {
    // Local setting
    /* resolution setting */
    // LOCK TO 1440 * 1050 ( May image position be outbounded )

    public static int WINDOWS_WIDTH                 = 1440;     // Testing at 1080 * 920
    public static int WINDOWS_HEIGHT                = 1050;
    
    /* in-game setting */
    public static final int HEALTHPOINT             = 10;
    public static final int TIMER                   = 20;
    public static final int GRIDSIZE                = 9; // Suggest 6 - 12 ** Cannot generate with 0-3 GRIDSIZE (Impossible generate random position)
                                                         // Outbound at 13 on func f(x) = 0.01xe^0.15 + 0.3                        
    // Logic Setting default    
    public static final double TILE_PADDING_RATIO   = 0.01 * GRIDSIZE * Math.exp(0.15) + 0.3;    // regression function [0.3, 0.5)
    public static final int TILE_WIDTH              = (int) (WINDOWS_WIDTH * (1 - 2 * TILE_PADDING_RATIO));    // Prefered to suitable size for every grid frame
    public static final int TILE_HEIGHT             = TILE_WIDTH / 2;
    public static final int TILE_STARTX             = WINDOWS_WIDTH / 2;   // Setting the grid has been start from middle (Align Center)
    public static final int TILE_STARTY             = WINDOWS_HEIGHT / 4;

    /* Pathfinder logic */
    public static final int PATHFINDER_ATTACKPOINT  = 2;
    public static final int PATHFINDER_MOVETURN     = 4;
    public static final int PATHFINDER_ATTACKTIMES  = 2;
    public static final int PATHFINDER_FILLTURN     = 2;

    /* Caster logic */
    public static final int CASTER_ATTACKPOINT      = 1;
    public static final int CASTER_MOVETURN         = 3;
    public static final int CASTER_ATTACKTIMES      = 1;
    public static final int CASTER_SKILLTURN        = 3;
    public static final int CASTER_TRAPTURN         = 2;

    /* Mob logic */
    public static final int MOB_ATTACKPOINT         = 1;
    public static final int MOB_SPAWNRATE           = 5;   // As 0.5 Indicator
    public static final int MOB_SPAWNBOUND          = 100; // As per spawn once
    public static final int MOB_HEALTHPOINT         = 1;
    public static final int MOBS_SKILLTURN          = 2;

    /* Abilities Range */
    public static final int ATTACK_RANGE            = 1;
    public static final int SKILL_RANGE             = 5;
    public static final int TRAP_RANGE              = 1;   
    public static final int FILL_RANGE              = 2; 

    /* All object damage */         
    public static final int SKILL_DAMAGE            = 3;
    public static final int TRAP_DAMAGE             = 2;
    public static final int POISON_DAMAGE           = 1;
    
    /* Hard logic EXIT LOOP */
    public static boolean RUNNABLE                  = true;

    // Hard Setting
    public static boolean WINDOWS_RESIZABLE         = false;
}
