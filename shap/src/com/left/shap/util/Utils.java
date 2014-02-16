package com.left.shap.util;

public class Utils {
	public static String coords(int x, int y) {
		return "(" + x + ", " + y + ")";
	}

	public static String pCoords(float x, float y) {
		return "(" + x + ", " + y + ")";
	}
	
	public static int min(int x, int y) {
		return x < y? x:y;
	}
	
	public static float min(float x, float y) {
		return x < y? x:y;
	}
	
	public static int max(int x, int y) {
		return x > y? x:y;
	}
	
	public static float max(float x, float y) {
		return x > y? x:y;
	}
	
	public static int between(int x, int min, int max) {
		return x < min? min: x > max? max: x;
	}
	
	// Is this supposed to be called "prune" ? rename it : delete this comment;
	public static float between(float x, float min, float max) {
		return x < min? min: x > max? max: x;
	}
	
	/**
	 * Checks if a coordinate is within a window.
	 * @param x Coordinate to check
	 * @param y Coordinate to check
	 * @param minX Top left corner of window
	 * @param minY Top left corner of window
	 * @param width Width of window
	 * @param height Height of window
	 * @return true if coordinate is inside the given window dimensions.
	 */
	public static boolean isWithin(int x, int y, int minX, int minY, int width, int height) {
		return minX <= x && x < minX + width && minY <= y && y < minY + height;
	}
}
