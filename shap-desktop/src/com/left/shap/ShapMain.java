package com.left.shap;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.left.shap.ShapeGame;

public class ShapMain {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "shap";
		cfg.useGL20 = false;
		cfg.width = 800;
		cfg.height = 600;
		cfg.resizable = true;
		
		new LwjglApplication(new ShapeGame(), cfg);
	}
}
