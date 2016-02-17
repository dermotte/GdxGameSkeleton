package at.juggle.gdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

import at.juggle.gdx.DodgeDotGameModel;
import at.juggle.gdx.GdxGame;
import at.juggle.gdx.ScreenManager;

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


    public GameOverScreen(GdxGame game) {
        this.parentGame = game;

        font = parentGame.getAssetManager().get("menu/Ravie_72.fnt");
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
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
        font.setColor(242f / 255, 159f / 255, 5f / 255, 1f);
        batch.begin();
        if (scoreWidth < 1) {
            font.setColor(0f, 0f, 0f, 0f);
            gameOverWidth = font.draw(batch, "Game Over!", GdxGame.GAME_WIDTH / 8, 2* GdxGame.GAME_HEIGHT / 3 + font.getLineHeight() / 2).width;
            scoreWidth = font.draw(batch, "scored " + DodgeDotGameModel.score + " points", GdxGame.GAME_WIDTH / 8, GdxGame.GAME_HEIGHT / 3 + font.getLineHeight() / 2).width;
        } else {
            font.draw(batch, "Game Over!", GdxGame.GAME_WIDTH / 2 - gameOverWidth / 2, 2* GdxGame.GAME_HEIGHT / 3 + font.getLineHeight() / 2);
            font.draw(batch, "scored " + DodgeDotGameModel.score + " points", GdxGame.GAME_WIDTH / 2 - scoreWidth / 2, GdxGame.GAME_HEIGHT / 3 + font.getLineHeight() / 2);
        }
        batch.end();
    }


    private void handleInput() {
        if (showTime > 2) {
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
