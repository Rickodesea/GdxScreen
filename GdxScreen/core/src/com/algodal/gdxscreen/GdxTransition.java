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

import com.badlogic.gdx.utils.Array;

/**
 * The transition screen delivers the new screen and removes the old screen.
 * The transition is rendered like any other screen.  This is useful for drawing
 * loading bars or other transitional effects.
 */
public class GdxTransition extends GdxScreen{
	GdxScreen newScreen;
	GdxScreen oldScreen;
	
	GdxScreen showing;
	boolean transfered;
	
	/**
	 * Hides the old screen and show the new screen.  The new screen will render, however it will not
	 * be the active screen.  The transition is still the active screen and it renders on top of the
	 * new screen.
	 */
	public final void removeOldScreen(){
		getGame().debug.assertEqual("this method is called inside the render method", getGame().currentState, GdxGame.State.Rendering);
		if(!transfered){
			oldScreen.hide();
			oldScreen.showed = false;
			unloadAssets(new GdxScreen[]{this, newScreen}, new GdxScreen[]{oldScreen});
			oldScreen.loaded = false;
			showing = newScreen;
			transfered = true;
		}
	}
	
	/**
	 * Terminates the transition screen and set the new screen as the active screen.  This calls
	 * the transition's hide method and unload any of its unused assets.
	 */
	public final void deliverNewScreen(){
		getGame().debug.assertEqual("this method is called inside the render method", getGame().currentState, GdxGame.State.Rendering);
		removeOldScreen();
		hide();
		showed = false;
		unloadAssets(new GdxScreen[]{newScreen}, new GdxScreen[]{this});
		loaded = false;
		getGame().setScreen(newScreen);
		transfered = false; //reset transfered
	}
	
	/**
	 * Call this if you want the new screen's assets to start loading the moment the transition is loaded.
	 * This is important if you want to show load progress in your custom transitions.  Otherwise,
	 * the new screen assets will not load until you call either removeOldScreen() or deliverNewScreen().
	 */
	public final void startAsynchronousLoadingOfNewScreenAssets(){
		getGame().debug.assertEqual("this method is called inside show method", getGame().currentState, GdxGame.State.Showing);
		newScreen.loadAssets();
		newScreen.loaded = true;
	}

	/**
	 * GdxTransition, the default class, will automatically run your new screen without any transitional effect.
	 * Of course, you are welcome to override this for your super awesome transitions.
	 */
	@Override
	public void render(float delta) {
		removeOldScreen();
		deliverNewScreen();
	}
	
	/**
	 * This is very important for doing transitional effects.  It provides asset loading progress data
	 * on the incoming screen.
	 * @return 0.0f to 1.0f percentage of screen's assets loaded.
	 */
	public final float getNewScreenAssetProgress(){
		return newScreen.assetProgress();
	}
	
	final void unloadAssets(GdxScreen[] visible, GdxScreen[] hidden){
		Array<String> visibleAssets = new Array<>();
		Array<String> hiddenAssets = new Array<>();
		
		for(GdxScreen screen : visible) visibleAssets.addAll(screen.assetRefs);
		for(GdxScreen screen : hidden) hiddenAssets.addAll(screen.assetRefs);
		
		for(String assetRef : hiddenAssets)
			if(!visibleAssets.contains(assetRef, false)) getGame().assetMap.get(assetRef).unload(); 
	}
}
