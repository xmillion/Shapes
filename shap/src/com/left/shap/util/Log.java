package com.left.shap.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class Log {
	private static final String TAG_MAIN = "Shap";

	public static void l(String message) {
		log(TAG_MAIN, message);
	}
	public static void log(String message) {
		log(TAG_MAIN, message);
	}
	public static void log(String tag, String message) {
		Gdx.app.log(tag, message);
	}
	
	public static String pCoords(float x, float y) {
		return "(" + x + ", " + y + ")";
	}
	
	public static String pCoords(Vector2 v) {
		return "(" + v.x + ", " + v.y + ")";
	}
	
	public static String pCoords(int x, int y) {
		return "(" + x + ", " + y + ")";
	}
}
