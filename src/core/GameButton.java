package core;

import java.awt.event.ActionListener;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Image;
import java.awt.AlphaComposite;

import javax.swing.*;


class GameButton extends JButton {

    private Image btnImage;

    public GameButton(Font font, String label, ActionListener actionListener) {
        super(label);
        setFont(font);

        addActionListener(actionListener);
        setFocusable(false);
    }

    public GameButton(Font font, String label, Image image, ActionListener actionListener) {
        super(label);
        this.btnImage = image;
        
        setFont(font);
        setForeground(Color.WHITE);
        configureButton();
        
        addActionListener(actionListener);
        setFocusable(false);
    }

    public GameButton(Font font, String label, ButtonImage image, ActionListener actionListener) {
        this(font, label, image.getImage(), actionListener);
    }

    public GameButton setEmpty() {
        setForeground(new Color(33, 26, 40));
        setBorder(null);
        return this;
    }

    public void setTextAlignButton(){
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.TOP);
    }

    public GameButton configureButton() {
        setRolloverEnabled(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setFocusable(false);

        return this;
    }


    /**
     *  @param btnImage for background implementing
     */
    @Override
    protected void paintComponent (Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // g2d.setColor(Color.GREEN);
        // g2d.fillRect(0, 0, getWidth(), getHeight());
        if (btnImage != null) {

            if (isEnabled()){
                g2d.drawImage(btnImage, 0 , 0, getWidth(), getHeight(), this);
            }
            else {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                g2d.drawImage(btnImage, 0 , 0, getWidth(), getHeight(), this);
            }
        }

        super.paintComponent(g); // Get first all button panel property

    }   
}

// setForeGround (Color..)