package at.juggle.gdx;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import at.juggle.gdx.sound.SoundSync;

/**
 * Created by mlux on 17.02.2016.
 * todo last man standing
 */
public class DodgeDotGameModel implements SoundSync {
    private boolean debug = false; // shows gamepad.
    // size of the grid on x-axis, y-axis has size 16:9
    private final int gridX = 40;
    private final int gridY = 23;

    // data model
    private int[][] bottomTopDots = new int[gridX][gridY];
    private int[][] leftRightDots = new int[gridX][gridY];
    private int[][] topBottomDots = new int[gridX][gridY];
    private int[][] rightLeftDots = new int[gridX][gridY];
    private int dotXone = gridX / 2, dotYone = gridY / 2; // one player.
    private int dotXtwo = gridX / 3, dotYtwo = 2 * gridY / 3; // the other player.

    // time:
    float lastTick = 0f;
    float gameTime = 0f;
    float gameOverTime = 0f;
    private boolean gameOver = false;
    Vector2 movePlayerOne = new Vector2(0f, 0f);
    Vector2 movePlayerTwo = new Vector2(0f, 0f);


    public static int score = 0;
    public static boolean twoPlayerMode = true;
    public static int playerWon = 0;
    public static boolean wallMode = false;


    // for rendering:
    Texture mainDot, battleDot, horizontalDot, verticalDot, gamepadTexture;
    BitmapFont font;
    GdxGame parentGame;
    OrthographicCamera camera;

    ParticleEffect xplodeMain, xplodeTwo;

    DodgeDotsGamepadInput gamepadPlayerOne, gamepadPlayerTwo;

    // for drawing:
    private float dotSide = GdxGame.GAME_WIDTH / gridX;
    private GlyphLayout glyphLayout = new GlyphLayout();

    // level config ...
    DodgeDotLevel[] levels = new DodgeDotLevel[]{
            new DodgeDotLevel(1f / 2.1666666f, 0f, 0f, 0.02f, 0f),
            new DodgeDotLevel(1f / 4.3333333f, 0f, 0f, 0.02f, 0f),
            new DodgeDotLevel(1f / 2.1666666f, 0f, 0f, 0.02f, 0.02f),
            new DodgeDotLevel(1f / 4.3333333f, 0f, 0f, 0.02f, 0.02f),
            new DodgeDotLevel(1f / 4.3333333f, 0f, 0f, 0.03f, 0.02f),
            new DodgeDotLevel(1f / 4.3333333f, 0.01f, 0f, 0.02f, 0.02f),
            new DodgeDotLevel(1f / 2.1666666f, 0.01f, 0.01f, 0.02f, 0.02f),
            new DodgeDotLevel(1f / 2.1666666f, 0.02f, 0.01f, 0.02f, 0.02f),
            new DodgeDotLevel(1f / 2.1666666f, 0.02f, 0.02f, 0.02f, 0.02f),
            new DodgeDotLevel(1f / 4.3333333f, 0.03f, 0.03f, 0f, 0f),
            new DodgeDotLevel(1f / 4.3333333f, 0.035f, 0.035f, 0f, 0f),
            new DodgeDotLevel(1f / 4.3333333f, 0.01f, 0.01f, 0.01f, 0.01f),
            new DodgeDotLevel(1f / 4.3333333f, 0f, 0f, 0.03f, 0.03f),
            new DodgeDotLevel(1f / 4.3333333f, 0f, 0f, 0.035f, 0.035f),
            new DodgeDotLevel(1f / 4.3333333f, 0.02f, 0.02f, 0.035f, 0.035f),
            new DodgeDotLevel(1f / 4.3333333f, 0.03f, 0.03f, 0.03f, 0.03f),
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

    public DodgeDotGameModel(GdxGame parentGame, OrthographicCamera cam) {
        init(parentGame, cam);
    }

    public void init(GdxGame parentGame, OrthographicCamera cam) {
        mainDot = parentGame.getAssetManager().get("game/dot_green.png", Texture.class);
        battleDot = parentGame.getAssetManager().get("game/dot_purple.png", Texture.class);
        horizontalDot = parentGame.getAssetManager().get("game/dot_orange.png", Texture.class);
        verticalDot = parentGame.getAssetManager().get("game/dot_yellow.png", Texture.class);
        font = parentGame.getAssetManager().get("menu/Homespun_42.fnt", BitmapFont.class);
        gamepadTexture = parentGame.getAssetManager().get("game/gamepad_400.png", Texture.class);
        this.parentGame = parentGame;
        parentGame.getSoundManager().setSoundSync(this);
        camera = cam;
        parentGame.getAssetManager().load("", ParticleEffect.class);
        xplodeMain = new ParticleEffect();
        xplodeMain.load(Gdx.files.getFileHandle("game/xplode.particle", Files.FileType.Internal), Gdx.files.getFileHandle("game", Files.FileType.Internal));
        xplodeTwo = new ParticleEffect();
        xplodeTwo.load(Gdx.files.getFileHandle("game/xplode_two.particle", Files.FileType.Internal), Gdx.files.getFileHandle("game", Files.FileType.Internal));

        gamepadPlayerOne = new DodgeDotsGamepadInput(GdxGame.GAME_WIDTH - gamepadTexture.getWidth() - dotSide * 2, dotSide * 2, gamepadTexture.getWidth(), gamepadTexture.getHeight());
        gamepadPlayerTwo = new DodgeDotsGamepadInput(dotSide * 2, GdxGame.GAME_HEIGHT - gamepadTexture.getHeight() - dotSide * 2, gamepadTexture.getWidth(), gamepadTexture.getHeight());

        if (twoPlayerMode) {
            dotXone = 2 * gridX / 3;
            dotYone = gridY / 3;
        }
        playerWon = 0;
        score = 0;
    }

    public void compute(float delta) {
        handleInput();
        if(delta<0.02f)
            delta = 0.02f
        // check for crash ...
        if (!gameOver) {
            if (bottomTopDots[dotXone][dotYone] > 0 || leftRightDots[dotXone][dotYone] > 0 || topBottomDots[dotXone][dotYone] > 0 || rightLeftDots[dotXone][dotYone] > 0) {
                // it's a crash!
                gameOver = true;
                gameOverTime = gameTime;
                parentGame.getSoundManager().playEvent("explode");
                parentGame.getSoundManager().fadeOut();
                xplodeMain.start();
                playerWon = 2;
            }

            // check for Player two ....
            if (twoPlayerMode && (bottomTopDots[dotXtwo][dotYtwo] > 0 || leftRightDots[dotXtwo][dotYtwo] > 0 || topBottomDots[dotXtwo][dotYtwo] > 0 || rightLeftDots[dotXtwo][dotYtwo] > 0)) {
                // it's a crash!
                gameOver = true;
                gameOverTime = gameTime;
                parentGame.getSoundManager().playEvent("explode");
                parentGame.getSoundManager().fadeOut();
                xplodeTwo.start();
                if (playerWon == 0) playerWon = 1;
                else playerWon = 0;
            }
        }
        // timing:
        gameTime += delta; // new time
        if (gameTime - lastTick < currentLevel.getTickTime()) { // return if not a tick!
            return;
        }
        // go into game over after 3 seconds.
        if (gameOver && Math.abs(gameTime - gameOverTime) > 3)
            gameOver();
        // OK, it's a tick!
        lastTick += currentLevel.getTickTime();

        // don't move if game is over ..
        if (gameOver) {
            return;
        }

        // increment score
        score = (int) Math.floor(gameTime * 13);

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
            if (Math.random() < currentLevel.getLeftRightSpawnProbability() * (y == dotYone ? 3 : 1) && bottomTopDots[1][y] == 0) { // here's a dot
                bottomTopDots[0][y] = 1;
            }
        }
        for (int x = 0; x < gridX; x++) {
            if (Math.random() < currentLevel.getBottomTopSpawnProbability() * (x == dotXone ? 3 : 1) && leftRightDots[x][1] == 0) { // here's a dot
                leftRightDots[x][0] = 1;
            }
        }
        for (int y = 0; y < gridY; y++) {
            if (Math.random() < currentLevel.getRightLeftSpawnProbability() * (y == dotYone ? 3 : 1) && topBottomDots[gridX - 2][y] == 0) { // here's a dot
                topBottomDots[gridX - 1][y] = 1;
            }
        }
        for (int x = 0; x < gridX; x++) {
            if (Math.random() < currentLevel.getTopBottomSpawnProbability() * (x == dotXone ? 3 : 1) && rightLeftDots[x][gridY - 2] == 0) { // here's a dot
                rightLeftDots[x][gridY - 1] = 1;
            }
        }
    }

    private void gameOver() {
        parentGame.getScreenManager().setCurrentState(ScreenManager.ScreenState.GameOver);
    }

    private void handleInput() {
        movePlayerOne.x=0;
        movePlayerOne.y=0;
        movePlayerTwo.x=0;
        movePlayerTwo.y=0;
        if (!gameOver) {
            // single player dot.
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                movePlayerOne.y = -1;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                movePlayerOne.y = 1;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                movePlayerOne.x = -1;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                movePlayerOne.x = 1;
            }

            if (twoPlayerMode) {
                // second dot.
                if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                    movePlayerTwo.y = -1;
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
                    movePlayerTwo.y = 1;
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
                    movePlayerTwo.x = -1;

                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
                    movePlayerTwo.x = 1;
                }
            }

            if (Gdx.input.justTouched()) {
                // touch from the left lower corner.
                Vector3 touch = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 1));
                switch (gamepadPlayerOne.handleTouch(touch)) {
                    case Up:
                        movePlayerOne.y = 1;
                        break;
                    case Down:
                        movePlayerOne.y = -1;
                        break;
                    case Left:
                        movePlayerOne.x = -1;
                        break;
                    case Right:
                        movePlayerOne.x = 1;
                        break;
                }
                if (twoPlayerMode) {
                    switch (gamepadPlayerTwo.handleTouch(touch)) {
                        case Up:
                            movePlayerTwo.y = 1;
                            break;
                        case Down:
                            movePlayerTwo.y = -1;
                            break;
                        case Left:
                            movePlayerTwo.x = -1;
                            break;
                        case Right:
                            movePlayerTwo.x = 1;
                            break;
                    }
                }

                // now move ...
//                    switch (index) {
//                        case 0:
//                            //                        System.out.println("north");
//                            dotYone += 1;
//                            break;
//                        case 1:
//                            //                        System.out.println("east");
//                            dotXone += 1;
//                            break;
//                        case 2:
//                            //                        System.out.println("south");
//                            dotYone -= 1;
//                            break;
//                        case 3:
//                            //                        System.out.println("west");
//                            dotXone -= 1;
//                            break;

            }

            dotXone += movePlayerOne.x;
            dotYone += movePlayerOne.y;
            if (twoPlayerMode) {
                dotXtwo += movePlayerTwo.x;
                dotYtwo += movePlayerTwo.y;
            }

            if (twoPlayerMode && wallMode && dotYone == dotYtwo && dotXone == dotXtwo) {
                movePlayerTwo.scl(-1f);
                movePlayerOne.scl(-1f);
                dotXone += movePlayerOne.x;
                dotYone += movePlayerOne.y;
                dotXtwo += movePlayerTwo.x;
                dotYtwo += movePlayerTwo.y;
            }

            if (!wallMode && twoPlayerMode && dotYone == dotYtwo && dotXone == dotXtwo) {
                if (movePlayerTwo.len2()>0) {
                    dotXone += movePlayerTwo.x;
                    dotYone += movePlayerTwo.y;
                } else if (movePlayerOne.len2()>0) {
                    dotXtwo += movePlayerOne.x;
                    dotYtwo += movePlayerOne.y;
                }
            }


            // see to it that the dot does not go away.
            dotYone %= gridY;
            dotXone %= gridX;
            if (dotXone < 0) dotXone = gridX + dotXone;
            if (dotYone < 0) dotYone = gridY + dotYone;

            if (twoPlayerMode) {
                dotYtwo %= gridY;
                dotXtwo %= gridX;
                if (dotXtwo < 0) dotXtwo = gridX + dotXtwo;
                if (dotYtwo < 0) dotYtwo = gridY + dotYtwo;
            }


        } else {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) || Gdx.input.justTouched()) {
                if (gameTime - gameOverTime > .75) // only if the explosion has been visible for at least 2 secs.
                    gameOver();
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

        // draw dot/s
        if (!gameOver) {
            batch.draw(mainDot, dotXone * dotSide, dotYone * dotSide, dotSide, dotSide);
            if (twoPlayerMode) {
                batch.draw(battleDot, dotXtwo * dotSide, dotYtwo * dotSide, dotSide, dotSide);
            }
        }
        if (gameOver) { // draw particle effect ...
            xplodeMain.setPosition(dotXone * dotSide + dotSide / 2, dotYone * dotSide + dotSide / 2);
            xplodeMain.draw(batch, Gdx.graphics.getDeltaTime());
        }

        if (gameOver && twoPlayerMode) { // draw particle effect ...
            xplodeTwo.setPosition(dotXtwo * dotSide + dotSide / 2, dotYtwo * dotSide + dotSide / 2);
            xplodeTwo.draw(batch, Gdx.graphics.getDeltaTime());
            if (playerWon == 1) batch.draw(mainDot, dotXone * dotSide, dotYone * dotSide, dotSide, dotSide);
            if (playerWon == 2) batch.draw(battleDot, dotXtwo * dotSide, dotYtwo * dotSide, dotSide, dotSide);
        }

        font.setColor(242f / 255, 159f / 255, 5f / 255, 1f);
        font.draw(batch, "Score: " + score, dotSide, dotSide);
        font.draw(batch, glyphLayout, GdxGame.GAME_WIDTH - dotSide - glyphLayout.width, dotSide);

        // las but not least: draw the gamepad:
        if (debug || !Gdx.input.isPeripheralAvailable(Input.Peripheral.HardwareKeyboard)) {
            // main gamepad, right bottom corner
            batch.setColor(132f / 255, 191f / 255, 4f / 255, .3f);
            batch.draw(gamepadTexture, gamepadPlayerOne.getX(), gamepadPlayerOne.getY());
            batch.setColor(1f, 1f, 1f, 1f);

            if (twoPlayerMode) {
                // second player left upper corner
                batch.setColor(132f / 255, 191f / 255, 4f / 255, .3f);
                batch.draw(gamepadTexture, gamepadPlayerTwo.getX(), gamepadPlayerTwo.getY());
                batch.setColor(1f, 1f, 1f, 1f);
            }
        }
    }

    @Override
    public void sync() {
        if (debug) System.out.println("syncing sound now.");
        lastTick = gameTime;
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
