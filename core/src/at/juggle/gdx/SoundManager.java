package at.juggle.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;

import at.juggle.gdx.sound.Song;
import at.juggle.gdx.sound.SoundSync;

/**
 * Created by Mathias Lux, mathias@juggle.at, 05.02.2016.
 */
public class SoundManager {

    enum MusicState {Running, FadeOut, FadeIn}
    private GdxGame parentGame;
    private HashMap<String, String> event2sound;
    private HashMap<String, Song> name2song;

    private Music currentMusic = null;
    private Music nextMusic = null;

    private Song currentSong = null;
    private float currentVolume = 0.7f;
    private float maxVolume = 0.7f;
    private SoundSync soundSync = null;

    private MusicState currentMusicState = MusicState.Running;

    private boolean soundOn = true, musicOn = true;
    private final Preferences gameOptions = Gdx.app.getPreferences(GdxGame.OPTIONS_FILE);

    public SoundManager(GdxGame parentGame) {
        this.parentGame = parentGame;
        event2sound = new HashMap<String, String>(20);
        name2song = new HashMap<String, Song>(5);
        if (Gdx.app.getPreferences(GdxGame.OPTIONS_FILE) != null) {
            soundOn = gameOptions.getBoolean("soundOn", true);
            musicOn = gameOptions.getBoolean("musicOn", true);
        }
    }

    /**
     * Plays an event registered in the constructor. Make sure that (i) the event is known and (ii) the
     * asset is loaded in the constructor of GdxGame.
     *
     * @param event
     */
    public void playEvent(String event) {
        if (soundOn) {
            if (event2sound.get(event) != null) {
                parentGame.getAssetManager().get(event2sound.get(event), Sound.class).play();
            } else {
                System.err.println("Event unknown.");
            }
        }
    }

    /**
     * Tell sound manager here to pre-load everything. I't loaded in the loading screen and is available when the intro / menu starts.
     * @param assMan
     */
    public void preload(AssetManager assMan) {
        // sounds
        loadSound(assMan, "sfx/blip.wav", "blip");
        loadSound(assMan, "sfx/explosion.wav", "explode");
        loadSound(assMan, "sfx/hit.wav", "hit");
        loadSound(assMan, "sfx/jump.wav", "jump");
        loadSound(assMan, "sfx/laser.wav", "laser");
        loadSound(assMan, "sfx/pickup.wav", "pickup");
        loadSound(assMan, "sfx/powerup.wav", "powerup");

        // music
        loadMusic(assMan, "music/main_intro.ogg");
        loadMusic(assMan, "music/main_lvl1.ogg");
        loadMusic(assMan, "music/main_lvl2.ogg");
        loadMusic(assMan, "music/main_lvl3.ogg");
        loadMusic(assMan, "music/main_lvl4.ogg");
        name2song.put("main", new Song("music/main_intro.ogg", null, new String[]{"music/main_lvl1.ogg", "music/main_lvl2.ogg", "music/main_lvl3.ogg", "music/main_lvl4.ogg"}));
    }

    /**
     * Starts a song registered under the given name.
     * @param name
     */
    public void startSong(String name) {
        if (!musicOn) return;
        // check if it is registered.
        if (name2song.get(name) == null) {
            System.err.println("Song not known.");
            return;
        }

        if (currentMusic != null) {
            currentMusic.stop();
            currentVolume = maxVolume;
        }

        // start intro and add main loop.
        currentSong = name2song.get(name);
        nextMusic = parentGame.getAssetManager().get(currentSong.getLevel()[0], Music.class);
        nextMusic.setLooping(true);
        currentSong.setCurrentLevel(0);
        if (currentSong.getIntro() !=null) {
            currentMusic = parentGame.getAssetManager().get(currentSong.getIntro(), Music.class);
            currentMusic.setOnCompletionListener(new Music.OnCompletionListener() {
                @Override
                public void onCompletion(Music music) {
                    currentMusic = nextMusic;
                    nextMusic = null;
                    currentMusic.setVolume(currentVolume);
                    currentMusic.play();
                    if (soundSync != null) soundSync.sync();
                }
            });
        } else {
            // there is no intro, just start with the main loop.
            currentMusic = nextMusic;
            nextMusic = null;
        }
        currentMusic.setVolume(currentVolume);
        currentMusic.play();
        if (soundSync != null) soundSync.sync();
        currentMusicState = MusicState.Running;
    }

    /**
     * Moves levels up and down.
     * @param increment 1 for next level, -1 for lower level.
     */
    public void addLevel(int increment) {
        if (!musicOn || currentSong == null) return;
        String[] level = currentSong.getLevel();
        int newLevel = currentSong.getCurrentLevel() + increment;
        if (level.length > newLevel && newLevel >= 0) { // check if it is a valid level ;)
            currentSong.setCurrentLevel(newLevel);
            nextMusic = parentGame.getAssetManager().get(level[newLevel]);
            currentMusic.setLooping(false);
            nextMusic.setLooping(true);
            currentMusic.setOnCompletionListener(new Music.OnCompletionListener() {
                @Override
                public void onCompletion(Music music) {
                    currentMusic = nextMusic;
                    nextMusic = null;
                    currentMusic.setVolume(currentVolume);
                    currentMusic.play();
                    if (soundSync != null) soundSync.sync();
                }
            });
        } else {
            System.err.println("SoundManager: Level not supported " + newLevel);
        }
    }

    /**
     * Fades out current music.
     */
    public void fadeOut() {
        currentMusicState = MusicState.FadeOut;
    }

    /**
     * Helper to load music in one line.
     * @param assMan
     * @param path
     */
    private void loadMusic(AssetManager assMan, String path) {
        assMan.load(path, Music.class);
    }

    /**
     * Helper to load and register Event in one line.
     * @param assMan
     * @param path
     * @param event
     */
    private void loadSound(AssetManager assMan, String path, String event) {
        assMan.load(path, Sound.class);
        event2sound.put(event, path);
    }

    /**
     * Syncs to the game time, is called from the game to induce the current game time for fades, etc.
     * @param deltaTime
     */
    public void handle(float deltaTime) {
        if (currentMusic!= null && currentMusicState == MusicState.FadeOut) {
            float fadeTime = 3f; // seconds for the fade.
            float step = maxVolume/fadeTime; // max volume divide by seconds time for the fade.
            currentVolume -= deltaTime*step;
            if (currentVolume <=0) { // stop fading.
                currentMusicState = MusicState.Running;
                currentMusic.stop();
                currentMusic = null;
                currentVolume = maxVolume;
                nextMusic = null;
            } else {
                currentMusic.setVolume(currentVolume);
            }
        }
    }

    public boolean isSoundOn() {
        return soundOn;
    }

    public void toggleSoundOn() {
        soundOn = !soundOn;
        gameOptions.putBoolean("soundOn", soundOn);
        gameOptions.flush();
    }

    public boolean isMusicOn() {
        return musicOn;
    }

    public void toggleMusicOn() {
        musicOn = !musicOn;
        gameOptions.putBoolean("musicOn", musicOn);
        gameOptions.flush();
    }

    public void setSoundSync(SoundSync soundSync) {
        this.soundSync = soundSync;
    }
}
