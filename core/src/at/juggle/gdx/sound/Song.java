package at.juggle.gdx.sound;

import com.badlogic.gdx.audio.Music;

/**
 * Data structure for storing a song consisting of one or more loops.
 * Created by Mathias Lux, mathias@juggle.at, 09.02.2016.
 */
public class Song {
    // all Strings refer to the paths from which the music can be pulled using the AssetManager.
    String intro, outro; // intro and outro loops
    String[] level; // main loops in different levels.

    private int currentLevel = 0;

    /**
     * Main constructor
     * @param intro can be null
     * @param outro can be null
     * @param level cannot be null
     */
    public Song(String intro, String outro, String[] level) {
        this.intro = intro;
        this.outro = outro;
        this.level = level;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getOutro() {
        return outro;
    }

    public void setOutro(String outro) {
        this.outro = outro;
    }

    public String[] getLevel() {
        return level;
    }

    public void setLevel(String[] level) {
        this.level = level;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }
}
