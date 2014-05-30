import org.bytedeco.javacv.CanvasFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.DateFormatter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.Buffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Linus Karsai (312070209) on 27/05/2014.
 */
public class PhotoBooth {
    // Image objects
    private ImageGrabber imageGrabber;
    private JFrame canvas = new CanvasFrame("Photo Booth");
    private JLabel frameRateTextArea = new JLabel();
    private JLabel helpTextArea = new JLabel();
    private JLabel background = new JLabel();
    private JLabel recordingLabel = new JLabel();

    private int frameRate = 0;
    private java.util.Timer frameRateTimer = new java.util.Timer();

    private float contrastFloat = 1.5f;
    private float brightnessFloat = 20;

    private Histogram histogram = new Histogram();
    private RecordVideo recorder;
    private ArrayList<Filter> filters = new ArrayList<Filter>();

    private boolean frameRateBool = false;
    private boolean toggleHelpBool = false;
    private boolean recording = false;

    public PhotoBooth() {
        // Set framerate timer to run every second
        frameRateTimer.schedule(new printFrameRate(), 0, 1000);
        // Create image grabber
        imageGrabber = new ImageGrabber(this);

        // set canvas options
        canvas.setLayout(null);
        canvas.add(background);
        canvas.setSize(640, 480);
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        background.setBounds(0, 0, 640, 480);
        background.setLayout(null);
        background.add(frameRateTextArea);
        background.add(helpTextArea);
        background.add(recordingLabel);

        // add keylistener
        canvas.addKeyListener(new keyManager());

        // Show help overlay
        toggleHelp();

        // run grabber
        imageGrabber.run();
    }

    /**
     * Get's called by image grabber when new image is ready
     * @param img the image from imageGrabber
     */
    public void imageGrabberHook(BufferedImage img) {
        //apply contrast/brightness
        img = ImageUtilities.contrast(img, contrastFloat, brightnessFloat);

        // Convert filterList into array, and apply each to image
        Filter filterArray[] = new Filter[filters.size()];
        filters.toArray(filterArray);
        for (int i = 0; i < filterArray.length; ++i) {
            img = filterArray[i].apply(img, null);
        }
        background.setIcon(new ImageIcon(img));

        // Other hooks
        histogram.drawHistogram(img);
        recordingHook(img);

        ++frameRate;
    }

    /**
     * Runs recording-related methods when recording=true
     * @param img the latest image
     */
    public void recordingHook(BufferedImage img) {
        if(recording) {
            recorder.addFrame(img);
            recordingLabel.setText("recording...");
            recordingLabel.setBounds(
                    (int)(canvas.getWidth()- 20 - recordingLabel.getPreferredSize().getWidth()),
                    (int)(canvas.getHeight() - 40 - recordingLabel.getPreferredSize().getHeight()),
                    (int)(recordingLabel.getPreferredSize().getWidth()),
                    (int)(recordingLabel.getPreferredSize().getHeight())
            );
        } else {
            recordingLabel.setText("");
        }
    }

    /**
     * Publicly apply a filter to the photo booth
     * @param f
     */
    public void addFilter(Filter f) {
        filters.add(f);
    }

    /**
     * Toggle whether the help screen is showing or not
     */
    private void toggleHelp(){
        if (toggleHelpBool) {
            helpTextArea.setText("");
            toggleHelpBool = false;
        } else {
            StringBuffer stringBuffer = new StringBuffer();
            BufferedReader br = null;
            try {
                String currentLine;

                br = new BufferedReader(
                        new InputStreamReader(this.getClass().getResourceAsStream("help.html")));
                while ((currentLine = br.readLine()) != null) {
                    stringBuffer.append(currentLine);
                }
            } catch (IOException e) {
                System.out.println("Couldn't find helpfile");
            }
            helpTextArea.setText(stringBuffer.toString());
            helpTextArea.setBounds(20, 0, (int) helpTextArea.getPreferredSize().getWidth(), (int) helpTextArea.getPreferredSize().getHeight());
            toggleHelpBool = true;
        }
    }

    /**
     * Print the framerate on canvas according to frameRateBool
     */
    class printFrameRate extends java.util.TimerTask {
        @Override
        public void run() {
            if (frameRateBool) {
                frameRateTextArea.setText("<html>FrameRate:<br>" + Integer.toString(frameRate) + "</html>");
                frameRateTextArea.setBounds(20, 20, (int) frameRateTextArea.getPreferredSize().getWidth(), (int) frameRateTextArea.getPreferredSize().getHeight());
                frameRate = 0;
            } else {
                frameRateTextArea.setText("");
            }
        }
    }

    /**
     * Keyadapter responds to user input
     */
    class keyManager extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent event) {
            int keyCode = event.getKeyCode();
            if (keyCode == event.VK_UP) {
                contrastFloat += 0.05f;
            }
            if (keyCode == event.VK_DOWN) {
                contrastFloat -= 0.05f;
            }
            if (keyCode == event.VK_LEFT) {
                brightnessFloat -= 10;
            }
            if (keyCode == event.VK_RIGHT) {
                brightnessFloat += 10;
            }
            if (keyCode == event.VK_H) {
                if (histogram.isVisible())
                    histogram.hide();
                else
                    histogram.show();
            }
            if (keyCode == event.VK_R) {
                resetFilters();
            }
            if (keyCode == event.VK_U) {
                if (filters.size() > 0)
                    filters.remove(filters.size()-1);
            }
            if (keyCode == event.VK_F1) {
                toggleHelp();
            }
            if (keyCode == event.VK_F5) {
                if (frameRateBool)
                    frameRateBool = false;
                else
                    frameRateBool = true;
            }
            if (keyCode == event.VK_SPACE) {
                saveImage();
            }
            if (keyCode == event.VK_ENTER) {
                if (!recording) {
                    System.out.println("Start recording");
                    // Set up for recording
                    recorder = new RecordVideo();
                    recorder.start();
                    recording = true;
                } else {
                    System.out.println("End recording");
                    recorder.stop();
                    recorder = null;
                    recording = false;
                }
            }
            if (keyCode == event.VK_1) {
                filters.add(new AndyWorhol());
            }
            if (keyCode == event.VK_2) {
                filters.add(new Sharpen());
            }
            if (keyCode == event.VK_3) {
                filters.add(new EdgeDetect());
            }
            if (keyCode == event.VK_4) {
                filters.add(new Emboss());
            }
            if (keyCode == event.VK_5) {
                filters.add(new Blur());
            }
            if (keyCode == event.VK_6) {
                filters.add(new Invert());
            }
            if (keyCode == event.VK_7) {
                filters.add(new BlackAndWhite());
            }
            if (keyCode == event.VK_8) {
                filters.add(new Speed());
            }

        }

    }

    /**
     * Save current canvas background to
     * ~/PhotoBooth/date_time.jpg
     */
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

    /**
     * Reset filters
     */
    private void resetFilters() {
        contrastFloat = 1.5f;
        brightnessFloat = 20;
        filters.clear();
    }

    public static void main(String args[]) {
        PhotoBooth app = new PhotoBooth();
    }
}
