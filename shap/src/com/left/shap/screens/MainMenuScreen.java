package com.left.shap.screens;

import static com.badlogic.gdx.math.Interpolation.pow2In;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.left.shap.util.Log.l;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.left.shap.ShapeGame;
import com.left.shap.ShapeGame.Screens;
import com.left.shap.model.ResetPositionAction;
import com.left.shap.model.Score;
import com.left.shap.services.MusicManager.GridMusic;
import com.left.shap.services.SoundManager.GridSound;
import com.left.shap.util.DefaultButtonListener;
import com.left.shap.util.Res;
import com.left.shap.util.Utils;

/**
 * Main Menu. TableLayout Editor (Java Web-Start): http://table-layout.googlecode.com/svn/wiki/jws/editor.jnlp
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
	private double newScore;
	private Table mainMenuTable;
	private Table scoreMenuTable;
	private ImageButton scoreBackButton;
	private ImageButton scoreAgainButton;
	private ImageButton scoreResetButton;

	// Falling Shaps
	private Texture[] shapAssets;
	private List<Image> fallingShaps;
	private static final float SHAP_LENGTH = 64;
	private static final float MAX_DURATION = Gdx.graphics.getHeight() / SHAP_LENGTH / 2;
	private static final float MAX_DELAY = 9;
	private static final float INIT_SPEED_CAP = 2;

	public MainMenuScreen(ShapeGame game) {
		super(game);

		// Load background
		shapAssets = new Texture[Res.SHAPS];
		for(int i = 0; i < Res.SHAPS; i++) {
			shapAssets[i] = new Texture(Res.SHAPASSETS + "shap" + i + ".png");
		}
		fallingShaps = new ArrayList<Image>();

		// Load UI
		buttonTexture = new Texture(Res.BUTTONS);
		stage = new Stage();
		mainMenuTable = new Table();
		scoreMenuTable = new Table();
		if(ShapeGame.DEVMODE) {
			mainMenuTable.debug();
			scoreMenuTable.debug();
		}
		loadMainMenu();
		loadScoreMenu();
		stage.addActor(mainMenuTable);
		stage.addActor(scoreMenuTable);

		// determine state
		Map<String, Object> globals = game.getGlobals();
		if(globals.containsKey("newscore")) {
			this.newScore = (Double) globals.remove("newscore");
			setState(MenuState.DRAWSCORES);
		} else {
			setState(MenuState.MAINMENU);
		}
	}

	private static Action fallingShapAction() {
		final float height = Gdx.graphics.getHeight();
		float duration = MAX_DURATION - (Utils.random.nextFloat() * INIT_SPEED_CAP);
		float delay = Utils.random.nextFloat() * MAX_DELAY;

		//@formatter:off
		return sequence(delay(delay), forever(sequence(
				ResetPositionAction.resetAction(height),
				moveBy(0, -height - 128, duration, pow2In))));
		//@formatter:on
	}

	public void setState(MenuState state) {
		this.currentState = state;
		switch(state) {
		case MAINMENU:
			mainMenuTable.setVisible(true);
			scoreMenuTable.setVisible(false);
			break;
		case MAINSCORES:
			mainMenuTable.setVisible(false);
			scoreMenuTable.setVisible(true);
			scoreBackButton.setVisible(true);
			scoreAgainButton.setVisible(false);
			break;
		case DRAWSCORES:
			// TODO Add a submit row to scores
			mainMenuTable.setVisible(false);
			scoreMenuTable.setVisible(true);
			scoreBackButton.setVisible(true);
			scoreAgainButton.setVisible(true);
			break;
		}
	}

	private void loadMainMenu() {
		Image mainTitle = new Image(new Texture(Res.TITLE_LABEL));
		Image mainPicture = new Image(new Texture(Res.TITLE_PICTURE));

		ImageButton scoreButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(
				buttonTexture, 0, BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT)));
		scoreButton.addListener(new DefaultButtonListener() {
			@Override
			public void pressed(InputEvent event, float x, float y, int pointer, int button) {
				game.getDeejay().play(GridSound.CLICK);
				MainMenuScreen.this.setState(MenuState.MAINSCORES);
			}
		});

		ImageButton drawButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(
				buttonTexture, 0, 0, BUTTON_WIDTH, BUTTON_HEIGHT)));
		drawButton.addListener(new DefaultButtonListener() {
			@Override
			public void pressed(InputEvent event, float x, float y, int pointer, int button) {
				game.getDeejay().play(GridSound.CLICK);
				stage.getRoot().addAction(sequence(fadeOut(0.15f), run(new Runnable() {
					public void run() {
						game.navigateTo(Screens.DRAW);
					}
				})));
			}
		});

		ImageButton exitButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(
				buttonTexture, 0, 7 * BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT)));
		exitButton.addListener(new DefaultButtonListener() {
			@Override
			public void pressed(InputEvent event, float x, float y, int pointer, int button) {
				l("Goodbye");
				game.getDeejay().play(GridSound.CLICK);
				stage.getRoot().addAction(sequence(fadeOut(0.1f), run(new Runnable() {
					public void run() {
						Gdx.app.exit();
					}
				})));
			}
		});

		mainMenuTable.add(mainTitle).colspan(3).pad(SPACING);
		mainMenuTable.row();
		mainMenuTable.add(mainPicture).expand().uniform().colspan(3);
		mainMenuTable.row();
		mainMenuTable.add(scoreButton).align(Align.left).pad(0, SPACING, SPACING, 0);
		mainMenuTable.add(drawButton).pad(0, 0, SPACING, 0);
		mainMenuTable.add(exitButton).align(Align.right).pad(0, 0, SPACING, SPACING);
		mainMenuTable.pack();
	}

	private void loadScoreMenu() {
		Image scoreTitle = new Image(new Texture(Res.SCORE_LABEL));

		scoreBackButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(
				buttonTexture, 0, 2 * BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT)));
		scoreBackButton.addListener(new DefaultButtonListener() {
			@Override
			public void pressed(InputEvent event, float x, float y, int pointer, int button) {
				game.getDeejay().play(GridSound.CLICK);
				MainMenuScreen.this.setState(MenuState.MAINMENU);
			}
		});

		scoreAgainButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(
				buttonTexture, 0, 4 * BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT)));
		scoreAgainButton.addListener(new DefaultButtonListener() {
			@Override
			public void pressed(InputEvent event, float x, float y, int pointer, int button) {
				game.getDeejay().play(GridSound.CLICK);
				stage.getRoot().addAction(sequence(fadeOut(0.15f), run(new Runnable() {
					public void run() {
						game.navigateTo(Screens.DRAW);
					}
				})));
			}
		});

		scoreResetButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(
				buttonTexture, 0, 3 * BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT)));
		scoreResetButton.addListener(new DefaultButtonListener() {
			@Override
			public void pressed(InputEvent event, float x, float y, int pointer, int button) {
				game.getDeejay().play(GridSound.CLICK);
				// TODO Pop up an "Are you sure?" dialog, then reset scores if confirmed.
			}
		});

		Table scoreList = new Table();
		List<Score> scores = game.getPrefs().getScores();
		final Skin skin = super.getSkin();
		if (scores.size() == 0) {
			//insert newScore
			TextField newNameField = new TextField("", skin);
			// TODO bug: newScore isn't set until after this method call.
			// Restructure this menuTable and scoreTable stuff, perhaps into separate classes.
			// a menuTable class for the menu, and scoreTable class for scores?
			// Either way, have the ability to add a score in constructor.
			Label newScoreLabel = new Label("" + (long)newScore, skin);
			scoreList.add(newNameField).align(Align.left).padBottom(SPACING).fillX();
			scoreList.add(newScoreLabel).align(Align.right).padBottom(SPACING);
			scoreList.row();
		} else {
			for(Score score : scores) {
				if (currentState == MenuState.DRAWSCORES && newScore > score.getScore()) {
					//insert newScore
					TextField newNameField = new TextField("", skin);
					Label newScoreLabel = new Label("" + (long)newScore, skin);
					scoreList.add(newNameField).align(Align.left).padBottom(SPACING).fillX();
					scoreList.add(newScoreLabel).align(Align.right).padBottom(SPACING);
					scoreList.row();
				}
				//insert score
				Label nameLabel = new Label(score.getName(), skin);
				// display score without decimals
				Label scoreLabel = new Label("" + (long)score.getScore(), skin);
				scoreList.add(nameLabel).align(Align.left).padBottom(SPACING).fillX();
				scoreList.add(scoreLabel).align(Align.right).padBottom(SPACING);
				scoreList.row();
			}
		}
		
		//Image temp = new Image(new Texture(Res.TITLE_PICTURE));
		//scoreList.add(temp);
		
		
		
		scoreMenuTable.add(scoreTitle).colspan(3).pad(SPACING);
		scoreMenuTable.row();
		scoreMenuTable.add(scoreList).expand().uniform().colspan(3);
		scoreMenuTable.row();
		scoreMenuTable.add(scoreBackButton).align(Align.left).pad(0, SPACING, SPACING, 0);
		scoreMenuTable.add(scoreAgainButton).pad(0, 0, SPACING, 0);
		scoreMenuTable.add(scoreResetButton).align(Align.right).pad(0, 0, SPACING, SPACING);
		scoreMenuTable.pack();
	}

	// Screen implementation

	@Override
	public void show() {
		super.show();
		Gdx.input.setInputProcessor(stage);
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
		scoreMenuTable.setBounds(0, 0, width, height);

		int copies = (int) ((Gdx.graphics.getWidth() / SHAP_LENGTH) * 6);
		if(Math.abs(copies - fallingShaps.size()) > 8) {
			// gotta remake the falling shaps
			if(copies > fallingShaps.size()) {
				for(int i = 0; i < fallingShaps.size(); i++) {
					Image image = fallingShaps.get(i);
					image.setPosition(-SHAP_LENGTH, -SHAP_LENGTH);
					image.clearActions();
					image.addAction(fallingShapAction());
				}
				for(int i = fallingShaps.size(); i < copies; i++) {
					Image image = new Image(shapAssets[i % shapAssets.length]);
					image.setScaling(Scaling.none);
					image.setBounds(0, -SHAP_LENGTH, SHAP_LENGTH, SHAP_LENGTH);
					image.addAction(fallingShapAction());
					fallingShaps.add(image);
					stage.addActor(image);
				}
			} else {
				for(int i = 0; i < copies; i++) {
					Image image = fallingShaps.get(i);
					image.setPosition(-SHAP_LENGTH, -SHAP_LENGTH);
					image.clearActions();
					image.addAction(fallingShapAction());
				}
				for(int i = copies; i < fallingShaps.size(); i++) {
					fallingShaps.remove(i).remove();
				}
			}
		}

		mainMenuTable.setZIndex(1000);
		scoreMenuTable.setZIndex(1000);

		// Reset stage
		stage.setViewport(width, height, false);
	}

	@Override
	public void dispose() {
		super.dispose();

		if(shapAssets != null) {
			for(Texture t: shapAssets) {
				if(t != null) {
					t.dispose();
				}
			}
		}
		buttonTexture.dispose();
	}
}
