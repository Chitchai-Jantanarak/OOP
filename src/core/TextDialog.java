package core;


import java.awt.Font;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

class TextDialog extends JLabel {
    
    public TextDialog(Font font, String text) {

        setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Set Margin
        
        setText(text);
        setFont(font);
        setHorizontalAlignment(JLabel.CENTER);
        setForeground(Color.WHITE);
    }

    public TextDialog(Font font, String text, Color color){
        setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Set Margin
        
        setText(text);
        setFont(font);
        setHorizontalAlignment(JLabel.CENTER);
        setForeground(color);
    }

    public TextDialog(Font font, String text, Color backgroundColor, Color foregroundColor){
        setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Set Margin
        
        setText(text);
        setFont(font);
        setHorizontalAlignment(JLabel.CENTER);
        setForeground(foregroundColor);
        setBackground(backgroundColor);
    }
}
