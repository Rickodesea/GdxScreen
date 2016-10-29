package com.algodal.gdxscreen;

import com.algodal.gdxscreen.utils.GdxDebug;
import com.algodal.gdxscreen.utils.GdxDebug.Operation;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

public class GdxGame extends Game{
	final ArrayMap<String, GdxScreen> screenMap;
	final ArrayMap<String, GdxTransition> transitionMap;
	final ArrayMap<String, GdxAsset<?>> assetMap;
	final AssetManager assetManager;
	State currentState;
	
	final GdxDebug debug;
	
	public GdxGame(){
		screenMap = new ArrayMap<>();
		transitionMap = new ArrayMap<>();
		assetMap = new ArrayMap<>();
		assetManager = new AssetManager();
		
		debug = new GdxDebug();
	}
	
	@Override
	public void create() {
		initialize(); //user defined method
		debug.assertGreaterEqualInt("registered atleast one screen", screenMap.size, 1);
		debug.assertGreaterEqualInt("registered atleast one transition", transitionMap.size, 1);
		setScreen(new GdxScreen()); //default screen - null is not allowed
		launch(transitionMap.getKeyAt(0), screenMap.getKeyAt(0)); //launch the first transition and the first screen registered
	}
	
	public void registerScreen(String ref, Class<? extends GdxScreen> screenClass){
		debug.assertNotNull("screen ref is not null", ref);
		debug.assertNotNull("screen class is not null", screenClass);
		debug.assertStringNotEmpty("screen ref is not empty", ref);
		debug.assertContructorEmpty("screen class has a empty constructor", screenClass);
		
		//generate screen object
		GdxScreen screen = debug.assertNoException("no screen allocation excepton", new Operation<GdxScreen>() {
			@Override
			public GdxScreen resultOf() throws Exception {
				return screenClass.newInstance();
			}
		});
		screen.setGame(this);
		
		debug.assertNotNull("screen is not null", screen);
		debug.assertTrue("screen class extends GdxScreen",screen instanceof GdxScreen);
		debug.assertFalse("screen ref is unique", screenMap.containsKey(ref));
		debug.assertFalse("screen object is unique", screenMap.containsValue(screen, false)); //see GdxScreen equals(ObjectS) method
		
		//add new reference
		screenMap.put(ref, screen);
	}
	
	public void registerTransition(String ref, Class<? extends GdxTransition> transitionClass){
		debug.assertNotNull("transition ref is not null", ref);
		debug.assertNotNull("transition class is not null", transitionClass);
		debug.assertStringNotEmpty("transition ref is not empty", ref);
		debug.assertContructorEmpty("transition class has empty constructor", transitionClass);
		
		//generate transition object
		GdxTransition transition = debug.assertNoException("transition allocation has no exception", new Operation<GdxTransition>() {
			@Override
			public GdxTransition resultOf() throws Exception {
				return transitionClass.newInstance();
			}
		});
		transition.setGame(this);
		
		debug.assertNotNull("transition is not null", transition);
		debug.assertTrue("transition class extends GdxTransition", transition instanceof GdxTransition);
		debug.assertFalse("transition ref is unique", transitionMap.containsKey(ref));
		debug.assertFalse("transition object is unique", transitionMap.containsValue(transition, false)); //see GdxTransition equals(ObjectS) method
		
		//add reference
		transitionMap.put(ref, transition);
	}
	
	public <T> void registerAsset(String ref, AssetDescriptor<T> descriptor){
		debug.assertNotNull("asset ref is not null", ref);
		debug.assertNotNull("asset descriptor is not null", descriptor);
		debug.assertStringNotEmpty("asset ref is not empty", ref);
		
		//generate asset object
		GdxAsset<T> asset = new GdxAsset<>();
		asset.setDescriptor(descriptor);
		asset.setAssetManager(assetManager);
		
		debug.assertFalse("asset ref is unique", assetMap.containsKey(ref));
		debug.assertFalse("asset object is unique", assetMap.containsValue(asset, false)); //see GdxAsset equals(ObjectS) method
		
		//add reference
		assetMap.put(ref, asset);
	}
	
	public void attachAssetToScreen(String screenRef, String assetRef){
		debug.assertNotNull("screen ref is not null", screenRef);
		debug.assertNotNull("asset ref is not null", assetRef);
		debug.assertStringNotEmpty("screen ref is not empty", screenRef);
		debug.assertStringNotEmpty("asset ref is not empty", assetRef);
		debug.assertTrue("screen ref exists", screenMap.containsKey(screenRef));
		debug.assertTrue("asset ref exists", assetMap.containsKey(assetRef));
		
		//get objects
		GdxScreen screen = screenMap.get(screenRef);
		//GdxAsset<?> asset = assetMap.get(assetRef);
		
		debug.assertFalse("screen ref gets new asset ref", screen.assetRefs.contains(assetRef, false));
		//debug.assertFalse("asset ref gets new screen ref", asset.screenRefs.contains(screenRef, false));
		
		//attachment
		screen.assetRefs.add(assetRef);
		//asset.screenRefs.add(screenRef);
	}
	
	public void attachAssetToTransition(String transitionRef, String assetRef){
		debug.assertNotNull("transition ref is not null", transitionRef);
		debug.assertNotNull("asset ref is not null", assetRef);
		debug.assertStringNotEmpty("transition ref is not empty", transitionRef);
		debug.assertStringNotEmpty("asset ref is not empty", assetRef);
		debug.assertTrue("transition ref exists", transitionMap.containsKey(transitionRef));
		debug.assertTrue("asset ref exists", transitionMap.containsKey(assetRef));
		
		//get objects
		GdxTransition transition = transitionMap.get(transitionRef);
		//GdxAsset<?> asset = assetMap.get(assetRef);
		
		debug.assertFalse("transition ref gets new asset ref", transition.assetRefs.contains(assetRef, false));
		//debug.assertFalse("asset ref gets new transition ref", asset.transitionRefs.contains(transitionRef, false));
		
		//attachment
		transition.assetRefs.add(assetRef);
		//asset.transitionRefs.add(transitionRef);
	}
	
	public void initialize(){}
	public void deinitialize(){}
	
	final void launch(String transitionRef, String screenRef){
		debug.assertTrue("transition ref exists", transitionMap.containsKey(transitionRef));
		debug.assertTrue("screen ref exists", screenMap.containsKey(screenRef));
		GdxTransition transition = transitionMap.get(transitionRef);
		GdxScreen screen = screenMap.get(screenRef);
		transition.outGoingScreen = (GdxScreen) getScreen();
		transition.inComingScreen = screen;
		transition.loadAssets();
		setScreen(transition);
		transition.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	public void render() {
		GdxScreen screen = (GdxScreen)getScreen();
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		float delta = Gdx.graphics.getDeltaTime();
		
		if(screen.assetProgress() == 1.0f){
			if(!screen.created){
				currentState = State.Creating;
				screen.create();
				screen.created = true;
			}
			
			if(!screen.showed){
				currentState = State.Showing;
				screen.show();
				screen.showed = true;
			}
			
			currentState = State.Rendering;
			screen.render(delta);
		}
		
		assetManager.update();
	}
	
	@Override
	public void dispose() {
		currentState = State.Disposing;
		for(Entry<String, GdxScreen> entry : screenMap) entry.value.dispose();
		for(Entry<String, GdxTransition> entry : transitionMap) entry.value.dispose();
		assetManager.dispose();
		deinitialize(); //user defined
	}

	@Override
	public void pause() {
		currentState = State.Pausing;
		super.pause();
	}

	@Override
	public void resume() {
		currentState = State.Resuming;
		super.resume();
	}

	@Override
	public void resize(int width, int height) {
		currentState = State.Resizing;
		super.resize(width, height);
	}



	public static enum State{
		Creating,
		Showing,
		Rendering,
		Pausing,
		Resuming,
		Resizing,
		Hiding,
		Disposing
	}
}
