package com.left.shap.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.left.shap.util.Log.l;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.left.shap.ShapeGame;
import com.left.shap.ShapeGame.Screens;
import com.left.shap.services.MusicManager.GridMusic;
import com.left.shap.services.SoundManager.GridSound;
import com.left.shap.util.DefaultButtonListener;
import com.left.shap.util.Res;
import com.left.shap.util.Utils;

public class DrawScreen extends AbstractScreen {
	private OrthographicCamera camera;
	private ShapeRenderer sr;

	// UI elements
	protected static final float BUTTON_WIDTH = 150f;
	protected static final float BUTTON_HEIGHT = 30f;
	protected static final float SPACING = 10f;
	private Texture buttonTexture;
	private final Stage stage;
	private Table table;
	private Label scoreLabel;
	private Label helpLabel;
	private ImageButton backButton;
	private ImageButton clearButton;
	private ImageButton submitButton;

	// State
	private boolean isDrawing = false;
	private boolean isCalculated = false;

	// Desired circle
	private double desiredRadius = -1;
	private Vector2 desiredCenter;
	private static final Color desiredColor = Color.BLUE;

	// Player drawn "circle"
	private final float RECORD_DELAY = 0.01f; // seconds
	private float recordDelta = RECORD_DELAY;
	private List<Vector2> drawn;
	private Color drawingColor = new Color(1, 1, 1, 1); // TODO allow player to pick their own color

	// Actual circle
	private double actualRadius; // negative value == error
	private Vector2 actualCenter; // negative value == error
	private static final Color actualColor = new Color(0.2f, 1.0f, 0.5f, 1);

	// Score
	private double score;

	public DrawScreen(ShapeGame game) {
		super(game);
		this.sr = new ShapeRenderer();
		this.buttonTexture = new Texture(Res.BUTTONS);
		this.stage = new Stage();
		this.table = new Table();
		if(ShapeGame.DEVMODE) {
			table.debug();
		}
		initTable();
		stage.addActor(table);
		
		drawn = new ArrayList<Vector2>(1000); // TODO get expected value of # points
	}
	
	private void initTable() {
		final Skin skin = super.getSkin();
		
		scoreLabel = new Label("Score: 0", skin);
		helpLabel = new Label("Try and draw a perfect circle!", skin);
		backButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(
				buttonTexture, 0, 2 * BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT)));
		backButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		backButton.addListener(new DefaultButtonListener() {
			@Override
			public void pressed(InputEvent event, float x, float y, int pointer, int button) {
				game.getDeejay().play(GridSound.CLICK);
				game.navigateTo(Screens.MENU);
			}
		});
		clearButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(
				buttonTexture, 0, 5 * BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT)));
		clearButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		clearButton.addListener(new DefaultButtonListener() {
			@Override
			public void pressed(InputEvent event, float x, float y, int pointer, int button) {
				game.getDeejay().play(GridSound.CLICK);
				// TODO clear screen
			}
		});
		submitButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(
				buttonTexture, 0, 6 * BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT)));
		submitButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		submitButton.addListener(new DefaultButtonListener() {
			@Override
			public void pressed(InputEvent event, float x, float y, int pointer, int button) {
				game.getDeejay().play(GridSound.CLICK);
				// TODO pass data to score screen
				game.navigateToMainMenu(score);
			}
		});
		
		table.add(scoreLabel).align(Align.left).pad(SPACING);
		table.add(helpLabel).align(Align.right).colspan(2).pad(SPACING);
		table.row();
		table.add(backButton).align(Align.left).pad(0, SPACING, SPACING, 0);
		table.add(clearButton).pad(0, 0, SPACING, 0);
		table.add(submitButton).align(Align.right).pad(0, 0, SPACING, SPACING);
		table.pack();
	}

	@Override
	public void show() {
		super.show();
		Gdx.input.setInputProcessor(this.stage);
		stage.getRoot().getColor().a = 0f;
		stage.getRoot().addAction(fadeIn(0.25f));
	}

	@Override
	public void render(float delta) {
		// Clear screen
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		SpriteBatch batch = super.getBatch();
		BitmapFont font = super.getFont();
		font.setColor(Color.WHITE);
		//TODO font.draw(batch, "Hello World", 200, 200);

		// Check for input
		if(Gdx.input.isButtonPressed(Buttons.LEFT) || Gdx.input.isTouched(0)) {
			// Player is drawing
			if(!isDrawing) {
				isDrawing = true;
				isCalculated = false;
				drawn.clear();
			}

			// Sampling instead of grabbing every frame
			recordDelta += delta;
			if(isDrawing && recordDelta > RECORD_DELAY) {

				Vector2 next = new Vector2(Gdx.input.getX(), Gdx.input.getY());
				if(drawn.size() == 0 || !next.epsilonEquals(drawn.get(drawn.size() - 1), 0.001f)) {
					// avoiding too many duplicates
					drawn.add(next);
				}
				recordDelta = 0f;
			}
		} else if(isDrawing) {
			// Player finished drawing
			isDrawing = false;

			// Create the actual circle
			this.actualCenter = calculateCentroid(drawn);
			this.actualRadius = calculateRadius(drawn, actualCenter);
			if(actualRadius >= 0) {
				// Calculate the score
				setScore(calculateScore(drawn, actualCenter, actualRadius, desiredRadius));
				isCalculated = true;
			}
		} else if(!isCalculated) {
			// Initial state, draw a reference circle
			sr.setColor(desiredColor);
			Gdx.gl10.glLineWidth(1);
			sr.begin(ShapeType.Line);
			sr.circle(desiredCenter.x, desiredCenter.y, (float) desiredRadius);
			sr.end();
		}

		// draw the best fit circle
		if(isCalculated) {
			sr.setColor(actualColor);
			Gdx.gl10.glLineWidth(1);
			sr.begin(ShapeType.Line);
			sr.circle(actualCenter.x, actualCenter.y, (float) actualRadius);
			sr.end();
		}

		// draw the player drawn "circle"
		sr.setColor(drawingColor);
		Gdx.gl10.glLineWidth(3);
		for(int i = 1; i < drawn.size(); i++) {
			sr.begin(ShapeType.Line);
			sr.line(drawn.get(i - 1), drawn.get(i));
			sr.end();
		}

		Gdx.gl10.glLineWidth(1);
		stage.act(delta);
		//stage.draw();
		Table.drawDebug(stage);
	}

	/**
	 * Calculates the centroid of a polygon. http://en.wikipedia.org/wiki/Centroid#Centroid_of_polygon
	 * 
	 * @param polygon
	 * @return the centroid, or (-1, -1) if invalid
	 */
	private static Vector2 calculateCentroid(List<Vector2> polygon) {
		if(polygon == null || polygon.size() < 2) {
			return new Vector2(-1, -1);
		}

		double sumX = 0;
		double sumY = 0;
		double sumArea = 0;
		for(int i = 0; i < polygon.size() - 1; i++) {
			float xi = polygon.get(i).x;
			float xj = polygon.get(i + 1).x;
			float yi = polygon.get(i).y;
			float yj = polygon.get(i + 1).y;
			double area = (xi * yj) - (xj * yi);
			sumX += (xi + xj) * area;
			sumY += (yi + yj) * area;
			sumArea += area;
		}

		// Close the polygon
		{
			float xi = polygon.get(polygon.size() - 1).x;
			float xj = polygon.get(0).x;
			float yi = polygon.get(polygon.size() - 1).y;
			float yj = polygon.get(0).y;
			double area = (xi * yj) - (xj * yi);
			sumX += (xi + xj) * area;
			sumY += (yi + yj) * area;
			sumArea += area;
		}

		float cx = (float) (sumX / (sumArea * 3));
		float cy = (float) (sumY / (sumArea * 3));
		return new Vector2(cx, cy);
	}

	/**
	 * Calculates the radius of a circle given a polygon. The radius is determined by the average distance between the center and each vertex.
	 * 
	 * @param center Center of the circle
	 * @param polygon
	 * @return the radius, or -1 if invalid.
	 */
	private static double calculateRadius(List<Vector2> polygon, Vector2 center) {
		if(center == null || polygon == null || polygon.size() == 0
				|| center.epsilonEquals(new Vector2(-1, -1), 0.1f) || Float.isInfinite(center.x)
				|| Float.isInfinite(center.y)) {
			return -1f;
		}

		double sum = 0;
		for(Vector2 vertex: polygon) {
			double dx = center.x - vertex.x;
			double dy = center.y - vertex.y;
			sum += Math.sqrt((dx * dx) + (dy * dy));
		}
		return sum / polygon.size();
	}

	/**
	 * Calculates the score based on: Variance of each point to mean radius Area of the circle (delta from desired area, which is proportional to
	 * device size) Time (not implemented yet)
	 * 
	 * @param center Vector2 with positive values
	 * @param radius radius with positive values
	 * @param polygon non-null list of points to calculate score with
	 * @return Score. High score is good score.
	 */
	private static double calculateScore(List<Vector2> polygon, Vector2 center, double radius,
			double desiredRadius) {
		// Most of the score is based on chi^2
		double chisq = 0;
		for(Vector2 vertex: polygon) {
			double distance = vertex.dst(center);
			chisq += (distance - radius) * (distance - radius);
		}

		// (chisq / #vertices) to normalize
		// (radius / chisq) to invert values
		// TODO probably need a way to remove relation between radius and chisq, and use ratios instead.
		double varScore = radius * polygon.size() / chisq;

		// Part of the score is based on ratio of radius to desired radius
		double radiusRatio = radius / desiredRadius;
		// Logistic function, returns a double [0,1)
		double radiusScore = 1 / (1 + Math.exp(5 - (8 * radiusRatio)));
		// Punish small circles
		if(radiusRatio < 0.25f) {
			radiusScore *= 1 - radiusRatio;
		}

		// TODO investigate using time as a factor for score?
		// double timeScore = 1;

		// Reasonable place value
		final double multiplier = 1000000;
		return (varScore * radiusScore) * multiplier;
	}

	public void setScore(double score) {
		this.score = score;
		l("Score: " + score);
		this.scoreLabel.setText("Score: " + (int) score);
	}

	public double getScore() {
		return score;
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		width *= ShapeGame.getUIScaling();
		height *= ShapeGame.getUIScaling();
		stage.setViewport(width, height, false);
		
		table.setBounds(0, 0, width, BUTTON_HEIGHT * 2 + SPACING);
		table.setPosition(0, 0);
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.update();
		sr.setProjectionMatrix(camera.combined);

		// Calculate desired circle
		this.desiredCenter = new Vector2(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		this.desiredRadius = Utils.min(Gdx.graphics.getWidth() / 2 * 0.8f,
				Gdx.graphics.getHeight() / 2 * 0.8f);
	}

	@Override
	public void dispose() {
		super.dispose();
		buttonTexture.dispose();
		// may crash, comment out if so.
		// http://www.badlogicgames.com/forum/viewtopic.php?f=11&t=3624
		stage.clear();
		stage.dispose();
	}
}
