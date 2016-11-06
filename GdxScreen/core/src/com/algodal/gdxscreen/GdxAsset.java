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

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;

/**
 * 
 * GdxAsset contains the libGdx descriptor of the asset
 * and the asset manager that loads it.  It manages the 
 * loading and unloading of one asset.  You will not have
 * not to use this class or its object directly.  It is utilized
 * indirectly when you register an asset, get an asset and show or 
 * hide a screen.
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
