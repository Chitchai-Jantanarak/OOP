package core;

import java.awt.Image;

import javax.swing.ImageIcon;

class Tiles {
    
    private static String TILE_DIRECTORY = "/resources/img/grid/";
    private static String[] TILE_FILE_LOC = {
        "GN",   // Default Tile
        "GF",   // Trapped by Caster Tile
        "GP",   // Trapped by Mob Tile
    };

    public static Image getTile(int index) {
        String fullPath = TILE_DIRECTORY + TILE_FILE_LOC[index] + "0" + ".png"; // Set as default
        Image image = null;
        try {
                image = new ImageIcon(Tiles.class.getResource(fullPath)).getImage();
                if (image == null) System.out.println("Image get Failed"); 
            }
            catch (Exception e) {
                System.out.println("Image load Failed");
                e.printStackTrace();
        }

        return image;
    }

    public static Image getTile(int index, Theme theme) {
        String fullPath = TILE_DIRECTORY + TILE_FILE_LOC[index] + theme.getThemeID() + ".png";
        Image image = null;

        try {
                image = new ImageIcon(Tiles.class.getResource(fullPath)).getImage();
                if (image == null) System.out.println("Image get Failed"); 
            }
            catch (Exception e) {
                System.out.println("Image load Failed");
                e.printStackTrace();
        }

        return image;
    }
}
