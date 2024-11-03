package core;

import java.awt.*;

import javax.swing.*;

class SelectionState extends JPanel {
    private char P1char;
    private char P2char;
    private Boolean isP1Selected    = false;
    private Boolean isP2Selected    = false;
    // GameButton variable declaration for setting flag
    private GameButton p1Path;
    private GameButton p1Cast;
    private GameButton p2Path;
    private GameButton p2Cast;

    private Fonts fonts44             = new Fonts(44.0);
    private Font default44            = fonts44.getFontAt(0);

    public SelectionState() {
        this.setLayout(new BorderLayout(50, 0));
        JPanel gridPanel = new JPanel(new GridLayout(2, 2)); // 2 * 2 grid

        JLabel jlbSelection = new TextDialog(default44, "SELECT YOUR CHARACTER");

        p1Path = new GameButton(default44, "", new Pathfinder('R').getImages()[0], e -> {
            p1Path.setEnabled(false);
            p1Cast.setEnabled(false);
            P1char = 'P';
            isP1Selected = true;

            p1Path.repaint();
            isSelected();
        }).configureButton();

        p1Cast = new GameButton(default44, "", new Caster('R').getImages()[0], e -> {
            p1Path.setEnabled(false);
            p1Cast.setEnabled(false);
            P1char = 'C';
            isP1Selected = true;

            p1Cast.repaint();
            isSelected();
        }).configureButton();

        p2Path = new GameButton(default44, "", new Pathfinder('B').getImages()[0], e -> {
            p2Path.setEnabled(false);
            p2Cast.setEnabled(false);
            P2char = 'P';
            isP2Selected = true;

            p2Path.repaint();
            isSelected();
        }).configureButton();

        p2Cast = new GameButton(default44, "", new Caster('B').getImages()[0], e -> {
            p2Path.setEnabled(false);
            p2Cast.setEnabled(false);
            P2char = 'C';
            isP2Selected = true;

            p2Cast.repaint();
            isSelected();
        }).configureButton();

        gridPanel.setOpaque(false);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, Setting.WINDOWS_WIDTH / 8, 20, Setting.WINDOWS_WIDTH / 8));

        gridPanel.add(p1Path); // 0, 0
        gridPanel.add(p2Path); // 0, 1
        gridPanel.add(p1Cast); // 1, 0
        gridPanel.add(p2Cast); // 1, 1

        add(jlbSelection, BorderLayout.NORTH);
        add(gridPanel, BorderLayout.CENTER);

        setBackground(new Color(33, 26, 40));
        
        setFocusable(true);
        setVisible(true);
    }

    private void isSelected() {
        if (!isP1Selected || !isP2Selected) {
            // System.out.println("MUST SELECTED ALL");
            return;
        }
        startGame();
    }

    private Mapping<Entities, Entities> characterSetter() {
        Mapping<Entities, Entities> characterSelected;
        Entities P1 = null;
        Entities P2 = null;

        if (P1char == 'P') {
            P1 = new Pathfinder('R');
        }
        else if (P1char == 'C') {
            P1 = new Caster('R');
        }
        else { System.out.println( "Character selection failed" );}

        if (P2char == 'P') {
            P2 = new Pathfinder('B');
        }
        else if (P2char == 'C') {
            P2 = new Caster('B');
        }
        else { System.out.println( "Character selection failed" );}

        characterSelected = new Mapping<>(P1, P2);
        return characterSelected;
    }

    private void startGame() {
        
        // Replace a new frame by get this size from ancestor
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.remove(this);

        GameState gameState = new GameState(characterSetter());
        frame.add(gameState);
        frame.revalidate();
        frame.repaint();

        gameState.requestFocusInWindow();
    }
}