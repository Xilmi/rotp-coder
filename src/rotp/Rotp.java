/*
 * Copyright 2015-2020 Ray Fowler
 * 
 * Licensed under the GNU General Public License, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     https://www.gnu.org/licenses/gpl-3.0.html
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rotp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import rotp.model.game.GameSession;
import rotp.ui.BasePanel;
import rotp.ui.RotPUI;
import rotp.ui.SwingExceptionHandler;
import rotp.ui.UserPreferences;
import rotp.util.FontManager;
import rotp.util.ImageManager;

public class Rotp {
    private static final int MB = 1048576;
    public static int IMG_W = 1229;
    public static int IMG_H = 768;
    public static String jarFileName = "rotp-"+RotpGovernor.governorVersion()+RotpGovernor.miniSuffix()+".jar";
    public static boolean countWords = false;
    private static String jarPath;
    private static JFrame frame;
    public static String releaseId = "0.91";
    public static long startMs = System.currentTimeMillis();
    public static long maxHeapMemory = Runtime.getRuntime().maxMemory() / 1048576;
    public static long maxUsedMemory;
    public static boolean logging = false;
    private static float resizeAmt =  -1.0f;
    public static int actualAlloc = -1;
    public static boolean reloadRecentSave = false;
    
    static GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    public static void main(String[] args) {
        frame = new JFrame("Remnants of the Precursors");
        if (args.length == 0) {
            if (restartWithMoreMemory(frame, false))
                return;
            logging = false;
        }
        reloadRecentSave = containsArg(args, "reload");
        logging = containsArg(args, "log");
        stopIfInsufficientMemory(frame, (int)maxHeapMemory);
        Thread.setDefaultUncaughtExceptionHandler(new SwingExceptionHandler());
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // note: referencing the RotPUI class executes its static block
        // which loads in sounds, images, etc
        frame.setLayout(new BorderLayout());
        frame.add(RotPUI.instance(), BorderLayout.CENTER);

        Image img = ImageManager.current().image("LANDSCAPE_RUINS_ORION");
        BufferedImage bimg = toBufferedImage(img);
        BufferedImage square = bimg.getSubimage(bimg.getWidth()-bimg.getHeight(), 0, bimg.getHeight(), bimg.getHeight());
        frame.setIconImage(square);

        if (UserPreferences.fullScreen()) {
            frame.setUndecorated(true);
            device.setFullScreenWindow(frame);
            resizeAmt();
        }
        else if (UserPreferences.borderless()) {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
            frame.setUndecorated(true);
            resizeAmt();
        }
        else {
            frame.setResizable(false);
            device.setFullScreenWindow(null);
            setFrameSize();
        }

        if (reloadRecentSave) 
            GameSession.instance().loadRecentSession(false);
        
        // this will not catch 32-bit JREs on all platforms, but better than nothing
        String bits = System.getProperty("sun.arch.data.model").trim();
        if (bits.equals("32"))
            RotPUI.instance().mainUI().showJava32BitPrompt();

        frame.setVisible(true);
    }
    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }
    public static boolean containsArg(String[] argList, String key) {
        for (String s: argList) {
            if (s.equalsIgnoreCase(key))
                return true;
        }
        return false;
    }
    public static void setFrameSize() {
        resizeAmt = -1;
        FontManager.current().resetFonts();
        double adj = resizeAmt();
        int vFrame = 0;
        int hFrame = 0;
        int maxX = (int)((hFrame+IMG_W)*adj);
        int maxY = (int)((vFrame+IMG_H)*adj);
        if (logging)
            System.out.println("setting size to: "+maxX+" x "+maxY);
        frame.getContentPane().setPreferredSize(new Dimension(maxX,maxY));
        frame.pack();
    }
    public static float resizeAmt() {
        int pct = UserPreferences.windowed() ? UserPreferences.screenSizePct() : 100;
        float sizeAdj = (float) pct / 100.0f;
        if (resizeAmt < 0) {
            Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
            int sizeW = (int) (sizeAdj*size.width);
            int sizeH = (int) (sizeAdj*size.height);
            int maxX = sizeH*8/5;
            int maxY = sizeW*5/8;
            if (maxY > sizeH)
                maxY = maxX*5/8;

            resizeAmt = (float) maxY/768;
            (new BasePanel()).loadScaledIntegers();
            if (logging)
                System.out.println("resize amt:"+resizeAmt);
        }
        return resizeAmt;
    }
    public static String jarPath()  {
        if (jarPath == null) {
            try {
                File jarFile = new File(Rotp.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
                jarPath = jarFile.getParentFile().getPath();
            } catch (URISyntaxException ex) {
                System.out.println("Unable to resolve jar path: "+ex.toString());
                jarPath = ".";
            }
        }
        return jarPath;
    }
    private static void stopIfInsufficientMemory(JFrame frame, int allocMb) {
        if (allocMb < 260) {
            JOptionPane.showMessageDialog(frame, "Error starting game: Not enough free memory to play");
            System.exit(0);
        }
    }
    public static boolean memoryLow() {
        // returns true if total memory allocated to the JVM is within 100 MB of maximum allowed
        long max = Runtime.getRuntime().maxMemory() / 1048576;
        long total = Runtime.getRuntime().totalMemory() / 1048576;
        long free = Runtime.getRuntime().freeMemory() / 1048576;
        return (max == total) && (free < 300);
    }
    public static void restart() {
        String execStr = actualAlloc < 0 ? "java -jar "+jarFileName : "java -Xmx"+actualAlloc+"m -jar "+jarFileName+" arg1";
        try {
            Runtime.getRuntime().exec(execStr);
            System.exit(0);
        } catch (IOException ex) {
            System.err.println("Error attempting restart: ");
            ex.printStackTrace();
        }            
    }
    public static void restartFromLowMemory() {
        restartWithMoreMemory(frame, true);
    }
    private static boolean restartWithMoreMemory(JFrame frame, boolean reload) {
        long memorySize = ((com.sun.management.OperatingSystemMXBean) ManagementFactory
                        .getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
        long freeMemory = ((com.sun.management.OperatingSystemMXBean) ManagementFactory
                        .getOperatingSystemMXBean()).getFreePhysicalMemorySize();
        int maxMb = (int) (memorySize / MB);
        long allocMb = Runtime.getRuntime().maxMemory() / MB;
        int freeMb = (int) (freeMemory / MB);
        String bits = System.getProperty("sun.arch.data.model");

        System.out.println("maxMB:"+maxMb+"  freeMB:"+freeMb+"  allocMb:"+allocMb+"   bits:"+bits);
        // if system has given us 2.5G+, then we're good
        if (!reload && (allocMb > 2560))
            return false;

        // desiredAlloc is 1G or 1/3rd of max memory, whichever is higher
        int desiredAlloc = Math.max(1024, (int)maxMb/3);
        // we'll alloc smallest of the desired Alloc or 75% of free memory (after 500mb overhead)
        actualAlloc = Math.min(desiredAlloc, (int)((freeMb+allocMb-500)*0.75));
        // if we're not a 64-bit JVM, limit reqested heap to 1600Mb
        if (!bits.equals("64"))
            actualAlloc = Math.min(actualAlloc, 1200);
        // if that amount is <500M, then show an error
        System.out.println("restarting with MB:"+actualAlloc);
        if (!reload && (actualAlloc < allocMb))
            return false;
        
        try {
            stopIfInsufficientMemory(frame, actualAlloc*9/10);
            String argString = reload ? " reload" : " arg1";
            String execStr  = "java -Xmx"+actualAlloc+"m -jar "+jarFileName+argString;
            System.out.println("Only "+(int) allocMb+"Mb memory allocated by OS. Restarting game with command: "+execStr);
            Runtime.getRuntime().exec(execStr);
            System.exit(0);
            return true;
        } catch (IOException ex) {
            System.err.println("Error attempting restart: ");
            ex.printStackTrace();
        }
        return false;
    }
}
