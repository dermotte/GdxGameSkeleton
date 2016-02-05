package at.juggle.gdx;

import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;

/**
 * Created by Mathias Lux, mathias@juggle.at, 05.02.2016.
 */
public class SoundManager {
    private GdxGame parentGame;
    private HashMap<String, String> event2sound;

    public SoundManager(GdxGame parentGame) {
        this.parentGame = parentGame;

        // register the available events.
        event2sound = new HashMap<String, String>(20);
        event2sound.put("blip", "sfx/blip.wav");
        event2sound.put("explode", "sfx/explosion.wav");
        event2sound.put("hit", "sfx/hit.wav");
        event2sound.put("jump", "sfx/jump.wav");
        event2sound.put("laser", "sfx/laser.wav");
        event2sound.put("pickup", "sfx/pickup.wav");
        event2sound.put("powerup", "sfx/powerup.wav");
    }

    /**
     * Plays an event registered in the constructor. Make sure that (i) the event is known and (ii) the
     * asset is loaded in the constructor of GdxGame.
     *
     * @param event
     */
    public void playEvent(String event) {
        if (event2sound.get(event) != null) {
            parentGame.getAssetManager().get(event2sound.get(event), Sound.class).play();
        } else {
            System.err.println("Event unknown.");
        }
    }
}
