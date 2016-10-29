package com.algodal.gdxscreen;

import com.algodal.gdxscreen.GdxGame.State;
import com.badlogic.gdx.Gdx;

public class GdxTransition extends GdxScreen{
	GdxScreen outGoingScreen;
	GdxScreen inComingScreen;
	
	@Override
	public void show() {
		inComingScreen.loadAssets();
	}
	
	public float getInComingScreenAssetProgress(){
		getGame().debug.assertEqual("method called in rendering", getGame().currentState, GdxGame.State.Rendering);
		return inComingScreen.assetProgress();
	}
	
	public void switchScreens(){
		getGame().debug.assertEqual("method called in rendering", getGame().currentState, GdxGame.State.Rendering);
		getGame().currentState = State.Hiding;
		if(outGoingScreen.showed){
			outGoingScreen.hide();
			outGoingScreen.unloadDifferentAssets(inComingScreen.assetRefs);
			outGoingScreen.showed = false;
		}
		
		hide();
		unloadDifferentAssets(inComingScreen.assetRefs);
		showed = false;
		getGame().setScreen(inComingScreen);
		inComingScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	public void hideOutGoingScreen(){
		getGame().debug.assertEqual("method called in rendering", getGame().currentState, GdxGame.State.Rendering);
		getGame().currentState = State.Hiding;
		if(outGoingScreen.showed){
			outGoingScreen.hide();
			outGoingScreen.unloadDifferentAssets(inComingScreen.assetRefs);
			outGoingScreen.showed = false;
		}
	}

	@Override
	public void launch(String transitionRef, String screenRef) {}

	public void renderOutGoingScreen(float delta) {
		getGame().debug.assertEqual("method called in rendering", getGame().currentState, GdxGame.State.Rendering);
		if(outGoingScreen.showed) outGoingScreen.render(delta);
		else getGame().debug.report("renderOutGoingScreen", "call has no effect because outGoingScreen is hidden.");
	}

	public void resizeOutGoingScreen(int width, int height) {
		getGame().debug.assertEqual("method called in resizing", getGame().currentState, GdxGame.State.Resizing);
		if(outGoingScreen.showed) outGoingScreen.resize(width, height);
		else getGame().debug.report("resizeOutGoingScreen", "call has no effect because outGoingScreen is hidden.");
	}

	public void pauseOutGoingScreen() {
		getGame().debug.assertEqual("method called in pausing", getGame().currentState, GdxGame.State.Pausing);
		if(outGoingScreen.showed) outGoingScreen.pause();
		else getGame().debug.report("pauseOutGoingScreen", "call has no effect because outGoingScreen is hidden.");
	}

	public void resumeOutGoingScreen() {
		getGame().debug.assertEqual("method called in resuming", getGame().currentState, GdxGame.State.Resuming);
		if(outGoingScreen.showed) outGoingScreen.resume();
		else getGame().debug.report("resumeOutGoingScreen", "call has no effect because outGoingScreen is hidden.");
	}

	@Override
	public void render(float delta) {
		switchScreens();
	}
}
