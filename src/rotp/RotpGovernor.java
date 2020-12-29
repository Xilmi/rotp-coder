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

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Check for Remnants.jar version before launching.
 * <p>
 * Remnants.jar is now added to classpath automatically via manifest. So just make sure when ROTP relaunches
 * to get more memory it uses the right jar file name.
 */
public class RotpGovernor {
    static String expectedROTPVersion = "Beta 2.05";

    private static String governorVersion = null;

    public static String governorVersion() {
        if (governorVersion == null) {
            try {
                governorVersion = readVersion();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return governorVersion;
    }

    // minified versions will use WebP images and Ogg sounds.
    private static Boolean mini = null;

    public static String miniSuffix() {
        if (mini == null) {
            try {
                mini = readMini();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        if (mini) {
            return "-mini";
        } else {
            return "";
        }
    }


    public static void main(String[] args) throws IOException {

        String jarFilename = "ROTP-" + governorVersion() + "-governor.jar";
        try {
            Class.forName("rotp.Rotp");
        } catch (ClassNotFoundException e) {
            String message = "Unable to find Remnants.jar\n" +
                    "Place " + jarFilename + " in the same directory as Remnants.jar and try again";

            System.out.println(message);
            JOptionPane.showMessageDialog(null, message, "Remnants.jar not found", JOptionPane.ERROR_MESSAGE);
            System.exit(2);
        }
        if (!expectedROTPVersion.equals(Rotp.releaseId)) {
            System.out.println("Version mismatch. Governor " + governorVersion() +
                    " expects ROTP " + expectedROTPVersion +
                    " but actual is " + Rotp.releaseId);
            Object result = JOptionPane.showInputDialog(null,
                    "Governor and ROTP veresions don't match\n" +
                            "Please upgrade either Governor or ROTP\n" +
                            "Link to latest governor release below\n" +
                            "Continue with incorrect version?",
                    "Version mismatch", JOptionPane.WARNING_MESSAGE, null,
                    null,
                    "https://github.com/coder111111/rotp-public-governor/releases"
            );
            if (result == null) {
                System.exit(1);
            }
        }
        Rotp.jarFileName = jarFilename;
        Rotp.main(args);
    }

    private static String readVersion() throws IOException {
        try (InputStream is = RotpGovernor.class.getResourceAsStream("/rotp-version.properties")) {
            if (is == null) {
                throw new FileNotFoundException("Unable to find rotp-version.properties");
            }
            Properties properties = new Properties();
            properties.load(is);
            return properties.getProperty("version");
        }
    }

    private static boolean readMini() throws IOException {
        try (InputStream is = RotpGovernor.class.getResourceAsStream("/rotp-mini.properties")) {
            if (is == null) {
                return false;
            }
            Properties properties = new Properties();
            properties.load(is);
            return "true".equalsIgnoreCase(properties.getProperty("mini"));
        }
    }
}
