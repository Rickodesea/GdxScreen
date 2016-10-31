package com.algodal.gdxscreen.utils;

import com.algodal.gdxscreen.utils.GdxDebug.Operation;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;

/**
 * GdxPrefs stores Preferences for a Java object and loads it back into the object.
 * The object must be POJO: no extension and only java.lang types.  The fields can only be
 * of java.lang types.  Anything else probably will have no effect.
 *
 * @param <PojoType> A type the mostly satisfies the POJO rule.
 */
public class GdxPrefs<PojoType> {
	private final Class<PojoType> clazz;
	private final PojoType object;
	private final Preferences prefs;
	public final GdxDebug debug;
	
	public GdxPrefs(String prefsName, Class<PojoType> clazz){
		debug = new GdxDebug().setOn(true);
		debug.assertNotNull("prefs name is not null", prefsName);
		debug.assertStringNotEmpty("prefs name is not empty", prefsName);
		debug.assertContructorEmpty("class type has empty contructor", clazz);
		prefs = Gdx.app.getPreferences(prefsName);
		this.clazz = clazz;
		object = debug.assertNoException("no exception is thrown", new Operation<PojoType>() {
			@Override
			public PojoType resultOf() throws Exception {
				return clazz.newInstance();
			}
		});
		upload();
	}
	
	private void download(){
		Field[] fields = ClassReflection.getDeclaredFields(clazz);
		for(Field field : fields) put(field);
	}
	
	private void put(Field field){
		if(field.getType().equals(Integer.class) || field.getType().equals(Short.class))
			prefs.putInteger(field.getName(), debug.assertNoException("get integer", getValue(field, Integer.class)));
		else if(field.getType().equals(Long.class))
			prefs.putLong(field.getName(), debug.assertNoException("get long", getValue(field, Long.class)));
		else if(field.getType().equals(Boolean.class))
			prefs.putBoolean(field.getName(), debug.assertNoException("get boolean", getValue(field, Boolean.class)));
		else if(field.getType().equals(Float.class) || field.getType().equals(Double.class))
			prefs.putFloat(field.getName(), debug.assertNoException("get float", getValue(field, Float.class)));
		else if(field.getType().equals(String.class))
			prefs.putString(field.getName(), debug.assertNoException("get string", getValue(field, String.class)));
	}
	
	private <T> GdxDebug.Operation<T> getValue(Field field, Class<T> clazz){
		return new Operation<T>() {
			@SuppressWarnings("unchecked")
			@Override
			public T resultOf() throws Exception {
				return (T) field.get(object);
			}
		};
	}
	
	private GdxDebug.Operation<Void> setValue(Field field, Object value){
		return new Operation<Void>() {
			@Override
			public Void resultOf() throws Exception {
				field.set(object, value);
				return null;
			}
		};
	}
	
	/**
	 * Saved your object to a preference file with the name you specify and under libGdx's default save
	 * location for preferences.
	 */
	public void save(){
		download();
		prefs.flush();
	}
	
	private void upload(){
		Field[] fields = ClassReflection.getDeclaredFields(clazz);
		for(Field field : fields) take(field);
	}
	
	private void take(Field field){
		if(field.getType().equals(Integer.class) || field.getType().equals(Short.class))
			setValue(field, prefs.getInteger(field.getName(), 0));
		else if(field.getType().equals(Long.class))
			setValue(field, prefs.getLong(field.getName(), 0));
		else if(field.getType().equals(Boolean.class))
			setValue(field, prefs.getBoolean(field.getName(), false));
		else if(field.getType().equals(Float.class) || field.getType().equals(Double.class))
			setValue(field, prefs.getFloat(field.getName(), 0.0f));
		else if(field.getType().equals(String.class))
			setValue(field, prefs.getString(field.getName(), null));

	}
}
