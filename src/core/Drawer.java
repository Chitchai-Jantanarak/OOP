package core;

import java.util.ArrayList;

import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Composite;
import java.awt.AlphaComposite;


class Drawer {
    
    public static void drawTile(Graphics2D g2d, Image image, Image scaler, int x, int y) {
        if ( image == null || scaler == null ) {
            System.out.println( "Drawer get image failed" );
            return;
        }
        // Scaling Image to line : "Should be buffered"
        double scaleX = (double) Setting.TILE_WIDTH / scaler.getWidth(null);
        double scaleY = (double) Setting.TILE_HEIGHT / scaler.getHeight(null);
        double scale = Math.min(scaleX, scaleY);

        int scaledWidth = (int) (scaler.getWidth(null) * scale );
        int scaledHeight = (int) (scaler.getHeight(null) * scale );
        int imageDrawX = x - scaledWidth / 2;
        int imageDrawY = y - scaledHeight / 2;
        
        g2d.drawImage(image, imageDrawX, imageDrawY, scaledWidth, scaledHeight, null);
    }

    public static TileObjectHover drawCoverTile(Graphics2D g2d, Color color, Image scaler, int x, int y) {
        // Check if the image is null
        if (scaler == null) {
            System.out.println( "Drawer get image failed" );
            return null;
        }
    
        // Scaling Image to line : "Should be buffered"
        double scaleX = (double) Setting.TILE_WIDTH / scaler.getWidth(null);
        double scaleY = (double) Setting.TILE_HEIGHT / scaler.getHeight(null);
        double scale = Math.min(scaleX, scaleY);
    
        // Calculate scaled dimensions
        int scaledWidth = (int) (scaler.getWidth(null) * scale);
        int scaledHeight = (int) (scaler.getHeight(null) * scale);

        int[] xPoints = {
            x - scaledWidth / 2,                       
            x ,
            x + scaledWidth / 2,
            x 
        };
    
        int[] yPoints = {
            y - (scaledHeight / 8),
            y - (scaledHeight / 8) - scaledHeight / 4,
            y - (scaledHeight / 8),
            y - (scaledHeight / 8) + scaledHeight / 4
        };
    
        Composite originalComposite = g2d.getComposite();

        // Set the opacity
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

        g2d.setColor(color);
        g2d.fillPolygon(xPoints, yPoints, 4);
        g2d.drawPolygon(xPoints, yPoints, 4); 

        
        // Restore the original composite
        g2d.setComposite(originalComposite);

        return new TileObjectHover(xPoints, yPoints);
    }

    public static void drawEntities(Graphics2D g2d, Image image, Image scaler, double x, double y) {
        if ( image == null || scaler == null ) {
            System.out.println( "Drawer get image failed" );
            return;
        }

        int drawSpriteX = (int) (Setting.TILE_STARTX + (x - y) * Setting.TILE_HEIGHT / 2);
        int drawSpriteY = (int) (Setting.TILE_STARTY + (x + y) * Setting.TILE_HEIGHT / 4);

        // Convert sprite position to isometric coordinates
        double scaleX = (double) Setting.TILE_WIDTH / scaler.getWidth(null) / 1.25;
        double scaleY = (double) Setting.TILE_HEIGHT / scaler.getHeight(null) / 1.25;
        double scale = Math.min(scaleX, scaleY);
        
        int scaledWidth = (int) (image.getWidth(null) * scale);
        int scaledHeight = (int) (image.getHeight(null) * scale);
    
        // Draw the scaled sprite at the calculated position
        // System.out.println(drawSpriteX - scaledWidth / 2 + " " + (drawSpriteY - scaledHeight));
        g2d.drawImage(image, drawSpriteX - scaledWidth / 2, drawSpriteY - scaledHeight , scaledWidth, scaledHeight, null);
    }

    public static void drawEntities(Graphics2D g2d, Image image, int x, int y, int width, int height) {
        if (image == null) {
            System.out.println( "Drawer get image failed" );
            return;
        }
        g2d.drawImage(image, x, y, width, height, null);
    }

    public static void drawAction(Graphics2D g2d, Image image, int x, int y) {
        Image scaler = Tiles.getTile(0);
        
        if ( image == null || scaler == null ) {
            System.out.println( "Drawer get image failed" );
            return;
        }

        int drawSpriteX = Setting.TILE_STARTX + (x - y) * Setting.TILE_HEIGHT / 2;
        int drawSpriteY = Setting.TILE_STARTY + (x + y) * Setting.TILE_HEIGHT / 4;

        // Convert sprite position to isometric coordinates
        double scaleX = (double) Setting.TILE_WIDTH / scaler.getWidth(null) * 1.25;
        double scaleY = (double) Setting.TILE_HEIGHT / scaler.getHeight(null) * 1.25;
        double scale = Math.min(scaleX, scaleY);
        
        int scaledWidth = (int) (image.getWidth(null) * scale);
        int scaledHeight = (int) (image.getHeight(null) * scale);
        
        // Draw the scaled sprite at the calculated position
        g2d.drawImage(image, drawSpriteX - scaledWidth / 2, drawSpriteY - scaledHeight, scaledWidth, scaledHeight, null);
        // g2d.drawImage(image, 0, 0, 500, 500, null);
    }

    // Using for GameState
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void sortingEntitiesLayers(ArrayList<Sprite<GameState>> sprites) {
        for (int i = 0; i < sprites.size(); i++) {
            for (int j = 0; j < sprites.size(); j++) {
                Entities Tempa = sprites.get(i).getSelf();
                Entities Tempb = sprites.get(j).getSelf();
    
                if (Tempa != null && Tempb != null) {
                    if (Tempa.getXPosition() < Tempb.getXPosition() &&
                        Tempa.getYPosition() < Tempb.getYPosition()) {
                        Sprite temp = sprites.get(i);
                        sprites.set(i, sprites.get(j));
                        sprites.set(j, temp);
                    }
                }
            }
        }
    }
}
