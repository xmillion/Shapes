package com.left.shap.screens;

import static com.badlogic.gdx.math.Interpolation.pow2In;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import com.left.shap.ShapeGame;
import com.left.shap.model.ResetPositionAction;
import com.left.shap.util.Res;
import com.left.shap.util.Utils;

/**
 * Slightly more specific Screen template for Menus.
 * Contains a Stage for UI elements, and a shared dynamic background.
 */
public abstract class AbstractMenuScreen extends AbstractScreen {

	// Standard UI sizes
	protected static final float BUTTON_WIDTH = 150f;
	protected static final float BUTTON_HEIGHT = 30f;
	protected static final float SPACING = 10f;

	protected final Stage stage;
	private Table table;
	
	// Falling Shaps
	private Texture[] shapAssets;
	private Image[] fallingShaps;
	private static final float SHAP_LENGTH = 64;
	private static final float MAX_DURATION = Gdx.graphics.getHeight() / SHAP_LENGTH / 2;
	private static final float MAX_DELAY = 9;
	private static final float INIT_SPEED_CAP = 2;

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
		
		// TODO rewrite this class to support the falling shaps
		// Set shap animations
		// Run animations
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
			stage.addActor(fallingShaps[i]);
		}
	}
	
	private static Action fallingShapAction() {
		final float height = Gdx.graphics.getHeight();
		float duration = MAX_DURATION - (Utils.random.nextFloat() * INIT_SPEED_CAP);
		float delay = Utils.random.nextFloat() * MAX_DELAY;
		
		return sequence(delay(delay), forever(sequence(ResetPositionAction.resetAction(height), moveBy(0, -height - 128, duration, pow2In))));
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

		if(shapAssets != null) {
			for(Texture t: shapAssets) {
				if (t != null) {
					t.dispose();
				}
			}
		}
	}
}
