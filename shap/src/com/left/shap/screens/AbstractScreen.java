package com.left.shap.screens;

import static com.left.shap.util.Log.log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.left.shap.ShapeGame;
import com.left.shap.util.Res;

public abstract class AbstractScreen implements Screen {

	protected final ShapeGame game;

	private BitmapFont font;
	private SpriteBatch batch;
	private ShapeRenderer shaper;
	private Skin skin;
	private TextureAtlas atlas;

	public AbstractScreen(ShapeGame game) {
		this.game = game;
	}

	protected boolean isGameScreen() {
		return false;
	}

	protected String getName() {
		// GWT does not have support for Class.getSimpleName()
		return getClass().getSimpleName();
		// return getClass().getName();
	}

	// Lazy loaded components

	public BitmapFont getFont() {
		if(font == null) {
			font = new BitmapFont();
		}
		return font;
	}

	public SpriteBatch getBatch() {
		if(batch == null) {
			batch = new SpriteBatch();
		}
		return batch;
	}

	public ShapeRenderer getShapeRenderer() {
		if(shaper == null) {
			shaper = new ShapeRenderer(4);
		}
		return shaper;
	}

	protected Skin getSkin() {
		if(skin == null) {
			FileHandle skinFile = Gdx.files.internal(Res.UI + "uiskin.json");
			skin = new Skin(skinFile);
		}
		return skin;
	}

	public TextureAtlas getAtlas() {
		if(atlas == null) {
			atlas = new TextureAtlas(Gdx.files.internal(Res.IMAGE + "textures.atlas"));
		}
		return atlas;
	}

	// Screen implementation

	@Override
	public void show() {
		// log("Show " + getName());
	}

	@Override
	public void resume() {
		// log("Resume " + getName());
	}

	@Override
	public void render(float delta) {
		// Clear screen
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	}

	@Override
	public void resize(int width, int height) {
		log("Resize " + getName() + " to " + width + "x" + height);
	}

	@Override
	public void pause() {
		// log("Pause " + getName());
	}

	@Override
	public void hide() {
		// log("Hide " + getName());
		dispose();
	}

	@Override
	public void dispose() {
		log("Dispose " + getName());
		if(font != null)
			font.dispose();
		if(batch != null)
			batch.dispose();
		if(skin != null)
			skin.dispose();
		if(atlas != null)
			atlas.dispose();
	}

}
