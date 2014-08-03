package com.left.shap.screens;

import static com.badlogic.gdx.math.Interpolation.pow2In;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.left.shap.util.Log.log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.left.shap.ShapeGame;
import com.left.shap.ShapeGame.Screens;
import com.left.shap.model.ResetPositionAction;
import com.left.shap.services.MusicManager.GridMusic;
import com.left.shap.services.SoundManager.GridSound;
import com.left.shap.util.DefaultButtonListener;
import com.left.shap.util.Res;
import com.left.shap.util.Utils;

/**
 * Main Menu.
 * TableLayout Editor (Java Web-Start):
 * http://table-layout.googlecode.com/svn/wiki/jws/editor.jnlp
 *
 */
public class MainMenuScreen extends AbstractScreen {
	
	// UI
	protected static final int BUTTON_WIDTH = 128;
	protected static final int BUTTON_HEIGHT = 32;
	protected static final float SPACING = 10f;
	private Texture buttonTexture;
	
	protected final Stage stage;
	public static enum MenuState {
		MAINMENU, MAINSCORES, DRAWSCORES
	}
	private MenuState currentState;
	private Table mainMenuTable;
	private boolean isMainMenuLoaded = false;
	private Table scoreMenuTable;
	private boolean isScoreMenuLoaded = false;
	
	// Falling Shaps
	private Texture[] shapAssets;
	private Image[] fallingShaps;
	private static final float SHAP_LENGTH = 64;
	private static final float MAX_DURATION = Gdx.graphics.getHeight() / SHAP_LENGTH / 2;
	private static final float MAX_DELAY = 9;
	private static final float INIT_SPEED_CAP = 2;
	
	public MainMenuScreen(ShapeGame game) {
		this(game, MenuState.MAINMENU);
	}

	public MainMenuScreen(ShapeGame game, MenuState state) {
		super(game);
		this.currentState = state;
		
		// Load UI
		buttonTexture = new Texture(Res.BUTTONS);
		stage = new Stage();
		mainMenuTable = new Table();
		scoreMenuTable = new Table();
		if(ShapeGame.DEVMODE) {
			mainMenuTable.debug();
			scoreMenuTable.debug();
		}
		stage.addActor(mainMenuTable);
		stage.addActor(scoreMenuTable);
		
		// Load background
		shapAssets = new Texture[Res.SHAPS];
		for(int i = 0; i < Res.SHAPS; i++) {
			shapAssets[i] = new Texture(Res.SHAPASSETS + "shap" + i + ".png");
		}
		
		int copies = (int) ((Gdx.graphics.getWidth() / SHAP_LENGTH) * 6);
		fallingShaps = new Image[copies];
		for(int i = 0; i < copies; i++) {
			fallingShaps[i] = new Image(shapAssets[i % shapAssets.length]);
			fallingShaps[i].setScaling(Scaling.none);
			fallingShaps[i].setBounds(0, -SHAP_LENGTH, SHAP_LENGTH, SHAP_LENGTH);
			fallingShaps[i].addAction(fallingShapAction());
			fallingShaps[i].setZIndex(0);
			stage.addActor(fallingShaps[i]);
		}
		mainMenuTable.setZIndex(100);
		scoreMenuTable.setZIndex(100);
	}

	private static Action fallingShapAction() {
		final float height = Gdx.graphics.getHeight();
		float duration = MAX_DURATION - (Utils.random.nextFloat() * INIT_SPEED_CAP);
		float delay = Utils.random.nextFloat() * MAX_DELAY;
		
		return sequence(delay(delay), forever(sequence(ResetPositionAction.resetAction(height), moveBy(0, -height - 128, duration, pow2In))));
	}
	
	public void setState(MenuState state) {
		switch(state) {
		case MAINMENU:
			loadMainMenu();
			mainMenuTable.setVisible(true);
			scoreMenuTable.setVisible(false);
			break;
		case MAINSCORES:
		case DRAWSCORES:
			// TODO difference between MAINSCORES and DRAWSCORES
			// MAINSCORES is score menu when accessed by main menu. There is no "Again?" button here.
			// DRAWSCORES is score menu when accessed after drawing. This adds a submit row to the high scores.
			loadScoreMenu();
			mainMenuTable.setVisible(false);
			scoreMenuTable.setVisible(true);
			break;
		}
	}
	
	private void loadMainMenu() {
		if (!isMainMenuLoaded) {
			Image mainTitle = new Image(new Texture(Res.TITLE_LABEL));
			Image mainPicture = new Image(new Texture(Res.TITLE_PICTURE));
			
			ImageButton drawButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(buttonTexture, 0, 0, BUTTON_WIDTH, BUTTON_HEIGHT)));
			drawButton.addListener(new DefaultButtonListener() {
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
			
			ImageButton scoreButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(buttonTexture, 0, BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT)));
			scoreButton.addListener(new DefaultButtonListener() {
				@Override
				public void pressed(InputEvent event, float x, float y, int pointer, int button) {
					game.getDeejay().play(GridSound.CLICK);
					MainMenuScreen.this.setState(MenuState.MAINSCORES);
				}
			});
			
			ImageButton exitButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(buttonTexture, 0, 7 * BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT)));
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
			
			mainMenuTable.add(mainTitle).colspan(3);
			mainMenuTable.row();
			mainMenuTable.add(mainPicture).uniform().colspan(3).space(SPACING, 0, SPACING, 0);
			mainMenuTable.row();
			mainMenuTable.add(drawButton).align(Align.left);
			mainMenuTable.add(scoreButton).space(0, SPACING, 0, SPACING);
			mainMenuTable.add(exitButton).align(Align.right);
			mainMenuTable.pack();
			isMainMenuLoaded = true;
		}
	}
	
	private void loadScoreMenu() {
		if (!isScoreMenuLoaded) {
			Image scoreTitle = new Image(new Texture(Res.SCORE_LABEL));
			ImageButton backButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(buttonTexture, 0, 2 * BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT)));
			backButton.addListener(new DefaultButtonListener() {
				@Override
				public void pressed(InputEvent event, float x, float y, int pointer, int button) {
					game.getDeejay().play(GridSound.CLICK);
					MainMenuScreen.this.setState(MenuState.MAINMENU);
				}
			});
			
			ImageButton againButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(buttonTexture, 0, 4 * BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT)));
			againButton.addListener(new DefaultButtonListener() {
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
			
			ImageButton resetButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(buttonTexture, 0, 3 * BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT)));
			resetButton.addListener(new DefaultButtonListener() {
				@Override
				public void pressed(InputEvent event, float x, float y, int pointer, int button) {
					game.getDeejay().play(GridSound.CLICK);
					// TODO Pop up an "Are you sure?" dialog, then reset scores if confirmed.
				}
			});
			
			scoreMenuTable.add(scoreTitle).colspan(3);
			scoreMenuTable.row();
			scoreMenuTable.add(backButton).align(Align.left);
			scoreMenuTable.add(againButton).space(0, SPACING, 0, SPACING);
			scoreMenuTable.add(resetButton).align(Align.right);
			scoreMenuTable.pack();
			isScoreMenuLoaded = true;
		}
	}

	// Screen implementation

	@Override
	public void show() {
		super.show();
		Gdx.input.setInputProcessor(stage);
		
		setState(currentState);

		game.getJukebox().play(GridMusic.MENU);
		stage.getRoot().getColor().a = 0f;
		stage.getRoot().addAction(fadeIn(0.25f));
	}
	
	@Override
	public void render(float delta) {
		// Update
		stage.act(delta);
		// Render
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		stage.draw();
		Table.drawDebug(stage);
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		width *= ShapeGame.getUIScaling();
		height *= ShapeGame.getUIScaling();
		mainMenuTable.setBounds(0, 0, width, height);
		mainMenuTable.setPosition((width - mainMenuTable.getWidth()) / 2, (height - mainMenuTable.getHeight()) / 2);
		scoreMenuTable.setBounds(0, 0, width, height);
		scoreMenuTable.setPosition((width - scoreMenuTable.getWidth()) / 2, (height - scoreMenuTable.getHeight()) / 2);
		
		// Reset stage
		stage.setViewport(width, height, false);
	}
	
	@Override
	public void dispose() {
		super.dispose();

		if(shapAssets != null) {
			for(Texture t: shapAssets) {
				if (t != null) {
					t.dispose();
				}
			}
		}
	}
}
