package Audio;

import java.net.URL;
import javafx.scene.media.AudioClip;

public class AudioPlayer {
    public static int MAIN_MENU = 0;
    public static int SAVE_ROOM = 1;

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
    private AudioClip[] songs;
    private float volume = 0.03f;


    public AudioPlayer() {
        loadSongs();
        loadEffects();
    }

    private void loadSongs() {
        String[] songNames = {
                "mainmenu", "savegameroom"
        };
        songs = new AudioClip[songNames.length];
        for (int i = 0; i < songs.length; i++) {
            songs[i] = getAudioSongClip(songNames[i]);
        }
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

    public boolean isSongPlaying(int song) {
        return songs[song].isPlaying();
    }

    public boolean isEffectPlaying(int effect) {
        return effects[effect].isPlaying();
    }

    public void playSong(int song) {
        AudioClip clip = songs[song];
        clip.setVolume(volume);
        clip.play();
    }

    public void playEffect(int effect) {
        AudioClip clip = effects[effect];
        clip.setVolume(volume);
        clip.play();
    }

    private AudioClip getAudioSongClip(String name) {
        URL url = getClass().getResource("/Audio/Songs/" + name + ".wav");
        if (url == null) {
            System.err.println("Nie znaleziono pliku audio: " + name);
            return null;
        }

        return new AudioClip(url.toExternalForm());
    }


    private AudioClip getAudioClip(String name) {
        URL url = getClass().getResource("/Audio/Effects/" + name + ".wav");
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
