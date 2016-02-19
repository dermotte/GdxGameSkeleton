package at.juggle.gdx;

import com.badlogic.gdx.Screen;

import at.juggle.gdx.screens.CreditsScreen;
import at.juggle.gdx.screens.DodgeDotGameScreen;
import at.juggle.gdx.screens.GameOverScreen;
import at.juggle.gdx.screens.LoadingScreen;
import at.juggle.gdx.screens.MenuScreen;

/**
 * Created by Mathias Lux, mathias@juggle.at, on 04.02.2016.
 */
public class ScreenManager {
    public enum ScreenState {Loading, Menu, Game, Credits, Help, GameOver}

    ;
    private Screen currentScreen;
    private ScreenState currentState;
    private GdxGame parentGame;

    public ScreenManager(GdxGame game) {
        this.parentGame = game;
        currentScreen = new LoadingScreen(game);
        currentState = ScreenState.Loading;
    }

    public Screen getCurrentScreen() {
        return currentScreen;
    }

    public ScreenState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(ScreenState state) {
        if (state != currentState) { // only if state changes.
            currentState = state;
            if (state == ScreenState.Menu) {
                currentScreen = new MenuScreen(parentGame);
            } else if (state == ScreenState.Game) {
                currentScreen = new DodgeDotGameScreen(parentGame);
                parentGame.getSoundManager().startSong("main"); // starts the main theme.
            } else if (state == ScreenState.GameOver) {
                currentScreen = new GameOverScreen(parentGame);
            } else if (state == ScreenState.Credits) {
                parentGame.getSoundManager().fadeOut(); // fade out music ...
                currentScreen = new CreditsScreen(parentGame);
            }
        }
    }

    public GdxGame getParentGame() {
        return parentGame;
    }

    public void setParentGame(GdxGame parentGame) {
        this.parentGame = parentGame;
    }
}
