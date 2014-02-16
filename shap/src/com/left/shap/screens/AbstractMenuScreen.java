package com.left.shap.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.repeat;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.left.shap.ShapeGame;
import com.left.shap.util.Res;

/**
 * Slightly more specific Screen template for Menus.
 * Contains a Stage for UI elements, and a shared dynamic background.
 */
public abstract class AbstractMenuScreen extends AbstractScreen {

	// Constants depend on background image
	protected final int WIDTH = 512, HEIGHT = 128;
	protected final float SPEED = 0.25f;
	protected final int REPEAT = (int) (WIDTH / SPEED) - 1;
	protected final int COPIES = Gdx.graphics.getWidth() / WIDTH + 3;

	// Standard UI sizes
	protected static final float BUTTON_WIDTH = 150f;
	protected static final float BUTTON_HEIGHT = 30f;
	protected static final float SPACING = 10f;
	
	private Texture backgroundTexture;
	private Image[] backgroundFrames;
	private static int backgroundOffset = 0;

	protected final Stage stage;
	private Table table;

	public AbstractMenuScreen(ShapeGame game) {
		super(game);
		stage = new Stage();
		loadBackground();
	}

	/**
	 * Override and return false if no background is to be shown on this screen.
	 * 
	 * @return
	 */
	protected boolean hasDynamicBackground() {
		return true;
	}

	// Lazy loaded components

	protected void loadBackground() {
		if(!hasDynamicBackground())
			return;

		int initialPosition = (int) (backgroundOffset * SPEED);
		backgroundTexture = new Texture(Res.UI + "background.png");
		backgroundFrames = new Image[COPIES];
		for(int i = 0; i < COPIES; i++) {
			backgroundFrames[i] = new Image(new TextureRegionDrawable(new TextureRegion(
					backgroundTexture, 0, 0, WIDTH, HEIGHT)));
			backgroundFrames[i].setScaling(Scaling.none);
			backgroundFrames[i].setBounds(i * WIDTH - initialPosition, 0, WIDTH, HEIGHT);
			backgroundFrames[i].addAction(sequence(forever(sequence(
					repeat(REPEAT, moveBy(-SPEED, 0)), moveTo(i * WIDTH - initialPosition, 0)))));
			stage.addActor(backgroundFrames[i]);
		}
	}

	protected Table getTable() {
		if(table == null) {
			table = new Table(getSkin());
			if(ShapeGame.DEVMODE) {
				table.debug();
			}
			stage.addActor(table);
		}
		return table;
	}

	// Screen implementation

	@Override
	public void show() {
		super.show();
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		backgroundOffset++;
		if(backgroundOffset > REPEAT) {
			backgroundOffset = 0;
		}

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
		
		// Reset stage
		stage.setViewport(width, height, false);
	}

	@Override
	public void dispose() {
		super.dispose();

		if(backgroundTexture != null) {
			backgroundTexture.dispose();
		}
	}
}
