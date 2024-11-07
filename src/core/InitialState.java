package core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.*;


import javax.swing.*;



public class InitialState extends JPanel implements MouseListener, KeyListener {

    private final String IMAGEPATH = "/resources/img/background/title.png";
    private Image titleImage;

    private Fonts fonts108             = new Fonts(108.0);
    private Font titleInline108        = fonts108.getFontAt(1);
    private Font titleOutline108       = fonts108.getFontAt(2);
    
    private Fonts fonts72              = new Fonts(72.0);
    private Font titleInline72         = fonts72.getFontAt(1);
    private Font titleOutline72        = fonts72.getFontAt(2);

    private Fonts fonts44              = new Fonts(44.0);
    private Font default44             = fonts44.getFontAt(0);
    private Font arrow44               = fonts44.getFontAt(4);

    private Fonts fonts32              = new Fonts(32.0);
    private Font default32             = fonts32.getFontAt(0);
    private Font arrow32               = fonts32.getFontAt(4);

    private Timer blinker;
    private Boolean blinkFlag          = true;


    public InitialState() {

        titleImage = new ImageIcon(getClass().getResource(IMAGEPATH)).getImage();
        
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(Setting.WINDOWS_HEIGHT / 8, 0, Setting.WINDOWS_HEIGHT / 8, 0));
        JPanel jpnOverlay       = new JPanel();
        JPanel jpnNested1       = new JPanel(new BorderLayout());
        JPanel jpnNested2       = new JPanel(new BorderLayout());
        JPanel jpnFlowLayout    = new JPanel(new FlowLayout());
        jpnOverlay.setLayout(new OverlayLayout(jpnOverlay));

        jpnOverlay.setOpaque(false);
        jpnNested1.setOpaque(false);
        jpnNested2.setOpaque(false);
        jpnFlowLayout.setOpaque(false);
        

        // Label setter
            JLabel jlbTitle1 = new TextDialog(Setting.WINDOWS_WIDTH > 1200 ? titleInline108 : titleInline72, "G R I D   W I T H   P A L S", Color.LIGHT_GRAY);
            JLabel jlbTitle2 = new TextDialog(Setting.WINDOWS_WIDTH > 1200 ? titleOutline108 : titleOutline72, "G R I D   W I T H   P A L S", Color.BLACK);
            JLabel jlbArrow  = new TextDialog(Setting.WINDOWS_WIDTH > 1200 ? arrow44 : arrow32, "5 ", Color.GRAY);
            JLabel jlbStart  = new TextDialog(Setting.WINDOWS_WIDTH > 1200 ? default44 : default32, "PRESS START", Color.GRAY);
        
        // Nested zone operation
            jpnFlowLayout.add(jlbArrow);
            jpnFlowLayout.add(jlbStart);

            jpnNested1.add(jlbTitle2, BorderLayout.NORTH);
            jpnNested1.add(jpnFlowLayout, BorderLayout.SOUTH);

            jpnNested2.add(jlbTitle1, BorderLayout.NORTH);

            jpnOverlay.add(jpnNested1);
            jpnOverlay.add(jpnNested2);
            add(jpnOverlay, BorderLayout.CENTER);

            
            
        
        blinker = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                blinkFlag = !blinkFlag; // Toggle the flag
                if (blinkFlag) {
                    jlbArrow.setText(" ");
                    jlbStart.setText(" ");
                }
                else {
                    jlbArrow.setText("5 ");
                    jlbStart.setText("PRESS START");
                }
            }
        });
        
        startBlinking();

        setBackground(new Color(33, 26, 40));
        setFocusable(true);
        
        requestFocusInWindow();
        addMouseListener(this);
        addKeyListener(this);
    }

    public void startBlinking() {
        blinker.start();
    }

    public void stopBlinking() {
        blinker.stop();
    }

    @Override
    protected void paintComponent (Graphics g){
        super.paintComponent(g);
        int targetWidth = getWidth();
        int targetHeight = (int) (targetWidth * 9.0 / 16.0);

        // Swapper
        if (targetHeight > getHeight()) {
            targetHeight = getHeight();
            targetWidth = (int) (targetHeight * 16.0 / 9.0);
        }

        // Align Center
        int x = (getWidth() - targetWidth) / 2;
        int y = (getHeight() - targetHeight) / 2;

        g.drawImage(titleImage, x, y, targetWidth, targetHeight, this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        sendState();
    } 

    @Override
    public void mousePressed(MouseEvent e) {}  // not implemented

    @Override
    public void mouseReleased(MouseEvent e) {} // not implemented

    @Override
    public void mouseEntered(MouseEvent e) {}  // not implemented

    @Override
    public void mouseExited(MouseEvent e) {}   // not implemented

    @Override
    public void keyTyped(KeyEvent e) {}        // not implemented

    @Override
    public void keyReleased(KeyEvent e) {}     // not implemented

    @Override
    public void keyPressed(KeyEvent e) {
        sendState();
    }

    private void sendState() {
        // Replace a new frame by get this size from ancestor
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        stopBlinking();
        frame.remove(this);

        DescribeState describeState = new DescribeState();
        frame.add(describeState);
        frame.revalidate();
        frame.repaint();

        describeState.requestFocusInWindow();
    }
}
