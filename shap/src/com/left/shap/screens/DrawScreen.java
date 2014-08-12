package com.left.shap.screens;

import static com.left.shap.util.Log.l;
import static com.left.shap.util.Log.pCoords;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.left.shap.ShapeGame;
import com.left.shap.ShapeGame.Screens;
import com.left.shap.util.DefaultButtonListener;
import com.left.shap.util.Utils;

public class DrawScreen extends AbstractScreen {
	private OrthographicCamera camera;
	private ShapeRenderer sr;

	// UI elements
	protected static final float BUTTON_WIDTH = 150f;
	protected static final float BUTTON_HEIGHT = 30f;
	protected static final float BUTTON_SPACING = 10f;
	private final Stage stage;
	private Label scoreLabel;
	private TextButton shareButton;
	private TextButton menuButton;

	// Player circle
	private final float RECORD_DELAY = 0.01f; // seconds
	private float recordDelta = RECORD_DELAY;
	private boolean isDrawing = false;
	private List<Vector2> drawn;

	// Best fit circle
	private boolean fitFound = false;
	private float fitRadius;
	private float fitX;
	private float fitY;

	// Score
	private boolean scoreFound = false;
	private float score;

	public DrawScreen(ShapeGame game) {
		super(game);
		sr = new ShapeRenderer();
		final Skin skin = getSkin();
		this.stage = new Stage();

		scoreLabel = new Label("Score: 0", skin);

		shareButton = new TextButton("Share", skin);
		shareButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		shareButton.addListener(new DefaultButtonListener() {
			@Override
			public void pressed(InputEvent event, float x, float y, int pointer, int button) {
				// TODO share current score
			}
		});

		menuButton = new TextButton("Menu", skin);
		menuButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		menuButton.addListener(new DefaultButtonListener() {
			@Override
			public void pressed(InputEvent event, float x, float y, int pointer, int button) {
				// TODO goto menu
				DrawScreen.this.game.setNextScreen(Screens.MENU);
			}
		});
		stage.addActor(scoreLabel);
		stage.addActor(shareButton);
		stage.addActor(menuButton);

		drawn = new ArrayList<Vector2>(1000);
	}

	@Override
	public void show() {
		super.show();
		Gdx.input.setInputProcessor(this.stage);
	}

	@Override
	public void render(float delta) {
		// Clear screen
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		SpriteBatch batch = getBatch();

		// Check for input
		if(Gdx.input.isButtonPressed(Buttons.LEFT) || Gdx.input.isTouched(0)) {
			// Player is drawing
			if(!isDrawing) {
				isDrawing = true;
				fitFound = false;
				drawn.clear();
			}

			recordDelta += delta;
			if(isDrawing && recordDelta > RECORD_DELAY) {

				Vector2 next = new Vector2(Gdx.input.getX(), Gdx.input.getY());
				if(drawn.size() == 0 || !next.epsilonEquals(drawn.get(drawn.size() - 1), 0.001f)) {
					// avoiding too many duplicates
					l("Pressed " + pCoords(next));
					drawn.add(next);
				}
				recordDelta = 0f;
			}
		} else if(isDrawing) {
			// Player finished drawing
			isDrawing = false;

			// Create the actual circle
			fitFound = calculateBestFitCircle();

			// Calculate the score
			scoreFound = calculateScore();
			if(scoreFound) {
				setScore(score);
			}
		} else {
			// Player isn't drawing
		}

		// draw a reference circle
		/*
		sr.setColor(0, 0, 0.7f, 1);
		Gdx.gl10.glLineWidth(1);
		sr.begin(ShapeType.Line);
		sr.circle(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2,
				Utils.min(Gdx.graphics.getWidth() / 2 * 0.8f, Gdx.graphics.getHeight() / 2 * 0.8f));
		sr.end();
		*/

		// draw the best fit circle
		if(fitFound) {
			sr.setColor(0.2f, 1.0f, 0.5f, 1);
			Gdx.gl10.glLineWidth(1);
			sr.begin(ShapeType.Line);
			sr.circle(this.fitX, this.fitY, this.fitRadius);
			sr.end();
		}

		// draw the player's circle
		sr.setColor(1, 1, 1, 1);
		Gdx.gl10.glLineWidth(3);
		for(int i = 1; i < drawn.size(); i++) {
			sr.begin(ShapeType.Line);
			sr.line(drawn.get(i - 1), drawn.get(i));
			sr.end();
		}

		Gdx.gl10.glLineWidth(1);
		stage.act(delta);
		stage.draw();
	}

	/**
	 * Calculates best fit circle based on "Centroid of polygon" http://en.wikipedia.org/wiki/Centroid#Centroid_of_polygon
	 */
	private boolean calculateBestFitCircle() {
		if(drawn.size() < 2) {
			l("Fit not found");
			return false;
		}

		// find center (centroid)
		float area = 0f;
		float sumX = 0f;
		float sumY = 0f;
		for(int i = 0; i < drawn.size() - 1; i++) {
			float xi = drawn.get(i).x;
			float xj = drawn.get(i + 1).x;
			float yi = drawn.get(i).y;
			float yj = drawn.get(i + 1).y;
			sumX += (xi + xj) * (xi * yj - xj * yi);
			sumY += (yi + yj) * (xi * yj - xj * yi);
			area += (xi * yj - xj * yi);
		}

		{
			// close the polygon
			float xi = drawn.get(drawn.size() - 1).x;
			float xj = drawn.get(0).x;
			float yi = drawn.get(drawn.size() - 1).y;
			float yj = drawn.get(0).y;
			sumX += (xi + xj) * (xi * yj - xj * yi);
			sumY += (yi + yj) * (xi * yj - xj * yi);
			area += (xi * yj - xj * yi);
		}

		this.fitX = sumX / (area * 3);
		this.fitY = sumY / (area * 3);

		// find radius (mean distance)
		float mean = 0f;
		for(int i = 0; i < drawn.size(); i++) {
			mean += drawn.get(i).dst(fitX, fitY);
		}
		mean /= drawn.size();
		this.fitRadius = mean;

		if(Float.isNaN(fitX) || Float.isNaN(fitY) || Float.isNaN(fitRadius)) {
			l("Fit not found");
			return false;
		} else {
			l("Fit found: center=" + pCoords(fitX, fitY) + " radius=" + fitRadius);
			return true;
		}
	}

	private boolean calculateScore() {
		if(!fitFound) {
			return false;
		}
		
		// lower chi^2 = better score
		float chisq = 0;
		for(int i=0; i < drawn.size(); i++) {
			float dst = drawn.get(i).dst(fitX, fitY);
			chisq += dst * dst;
		}
		
		float chiScore = new Double(Math.sqrt(chisq)).floatValue() / drawn.size();
		
		// larger radius = better score
		float radScore = Utils.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) / fitRadius;
		
		// lower score = better score
		score = chiScore * radScore;
		return true;
	}
	
	public void setScore(float score) {
		this.scoreLabel.setText("Score: " + score);
	}
	
	public float getScore() {
		return score;
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		width *= ShapeGame.getUIScaling();
		height *= ShapeGame.getUIScaling();
		stage.setViewport(width, height, false);

		// Shift everything back into the viewport.
		scoreLabel.setPosition(0, BUTTON_HEIGHT);
		shareButton.setPosition(0, 0);
		menuButton.setPosition(width - menuButton.getWidth(), 0);

		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.update();
		sr.setProjectionMatrix(camera.combined);
	}

	@Override
	public void dispose() {
		super.dispose();
		// may crash, comment out if so.
		// http://www.badlogicgames.com/forum/viewtopic.php?f=11&t=3624
		stage.clear();
		stage.dispose();
	}
}
