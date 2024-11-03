package core;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


import javax.swing.*;

class DescribeState extends JPanel implements MouseListener, KeyListener {

    class ImagePanel extends JPanel {

        @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // 16 : 9 PNG
                
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

                if (images[currentImage] != null) {
                    g.drawImage(images[currentImage], x, y, targetWidth, targetHeight, this);
                }

            }
    }

    private static String IMAGE_PATH = "/resources/img/describe/";
    protected Image[] images;
    protected int currentImage;

    private ImagePanel imagePanel;

    private Fonts fonts44              = new Fonts(44.0);
    private Font default44             = fonts44.getFontAt(0);
    private Font arrow44               = fonts44.getFontAt(4);

    private Fonts fonts32              = new Fonts(32.0);
    private Font default32             = fonts32.getFontAt(0);
    private Font arrow32               = fonts32.getFontAt(4);

    private Timer blinker;
    private Boolean blinkFlag = true;

    public DescribeState() {

        // Initailize Dependent
            collectImage();
            setLayout(new BorderLayout());
            JPanel jpnFlow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            imagePanel   = new ImagePanel();

            jpnFlow.setOpaque(false);
            imagePanel.setOpaque(false);

            currentImage = 0;
    
        // Label Setter Zone
            JLabel jlbArrow  = new TextDialog(Setting.WINDOWS_WIDTH > 1200 ? arrow44 : arrow32, " [[ ", Color.WHITE);
            JLabel jlbNext   = new TextDialog(Setting.WINDOWS_WIDTH > 1200 ? default44 : default32, "NEXT", Color.WHITE);

        // Action Input
            blinker = new Timer(1000, e -> {
                blinkFlag = !blinkFlag; // Toggle the flag
                if (blinkFlag) {
                    jlbArrow.setText(" ");
                    jlbNext.setText(" ");
                }
                else {
                    jlbArrow.setText(" [[ ");
                    jlbNext.setText("NEXT");
                }
            });
            startBlinking();

        // Set Alignment & Adder
            jpnFlow.setAlignmentX(RIGHT_ALIGNMENT);
            jpnFlow.add(jlbNext);
            jpnFlow.add(jlbArrow);

            add(imagePanel, BorderLayout.CENTER);
            add(jpnFlow, BorderLayout.SOUTH);

        setBackground(new Color(34, 2, 50));
        setFocusable(true);

        requestFocusInWindow();
        addMouseListener(this);
        addKeyListener(this);
    }

    protected void collectImage(){
        images = new Image[3]; // Hard Code Size

        for (int i = 0; i < images.length; i++) {
            String imageName = "D" + i + ".png";
            String fullPath = IMAGE_PATH + imageName;
            
            try {
                images[i] = new ImageIcon(getClass().getResource(fullPath)).getImage();
                if (images[i] == null) System.out.println("Image get Failed"); 
            }
            catch (Exception e) {
                System.out.println("Image load Failed");
                e.printStackTrace();
            }
        }
    }

    public void startBlinking() {
        blinker.start();
    }

    public void stopBlinking() {
        blinker.stop();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (currentImage < images.length - 1){
            currentImage++;
            imagePanel.repaint();
        }
        else{
            sendState();
        }
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
        if (currentImage < images.length - 1){
            currentImage++;
            imagePanel.repaint();
        }
        else{
            sendState();
        }
    }

    private void sendState() {
        // Replace a new frame by get this size from ancestor
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.remove(this);
        stopBlinking();

        SelectionState selectionState = new SelectionState();
        frame.add(selectionState);
        frame.revalidate();
        frame.repaint();

        selectionState.requestFocusInWindow();
    }
}

