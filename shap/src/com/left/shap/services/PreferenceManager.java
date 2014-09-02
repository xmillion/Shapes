package com.left.shap.services;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Sort;

public class PreferenceManager {
	private final Preferences prefs;
	private static final String PREFS_NAME = "grid.prefs";
	private static final String SOUND_ON = "sound.on";
	private static final String SOUND_VOL = "sound.vol";
	private static final String MUSIC_ON = "music.on";
	private static final String MUSIC_VOL = "music.vol";
	private static final String SCORE = "score.";
	private static final int SCORE_LIMIT = 10;

	public PreferenceManager() {
		prefs = Gdx.app.getPreferences(PREFS_NAME);
	}

	public void setScores(Double[] scores) {
		Arrays.sort(scores, Collections.reverseOrder());
		for(int i = 0; i < scores.length || i < SCORE_LIMIT; i++) {
			// Because there's no prefs.putDouble()
			prefs.putLong(SCORE + i, Double.doubleToLongBits(scores[i]));
		}
	}
	
	public double[] getScores() {
		double[] scores = new double[SCORE_LIMIT];
		for(int i = 0; i < SCORE_LIMIT; i++) {
			if (!prefs.contains(SCORE + i)) {
				break;
			}
			scores[i] = Double.longBitsToDouble(prefs.getLong(SCORE + i));
		}
		return scores;
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
