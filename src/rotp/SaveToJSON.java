package rotp;

import rotp.model.game.GameSession;

import java.io.*;

public class SaveToJSON {
    static {
        // highlights problems
        System.setProperty("java.awt.headless", "true");
    }

    public static void main(String arg[]) throws IOException, ClassNotFoundException {
        if (arg.length != 2) {
            System.out.println("SaveToJSON input.rotp output.json");
            System.exit(2);
        }
        File inputFile = new File(arg[0]);

        RotpCommon.headlessInit();

        System.out.println("SUCCESS");

        InputStream file = new FileInputStream(inputFile);
        InputStream buffer = new BufferedInputStream(file);
        ObjectInput input = new ObjectInputStream(buffer);
        GameSession newSession = (GameSession) input.readObject();
        RotpJSON.setStaticField(GameSession.class, "instance", newSession);

        String json = RotpJSON.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(newSession);
        System.out.println("json=" + json);
    }
}
