/*******************************************************************************
 * Copyright 2016 Alrick Grandison (Algodal)  alrickgrandison@gmail.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.algodal.gdxscreen;

import com.algodal.gdxscreen.utils.GdxLibrary;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Array;

/**
 * GdxScreen implements libGdx's Screen.  Additionally, it provides
 * a create method to create initializations that will last until the 
 * game life-span ends; getAsset to get any asset associated
 * with the screen at initialization and launch to call another screen to be active.  
 * Unlike. libGdx's Screens, GdxScreens
 * are automatically disposed when the game disposes, so do not dispose them
 * yourself.  You are free to override any Screen method and create().
 */
public class GdxScreen implements Screen{

	final Array<String> assetRefs;
	private GdxGame game;
	boolean created;
	boolean showed;
	boolean loaded;
	
	public GdxScreen(){
		assetRefs = new Array<>();
	}
	
	/**
	 * Set during registration.
	 * @param game the game that register it.
	 * @return the screen for convenience.
	 */
	final GdxScreen setGame(GdxGame game){
		this.game = game;
		return this;
	}
	
	final GdxGame getGame(){
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
	
	final void loadAssets(){
		for(String assetRef : assetRefs)
			game.assetMap.get(assetRef).load();
	}
	
	final float assetProgress(){
		int totalAssets = assetRefs.size + 1;
		int readyAssets = 1;
		for(String assetRef : assetRefs)
			if(game.assetMap.get(assetRef).isloaded())
				readyAssets ++;
		return (float)readyAssets / (float)totalAssets;
	}
	
	/**
	 * This method is called absolutely once.  Any variable you initialize here will remain
	 * initialized until the game ends.
	 */
	public void create(){}
	
	/**
	 * Set a new screen as the active / current screen.
	 * @param transitionRef Reference to the transition to deliver the new screen
	 * @param screenRef Reference to the new screen
	 */
	final public void launch(String transitionRef, String screenRef){
		getGame().debug.assertEqual("method called in rendering", getGame().currentState, GdxGame.State.Rendering);
		game.launch(transitionRef, screenRef);
	}
	
	/**
	 * Get an asset this screen has access to,
	 * @param assetRef Reference to the asset that belongs to this screen
	 * @param <T> type of asset.
	 * @return An asset.
	 */
	final public <T> T getAsset(String assetRef){
		game.debug.assertTrue("asset ref exists for this screen", assetRefs.contains(assetRef = assetRef.trim(), false));
		return game.assetManager.get(game.assetMap.get(assetRef).getDescriptor().fileName);
	}
	
	//for convenience
	final public <T> T getAsset(String assetRef, Class<T> clazz){
		return getAsset(assetRef);
	}
	
	final public GdxLibrary getGameLibrary(){
		return game.library;
	}
}
