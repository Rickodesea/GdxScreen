package com.algodal.gdxscreen.utils;

import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap.Entry;

/**
 * The class allows for the storage, loading and disposing of data
 * that is to be global and life span is to last the entirety of the
 * Game's life span.
 */
public class GdxLibrary {
	private final ArrayMap<String, Content<?>> contentMap;
	
	public final GdxDebug debug;
	
	public GdxLibrary(){
		contentMap = new ArrayMap<>();
		debug = new GdxDebug().setOn(true);
	}
	
	/**
	 * Disposes all its contents.
	 */
	public final void destroy(){
		for(Entry<String, Content<?>> entry : contentMap) if(!entry.value.independent) entry.value.dispose();
	}
	
	/**
	 * Initializes all its contents.
	 */
	public final void create(){
		for(Entry<String, Content<?>> entry : contentMap) if(!entry.value.independent) entry.value.initialize();
	}
	
	/**
	 * Loads all its contents.
	 */
	public final void load(){
		for(Entry<String, Content<?>> entry : contentMap) if(!entry.value.independent) entry.value.load();
	}
	
	/**
	 * Unloads all its contents.
	 */
	public final void unload(){
		for(Entry<String, Content<?>> entry : contentMap) if(!entry.value.independent) entry.value.unload();
	}
	
	/**
	 * Get the content stored in the library.
	 * @param ref Your unique defined reference string
	 * @return the content you requested.
	 */
	@SuppressWarnings("unchecked")
	public final <T> Content<T> getContent(String ref){
		debug.assertNotNull("content ref is not null", ref);
		debug.assertStringNotEmpty("content ref is not empty", (ref = ref.trim()));
		debug.assertTrue("content ref exists", contentMap.containsKey(ref));
		
		return (Content<T>) contentMap.get(ref);
	}
	
	//for convenience
	public final <T> Content<T> getContent(String ref, Class<T> clazz){
		return getContent(ref);
	}
	
	public final <T> void setContent(String ref, Content<T> content){
		debug.assertNotNull("content ref is not null", ref);
		debug.assertNotNull("content is not null", content);
		debug.assertStringNotEmpty("content ref is not empty", (ref = ref.trim())); //Space is not valid reference
		debug.assertFalse("content ref is unique", contentMap.containsKey(ref)); //unique reference
		debug.assertFalse("content object is unique", contentMap.containsValue(content, true));
		
		//add reference
		contentMap.put(ref, content);
	}
	
	/**
	 * Content that is stored in the library.  Remember to set the
	 * variables initialized, object and loaded inside the respective method bodies.
	 * The content allows you to initialize, load, unload and get the objects any time
	 * you want.
	 *
	 * @param <T> Any object.
	 */
	public static abstract class Content <T> implements Disposable{
		/**
		 * Store your initialized object into this variable.  The
		 * contents of this variable is what get() returns.
		 */
		protected T object;
		
		/**
		 * Set this to true when you call load or false
		 * when you call unload.  The value of this is what isLoaded()
		 * returns.
		 */
		protected boolean loaded;
		
		/**
		 * Set this to true when you call the initialized method.  
		 * Its value is what isInitialized() returns.
		 */
		protected boolean initialized;

		/**
		 * Initialize the content.
		 */
		abstract public Content<T> initialize();
		
		private boolean independent;
		
		/**
		 * Tells the library container not to process it when
		 * it is processing contents in batch.
		 * @return the independent
		 */
		public final boolean isIndependent() {
			return independent;
		}

		/**
		 * Tells the library container not to process it when
		 * it is processing contents in batch. 
		 * @param independent the independent to set
		 */
		public final Content<T> setIndependent(boolean independent) {
			this.independent = independent;
			return this;
		}
		
		/**
		 * 
		 * @return content of object
		 */
		public final T get(){
			return object;
		}
		
		/**
		 * 
		 * @return the content of loaded
		 */
		public final boolean isLoaded(){
			return loaded;
		}
		
		/**
		 * 
		 * @return the content of initialized
		 */
		public final boolean isInitialized(){
			return initialized;
		}
		
		/**
		 * Loads the content, i.e. make it usable.
		 */
		abstract public Content<T> load();
		
		/**
		 * Unloads the content, i.e. can not be used after unloading.
		 */
		abstract public void unload();
	}
}
