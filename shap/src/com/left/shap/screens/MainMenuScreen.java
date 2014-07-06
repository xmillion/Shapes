package com.left.shap.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.left.shap.util.Log.log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.left.shap.ShapeGame;
import com.left.shap.ShapeGame.Screens;
import com.left.shap.services.MusicManager.GridMusic;
import com.left.shap.services.SoundManager.GridSound;
import com.left.shap.util.DefaultButtonListener;

/**
 * Main Menu.
 * TableLayout Editor (Java Web-Start):
 * http://table-layout.googlecode.com/svn/wiki/jws/editor.jnlp
 *
 */
public class MainMenuScreen extends AbstractMenuScreen {

	public MainMenuScreen(ShapeGame game) {
		super(game);
	}

	@Override
	public void show() {
		super.show();
		Skin skin = super.getSkin();
		
		Label title = new Label("Shap", getSkin());
		title.setAlignment(Align.center, Align.center);
		TextButton startButton = new TextButton("Draw", skin);
		startButton.addListener(new DefaultButtonListener() {
			@Override
			public void pressed(InputEvent event, float x, float y, int pointer, int button) {
				game.getDeejay().play(GridSound.CLICK);
				stage.getRoot().addAction(sequence(fadeOut(0.15f), run(new Runnable() {
					public void run() {
						game.setNextScreen(Screens.DRAW);
					}
				})));
			}
		});
		TextButton scoreButton = new TextButton("Score", skin);
		scoreButton.addListener(new DefaultButtonListener() {
			@Override
			public void pressed(InputEvent event, float x, float y, int pointer, int button) {
				game.getDeejay().play(GridSound.CLICK);
				game.setNextScreen(Screens.SCORE);
			}
		});
		TextButton exitButton = new TextButton("Exit", skin);
		exitButton.addListener(new DefaultButtonListener() {
			@Override
			public void pressed(InputEvent event, float x, float y, int pointer, int button) {
				log("Goodbye");
				game.getDeejay().play(GridSound.CLICK);
				stage.getRoot().addAction(sequence(fadeOut(0.1f), run(new Runnable() {
					public void run() {
						Gdx.app.exit();
					}
				})));
			}
		});
		
		Table table = super.getTable();
		table.add(title).size(BUTTON_WIDTH, BUTTON_HEIGHT).spaceBottom(SPACING);
		table.row();
		table.add(startButton).size(BUTTON_WIDTH, BUTTON_HEIGHT).uniform().fill().spaceBottom(SPACING);
		table.row();
		table.add(scoreButton).size(BUTTON_WIDTH, BUTTON_HEIGHT).uniform().fill().spaceBottom(SPACING);
		table.row();
		table.add(exitButton).size(BUTTON_WIDTH, BUTTON_HEIGHT).uniform().fill().spaceBottom(SPACING);
		table.pack();

		game.getJukebox().play(GridMusic.MENU);
		stage.getRoot().getColor().a = 0f;
		stage.getRoot().addAction(fadeIn(0.25f));
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		width *= ShapeGame.getUIScaling();
		height *= ShapeGame.getUIScaling();
		Table table = super.getTable();
		table.setPosition(width - table.getWidth() - SPACING, height - table.getHeight() - SPACING);
	}
}
