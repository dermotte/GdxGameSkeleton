package at.juggle.gdx.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import at.juggle.gdx.GdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
//		config.width = 1280;
//		config.height = 720;
		 config.width = 1920;
		 config.height = 1080;
		new LwjglApplication(new GdxGame(), config);
	}
}
