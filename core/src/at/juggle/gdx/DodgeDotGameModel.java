package at.juggle.gdx;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
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
    private int[][] bottomTopDots = new int[gridX][gridY];
    private int[][] leftRightDots = new int[gridX][gridY];
    private int[][] topBottomDots = new int[gridX][gridY];
    private int[][] rightLeftDots = new int[gridX][gridY];
    private int dotX = gridX / 2, dotY = gridY / 2;

    // time:
    float lastTick = 0f;
    float gameTime = 0f;
    float gameOverTime = 0f;
    private boolean gameOver = false;
    public static long score = 0l;

    // for rendering:
    Texture mainDot, horizontalDot, verticalDot, gamepad;
    BitmapFont font;
    GdxGame parentGame;
    OrthographicCamera camera;

    ParticleEffect xplode;

    // for drawing:
    private float dotSide = GdxGame.GAME_WIDTH / gridX;
    private GlyphLayout glyphLayout = new GlyphLayout();

    // level config ...
    DodgeDotLevel[] levels = new DodgeDotLevel[]{
            new DodgeDotLevel(1f / 4f, 0f, 0f, 0.02f, 0f),
            new DodgeDotLevel(1f / 4f, 0f, 0f, 0.02f, 0.02f),
            new DodgeDotLevel(1f / 4f, 0f, 0f, 0.03f, 0.02f),
            new DodgeDotLevel(1f / 4f, 0.01f, 0f, 0.03f, 0.02f),
            new DodgeDotLevel(1f / 4f, 0.01f, 0.01f, 0.03f, 0.03f),
            new DodgeDotLevel(1f / 4f, 0.03f, 0.01f, 0.03f, 0.03f),
            new DodgeDotLevel(1f / 4f, 0.03f, 0.03f, 0.03f, 0.03f),
            new DodgeDotLevel(1f / 4f, 0.03f, 0.03f, 0.03f, 0.03f),
            new DodgeDotLevel(1f / 5f, 0.03f, 0.03f, 0.03f, 0.03f),
            new DodgeDotLevel(1f / 5f, 0.05f, 0.05f, 0f, 0f),
            new DodgeDotLevel(1f / 6f, 0.05f, 0.05f, 0f, 0f),
            new DodgeDotLevel(1f / 6f, 0.05f, 0.05f, 0.01f, 0f),
            new DodgeDotLevel(1f / 6f, 0.05f, 0.05f, 0.01f, 0.01f),
            new DodgeDotLevel(1f / 6f, 0.05f, 0.05f, 0.03f, 0.03f),
            new DodgeDotLevel(1f / 6f, 0.05f, 0.05f, 0.05f, 0.05f),
            new DodgeDotLevel(1f / 7f, 0.05f, 0.05f, 0.05f, 0.05f),
            new DodgeDotLevel(1f / 8f, 0.05f, 0.05f, 0.05f, 0.05f),
            new DodgeDotLevel(1f / 9f, 0.05f, 0.05f, 0.05f, 0.05f),
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
        gamepad = parentGame.getAssetManager().get("game/gamepad_400.png", Texture.class);
        this.parentGame = parentGame;
        score = 0;
        camera = cam;
        parentGame.getAssetManager().load("", ParticleEffect.class);
        xplode = new ParticleEffect();
        xplode.load(Gdx.files.getFileHandle("game/xplode.particle", Files.FileType.Internal), Gdx.files.getFileHandle("game", Files.FileType.Internal));
    }

    public void compute(float delta) {
        handleInput();
        // check for crash ...
        if (!gameOver && (bottomTopDots[dotX][dotY] > 0 || leftRightDots[dotX][dotY] > 0 || topBottomDots[dotX][dotY] > 0 || rightLeftDots[dotX][dotY] > 0)) {
            // it's a crash!
            gameOver = true;
            gameOverTime = gameTime;
            parentGame.getSoundManager().addLevel(-1);
            parentGame.getSoundManager().playEvent("explode");
            parentGame.getSoundManager().fadeOut();
            xplode.start();
        }
        // timing:
        gameTime += delta; // new time
        if (gameTime - lastTick < currentLevel.getTickTime()) { // return if not a tick!
            return;
        }
        // go into game over after 3 seconds.
        if (gameOver && Math.abs(gameTime-gameOverTime) > 3)
            parentGame.getScreenManager().setCurrentState(ScreenManager.ScreenState.GameOver);
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
            if (currentLevelIndex % 3 == 0) parentGame.getSoundManager().addLevel(1);
            currentLevel = levels[currentLevelIndex];
            glyphLayout.setText(font, "Level: " + (currentLevelIndex + 1));
        }

        // now let's move
        for (int x = gridX - 1; x >= 0; x--) {
            for (int y = gridY - 1; y >= 0; y--) {
                if (bottomTopDots[x][y] > 0) { // here's a dot
                    // move it to the top.
                    bottomTopDots[x][y] = 0;
                    if (x + 1 < gridX) { // only if it's within the array
                        bottomTopDots[x + 1][y] = 1;
                    }
                }
                if (leftRightDots[x][y] > 0) { // here's a dot
                    // move it to the bottom.
                    leftRightDots[x][y] = 0;
                    if (y + 1 < gridY) { // only if it's within the array
                        leftRightDots[x][y + 1] = 1;
                    }
                }
            }
        }

        for (int x = 0; x < gridX; x++) {
            for (int y = 0; y < gridY; y++) {
                if (topBottomDots[x][y] > 0) { // here's a dot
                    // move it to the bottom.
                    topBottomDots[x][y] = 0;
                    if (x > 0) { // only if it's within the array
                        topBottomDots[x - 1][y] = 1;
                    }
                }
                if (rightLeftDots[x][y] > 0) { // here's a dot
                    // move it to the bottom.
                    rightLeftDots[x][y] = 0;
                    if (y > 0) { // only if it's within the array
                        rightLeftDots[x][y - 1] = 1;
                    }
                }
            }
        }

        // spawn new dots:
        for (int y = 0; y < gridY; y++) {
            if (Math.random() < currentLevel.getLeftRightSpawnProbability()*(y==dotY?3:1) && bottomTopDots[1][y] == 0) { // here's a dot
                bottomTopDots[0][y] = 1;
            }
        }
        for (int x = 0; x < gridX; x++) {
            if (Math.random() < currentLevel.getBottomTopSpawnProbability()*(x==dotX?3:1) && leftRightDots[x][1] == 0) { // here's a dot
                leftRightDots[x][0] = 1;
            }
        }
        for (int y = 0; y < gridY; y++) {
            if (Math.random() < currentLevel.getRightLeftSpawnProbability()*(y==dotY?3:1) && topBottomDots[gridX - 2][y] == 0) { // here's a dot
                topBottomDots[gridX - 1][y] = 1;
            }
        }
        for (int x = 0; x < gridX; x++) {
            if (Math.random() < currentLevel.getTopBottomSpawnProbability()*(x==dotX?3:1) && rightLeftDots[x][gridY - 2] == 0) { // here's a dot
                rightLeftDots[x][gridY - 1] = 1;
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

                // define points on screen by using the gamepad ...
                // batch.draw(gamepad, GdxGame.GAME_WIDTH-gamepad.getWidth()-dotSide, dotSide);
                float x0 = GdxGame.GAME_WIDTH - gamepad.getWidth() - dotSide;
                float y0 = dotSide;
                Vector3 north = new Vector3(x0 + gamepad.getWidth() / 2, y0 + gamepad.getHeight(), touch.z);
                Vector3 south = new Vector3(x0 + gamepad.getWidth() / 2, y0, touch.z);
                Vector3 west = new Vector3(x0, y0 + gamepad.getWidth() / 2, touch.z);
                Vector3 east = new Vector3(x0 + gamepad.getWidth(), y0 + gamepad.getWidth() / 2, touch.z);

                // find which is nearest.
                float[] d = new float[4];
                d[0] = touch.dst(north);
                d[1] = touch.dst(east);
                d[2] = touch.dst(south);
                d[3] = touch.dst(west);

                float min = d[0];
                int index = 0;
                for (int i = 1; i < d.length; i++) {
                    if (d[i] < min) {
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
                if (gameTime - gameOverTime > .75) // only if the explosion has been visible for at least 2 secs.
                    parentGame.getScreenManager().setCurrentState(ScreenManager.ScreenState.GameOver);
            }

        }
    }

    public void render(SpriteBatch batch) {
        for (int x = gridX - 1; x >= 0; x--) {
            for (int y = 0; y < gridY; y++) {
                if (bottomTopDots[x][y] > 0) { // paint the dot ...
                    batch.draw(verticalDot, x * dotSide, y * dotSide, dotSide, dotSide);
                    batch.setColor(1f, 1f, 1f, 0.33f);
                    batch.draw(verticalDot, x * dotSide - dotSide / 2, y * dotSide, dotSide, dotSide);
                    batch.setColor(1f, 1f, 1f, 1f);
                }
                if (leftRightDots[x][y] > 0) { // paint the dot ...
                    batch.draw(horizontalDot, x * dotSide, y * dotSide, dotSide, dotSide);
                    batch.setColor(1f, 1f, 1f, 0.33f);
                    batch.draw(horizontalDot, x * dotSide, y * dotSide - dotSide / 2, dotSide, dotSide);
                    batch.setColor(1f, 1f, 1f, 1f);
                }
                if (topBottomDots[x][y] > 0) { // paint the dot ...
                    batch.draw(verticalDot, x * dotSide, y * dotSide, dotSide, dotSide);
                    batch.setColor(1f, 1f, 1f, 0.33f);
                    batch.draw(verticalDot, x * dotSide + dotSide / 2, y * dotSide, dotSide, dotSide);
                    batch.setColor(1f, 1f, 1f, 1f);
                }
                if (rightLeftDots[x][y] > 0) { // paint the dot ...
                    batch.draw(horizontalDot, x * dotSide, y * dotSide, dotSide, dotSide);
                    batch.setColor(1f, 1f, 1f, 0.33f);
                    batch.draw(horizontalDot, x * dotSide, y * dotSide + dotSide / 2, dotSide, dotSide);
                    batch.setColor(1f, 1f, 1f, 1f);
                }
            }
        }

        if (!gameOver) batch.draw(mainDot, dotX * dotSide, dotY * dotSide, dotSide, dotSide);
        if (gameOver) { // draw particle effect ...
            xplode.setPosition(dotX * dotSide + dotSide / 2, dotY * dotSide + dotSide / 2);
            xplode.draw(batch, Gdx.graphics.getDeltaTime());
        }

        font.setColor(242f / 255, 159f / 255, 5f / 255, 1f);
        font.draw(batch, "Score: " + score, dotSide, dotSide);
        font.draw(batch, glyphLayout, GdxGame.GAME_WIDTH - dotSide - glyphLayout.width, dotSide);

        // las but not least: draw the gamepad:
        if (true || !Gdx.input.isPeripheralAvailable(Input.Peripheral.HardwareKeyboard)) {
            batch.setColor(1f, 1f, 1f, .3f);
            batch.draw(gamepad, GdxGame.GAME_WIDTH - gamepad.getWidth() - dotSide, dotSide);
            batch.setColor(1f, 1f, 1f, 1f);
        }
    }
}

class DodgeDotLevel {
    float tickTime; // the faster the merrier
    float leftRightSpawnProbability, rightLeftSpawnProbability;
    float bottomTopSpawnProbability, topBottomSpawnProbability;

    public DodgeDotLevel(float tickTime, float leftRightSpawnProbability, float rightLeftSpawnProbability, float bottomTopSpawnProbability, float topBottomSpawnProbability) {
        this.tickTime = tickTime;
        this.leftRightSpawnProbability = leftRightSpawnProbability;
        this.rightLeftSpawnProbability = rightLeftSpawnProbability;
        this.bottomTopSpawnProbability = bottomTopSpawnProbability;
        this.topBottomSpawnProbability = topBottomSpawnProbability;
    }

    public float getTickTime() {
        return tickTime;
    }

    public float getLeftRightSpawnProbability() {
        return leftRightSpawnProbability;
    }

    public float getBottomTopSpawnProbability() {
        return bottomTopSpawnProbability;
    }

    public float getRightLeftSpawnProbability() {
        return rightLeftSpawnProbability;
    }

    public float getTopBottomSpawnProbability() {
        return topBottomSpawnProbability;
    }
}
