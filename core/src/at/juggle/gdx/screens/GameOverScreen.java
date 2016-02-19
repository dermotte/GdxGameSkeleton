package at.juggle.gdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;

import at.juggle.gdx.DodgeDotGameModel;
import at.juggle.gdx.GdxGame;
import at.juggle.gdx.ScreenManager;
import at.juggle.gdx.animation.BackgroundAnimation;

/**
 * Created by mlux on 17.02.2016.
 */
public class GameOverScreen extends ScreenAdapter {
    private final SpriteBatch batch;
    private final OrthographicCamera cam;
    private GdxGame parentGame;
    float gameOverWidth = 0f;
    float scoreWidth = 0f;
    BitmapFont font;

    float showTime = 0f;
    private Preferences highscore = null;
    private int highscoreValue = 0;
    private boolean hasBeatenHighscore = false;
    private float highscoreWidth;

    BackgroundAnimation animation;
    Texture backgroundImage;

    public GameOverScreen(GdxGame game) {
        this.parentGame = game;

        backgroundImage = parentGame.getAssetManager().get("menu/menu_background.jpg");
        font = parentGame.getAssetManager().get("menu/Homespun_112.fnt");
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        // check out the high score:
        highscore = Gdx.app.getPreferences("highscore");
        highscoreValue = highscore.getInteger("highscore");
        hasBeatenHighscore = DodgeDotGameModel.score > highscore.getInteger("highscore");
        if (hasBeatenHighscore) {
            highscore.putInteger("highscore", (int) DodgeDotGameModel.score);
            highscore.flush();
        }
        animation = new BackgroundAnimation(game);

        // Create camera that projects the game onto the actual screen size.
        cam = new OrthographicCamera(GdxGame.GAME_WIDTH, GdxGame.GAME_HEIGHT);

        cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
        cam.update();

        batch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        showTime += delta;
        handleInput();
        // camera:
        cam.update();
        batch.setProjectionMatrix(cam.combined);
        Gdx.gl.glClearColor(0.2431372f, 0.278431f, 0.3490196f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();

        // animation.
        animation.compute(delta);
        animation.render(batch);
        // draw bgImage ...
        batch.setColor(1f, 1f, 1f, 0.66f);
        batch.draw(backgroundImage, 0, 0, GdxGame.GAME_WIDTH, GdxGame.GAME_HEIGHT);
        batch.setColor(1f, 1f, 1f, 1f);

        String score = DodgeDotGameModel.score + " points";
        String highscore = "Current highscore: " + highscoreValue + " points";
        if (hasBeatenHighscore)
            highscore = "New highscore!";
        if (scoreWidth < 1) {
            font.setColor(0f, 0f, 0f, 0f);
            font.getData().setScale(1f);
            gameOverWidth = font.draw(batch, "Game Over!", GdxGame.GAME_WIDTH / 8, 2* GdxGame.GAME_HEIGHT / 3 + font.getLineHeight() ).width;
            highscoreWidth = font.draw(batch, highscore, GdxGame.GAME_WIDTH / 8, GdxGame.GAME_HEIGHT / 3 + font.getLineHeight() / 2).width;
            font.getData().setScale(2f);
            scoreWidth = font.draw(batch, score, GdxGame.GAME_WIDTH / 8, GdxGame.GAME_HEIGHT / 2 + font.getLineHeight() / 2).width;
        } else {
            font.getData().setScale(1f);
            font.setColor(242f / 255, 159f / 255, 5f / 255, 1 - (float) Math.min(1d, (Math.max((showTime - 2) / 5, 0))));
            font.draw(batch, "Game Over!", GdxGame.GAME_WIDTH / 2 - gameOverWidth / 2, 2 * GdxGame.GAME_HEIGHT / 3 + font.getLineHeight() );
            font.setColor(242f / 255, 159f / 255, 5f / 255, (float) Math.min(1d, (Math.max((showTime - 0.5) / 2, 0))));
            font.draw(batch, highscore, GdxGame.GAME_WIDTH / 2 - highscoreWidth / 2, GdxGame.GAME_HEIGHT / 3 + font.getLineHeight() / 2);
            font.getData().setScale(2f);
            font.draw(batch, score, GdxGame.GAME_WIDTH / 2 - scoreWidth / 2, GdxGame.GAME_HEIGHT / 2 + font.getLineHeight() / 2);
            font.getData().setScale(1f);
        }
        batch.end();
    }


    private void handleInput() {
        if (showTime > 0.5) {
            // keys ...
            if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
                parentGame.getScreenManager().setCurrentState(ScreenManager.ScreenState.Menu);
            }
            // touch
            if (Gdx.input.justTouched()) {
                parentGame.getScreenManager().setCurrentState(ScreenManager.ScreenState.Menu);
            }
        }
    }

}
