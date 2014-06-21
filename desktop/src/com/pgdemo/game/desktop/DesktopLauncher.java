package com.pgdemo.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.pgdemo.game.PGDemo;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title="PeakGames-Demo";
		config.width=800;
		config.height=480;
		new LwjglApplication(new PGDemo(), config);
	}
}
