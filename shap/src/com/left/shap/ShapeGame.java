package com.left.shap;

import static com.left.shap.util.Log.log;

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

	public static enum Screens {
		SPLASH, MAINMENU, SCORE, DRAW;
	}
	
	public void setNextScreen(Screens s) {
		nextScreen = s;
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
		case MAINMENU:
			screen = new MainMenuScreen(this);
			break;
		case SCORE:
			screen = new ScoreScreen(this);
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
		return musicManager;
	}

	public SoundManager getDeejay() {
		return soundManager;
	}

	@Override
	public void create() {
		log("Create Game");
		
		preferenceManager = new PreferenceManager();
		
		//gridSerializer = new GridSerializer();

		musicManager = new MusicManager();
		musicManager.setEnabled(preferenceManager.isMusicOn());
		musicManager.setVolume(preferenceManager.getMusicVolume());

		soundManager = new SoundManager();
		soundManager.setEnabled(preferenceManager.isSoundOn());
		soundManager.setVolume(preferenceManager.getSoundVolume());

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
