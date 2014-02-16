package com.left.shap.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class PreferenceManager {
	private final Preferences prefs;
	private static final String PREFS_NAME = "grid.prefs";
	private static final String SOUND_ON = "sound.on";
	private static final String SOUND_VOL = "sound.vol";
	private static final String MUSIC_ON = "music.on";
	private static final String MUSIC_VOL = "music.vol";
	
	public PreferenceManager() {
		prefs = Gdx.app.getPreferences(PREFS_NAME);
	}
	
	public void setSoundOn(boolean on) {
		prefs.putBoolean(SOUND_ON, on);
		prefs.flush();
	}
	
	public boolean isSoundOn() {
		return prefs.getBoolean(SOUND_ON, true);
	}

	public void setSoundVolume(float volume) {
		prefs.putFloat(SOUND_VOL, volume);
		prefs.flush();
	}
	
	public float getSoundVolume() {
		return prefs.getFloat(SOUND_VOL, 1f);
	}
	
	public void setMusicOn(boolean on) {
		prefs.putBoolean(MUSIC_ON, on);
		prefs.flush();
	}
	
	public boolean isMusicOn() {
		return prefs.getBoolean(MUSIC_ON, true);
	}
	
	public void setMusicVolume(float volume) {
		prefs.putFloat(MUSIC_VOL, volume);
		prefs.flush();
	}
	
	public float getMusicVolume() {
		return prefs.getFloat(MUSIC_VOL, 1f);
	}
	
	public void clearPrefs() {
		prefs.clear();
		prefs.flush();
	}
}
