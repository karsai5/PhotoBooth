import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by linuskarsai on 28/05/2014.
 */
public class Settings {
    private JFrame canvas = new JFrame("Settings");
    private PhotoBooth pb;

    public Settings(PhotoBooth pb) {
        this.pb = pb;
        canvas.setVisible(true);
        canvas.setSize(300, 400);
        canvas.setLayout(new FlowLayout());
        canvas.addKeyListener(pb.getKeyManager());

        JButton andyWarholButton = new JButton("Andy Warhol");
        andyWarholButton.addActionListener(new andyWarholListener());
        canvas.add(andyWarholButton);

        JButton sharpenButton = new JButton("Sharpen");
        sharpenButton.addActionListener(new sharpenListener());
        canvas.add(sharpenButton);
    }

    private class andyWarholListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            pb.addFilter(new AndyWorhol());
        }
    }

    private class sharpenListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            System.out.println("test");
            pb.addFilter(new Sharpen());
        }
    }
}
