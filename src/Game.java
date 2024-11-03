import core.*;
import javax.swing.*;


public class Game {

    public static void main(String[] args) {
        
        JFrame frame = new JFrame();
        InitialState initialState = new InitialState();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(Setting.WINDOWS_WIDTH, Setting.WINDOWS_HEIGHT);
        frame.setResizable(Setting.WINDOWS_RESIZABLE);
        frame.add(initialState);
        frame.setVisible(true);

    }
    
}
