package com.algodal.gdxscreen.utils;

import com.algodal.gdxscreen.utils.GdxDebug.Operation;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;

/**
 * GdxPrefs stores Preferences for a Java object and loads it back into the object.
 * The object should be POJO: no extension, only java.lang types and definitely a empty
 * constructor.  The fields can ONLY be
 * of java.lang types (no other type is allowed!).  Anything else probably will have NO EFFECT.
 * 
 *
 * @param <PojoType> A type the mostly satisfies the POJO rule.
 */
public class GdxPrefs<PojoType> {
	private final Class<PojoType> clazz;
	private final Preferences prefs;
	
	public final PojoType object;
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
		field.setAccessible(true); //allow private access, important unless the POJO has only public fields
		if(field.getType().equals(Integer.class))
			prefs.putInteger(field.getName(), debug.assertNoException("get integer", getValue(field, Integer.class)));
		if(field.getType().equals(Short.class))
			prefs.putInteger(field.getName(), debug.assertNoException("get integer", getValue(field, Short.class)));
		if(field.getType().equals(int.class))
			prefs.putInteger(field.getName(), debug.assertNoException("get integer", getValue(field, int.class)));
		if(field.getType().equals(short.class))
			prefs.putInteger(field.getName(), debug.assertNoException("get integer", getValue(field, short.class)));
		else if(field.getType().equals(Long.class))
			prefs.putLong(field.getName(), debug.assertNoException("get long", getValue(field, Long.class)));
		else if(field.getType().equals(long.class))
			prefs.putLong(field.getName(), debug.assertNoException("get long", getValue(field, long.class)));
		else if(field.getType().equals(Boolean.class))
			prefs.putBoolean(field.getName(), debug.assertNoException("get boolean", getValue(field, Boolean.class)));
		else if(field.getType().equals(boolean.class))
			prefs.putBoolean(field.getName(), debug.assertNoException("get boolean", getValue(field, boolean.class)));
		else if(field.getType().equals(Float.class) || field.getType().equals(Double.class) || field.getType().equals(float.class) || field.getType().equals(double.class))
			prefs.putFloat(field.getName(), debug.assertNoException("get float", getValue(field, Float.class)));
		else if(field.getType().equals(float.class))
			prefs.putFloat(field.getName(), debug.assertNoException("get float", getValue(field, float.class)));
		else if(field.getType().equals(String.class))
			prefs.putString(field.getName(), debug.assertNoException("get string", getValue(field, String.class)));
		if(field.getType().equals(Character.class) || field.getType().equals(char.class) || field.getType().equals(Byte.class) || field.getType().equals(byte.class))
			prefs.putString(field.getName(), debug.assertNoException("get string", getStringValueFromCharacter(field)));
	}
	
	private GdxDebug.Operation<String> getStringValueFromCharacter(Field field){
		return new Operation<String>() {
			@Override
			public String resultOf() throws Exception {
				if(field.getType().equals(Character.class) || field.getType().equals(char.class)){
					Character c = (Character)field.get(object);
					return c.toString();
				}else if(field.getType().equals(Byte.class) || field.getType().equals(byte.class)){
					Byte b = (Byte)field.get(object);
					return b.toString();
				}
				
				return null;
			}
		};
	}
	
	private GdxDebug.Operation<Void> setStringValueFromCharacter(Field field, String value){
		return new Operation<Void>() {
			@Override
			public Void resultOf() throws Exception {
				if(field.getType().equals(Character.class) || field.getType().equals(char.class)){
					Character c = (value == null ) ? 0 : value.toCharArray()[0];
					field.set(object, c);
				}else if(field.getType().equals(Byte.class) || field.getType().equals(byte.class)){
					Byte b = (value == null ) ? 0 : value.getBytes()[0];
					field.set(object, b);
				}
				
				return null;
			}
		};
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
		field.setAccessible(true); //allow private access, important unless the POJO has only public fields
		if(field.getType().equals(Integer.class) || field.getType().equals(Short.class) || field.getType().equals(int.class) || field.getType().equals(short.class))
			setValue(field, prefs.getInteger(field.getName(), 0));
		else if(field.getType().equals(Long.class) || field.getType().equals(long.class))
			setValue(field, prefs.getLong(field.getName(), 0));
		else if(field.getType().equals(Boolean.class) || field.getType().equals(boolean.class))
			setValue(field, prefs.getBoolean(field.getName(), false));
		else if(field.getType().equals(Float.class) || field.getType().equals(Double.class) || field.getType().equals(float.class) || field.getType().equals(double.class))
			setValue(field, prefs.getFloat(field.getName(), 0.0f));
		else if(field.getType().equals(String.class))
			setValue(field, prefs.getString(field.getName(), null));
		if(field.getType().equals(Character.class) || field.getType().equals(char.class) || field.getType().equals(Byte.class) || field.getType().equals(byte.class))
			setStringValueFromCharacter(field, prefs.getString(field.getName(), null));
	}
}
