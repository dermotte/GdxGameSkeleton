package at.juggle.gdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import at.juggle.gdx.GdxGame;
import at.juggle.gdx.ScreenManager;
import at.juggle.gdx.animation.BackgroundAnimation;

/**
 * Created by Mathias Lux, mathias@juggle.at,  on 04.02.2016.
 */
public class CreditsScreen extends ScreenAdapter {
    private final SpriteBatch batch;
    private final OrthographicCamera cam;
    private GdxGame parentGame;
    private BackgroundAnimation animation;

    Texture backgroundImage, gradientTop, gradientBottom;
    BitmapFont creditsFont;

    String[] credits = ("DodgeDots\n" +
            "by Mathias Lux\n" +
            "mathias@juggle.at\n" +
            "\n" +
            "Graphic and sound assets\n    are public domain\n" +
            "Game licensed under Apache 2.0 license\n" +
            "\n" +
            "Implemented with libGDX.\n" +
            "Special thanks to Mario Zechner!\n" +
            "\n" +
            "Music created with a Novation Circuit.\n" +
            "Sound created with bfxr.\n" +
            "\n" +
            "Homespun font by\n    Aenigma fonts (public domain)\n" +
            "\n" +
            "Feel free to adapt!").split("\\n");
    private float moveY;


    public CreditsScreen(GdxGame game) {
        this.parentGame = game;

        backgroundImage = parentGame.getAssetManager().get("menu/menu_background.jpg");
        gradientTop = parentGame.getAssetManager().get("credits/gradient_top.png");
        gradientBottom = parentGame.getAssetManager().get("credits/gradient_bottom.png");

        creditsFont = parentGame.getAssetManager().get("menu/Homespun_72.fnt");

        // Create camera that projects the game onto the actual screen size.
        cam = new OrthographicCamera(GdxGame.GAME_WIDTH, GdxGame.GAME_HEIGHT);

        cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
        cam.update();

        batch = new SpriteBatch();
        animation = new BackgroundAnimation(game);
    }

    @Override
    public void render(float delta) {
        moveY += delta*100;
        handleInput();
        // camera:
        cam.update();
        batch.setProjectionMatrix(cam.combined);


        Gdx.gl.glClearColor(0.2431372f, 0.278431f, 0.3490196f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        // animation
        animation.compute(delta);
        animation.render(batch);
        // draw bgImage
        batch.setColor(1f, 1f, 1f, 0.5f);
        batch.draw(backgroundImage, 0, 0, GdxGame.GAME_WIDTH, GdxGame.GAME_HEIGHT);

        // draw moving text:
        for (int i = 0; i < credits.length; i++) {
            creditsFont.setColor(242f / 255, 159f / 255, 5f / 255, 1f);
            creditsFont.draw(batch, credits[i], GdxGame.GAME_WIDTH/8, moveY - i*creditsFont.getLineHeight()*1.5f);
        }


        // draw gradient
//        batch.draw(gradientBottom, 0, 0, GdxGame.GAME_WIDTH, gradientBottom.getHeight());
//        batch.draw(gradientTop, 0, GdxGame.GAME_HEIGHT-gradientTop.getHeight(), GdxGame.GAME_WIDTH, gradientTop.getHeight());

        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) || Gdx.input.justTouched()) {
            parentGame.getScreenManager().setCurrentState(ScreenManager.ScreenState.Menu);
        }
    }


}
