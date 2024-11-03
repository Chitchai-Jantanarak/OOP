package core;

import javax.swing.*;
import java.awt.*;

// HPBar class to represent the health bar of a character
class HPBar extends JPanel {
    private int currentHP;
    private int maxHP;

    private Fonts fonts24 = new Fonts(24);
    private Font default24 = fonts24.getFontAt(0);

    private Color mainBarColor;

    public HPBar(int maxHP, Color mainColor) {
        this.maxHP        = maxHP;
        this.currentHP    = maxHP;
        this.mainBarColor = mainColor;
        setPreferredSize(new Dimension(((int)(Setting.WINDOWS_WIDTH / 3)), 0)); // Set preferred size
        setOpaque(false);
    }

    public void setCurrentHP(int currentHP) {
        this.currentHP = currentHP;
        repaint(); // Repaint 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Enable anti-aliasing
    
        // Calculate health bar
        int barWidth = (int) ((double) currentHP / maxHP * getWidth());
    
        int arcWidth = 15;
        int arcHeight = 15;
    
        // Draw foreground
        g2d.setColor(this.mainBarColor);
        g2d.fillRoundRect(0, 0, barWidth, getHeight(), arcWidth, arcHeight);
    
        // Draw HP text
        g2d.setColor(Color.WHITE);
        g2d.setFont(default24);

        String hpText = currentHP + " / " + maxHP;
        FontMetrics fm = g2d.getFontMetrics();

        int textWidth = fm.stringWidth(hpText);
        int textX = (getWidth() - textWidth) / 2;
        int textY = (getHeight() + fm.getAscent()) / 2; // Center vertically
        
        g2d.drawString(hpText, textX, textY);
    }
    
}
