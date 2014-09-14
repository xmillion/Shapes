package com.left.shap.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Sort;
import com.left.shap.model.Score;

public class PreferenceManager {
	private final Preferences prefs;
	private static final String PREFS_NAME = "grid.prefs";
	private static final String SOUND_ON = "sound.on";
	private static final String SOUND_VOL = "sound.vol";
	private static final String MUSIC_ON = "music.on";
	private static final String MUSIC_VOL = "music.vol";
	private static final String NAME = "name.";
	private static final String SCORE = "score.";
	public static final int SCORE_LIMIT = 10;

	public PreferenceManager() {
		prefs = Gdx.app.getPreferences(PREFS_NAME);
	}

	/**
	 * Be aware of SCORE_LIMIT when setting scores.
	 * 
	 * @param scores
	 */
	public void setScores(Score[] scores) {
		Arrays.sort(scores, Collections.reverseOrder());
		for(int i = 0; i < scores.length || i < SCORE_LIMIT; i++) {
			prefs.putString(NAME + i, scores[i].getName());
			// Because there's no prefs.putDouble()
			prefs.putLong(SCORE + i, Double.doubleToLongBits(scores[i].getScore()));
		}
	}

	/**
	 * Retrieves the list of scores, sorted in descending order.
	 * 
	 * @return
	 */
	public List<Score> getScores() {
		List<Score> scores = new ArrayList<Score>(SCORE_LIMIT);
		int i;
		for(i = 0; i < SCORE_LIMIT; i++) {
			if(!prefs.contains(NAME + i)) {
				break;
			}
			String name = prefs.getString(NAME + i);
			double score = Double.longBitsToDouble(prefs.getLong(SCORE + i));
			scores.add(new Score(name, score));
		}
		// They were saved in descending order, so no sorting needed.
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
