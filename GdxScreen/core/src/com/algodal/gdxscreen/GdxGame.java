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

import com.algodal.gdxscreen.utils.GdxDebug;
import com.algodal.gdxscreen.utils.GdxDebug.Operation;
import com.algodal.gdxscreen.utils.GdxLibrary;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

/**
 * This is the main class of the library the user will interact with.
 * It extends the Game but you are advised not to override any of the
 * Game's methods unless you know what you are doing.  You are advised
 * instead to override only two of GdxGame methods: initialize() and
 * deinitialize():  initialize() is the first method called within
 * the create() method and deinitialize() is the last method called within
 * the disposed() method.
 */
public class GdxGame extends Game{
	final ArrayMap<String, GdxScreen> screenMap;
	final ArrayMap<String, GdxScreen> transitionMap;
	final ArrayMap<String, GdxAsset<?>> assetMap;
	final AssetManager assetManager;
	
	final GdxScreen defaultScreen;
	final ScreenListener screenListener; //important for implementing the transition system
	
	State currentState; //important for controlling where methods are called
	
	/**
	 * call setOn(false) to turn off debug info.
	 * Only do it when you are you done developing.
	 */
	public final GdxDebug debug;
	
	
	/**
	 * Store global objects here.  You can access each content and process them individually
	 * or you can treat them as a whole using the library's methods.
	 */
	public final GdxLibrary library;
	
	public GdxGame(){
		screenMap = new ArrayMap<>();
		transitionMap = new ArrayMap<>();
		assetMap = new ArrayMap<>();
		assetManager = new AssetManager();
		
		defaultScreen = new GdxScreen().setGame(this);
		screenListener = new ScreenListener();
		
		debug = new GdxDebug().setOn(true); //debugging is on by default
		//I like this kind of coding where I embed debugging within my main
		//code.  I find it useful because it allows me to avoid using large
		//amount of nested if statements.  In this code I do not have to add
		//comment to the debug code because the tag strings are descriptive.
		
		library = new GdxLibrary();
	}
	
	/**
	 * Do not override unless you know what you are doing.  This rule
	 * includes all the Game's methods.
	 */
	@Override
	public void create() {
		currentState = State.Initializing; //applies to the initialize() method
		initialize(); //user defines all his code here
		debug.assertGreaterEqualInt("registered atleast one screen", screenMap.size, 1);
		debug.assertGreaterEqualInt("registered atleast one transition", transitionMap.size, 1);
		setScreen(defaultScreen); //default screen - null is not allowed. I try to avoid using null in my code.
		launch(transitionMap.getKeyAt(0), screenMap.getKeyAt(0)); //launch the first transition and the first screen registered
	}
	
	/**
	 * Any screen that is going to be shown must first be registered.  You can only call this method
	 * within the initialize() method.
	 * @param ref A unique string that you defined.
	 * @param screenClass Any class that extends GdxScreen
	 */
	public final void registerScreen(String ref, Class<? extends GdxScreen> screenClass){
		registerScreen(screenMap, "GdxScreen", ref, screenClass);
	}
	
	/**
	 * Every transition that is going to be used must be first be registered.  This method
	 * can only be called within the initialize method.
	 * @param ref A unique string that you defined
	 * @param transitionClass Any class that extends GdxTransition
	 */
	public final void registerTransition(String ref, Class<? extends GdxTransition> transitionClass){
		Class<? extends GdxScreen> clazz = (Class<? extends GdxScreen>)transitionClass;
		registerScreen(transitionMap, "GdxTransition", ref, clazz );
	}
	
	/**
	 * Any asset that is going to be used must be first registered.  Must be called in
	 * the initialize method.
	 * @param ref A unique string reference created by you.
	 * @param descriptor A libGdx asset descriptor
	 * @param <T> Type of asset
	 */
	public final <T> void registerAsset(String ref, AssetDescriptor<T> descriptor){
		debug.assertEqual("method is called in initialize", currentState, State.Initializing);
		debug.assertNotNull("asset ref is not null", ref);
		debug.assertNotNull("asset descriptor is not null", descriptor);
		debug.assertStringNotEmpty("asset ref is not empty",  (ref = ref.trim())); //We are not allowing space as valid references.
		
		//generate asset object
		GdxAsset<T> asset = new GdxAsset<>();
		asset.setDescriptor(descriptor);
		asset.setAssetManager(assetManager);
		
		debug.assertFalse("asset ref is unique", assetMap.containsKey(ref));
		debug.assertFalse("asset object is unique", assetMap.containsValue(asset, false)); //see GdxAsset equals(ObjectS) method
		
		//add reference
		assetMap.put(ref, asset);
	}
	
	/**
	 * After registering your assets and screens, you can group them.  Every screen gets a set
	 * of assets it will need to render it self.  More than one screens are allowed to use the
	 * same assets.  This method may only be called within initialize()
	 * @param screenRef The reference you registered for the screen.
	 * @param assetRef The reference you created for the asset.
	 */
	public final void attachAssetToScreen(String screenRef, String assetRef){
		attachAssetToScreen(screenMap, "GdxScreen", screenRef, assetRef);
	}
	
	/**
	 * After registering your assets and transitions, you can group them.  Every transition gets a set
	 * of assets it will need to render it self.  More than one transitions are allowed to use the
	 * same assets.  Screens and transitions are allowed to share the same assets.
	 * This method may only be called within initialize().
	 * @param transitionRef The reference you registered for the transition.
	 * @param assetRef The reference you created for the asset.
	 */
	public final void attachAssetToTransition(String transitionRef, String assetRef){
		attachAssetToScreen(transitionMap, "GdxTransition", transitionRef, assetRef);
	}
	
	private <T extends GdxScreen> void registerScreen(ArrayMap<String, GdxScreen> map, String name, String ref, Class<T> clazz){
		debug.assertEqual("method is called in initialize", currentState, State.Initializing);
		debug.assertNotNull(name + " ref is not null", ref);
		debug.assertNotNull(name + " class is not null", clazz);
		debug.assertStringNotEmpty(name + " ref is not empty", (ref = ref.trim())); //The trimmed down version of the string is used
		debug.assertContructorEmpty(name + " class has a empty constructor", clazz);
		
		//generate screen object
		T screen = debug.assertNoException("no allocation excepton", new Operation<T>() {
			@Override
			public T resultOf() throws Exception {
				return clazz.newInstance();
			}
		});
		screen.setGame(this); //This is a must.  Every screen must know their game.
		
		debug.assertNotNull(name + " is not null", screen);
		debug.assertFalse(name + " ref is unique", map.containsKey(ref)); //unique reference
		debug.assertFalse(name + " object is unique", map.containsValue(screen, false)); //unique screen: see GdxScreen equals(ObjectS) method
		
		//add new reference
		map.put(ref, screen); 
	}
	
	private void attachAssetToScreen(ArrayMap<String, GdxScreen> map, String name, String screenRef, String assetRef){
		debug.assertEqual("method is called in initialize", currentState, State.Initializing);
		debug.assertNotNull(name + " ref is not null", screenRef);
		debug.assertNotNull("asset ref is not null", assetRef);
		debug.assertStringNotEmpty(name + "ref is not empty", (screenRef = screenRef.trim()));
		debug.assertStringNotEmpty("asset ref is not empty", (assetRef = assetRef.trim()));
		debug.assertTrue(name + " ref exists", map.containsKey(screenRef));
		debug.assertTrue("asset ref exists", assetMap.containsKey(assetRef));
		
		//get objects
		GdxScreen screen = map.get(screenRef);
		
		debug.assertFalse(name + " ref gets new asset ref", screen.assetRefs.contains(assetRef, false));
		
		//attachment
		screen.assetRefs.add(assetRef);
	}
	
	/**
	 * Put all your initializations in this method
	 */
	public void initialize(){}
	
	/**
	 * Put all your de-initializations in this method
	 */
	public void deinitialize(){}
	
	final void launch(String transitionRef, String screenRef){
		debug.assertTrue("transition ref exists", transitionMap.containsKey(transitionRef = transitionRef.trim()));
		debug.assertTrue("screen ref exists", screenMap.containsKey(screenRef = screenRef.trim()));
		GdxTransition transition = (GdxTransition)transitionMap.get(transitionRef);
		GdxScreen screen = screenMap.get(screenRef);
		transition.newScreen = screen;
		transition.oldScreen = (GdxScreen)getScreen();
		transition.showing = transition.oldScreen;
		setScreen(transition);
	}

	/**
	 * Do not override unless you know what you are doing.  This rule
	 * includes all the Game's methods.
	 */
	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		float delta = Gdx.graphics.getDeltaTime();
		screenListener.render(delta);
		assetManager.update();
	}
	
	/**
	 * Do not override unless you know what you are doing.  This rule
	 * includes all the Game's methods.
	 */
	@Override
	public void dispose() {
		currentState = State.Disposing;
		//Only screens that were initially created may be disposed.
		for(Entry<String, GdxScreen> entry : screenMap) if(entry.value.created) entry.value.dispose();
		for(Entry<String, GdxScreen> entry : transitionMap) if(entry.value.created) entry.value.dispose();
		assetManager.dispose();
		currentState = State.Deinitializing;
		deinitialize(); //user defined
	}

	/**
	 * Do not override unless you know what you are doing.  This rule
	 * includes all the Game's methods.
	 */
	@Override
	public void pause() {
		screenListener.pause();
	}

	/**
	 * Do not override unless you know what you are doing.  This rule
	 * includes all the Game's methods.
	 */
	@Override
	public void resume() {
		screenListener.resume();
	}

	/**
	 * Do not override unless you know what you are doing.  This rule
	 * includes all the Game's methods.
	 */
	@Override
	public void resize(int width, int height) {
		screenListener.resize(width, height);
	}
	
	
	/**
	 * Do not override unless you know what you are doing.  This rule
	 * includes all the Game's methods.
	 */
	@Override
	public void setScreen(Screen screen) {
		this.screen = screen;
	}
	
	/**
	 * Useful information to tell the general area the game processing is at so
	 * algorithms can act accordingly.
	 *
	 */
	static enum State{
		Creating,
		Showing,
		Rendering,
		Pausing,
		Resuming,
		Resizing,
		Hiding,
		Disposing,
		Initializing,
		Deinitializing
	}
	
	/**
	 * Handles the rendering of the screen.  It implements the transition system.  It basically
	 * gives a special screen, called the transition, control over the out going screen and the
	 * incoming screen.  It also decides when the two screens switch positions.
	 */
	public class ScreenListener{
		private void render(GdxScreen screen, float delta){
			//if the screen's assets are not loaded, start loading them
			if(!screen.loaded){
				screen.loadAssets();
				screen.loaded = true;
			}
			
			//only render the screen after all its assets have been completely loaded.
			if(screen.assetProgress() == 1.0f){
				//if the screen has not been created then create it.
				if(!screen.created){
					currentState = State.Creating;
					screen.create();
					screen.created = true;
				}
				
				//if the screen was hidden before, then show it.
				if(!screen.showed){
					//always resize screen when you are about to show it.
					currentState = State.Resizing;
					screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
					
					//show the screen once until it is hidden again.
					currentState = State.Showing;
					screen.show();
					screen.showed = true;
				}
				
				//render the screen.
				currentState = State.Rendering;
				screen.render(delta);
			}
		}
		
		private void resize(GdxScreen screen, int width, int height){
			screen.resize(width, height);
		}
		
		private void pause(GdxScreen screen){
			if(screen.assetProgress() == 1.0f) screen.pause();
		}
		
		private void resume(GdxScreen screen){
			if(screen.assetProgress() == 1.0f) screen.resume();
		}
		
		public void render(float delta){
			//get the screen
			GdxScreen screen = (GdxScreen)getScreen();
			
			//if it is special, handle it specially
			if(screen instanceof GdxTransition){
				//transitions render themselves and either the outgoing screen or the incoming screen.
				GdxTransition transition = (GdxTransition)screen;
				render(transition.showing, delta);
				render(transition, delta);
			}else{
				//else handle it normally
				render(screen, delta);
			}
		}
		
		public void resize(int width, int height){
			GdxScreen screen = (GdxScreen)getScreen();
			
			if(screen instanceof GdxTransition){
				GdxTransition transition = (GdxTransition)screen;
				resize(transition.showing, width, height);
				resize(transition, width, height);
			}else{
				resize(screen, width, height);
			}
		}
		
		public void pause(){
			GdxScreen screen = (GdxScreen)getScreen();
			
			if(screen instanceof GdxTransition){
				GdxTransition transition = (GdxTransition)screen;
				pause(transition.showing);
				pause(transition);
			}else{
				pause(screen);
			}
		}
		
		public void resume(){
			GdxScreen screen = (GdxScreen)getScreen();
			
			if(screen instanceof GdxTransition){
				GdxTransition transition = (GdxTransition)screen;
				resume(transition.showing);
				resume(transition);
			}else{
				resume(screen);
			}
		}
	}
}
