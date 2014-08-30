package com.left.shap.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.left.shap.ShapeGame;
import com.left.shap.ShapeGame.Screens;
import com.left.shap.util.AnimatedImage;
import com.left.shap.util.AnimationDrawable;
import com.left.shap.util.Res;

/**
 * Screen showing logos and stuff
 */
public class SplashScreen extends AbstractScreen {
	private final Stage stage;
	private Texture texture;
	private Image logo;
	private AnimatedImage arrows;

	public SplashScreen(ShapeGame game) {
		super(game);
		stage = new Stage();
	}

	@Override
	public void show() {
		super.show();
		int centerX = Gdx.graphics.getWidth() / 2;
		int centerY = Gdx.graphics.getHeight() / 2;

		texture = new Texture(Res.SPLASH);
		logo = new Image(
				new TextureRegionDrawable(new TextureRegion(texture, 0, 31, 116, 22)),
				Scaling.none, Align.center);
		logo.setPosition(centerX - logo.getWidth() / 2, centerY);

		TextureRegion[] arrowFrames = new TextureRegion[96];
		for(int i = 0; i < 96; i++) {
			arrowFrames[i] = new TextureRegion(texture, i, 0, 128, 31);
		}
		Animation anim = new Animation(0.025f, arrowFrames);
		anim.setPlayMode(Animation.LOOP);
		arrows = new AnimatedImage(new AnimationDrawable(anim), Scaling.none, Align.center);
		arrows.setPosition(centerX - arrows.getWidth() / 2, centerY - arrows.getHeight());
		
		stage.addActor(logo);
		stage.addActor(arrows);
		stage.getRoot().getColor().a = 0f;
		stage.getRoot().addAction(sequence(delay(0.25f), fadeIn(0.25f), delay(1.0f), fadeOut(0.25f),
				run(new Runnable() {
					public void run() {
						game.navigateTo(Screens.MENU);
					}
				})));
		
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
		int centerX = (int) (width * ShapeGame.getUIScaling() / 2);
		int centerY = (int) (height * ShapeGame.getUIScaling() / 2);
		logo.setPosition(centerX - logo.getWidth() / 2, centerY);
		arrows.setPosition(centerX - arrows.getWidth() / 2, centerY - arrows.getHeight());
		
		// Reset stage
		stage.setViewport(width, height, false);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		texture.dispose();
	}
}
