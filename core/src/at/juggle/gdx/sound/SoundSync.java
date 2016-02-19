package at.juggle.gdx.sound;

/**
 * Created by mlux on 19.02.2016.
 */
public interface SoundSync {
    /**
     * Is called from sound manager to indicate a ktimepoint to which sync is possible.
     */
    public void sync();
}
