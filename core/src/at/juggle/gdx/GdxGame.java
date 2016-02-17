package at.juggle.gdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GdxGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private AssetManager assMan;
    private ScreenManager screenManager;
    private SoundManager soundManager;

    // gives the original size for all screen working with the scaling orthographic camera
    // set in DesktopLauncher to any resolution and it will be scaled automatically.
    public static final int GAME_WIDTH = 1920;
    public static final int GAME_HEIGHT = 1080;


    @Override
    public void create() {
        screenManager = new ScreenManager(this);
        soundManager = new SoundManager(this);

        // LOAD ASSETS HERE ...
        // Loading screen will last until the last one is loaded.
        assMan = new AssetManager();
        // for the menu
        assMan.load("menu/Ravie_42.fnt", BitmapFont.class);
        assMan.load("menu/Ravie_72.fnt", BitmapFont.class);
        assMan.load("menu/menu_background.jpg", Texture.class);
        // for the credits
        assMan.load("credits/gradient_top.png", Texture.class);
        assMan.load("credits/gradient_bottom.png", Texture.class);
        // for the sounds
        soundManager.preload(assMan);
        // for the actual game:
        assMan.load("game/dot_green.png", Texture.class);
        assMan.load("game/dot_back.png", Texture.class);
        assMan.load("game/dot_green.png", Texture.class);
        assMan.load("game/dot_yellow.png", Texture.class);
        assMan.load("game/dot_light_orange.png", Texture.class);
        assMan.load("game/dot_orange.png", Texture.class);
    }

    @Override
    public void render() {
        // make sure the sound stays in sync.
        soundManager.handle(Gdx.graphics.getDeltaTime());
        // render the screen.
        screenManager.getCurrentScreen().render(Gdx.graphics.getDeltaTime());
    }

    public AssetManager getAssetManager() {
        return assMan;
    }

    public ScreenManager getScreenManager() {
        return screenManager;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }
}
