package com.algodal.gdxscreen;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
//import com.badlogic.gdx.utils.Array;

public class GdxAsset <T>{
	//final Array<String> screenRefs;
	//final Array<String> transitionRefs;
	private AssetDescriptor<T> descriptor;
	private boolean onAssetManager;
	private AssetManager assetManager;
	
	public GdxAsset(){
		//screenRefs = new Array<>();
		//transitionRefs = new Array<>();
	}

	public AssetDescriptor<T> getDescriptor() {
		return descriptor;
	}

	void setDescriptor(AssetDescriptor<T> descriptor) {
		this.descriptor = descriptor;
	}
	
	void setAssetManager(AssetManager assetManager){
		this.assetManager = assetManager;
	}
	
	void load(){
		if(!onAssetManager){
			assetManager.load(descriptor);
			onAssetManager = true;
		}
	}
	
	void unload(){
		if(onAssetManager){
			assetManager.unload(descriptor.fileName);
			onAssetManager = false;
		}
	}
	
	boolean isloaded(){
		return assetManager.isLoaded(descriptor.fileName);
	}

	@Override
	public boolean equals(Object obj) {
		GdxAsset<?> asset = (GdxAsset<?>)obj;
		AssetDescriptor<?> d = asset.getDescriptor();
		if(descriptor == null || d == null) return false;
		return
				descriptor.fileName.equals(d.fileName) &&
				descriptor.file.equals(d.file) &&
				descriptor.type.equals(d.type);
	}
}
