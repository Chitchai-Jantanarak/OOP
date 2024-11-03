package core;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

class Fonts {

    private static String FONT_DIRECTORY = "/resources/font/";
    private static String[] FONT_FILES_LOC = {
        "font1/BMarmy.ttf",             // Default Font
        "font2/8-bit Arcade In.ttf",    // Header In-line
        "font2/8-bit Arcade Out.ttf",   // Header Out-line
        "font3/pixeldu.ttf",           // Article normal Font
        "font4/PixArrows.ttf"           // Arrow Syntax
    };

    private Font[] fonts;
    private Font currentFont;

    public Fonts(double size) {
        fonts = new Font[FONT_FILES_LOC.length];
        try {
            for (int i = 0; i < FONT_FILES_LOC.length; i++) {
                String fontPath = FONT_DIRECTORY + FONT_FILES_LOC[i];
                 try (InputStream fontStream = getClass().getResourceAsStream(fontPath)) {
                    if (fontStream != null) {
                        fonts[i] = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont((float)size);
                    } else {
                        System.out.println("Font not found: " + fontPath);
                    }
                }
            }
            currentFont = fonts[0]; // Set the first font as the default

        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            currentFont = new Font("Serif", Font.PLAIN, (int) size);
        }
    }

    public Font getCurrentFont() {
        return currentFont;
    }

    public Font getFontAt(int index) {
        if (index < 0 || index >= fonts.length){
            System.out.println( "Font load failed" );
            return currentFont;
        }

        return fonts[index];
    }
}
