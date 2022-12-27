import javax.swing.*;
import java.awt.*;

public class MyPanel extends JPanel{
    public JFrame frame;

    public MyPanel(JFrame frame){
        this.frame = frame;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}