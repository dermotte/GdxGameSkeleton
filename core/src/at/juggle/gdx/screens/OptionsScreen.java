package at.juggle.gdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

import at.juggle.gdx.GdxGame;
import at.juggle.gdx.ScreenManager;
import at.juggle.gdx.animation.BackgroundAnimation;

/**
 * Created by Mathias Lux, mathias@juggle.at,  on 04.02.2016.
 */
public class OptionsScreen extends ScreenAdapter {
    private final SpriteBatch batch;
    private final OrthographicCamera cam;
    private final Preferences highscorePreferences;
    private GdxGame parentGame;
    private BackgroundAnimation animation;

    Texture mainDot, horizontalDot, verticalDot;
    Texture backgroundImage;
    BitmapFont menuFont, smallFont;

    // you can add strings here ...
    String[] menuStrings = {"Toogle music", "Toggle sound", "Delete highscore", "Back to menu"};//, "Exit"};
    int currentMenuItem = 0;

    String highScore = "", musicString = "On", soundString = "On";
    float highScoreStringLength = 0, musicStringLength = 0f, soundStringLength = 0f;

    // put it where we can see it :)
    float offsetLeft = GdxGame.GAME_WIDTH / 8, offsetTop = GdxGame.GAME_WIDTH / 8, offsetY = GdxGame.GAME_HEIGHT / 6;


    public OptionsScreen(GdxGame game) {
        this.parentGame = game;
        if (parentGame.getBackgroundAnimation() == null) {
            parentGame.setBackgroundAnimation(new BackgroundAnimation(parentGame));
        }
        animation = parentGame.getBackgroundAnimation();
        backgroundImage = parentGame.getAssetManager().get("menu/menu_background.jpg", Texture.class);

        menuFont = parentGame.getAssetManager().get("menu/Homespun_112.fnt");
        menuFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        smallFont = parentGame.getAssetManager().get("menu/Homespun_42.fnt");
        smallFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);


        // for the animation ...
        mainDot = parentGame.getAssetManager().get("game/dot_green.png", Texture.class);
        horizontalDot = parentGame.getAssetManager().get("game/dot_orange.png", Texture.class);
        verticalDot = parentGame.getAssetManager().get("game/dot_yellow.png", Texture.class);

        // Create camera that projects the game onto the actual screen size.
        cam = new OrthographicCamera(GdxGame.GAME_WIDTH, GdxGame.GAME_HEIGHT);

        cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
        cam.update();

        batch = new SpriteBatch();
        highscorePreferences = Gdx.app.getPreferences(GdxGame.HIGHSCORE_FILE);
        refreshOptions();
    }

    private void refreshOptions() {
        musicString = parentGame.getSoundManager().isMusicOn() ? "ON" : "OFF";
        soundString = parentGame.getSoundManager().isSoundOn() ? "ON" : "OFF";
        int h = highscorePreferences.getInteger("highscore", 0);
        highScore = "";
        if (h > 0) {
            highScore = h + " points";
        }
    }

    @Override
    public void render(float delta) {
        handleInput();
        // camera:
        cam.update();
        batch.setProjectionMatrix(cam.combined);


        Gdx.gl.glClearColor(0.2431372f, 0.278431f, 0.3490196f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();

        // draw animation ...
        animation.compute(delta);
        animation.render(batch);

        // draw bgImage ...
        batch.setColor(1f, 1f, 1f, 0.5f);
        batch.draw(backgroundImage, 0, 0, GdxGame.GAME_WIDTH, GdxGame.GAME_HEIGHT);

        // draw Strings ...
        for (int i = 0; i < menuStrings.length; i++) {
            if (i == currentMenuItem && Gdx.input.isPeripheralAvailable(Input.Peripheral.HardwareKeyboard)) {// show color on those with keyboard.
                menuFont.setColor(132f / 255, 191f / 255, 4f / 255, 1f);
            } else menuFont.setColor(192f / 255, 131f / 255, 4f / 255, 1f);
            menuFont.draw(batch, menuStrings[i], offsetLeft, GdxGame.GAME_HEIGHT - offsetTop - i * offsetY);
        }
        // music and sound:
        smallFont.setColor(192f / 255, 131f / 255, 4f / 255, 1f);
        musicStringLength = smallFont.draw(batch, musicString, GdxGame.GAME_WIDTH - offsetLeft - musicStringLength, GdxGame.GAME_HEIGHT - offsetTop - 0 * offsetY - (menuFont.getLineHeight() / 2 - smallFont.getLineHeight() / 2)).width;
        soundStringLength = smallFont.draw(batch, soundString, GdxGame.GAME_WIDTH - offsetLeft - soundStringLength, GdxGame.GAME_HEIGHT - offsetTop - 1 * offsetY - (menuFont.getLineHeight() / 2 - smallFont.getLineHeight() / 2)).width;


        // highscore
        if (highScore.length() > 1) { // draw highscore if available.
            smallFont.setColor(192f / 255, 131f / 255, 4f / 255, 1f);
            highScoreStringLength = smallFont.draw(batch, highScore, GdxGame.GAME_WIDTH - offsetLeft - highScoreStringLength, GdxGame.GAME_HEIGHT - offsetTop - 2 * offsetY - (menuFont.getLineHeight() / 2 - smallFont.getLineHeight() / 2)).width;
        }
        batch.end();
    }

    private void handleInput() {
        // keys ...
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            currentMenuItem = (currentMenuItem + 1) % menuStrings.length;
            parentGame.getSoundManager().playEvent("blip");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            if (currentMenuItem > 0) currentMenuItem = (currentMenuItem - 1);
            else currentMenuItem = menuStrings.length - 1;
            parentGame.getSoundManager().playEvent("blip");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            System.out.println("Next level in music ...");
            parentGame.getSoundManager().addLevel(1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            System.out.println("Previous level in music ...");
            parentGame.getSoundManager().addLevel(-1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            Gdx.app.exit();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) { // "Toogle music", "Toggle sound", "Delete highscore",
            menuCommand(currentMenuItem);
        }
        // touch
        if (Gdx.input.justTouched()) {
            Vector3 touchWorldCoords = cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 1));
            // find the menu item ..
            for (int i = 0; i < menuStrings.length; i++) {
                if (touchWorldCoords.x > offsetLeft) {
                    float pos = GdxGame.GAME_HEIGHT - offsetTop - i * offsetY;
                    if (touchWorldCoords.y < pos && touchWorldCoords.y > pos - menuFont.getLineHeight()) {
                        menuCommand(i);
                    }
                }

            }
        }
    }

    private void menuCommand(int index) {
        if (menuStrings[index].equals("Back to menu")) {
            parentGame.getScreenManager().setCurrentState(ScreenManager.ScreenState.Menu);
        } else if (menuStrings[index].equals("Toogle music")) {
            parentGame.getSoundManager().toggleMusicOn();
        } else if (menuStrings[index].equals("Toggle sound")) {
            parentGame.getSoundManager().toggleSoundOn();
        } else if (menuStrings[index].equals("Delete highscore")) {
            highscorePreferences.putInteger("highscore", 0);
            highscorePreferences.flush();
        }
        refreshOptions();
    }


}
