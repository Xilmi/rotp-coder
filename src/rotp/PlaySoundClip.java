package rotp;

import rotp.util.sound.OggClip;
import rotp.util.sound.WavClip;

/**
 * Executable file to test sound playback
 */
public class PlaySoundClip {

    public static void main(String avg[]) throws Exception {
        if (1 == 0) {
            WavClip.play("/rotp/data/sounds/combat_open.wav", 100, 100);
        }
        OggClip.play("/rotp/data/sounds/combat_open.ogg", 100, 100);
        Thread.sleep(3000);
    }

}