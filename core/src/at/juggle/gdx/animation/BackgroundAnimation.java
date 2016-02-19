package at.juggle.gdx.animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import at.juggle.gdx.GdxGame;

/**
 * Created by mlux on 18.02.2016.
 */
public class BackgroundAnimation {
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
    float tickTime = 1f/2.1666666f;
    private boolean gameOver = false;
    public static long score = 0l;

    // for rendering:
    Texture mainDot, horizontalDot, verticalDot;
    GdxGame parentGame;
    private float spawnProb = 0.03f;

    // for drawing:
    float dotSide = GdxGame.GAME_WIDTH / gridX;

    public BackgroundAnimation(GdxGame parentGame) {
        this.parentGame = parentGame;
        mainDot = parentGame.getAssetManager().get("game/dot_green.png", Texture.class);
        horizontalDot = parentGame.getAssetManager().get("game/dot_orange.png", Texture.class);
        verticalDot = parentGame.getAssetManager().get("game/dot_yellow.png", Texture.class);
    }

    public void compute(float delta) {
        // timing:
        gameTime += delta; // new time
        if (gameTime - lastTick < tickTime) { // return if not a tick!
            return;
        }
        // OK, it's a tick!
        lastTick += tickTime;

        // move them ...
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
            if (Math.random() < spawnProb && vertDots[1][y] == 0) { // here's a dot
                vertDots[0][y] = 1;
            }
        }
        for (int x = 0; x < gridX; x++) {
            if (Math.random() < spawnProb && horiDots[x][1] == 0) { // here's a dot
                horiDots[x][0] = 1;
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (int x = gridX - 1; x >= 0; x--) {
            for (int y = 0; y < gridY; y++) {
                if (vertDots[x][y] > 0) { // paint the dot ...
                    batch.draw(verticalDot, x * dotSide, y * dotSide, dotSide, dotSide);
                    batch.setColor(1f, 1f, 1f, 0.33f);
                    batch.draw(verticalDot, x * dotSide - dotSide / 2, y * dotSide, dotSide, dotSide);
                    batch.setColor(1f, 1f, 1f, 1f);
                }
                if (horiDots[x][y] > 0) { // paint the dot ...
                    batch.draw(horizontalDot, x * dotSide, y * dotSide, dotSide, dotSide);
                    batch.setColor(1f, 1f, 1f, 0.33f);
                    batch.draw(horizontalDot, x * dotSide, y * dotSide - dotSide / 2, dotSide, dotSide);
                    batch.setColor(1f, 1f, 1f, 1f);
                }
            }
        }
    }
}
