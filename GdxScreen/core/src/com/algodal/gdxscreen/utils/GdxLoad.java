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
package com.algodal.gdxscreen.utils;

import com.algodal.gdxscreen.utils.GdxDebug.Operation;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Used for loading data from file created with a GdxSave object.
 */
public class GdxLoad {
	public final GdxDebug debug;
	private final FileHandle handle;
	
	public GdxLoad(FileHandle handle){
		debug = new GdxDebug().setOn(true);
		debug.assertNotNull("handle is not null", handle);
		this.handle = handle;
	}
	
	public LoadData load(){
		XmlReader xmlReader = new XmlReader();
		Json json = new Json();
		return debug.assertNoException("No exception during loading", new Operation<LoadData>() {
			@Override
			public LoadData resultOf() throws Exception {
				Element element = xmlReader.parse(handle);
				final LoadData data = new LoadData();
				data.setName(element.getAttribute(GdxSave.ROOT_NAME));
				data.setTime(element.getAttribute(GdxSave.ROOT_TIME));
				data.setCount(Integer.parseInt(element.getAttribute(GdxSave.ROOT_COUNT)));
				data.setPlainOldJavaObjects(new Array<Object>());
				data.setRepresentation(element.toString());
				debug.assertEqual("root element is " + GdxSave.ROOT_ELEMENT, element.getName(), GdxSave.ROOT_ELEMENT);
				for(int i = 0; i < element.getChildCount(); i ++){
					Element child = element.getChild(i);
					debug.assertEqual("child element is " + GdxSave.CHILD_ELEMENT, child.getName(), GdxSave.CHILD_ELEMENT);
					Object childObject = json.fromJson(Object.class, child.getText());
					data.getPlainOldJavaObjects().add(childObject);
				}
				return data;
			}
		});
	}
	
	public static class LoadData{
		private Array<Object> plainOldJavaObjects;
		private String time;
		private String name;
		private int count;
		private String representation;
		
		/**
		 * List of java objects that was saved.  Their field values are specific to what
		 * they were at the time of saving.
		 * @return List of Java objects.
		 */
		public final Array<Object> getPlainOldJavaObjects() {
			return plainOldJavaObjects;
		}
		
		final void setPlainOldJavaObjects(Array<Object> plainOldJavaObjects) {
			this.plainOldJavaObjects = plainOldJavaObjects;
		}
		
		/**
		 * 
		 * @return Time the data was saved.
		 */
		public final String getTime() {
			return time;
		}
		
		final void setTime(String time) {
			this.time = time;
		}
		
		public final String getName() {
			return name;
		}
		
		/**
		 * 
		 * @param name Name assigned to the save data.
		 */
		final void setName(String name) {
			this.name = name;
		}
		
		/**
		 * 
		 * @return Number of objects that were saved.
		 */
		public final int getCount() {
			return count;
		}
		
		final void setCount(int count) {
			this.count = count;
		}

		/**
		 * @return the string representation of the data.
		 */
		public String getRepresentation() {
			return representation;
		}

		final void setRepresentation(String representation) {
			this.representation = representation;
		}
	}
}
