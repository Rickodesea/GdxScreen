package com.algodal.gdxscreen;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;

/**
 * 
 * GdxAsset contains the libGdx descriptor of the asset
 * and the asset manager that loads it.  It manages the 
 * loading and unloading of one asset.  You will not have
 * not to use this class or its object directly.
 *
 * @param <T> The type of asset.
 */
public class GdxAsset <T>{
	private AssetDescriptor<T> descriptor;
	private boolean onAssetManager;
	private AssetManager assetManager;
	
	public AssetDescriptor<T> getDescriptor() {
		return descriptor;
	}

	void setDescriptor(AssetDescriptor<T> descriptor) {
		this.descriptor = descriptor;
	}
	
	void setAssetManager(AssetManager assetManager){
		this.assetManager = assetManager;
	}
	
	/**
	 * Calls AssetManager.load(AssetDescriptor)
	 */
	void load(){
		if(!onAssetManager){
			assetManager.load(descriptor);
			onAssetManager = true;
		}
	}
	
	/**
	 * Calls AssetManager.unload(AssetDescriptor.fileName)
	 */
	void unload(){
		if(onAssetManager){
			assetManager.unload(descriptor.fileName);
			onAssetManager = false;
		}
	}
	
	/**
	 * 
	 * @return True if the asset manager has completely load the asset into memory
	 */
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
