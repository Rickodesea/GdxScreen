package com.algodal.gdxscreen.utils;

import java.io.StringWriter;

import com.algodal.gdxscreen.utils.GdxDebug.Operation;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.XmlWriter;

/**
 * This is for saving a list of objects of any class types, as long the
 * classes mostly satisfies POJO rules, as JSON strings nested inside a XML
 * format.  The object should be POJO: no extension, only java.lang types and definitely
 * an empty constructor.  It might work if
 * you break the POJO some rules(not the constructor one), which is OK, but be careful, 
 * in some instances the JSON generator
 * might run of memory when processing your object.
 * The fields of the objects may be of java.lang types and POJO class types.  
 * Any thing else
 * may cause memory issues.  For private fields, there may have to be getters (and setters)
 * for the json processor to read or write their values.
 * 
 */
public class GdxSave {
	private final FileHandle handle;
	private final String dataName;
	private final Array<Object> plainOldJavaObjects;
	public final GdxDebug debug;
	
	public static final String ROOT_ELEMENT = "group";
	public static final String CHILD_ELEMENT = "unit";
	public static final String ROOT_NAME = "name";
	public static final String ROOT_TIME = "time";
	public static final String ROOT_COUNT = "count";
	public static final String CHILD_ID = "id";
	
	public GdxSave(FileHandle handle, String dataName){
		debug = new GdxDebug().setOn(true);
		debug.assertNotNull("handle is not null", handle);
		debug.assertNotNull("data name is not null", dataName);
		debug.assertStringNotEmpty("data name is not empty", dataName);
		this.handle = handle;
		this.dataName = dataName;
		plainOldJavaObjects = new Array<>();
	}
	
	/**
	 * Get the list of objects and add your POJO style objects to it.
	 * @return The list.
	 */
	public Array<Object> getPlainOldJavaObjects(){
		return plainOldJavaObjects;
	}
	
	/**
	 * Saves all objects in the list to the file you specify.  The file is overwritten or created.
	 * The save format possess additional information such as time of save and number of objects.
	 */
	public String save(){
		StringWriter stringWriter = new StringWriter();
		XmlWriter xmlWriter = new XmlWriter(stringWriter);
		Json json = new Json();
		
		debug.assertNoException("No exception during save", new Operation<Void>() {
			@Override
			public Void resultOf() throws Exception {
				xmlWriter.element(ROOT_ELEMENT).attribute(ROOT_NAME, dataName)
				.attribute(ROOT_TIME, flashTime()).attribute(ROOT_COUNT, Integer.toString(plainOldJavaObjects.size));
				for(int i = 0; i < plainOldJavaObjects.size; i++){
					debug.assertContructorEmpty("object has null constructor class", plainOldJavaObjects.get(i).getClass());
					xmlWriter.element(CHILD_ELEMENT).attribute(CHILD_ID, Integer.toString(i));
					xmlWriter.text(json.toJson(plainOldJavaObjects.get(i), Object.class));
					xmlWriter.pop();
				}
				xmlWriter.pop();
				xmlWriter.close();
				return null;
			}
		});
		
		String xmlJsonString = stringWriter.toString();
		handle.writeString(xmlJsonString, false);
		return xmlJsonString;
	}
	
	private String flashTime(){
		return new java.util.Date().toString();
	}
}
