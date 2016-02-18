package at.juggle.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by mlux on 17.02.2016.
 */
public class DodgeDotGameModel {
    // size of the grid on x-axis, y-axis has size 16:9
    private final int gridX = 40;
    private final int gridY = 23;

    // data model
    private int[][] vertDots = new int[gridX][gridY];
    private int[][] horiDots = new int[gridX][gridY];
    private int dotX = gridX / 2, dotY = gridY / 2;

    // time:
    float lastTick = 0f;
    float gameTime = 0f;
    float gameOverTime = 0f;
    private boolean gameOver = false;
    public static long score = 0l;

    // for rendering:
    Texture mainDot, horizontalDot, verticalDot;
    BitmapFont font;
    GdxGame parentGame;
    OrthographicCamera camera;

    // for drawing:
    private float dotSide = GdxGame.GAME_WIDTH / gridX;
    private GlyphLayout glyphLayout = new GlyphLayout();

    // level config ...
    DodgeDotLevel[] levels = new DodgeDotLevel[]{
            new DodgeDotLevel(1f / 4f, 0.00f, 0.03f),
            new DodgeDotLevel(1f / 4f, 0.01f, 0.03f),
            new DodgeDotLevel(1f / 4f, 0.02f, 0.03f),
            new DodgeDotLevel(1f / 4f, 0.03f, 0.03f),
            new DodgeDotLevel(1f / 5f, 0.03f, 0.03f),
            new DodgeDotLevel(1f / 5f, 0.03f, 0.04f),
            new DodgeDotLevel(1f / 5f, 0.04f, 0.04f),
            new DodgeDotLevel(1f / 6f, 0.04f, 0.04f),
            new DodgeDotLevel(1f / 6f, 0.04f, 0.05f),
            new DodgeDotLevel(1f / 6f, 0.05f, 0.05f),
            new DodgeDotLevel(1f / 7f, 0.05f, 0.05f),
            new DodgeDotLevel(1f / 8f, 0.05f, 0.05f),
            new DodgeDotLevel(1f / 9f, 0.05f, 0.05f),
            new DodgeDotLevel(1f / 10f, 0.05f, 0.05f),
    };
    int currentLevelIndex = 0;
    DodgeDotLevel currentLevel = levels[currentLevelIndex];

    /**
     * Constructor taking the textures needed.
     */
    public DodgeDotGameModel(GdxGame parentGame, OrthographicCamera cam) {
        mainDot = parentGame.getAssetManager().get("game/dot_green.png", Texture.class);
        horizontalDot = parentGame.getAssetManager().get("game/dot_orange.png", Texture.class);
        verticalDot = parentGame.getAssetManager().get("game/dot_yellow.png", Texture.class);
        font = parentGame.getAssetManager().get("menu/Homespun_42.fnt", BitmapFont.class);
        this.parentGame = parentGame;
        score = 0;
        camera = cam;
    }

    public void compute(float delta) {
        handleInput();
        // check for crash ...
        if (!gameOver && (vertDots[dotX][dotY] > 0 || horiDots[dotX][dotY] > 0)) {
            // it's a crash!
            gameOver = true;
            gameOverTime = gameTime;
            parentGame.getSoundManager().addLevel(-1);
            parentGame.getSoundManager().playEvent("explode");
            parentGame.getSoundManager().fadeOut();
        }
        // timing:
        gameTime += delta; // new time
        if (gameTime - lastTick < currentLevel.getTickTime()) { // return if not a tick!
            return;
        }
        // OK, it's a tick!
        lastTick += currentLevel.getTickTime();

        // don't move if game is over ..
        if (gameOver) {
            return;
        }

        // increment score
        score = (long) Math.floor(gameTime * 13);

        // set Level ...
        currentLevelIndex = (int) Math.min(levels.length - 1, Math.floor(gameTime / 10));
        if (currentLevel != levels[currentLevelIndex]) {
            parentGame.getSoundManager().playEvent("pickup");
            if (currentLevelIndex == 2) parentGame.getSoundManager().addLevel(1);
            currentLevel = levels[currentLevelIndex];
            glyphLayout.setText(font, "Level: " + (currentLevelIndex + 1));
        }

        // now let's move
        for (int x = gridX - 1; x >= 0; x--) {
            for (int y = gridY - 1; y >= 0; y--) {
                if (vertDots[x][y] > 0) { // here's a dot
                    // move it to the right.
                    vertDots[x][y] = 0;
                    if (x + 1 < gridX) { // only if it's within the array
                        vertDots[x + 1][y] = 1;
                    }
                }
                if (horiDots[x][y] > 0) { // here's a dot
                    // move it to the bottom.
                    horiDots[x][y] = 0;
                    if (y + 1 < gridY) { // only if it's within the array
                        horiDots[x][y + 1] = 1;
                    }
                }
            }
        }

        // spawn new dots:
        for (int y = 0; y < gridY; y++) {
            if (Math.random() < currentLevel.getVerticalSpawnProbability() && vertDots[1][y] == 0) { // here's a dot
                vertDots[0][y] = 1;
            }
        }
        for (int x = 0; x < gridX; x++) {
            if (Math.random() < currentLevel.getHorizontalSpawnProbability() && horiDots[x][1] == 0) { // here's a dot
                horiDots[x][0] = 1;
            }
        }
    }

    private void handleInput() {
        if (!gameOver) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                dotY -= 1;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                dotY += 1;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                dotX -= 1;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                dotX += 1;
            }

            if (Gdx.input.justTouched()) {
                // touch from the left lower corner.
                Vector3 touch = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 1));

                // deifne points on screen
                Vector3 north = new Vector3(GdxGame.GAME_WIDTH/2, GdxGame.GAME_HEIGHT, touch.z);
                Vector3 south = new Vector3(GdxGame.GAME_WIDTH/2, 0, touch.z);
                Vector3 west = new Vector3(0, GdxGame.GAME_HEIGHT/2, touch.z);
                Vector3 east = new Vector3(GdxGame.GAME_WIDTH, GdxGame.GAME_HEIGHT/2, touch.z);

                // find which is nearest.
                float[] d = new float[4];
                d[0] = touch.dst(north);
                d[1] = touch.dst(east);
                d[2] = touch.dst(south);
                d[3] = touch.dst(west);

                float min = d[0];
                int index = 0;
                for (int i = 1; i < d.length; i++) {
                    if (d[i]<min) {
                        index = i;
                        min = d[i];
                    }
                }

                // now move ...
                switch (index) {
                    case 0:
//                        System.out.println("north");
                        dotY += 1;
                        break;
                    case 1:
//                        System.out.println("east");
                        dotX += 1;
                        break;
                    case 2:
//                        System.out.println("south");
                        dotY -= 1;
                        break;
                    case 3:
//                        System.out.println("west");
                        dotX -= 1;
                        break;
                }

            }

            dotY %= gridY;
            dotX %= gridX;


        } else {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) || Gdx.input.justTouched()) {
                if (gameTime - gameOverTime > 2) // only if the explosion has been visible for at least 2 secs.
                    parentGame.getScreenManager().setCurrentState(ScreenManager.ScreenState.GameOver);
            }

        }
    }

    public void render(SpriteBatch batch) {
        if (gameOver) {
            float size = dotSide + 6 * dotSide * Interpolation.exp5Out.apply(gameTime - gameOverTime);
            batch.draw(parentGame.getAssetManager().get("game/dot_light_orange.png", Texture.class), dotX * dotSide + dotSide / 2 - size / 2, dotY * dotSide + dotSide / 2 - size / 2, size, size);
        }
        for (int x = gridX - 1; x >= 0; x--) {
            for (int y = 0; y < gridY; y++) {
                if (vertDots[x][y] > 0) { // paint the dot ...
                    batch.draw(verticalDot, (x - 1) * dotSide, y * dotSide, dotSide * 2, dotSide);
                }
                if (horiDots[x][y] > 0) { // paint the dot ...
                    batch.draw(horizontalDot, x * dotSide, (y - 1) * dotSide, dotSide, dotSide * 2);
                }
            }
        }

        batch.draw(mainDot, dotX * dotSide, dotY * dotSide, dotSide, dotSide);

        font.setColor(242f / 255, 159f / 255, 5f / 255, 1f);
        font.draw(batch, "Score: " + score, dotSide, dotSide);
        font.draw(batch, glyphLayout, GdxGame.GAME_WIDTH - dotSide - glyphLayout.width, dotSide);
    }
}

class DodgeDotLevel {
    float tickTime; // the faster the merrier
    float verticalSpawnProbability;
    float horizontalSpawnProbability;

    public DodgeDotLevel(float tickTime, float verticalSpawnProbability, float horizontalSpawnProbability) {
        this.tickTime = tickTime;
        this.verticalSpawnProbability = verticalSpawnProbability;
        this.horizontalSpawnProbability = horizontalSpawnProbability;
    }

    public float getTickTime() {
        return tickTime;
    }

    public float getVerticalSpawnProbability() {
        return verticalSpawnProbability;
    }

    public float getHorizontalSpawnProbability() {
        return horizontalSpawnProbability;
    }
}
