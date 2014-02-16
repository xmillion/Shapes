package com.left.shap.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.left.shap.util.Res;

public class MusicManager {

	public enum GridMusic {
		MENU("3kuloop.wav"),
		MENU_ALT("3kloop.wav");
		
		private String fileName;
		private GridMusic(String fileName) {
			this.fileName = fileName;
		}
		public String getFileName() {
			return Res.MUSIC + fileName;
		}
	}
	
	private boolean enabled = true;
	private float volume = 1f;
	/** Currently playing music (there can only be one) */
	private GridMusic currentMusic;
	private Music musicResource;
	
	public MusicManager() {
	}
	
	public void play(GridMusic music) {
		if(!enabled) return;
		
		// Don't need to restart the same music
		if(currentMusic == music) return;
		
		// stop current music
		stop();
		
		// Stream the new music
		FileHandle musicFile = Gdx.files.internal(music.getFileName());
		Music resource = Gdx.audio.newMusic(musicFile);
		resource.setVolume(volume);
		resource.setLooping(true);
		resource.play();
		
		this.currentMusic = music;
		this.musicResource = resource;
	}

	private void stop() {
		if(musicResource != null) {
			this.musicResource.stop();
			this.musicResource.dispose();
			this.musicResource = null;
            this.currentMusic = null;
		}
	}
	
	public void setVolume(float volume) {
		if(volume < 0 || volume > 1f) {
			throw new IllegalArgumentException("Volume must be in the range [0,1]");
		}
		this.volume = volume;
		
		// immediate effect
		if(musicResource != null && enabled) {
			this.musicResource.setVolume(volume);
		}
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		
		// immediate effect
		if(musicResource != null) {
			if(enabled) {
				this.musicResource.setVolume(volume);
			} else {
				this.musicResource.setVolume(0f);
			}
		}
	}
	
	public void dispose() {
		stop();
	}
}
