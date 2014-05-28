import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import org.bytedeco.javacv.CanvasFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.DateFormatter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by linuskarsai on 27/05/2014.
 */
public class PhotoBooth {
    private ImageGrabber imageGrabber;
    private ImageUtilities imageUtilities;
    private JFrame canvas = new CanvasFrame("Web Cam");
    private int frameRate = 0;
    private java.util.Timer frameRateTimer = new java.util.Timer();
    private JLabel frameRateTextArea = new JLabel();
    private JLabel helpTextArea = new JLabel();
    private JLabel background = new JLabel();
    private float scaleFloat = 1.5f;
    private float brightnessFloat = 20;
    private Histogram histogram = new Histogram();
    private Settings settings;
    private ArrayList<Filter> filters = new ArrayList<Filter>();
    private boolean frameRateBool = false;
    private boolean toggleHelpBool = false;
    private RecordVideo recorder;
    private boolean recording = false;

    public PhotoBooth() {
        // Set framerate timer to run every second
        frameRateTimer.schedule(new printFrameRate(), 0, 1000);
        imageGrabber = new ImageGrabber(this);

        // set canvas options
        canvas.setLayout(null);
        canvas.add(background);
        canvas.setSize(640, 480);
        background.setBounds(0, 0, 640, 480);
        background.setLayout(null);
        background.add(frameRateTextArea);
        background.add(helpTextArea);

        // add keylistener
        canvas.addKeyListener(new keyManager());

        // show settings dialog
        // settings = new Settings(this);

        // Toggle help overlay
        toggleHelp();

        // run grabber
        imageGrabber.run();
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void imageGrabberHook(BufferedImage img) {
        //apply contrast/brightness
        img = imageUtilities.contrast(img, scaleFloat, brightnessFloat);

        // Convert filterList into array, and apply each to image
        Filter filterArray[] = new Filter[filters.size()];
        filters.toArray(filterArray);
        for (int i = 0; i < filterArray.length; ++i) {
            img = filterArray[i].apply(img, null);
        }
        background.setIcon(new ImageIcon(img));

        // Other hooks
        clearFrameRate();
        histogram.drawHistogram(img);
        if(recording)
            recorder.addFrame(img);

        ++frameRate;
    }

    public void clearFrameRate() {
        if (!frameRateBool && frameRateTextArea.getText()!= "") {
            frameRateTextArea.setText("");
        }
    }

    public void addFilter(Filter f) {
        filters.add(f);
    }

    public KeyListener getKeyManager() {
        return canvas.getKeyListeners()[0];
    }

    private void toggleHelp(){
        if (toggleHelpBool) {
            helpTextArea.setText("");
            toggleHelpBool = false;
        } else {
            StringBuffer stringBuffer = new StringBuffer();
            BufferedReader br = null;
            try {
                String currentLine;
                br = new BufferedReader(new FileReader("help.html"));
                while ((currentLine = br.readLine()) != null) {
                    stringBuffer.append(currentLine);
                }
            } catch (IOException e) {
                System.out.println("Couldn't find helpfile");
            }
            helpTextArea.setText(stringBuffer.toString());
            helpTextArea.setBounds(20, 20, (int) helpTextArea.getPreferredSize().getWidth(), (int) helpTextArea.getPreferredSize().getHeight());
            toggleHelpBool = true;
        }
    }

    class printFrameRate extends java.util.TimerTask {
        @Override
        public void run() {
            if (frameRateBool) {
                frameRateTextArea.setText("<html>FrameRate:<br>" + Integer.toString(frameRate) + "</html>");
                frameRateTextArea.setBounds(20, 20, (int) frameRateTextArea.getPreferredSize().getWidth(), (int) frameRateTextArea.getPreferredSize().getHeight());
                frameRate = 0;
            }
        }
    }

    class keyManager extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent event) {
            int keyCode = event.getKeyCode();
            if (keyCode == event.VK_UP) {
                scaleFloat += 0.05f;
            } else if (keyCode == event.VK_DOWN) {
                scaleFloat -= 0.05f;
            } else if (keyCode == event.VK_LEFT) {
                brightnessFloat -= 10;
            } else if (keyCode == event.VK_RIGHT) {
                brightnessFloat += 10;
            } else if (keyCode == event.VK_H) {
                if (histogram.isVisible())
                    histogram.hide();
                else
                    histogram.show();
            } else if (keyCode == event.VK_R) {
                scaleFloat = 1.5f;
                brightnessFloat = 20;
                filters.clear();
            } else if (keyCode == event.VK_1) {
                filters.add(new AndyWorhol());
            } else if (keyCode == event.VK_2) {
                filters.add(new Sharpen());
            } else if (keyCode == event.VK_U) {
                if (filters.size() > 0)
                    filters.remove(filters.size()-1);
            } else if (keyCode == event.VK_F5) {
                if (frameRateBool)
                    frameRateBool = false;
                else
                    frameRateBool = true;
            } else if (keyCode == event.VK_3) {
                filters.add(new EdgeDetect());
            } else if (keyCode == event.VK_4) {
                filters.add(new Emboss());
            } else if (keyCode == event.VK_5) {
                filters.add(new Blur());
            } else if (keyCode == event.VK_6) {
                filters.add(new Invert());
            }
            if (keyCode == event.VK_7) {
                filters.add(new BlackAndWhite());
            }
            if (keyCode == event.VK_F1) {
                toggleHelp();
            }
            if (keyCode == event.VK_8) {
                filters.add(new Speed());
            }
            if (keyCode == event.VK_SPACE) {
                saveImage();
            }
            if (keyCode == event.VK_ENTER) {
                if (!recording) {
                    System.out.println("Start recording");
                    // Set up for recording
                    recorder = new RecordVideo(
                            background.getWidth(),
                            background.getHeight()
                    );
                    recorder.start();
                    recording = true;
                } else {
                    System.out.println("End recording");
                    recorder.stop();
                    recorder = null;
                    recording = false;
                }
            }
        }
    }

    public void saveImage() {
        String home = System.getProperty("user.home");
        File dir = new File(home + "/PhotoBooth/");

        // Create photo directory if it doesn't exist
        if (!dir.exists()) {
            System.out.println("Creating photo directory");
            Boolean result = dir.mkdir();
            if(result)
                System.out.println("Directory created");
        }

        // Save icon to Graphic
        Icon icon = background.getIcon();
        BufferedImage bi = new BufferedImage(
                icon.getIconWidth(),
                icon.getIconHeight(),
                BufferedImage.TYPE_INT_RGB
        );
        Graphics g = bi.createGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.dispose();

        //get date for filename
        DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
        Date date = new Date();

        File outputfile = new File(dir + "/" + dateFormat.format(date) + ".jpg");
        try {
            ImageIO.write(bi, "jpg", outputfile);
        } catch (Exception e) {
            System.out.println("Couldn't save file!");
        }
    }

    public static void main(String args[]) {
        PhotoBooth app = new PhotoBooth();
    }
}
