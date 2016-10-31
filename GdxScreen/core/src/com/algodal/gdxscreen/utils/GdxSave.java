package com.algodal.gdxscreen.utils;

import java.io.StringWriter;

import com.algodal.gdxscreen.utils.GdxDebug.Operation;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.XmlWriter;

/**
 * This is for saving a list of objects of any class types, as long the
 * classes mostly satisfies POJO rules, as JSON strings nested inside a XML
 * format.  The object must be POJO: no extension and only java.lang types.  It might work if
 * you break the POJO rules, which is OK, but be careful, in some instances the JSON generator
 * might run of memory when processing your object.
 * The fields of the objects may be of java.lang types and POJO class types.  Any thing else
 * may cause memory issues.
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
	public void save(){
		StringWriter stringWriter = new StringWriter();
		XmlWriter xmlWriter = new XmlWriter(stringWriter);
		Json json = new Json();
		
		debug.assertNoException("No exception during save", new Operation<Void>() {
			@Override
			public Void resultOf() throws Exception {
				xmlWriter.element(ROOT_NAME).attribute(ROOT_NAME, dataName)
				.attribute(ROOT_TIME, flashTime()).attribute(ROOT_COUNT, Integer.toString(plainOldJavaObjects.size));
				for(int i = 0; i < plainOldJavaObjects.size; i++){
					debug.assertContructorEmpty("object has null constructor class", plainOldJavaObjects.get(i).getClass());
					xmlWriter.element(CHILD_ELEMENT).attribute(CHILD_ID, Integer.toString(i));
					xmlWriter.text(json.toJson(plainOldJavaObjects.get(i)));
					xmlWriter.pop();
				}
				xmlWriter.pop();
				xmlWriter.close();
				return null;
			}
		});
		
		handle.writeString(stringWriter.toString(), false);
	}
	
	private String flashTime(){
		long time = TimeUtils.nanoTime();
		long seconds = time / (long)1E9;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		
		class TenLead{
			public String leadedValue(long number){
				if(number < 10) return "0" + number;
				else return Long.toString(number);
			}
		}
		
		TenLead tln = new TenLead();
		
		return hours + ":" + tln.leadedValue(minutes) + ":" + tln.leadedValue(seconds);
	}
}
