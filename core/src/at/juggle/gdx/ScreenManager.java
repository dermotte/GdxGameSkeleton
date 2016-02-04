package at.juggle.gdx;

import com.badlogic.gdx.Screen;

import at.juggle.gdx.screens.CreditsScreen;
import at.juggle.gdx.screens.LoadingScreen;
import at.juggle.gdx.screens.MenuScreen;

/**
 * Created by Mathias Lux, mathias@juggle.at, on 04.02.2016.
 */
public class ScreenManager {
    public enum ScreenState {Loading, Menu, Game, Credits, GameOver};
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
            } else if (state == ScreenState.Credits) {
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
