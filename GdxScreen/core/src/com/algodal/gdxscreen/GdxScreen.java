package com.algodal.gdxscreen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Array;

public class GdxScreen implements Screen{

	final Array<String> assetRefs;
	private GdxGame game;
	boolean created;
	boolean showed;
	
	public GdxScreen(){
		assetRefs = new Array<>();
	}
	
	void setGame(GdxGame game){
		this.game = game;
	}
	
	GdxGame getGame(){
		return game;
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean equals(Object obj) {
		GdxScreen screen = (GdxScreen)obj;
		return getClass().equals(screen.getClass());
	}
	
	void loadAssets(){
		for(String assetRef : assetRefs)
			game.assetMap.get(assetRef).load();
	}
	
	void unloadAssets(){
		for(String assetRef : assetRefs)
			game.assetMap.get(assetRef).unload();
	}
	
	void unloadDifferentAssets(Array<String> otherAssetRefs){
		for(String assetRef : assetRefs)
			if(!otherAssetRefs.contains(assetRef, false))
				game.assetMap.get(assetRef).unload();
	}
	
	float assetProgress(){
		int totalAssets = assetRefs.size + 1;
		int readyAssets = 1;
		for(String assetRef : assetRefs)
			if(game.assetMap.get(assetRef).isloaded())
				readyAssets ++;
		return (float)readyAssets / (float)totalAssets;
	}
	
	public void create(){}
	
	public void launch(String transitionRef, String screenRef){
		getGame().debug.assertEqual("method called in rendering", getGame().currentState, GdxGame.State.Rendering);
		game.launch(transitionRef, screenRef);
	}
	
	public <T> T getAsset(String assetRef){
		game.debug.assertTrue("asset ref exists", assetRefs.contains(assetRef, false));
		return game.assetManager.get(game.assetMap.get(assetRef).getDescriptor().fileName);
	}
}
