package at.juggle.gdx.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.backends.gwt.GwtGraphics;

import at.juggle.gdx.GdxGame;

public class HtmlLauncher extends GwtApplication {

    @Override
    public GwtApplicationConfiguration getConfig() {
        GwtApplicationConfiguration gwtApplicationConfiguration = new GwtApplicationConfiguration(1280, 720);
        gwtApplicationConfiguration.fullscreenOrientation = GwtGraphics.OrientationLockType.LANDSCAPE;
        gwtApplicationConfiguration.antialiasing = true;
        return gwtApplicationConfiguration;
    }

    @Override
    public ApplicationListener createApplicationListener() {
        return new GdxGame();
    }
}