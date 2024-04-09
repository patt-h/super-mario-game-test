package Audio;

import java.net.URL;
import javafx.scene.media.AudioClip;

public class AudioPlayer {
    public static final int DEAD = 0;
    public static final int JUMP = 1;
    public static final int POWER_UP = 2;
    public static final int FIREBALL = 3;
    public static final int PIPE = 4;
    public static final int FINISH_LEVEL = 5;
    public static final int TIME_DECREASING = 6;
    public static final int GOOMBA_DEAD = 7;
    public static final int BREAK = 8;
    public static final int COIN = 9;
    public static final int BUMP = 10;
    public static final int APPEAR = 11;
    public static final int KICK = 12;

    private AudioClip[] effects;
    public static AudioClip timeDecreasing;
    private float volume = 0.03f;


    public AudioPlayer() {
        loadEffects();
    }

    private void loadEffects() {
        String[] effectNames = {
                "dead", "jump", "powerup", "shoot_fireball", "pipe", "finish_level", "time_decreasing",
                "goomba_dead", "break", "coin", "bump", "appear", "kick"
        };
        effects = new AudioClip[effectNames.length];
        for (int i = 0; i < effects.length; i++) {
            effects[i] = getAudioClip(effectNames[i]);
        }
    }

    public boolean isPlaying(int effect) {
        return effects[effect].isPlaying();
    }

    public void playEffect(int effect) {
        AudioClip clip = effects[effect];
        clip.setVolume(volume);
        clip.play();
    }

    private AudioClip getAudioClip(String name) {
        URL url = getClass().getResource("/Audio/" + name + ".wav");
        if (url == null) {
            System.err.println("Nie znaleziono pliku audio: " + name);
            return null;
        }

        return new AudioClip(url.toExternalForm());
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }
}
