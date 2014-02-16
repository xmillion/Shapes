package com.left.shap.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.left.shap.ShapeGame;
import com.left.shap.ShapeGame.Screens;
import com.left.shap.services.MusicManager;
import com.left.shap.services.MusicManager.GridMusic;
import com.left.shap.services.PreferenceManager;
import com.left.shap.services.SoundManager;
import com.left.shap.services.SoundManager.GridSound;
import com.left.shap.util.DefaultButtonListener;
import com.left.shap.screens.AbstractMenuScreen;

public class OptionsScreen extends AbstractMenuScreen {
	
	public OptionsScreen(ShapeGame game) {
		super(game);
	}
	
	@Override
	public void show() {
		super.show();
		final Skin skin = getSkin();
		final PreferenceManager prefs = game.getPrefs();
		final MusicManager music = game.getJukebox();
		final SoundManager sound = game.getDeejay();
		
		Label title = new Label("Options", skin);
		title.setAlignment(Align.center, Align.center);
		
		final CheckBox soundCheckbox = new CheckBox("Sound", skin);
		soundCheckbox.setChecked(prefs.isSoundOn());
		soundCheckbox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				boolean on = soundCheckbox.isChecked();
				prefs.setSoundOn(on);
				sound.setEnabled(on);
				if(on) sound.play(GridSound.TEST);
			}
		});
		
		final Slider soundSlider = new Slider(0f, 1f, 0.01f, false, skin);
		soundSlider.setValue(prefs.getSoundVolume());
		soundSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				float volume = soundSlider.getValue();
				prefs.setSoundVolume(volume);
				sound.setVolume(volume);
				sound.play(GridSound.TEST);
			}
		});

		final CheckBox musicCheckbox = new CheckBox("Music", skin);
		musicCheckbox.setChecked(prefs.isMusicOn());
		musicCheckbox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				boolean on = musicCheckbox.isChecked();
				prefs.setMusicOn(on);
				music.setEnabled(on);
				if(on) music.play(GridMusic.MENU);
			}
		});
		
		final Slider musicSlider = new Slider(0f, 1f, 0.01f, false, skin);
		musicSlider.setValue(prefs.getMusicVolume());
		musicSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				float volume = musicSlider.getValue();
				prefs.setMusicVolume(volume);
				music.setVolume(volume);
			}
		});
		
		TextButton resetButton = new TextButton("Reset Data", skin);
		resetButton.addListener(new DefaultButtonListener() {
			@Override
			public void pressed(InputEvent event, float x, float y, int pointer, int button) {
				game.getDeejay().play(GridSound.CLICK);
				prefs.clearPrefs();
				soundCheckbox.setChecked(prefs.isSoundOn());
				soundSlider.setValue(prefs.getSoundVolume());
				musicCheckbox.setChecked(prefs.isMusicOn());
				musicSlider.setValue(prefs.getMusicVolume());
			}
		});
		
		TextButton backButton = new TextButton("Back", skin);
		backButton.addListener(new DefaultButtonListener() {
			@Override
			public void pressed(InputEvent event, float x, float y, int pointer, int button) {
				game.getDeejay().play(GridSound.CLICK);
				game.setNextScreen(Screens.MAINMENU);
			}
		});
		
		Table table = super.getTable();
		table.add(title).colspan(3).fill().spaceBottom(SPACING);
		table.row();
		table.add(soundCheckbox).align(Align.left).spaceTop(SPACING);
		table.add(soundSlider).width(2*BUTTON_WIDTH).colspan(2).fill();
		table.row();
		table.add(musicCheckbox).align(Align.left).spaceTop(SPACING);
		table.add(musicSlider).width(2*BUTTON_WIDTH).colspan(2).fill();
		table.row();
		table.add(resetButton).size(BUTTON_WIDTH, BUTTON_HEIGHT).uniform().fill().spaceTop(SPACING);
		table.add();
		table.add(backButton).size(BUTTON_WIDTH, BUTTON_HEIGHT).uniform().fill().spaceTop(SPACING);
		table.pack();
		
		stage.addActor(table);
		game.getJukebox().play(GridMusic.MENU);
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		width *= ShapeGame.getUIScaling();
		height *= ShapeGame.getUIScaling();
		Table table = super.getTable();
		table.setPosition((width - table.getWidth())/2, (height - table.getHeight())/2);
	}
}
