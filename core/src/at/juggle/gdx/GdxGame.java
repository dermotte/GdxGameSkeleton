package at.juggle.gdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GdxGame extends ApplicationAdapter {
    SpriteBatch batch;
    AssetManager assMan;
    ScreenManager screenManager;

    public static final int GAME_WIDTH = 1920;
    public static final int GAME_HEIGHT = 1080;


    @Override
    public void create() {
        screenManager = new ScreenManager(this);

        // LOAD ASSETS ...
        assMan = new AssetManager();
        // for the menu
        assMan.load("menu/Ravie_42.fnt", BitmapFont.class);
        assMan.load("menu/menu_background.jpg", Texture.class);
    }

    @Override
    public void render() {
        screenManager.getCurrentScreen().render(Gdx.graphics.getDeltaTime());
    }

    public AssetManager getAssetManager() {
        return assMan;
    }

    public ScreenManager getScreenManager() {
        return screenManager;
    }
}
