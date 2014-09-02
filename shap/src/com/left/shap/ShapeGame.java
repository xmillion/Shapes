package com.left.shap;

import static com.left.shap.util.Log.log;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.left.shap.screens.*;
import com.left.shap.services.*;

public class ShapeGame extends Game implements ApplicationListener {
	public static final boolean DEVMODE = true;

	private Screens nextScreen;
	private PreferenceManager preferenceManager;
	private MusicManager musicManager;
	private SoundManager soundManager;
	private Map<String, Object> globals;

	public static enum Screens {
		SPLASH, MENU, DRAW;
	}
	
	/**
	 * Navigate to the given screen.
	 * @param s
	 */
	public void navigateTo(Screens s) {
		nextScreen = s;
	}
	
	/**
	 * Navigate to the main menu to add a new high score.
	 * @param score new score entry
	 */
	public void navigateToMainMenu(double score) {
		nextScreen = Screens.MENU;
		globals.put("newscore", score);
	}
	
	/**
	 * It is safer to use setNextScreen(s);
	 */
	public void setScreen(Screens s) {
		AbstractScreen screen;
		switch(s) {
		case SPLASH:
			screen = new SplashScreen(this);
			break;
		case MENU:
			screen = new MainMenuScreen(this);
			break;
		case DRAW:
			screen = new DrawScreen(this);
			break;
		default:
			log("Screen not listed!");
			return;
		}
		assert (screen != null);
		setScreen(screen);
	}
	
	public PreferenceManager getPrefs() {
		return preferenceManager;
	}

	public MusicManager getJukebox() {
		if (musicManager == null) {
			musicManager = new MusicManager();
			musicManager.setEnabled(preferenceManager.isMusicOn());
			musicManager.setVolume(preferenceManager.getMusicVolume());
		}
		return musicManager;
	}

	public SoundManager getDeejay() {
		if (soundManager == null) {
			soundManager = new SoundManager();
			soundManager.setEnabled(preferenceManager.isSoundOn());
			soundManager.setVolume(preferenceManager.getSoundVolume());
		}
		return soundManager;
	}
	
	public Map<String, Object> getGlobals() {
		return globals;
	}

	@Override
	public void create() {
		globals = new HashMap<String, Object>();
		preferenceManager = new PreferenceManager();
		
		nextScreen = null;
		setScreen(Screens.SPLASH);
	}

	@Override
	public void render() {
		super.render();

		if(nextScreen != null) {
			setScreen(nextScreen);
			nextScreen = null;
		}
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void dispose() {
		super.dispose();
	}
	
	/**
	 * Let UI elements be scaled by this value.
	 * This allows buttons to be big enough to touch.
	 * 
	 * @return Scaling factor
	 */
	public static float getUIScaling() {
		switch(Gdx.app.getType()) {
		case Desktop:
		case WebGL:
		case Applet:
			// don't need to scale
			return 1f;
		case Android:
		case iOS:
			// decrease resolution to keep UI elements big enough
			float density = Gdx.graphics.getDensity();
			if(density < 1) {
				return 1f;
			} else if(density < 2) {
				return 0.5f;
			} else {
				// That's a nice screen you got there!
				return 0.25f;
			}
		default:
			// maybe there's a new supported platform :D
			log("You reached unreachable code");
			return 1f;
		}
	}
}
