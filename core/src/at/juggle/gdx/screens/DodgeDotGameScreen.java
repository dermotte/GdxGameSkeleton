package at.juggle.gdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import at.juggle.gdx.DodgeDotGameModel;
import at.juggle.gdx.GdxGame;

/**
 * Created by mlux on 17.02.2016.
 *
 * Colors for the game: https://color.adobe.com/de/Graffiti-Tatendrang-Hell-color-theme-7603232/
 * 3E4759, 84BF04, F2E205, F29F05, F25C05 ... contrast to green: 8E04BF
 */
public class DodgeDotGameScreen extends ScreenAdapter {
    GdxGame parentGame;
    private final SpriteBatch batch;
    private final OrthographicCamera cam;
    private DodgeDotGameModel model;

    public DodgeDotGameScreen(GdxGame parentGame) {
        // init camera
        cam = new OrthographicCamera(GdxGame.GAME_WIDTH, GdxGame.GAME_HEIGHT);
        cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
        cam.update();

        this.parentGame = parentGame;
        model = new DodgeDotGameModel(parentGame, cam);

        // init batch
        batch = new SpriteBatch();

    }

    @Override
    public void render(float delta) {
        super.render(delta);
        model.compute(delta);

        // camera:
        cam.update();
        batch.setProjectionMatrix(cam.combined);

        Gdx.gl.glClearColor(0.2431372f, 0.278431f, 0.3490196f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        model.render(batch);
        batch.end();
    }
}
