package core;

import javax.swing.ImageIcon;

class ButtonImage extends ImageIcon {
    
    private static final String IMAGE_PATH = "/resources/img/btnIcon/";

    public ButtonImage(String keyword) {

        super(ButtonImage.class.getResource(getFullPath(keyword)));

    }

    private static String getFullPath(String keyword) {

        return switch (keyword) {
            case "ATTACK"  -> IMAGE_PATH + "ATTACK.png";
            case "SHOOT"   -> IMAGE_PATH + "SHOOT.png";
            case "TRAP"    -> IMAGE_PATH + "TRAP.png";
            case "FILL"    -> IMAGE_PATH + "FILL.png";
            case "ENDTURN" -> IMAGE_PATH + "ENDTURN.png";
            default -> null;
        };
    }
}
