package at.juggle.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;

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

    // for drawing:
    float dotSide = GdxGame.GAME_WIDTH / gridX;

    // level config ...
    DodgeDotLevel[] levels = new DodgeDotLevel[]{
            new DodgeDotLevel(1f / 4f, 0.00f, 0.03f),
            new DodgeDotLevel(1f / 4f, 0.01f, 0.03f),
            new DodgeDotLevel(1f / 4f, 0.02f, 0.05f),
            new DodgeDotLevel(1f / 4f, 0.05f, 0.05f),
            new DodgeDotLevel(1f / 5f, 0.05f, 0.05f),
    };
    int currentLevelIndex = 0;
    DodgeDotLevel currentLevel = levels[currentLevelIndex];

    /**
     * Constructor taking the textures needed.
     */
    public DodgeDotGameModel(GdxGame parentGame) {
        mainDot = parentGame.getAssetManager().get("game/dot_green.png", Texture.class);
        horizontalDot = parentGame.getAssetManager().get("game/dot_orange.png", Texture.class);
        verticalDot = parentGame.getAssetManager().get("game/dot_yellow.png", Texture.class);
        font = parentGame.getAssetManager().get("menu/Ravie_42.fnt", BitmapFont.class);
        this.parentGame = parentGame;
        score = 0;
    }

    public void compute(float delta) {
        handleInput();
        // check for crash ...
        if (!gameOver && (vertDots[dotX][dotY] > 0 || horiDots[dotX][dotY] > 0)) {
            // it's a crash!
            gameOver = true;
            gameOverTime = gameTime;
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
        currentLevel = levels[currentLevelIndex];

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
            dotY %= gridY;
            dotX %= gridX;
        } else {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) || Gdx.input.justTouched()) {
                if (gameTime-gameOverTime>2) // only if the explosion has been visible for at least 2 secs.
                    parentGame.getScreenManager().setCurrentState(ScreenManager.ScreenState.GameOver);
            }

        }
    }

    public void render(SpriteBatch batch) {
        if (gameOver) {
            float size = dotSide + 6*dotSide*Interpolation.exp5Out.apply(gameTime - gameOverTime);
            batch.draw(parentGame.getAssetManager().get("game/dot_light_orange.png", Texture.class), dotX * dotSide + dotSide/2 -size/2, dotY * dotSide + dotSide/2 -size/2, size, size);
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
        font.draw(batch, "Sc0re: " + score, dotSide, dotSide);
        font.draw(batch, "Level: " + (currentLevelIndex + 1), GdxGame.GAME_WIDTH - 8 * dotSide, dotSide);
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
