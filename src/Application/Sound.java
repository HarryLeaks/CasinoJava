package Application;

import javafx.scene.media.AudioClip;

public class Sound
{
    private static AudioClip[] soundLib;
    private static String[] soundName;
    
    static {
        Sound.soundLib = new AudioClip[2];
        Sound.soundName = new String[2];
    }
    
    public static void addSound(String name, AudioClip clip, int pos) {
        Sound.soundName[pos] = name;
        Sound.soundLib[pos] = clip;
    }
    
    public static void playSound(String name) {
        for (int i = 0; i < Sound.soundName.length; ++i) {
            if (Sound.soundName[i].equals(name)) {
                Sound.soundLib[(int)(Math.random() * Sound.soundLib.length)].play();
            }
        }
    }
}
