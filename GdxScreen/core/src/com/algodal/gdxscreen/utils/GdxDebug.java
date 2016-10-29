package com.algodal.gdxscreen.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;

public class GdxDebug {
	private boolean on;
	private boolean iterate, iterating;
	
	private boolean debug(){
		return
				on &&
				(iterating == true) ? iterate : true;
	}
	
	public void assertTrue(String tag, boolean condition){
		if(debug())
			if(!condition)
				throw new GdxRuntimeException("failed: " + tag);
	}
	
	public void assertFalse(String tag, boolean condition){
		if(debug())
			if(condition)
				throw new GdxRuntimeException("failed: " + tag);
	}
	
	public void assertNotNull(String tag, Object object){
		if(debug())
			if(object == null)
				throw new GdxRuntimeException("failed: " + tag);
	}
	
	public void assertContructorEmpty(String tag, Class<?> clazz){
		if(debug()){
			Constructor[] cstrs = ClassReflection.getConstructors(clazz);
			for(Constructor cstr : cstrs){
				if(cstr.getParameterTypes().length == 0) return;
			}
			throw new GdxRuntimeException("failed: " + tag);
		}
	}
	
	public <T> T assertNoException(String tag, Operation<T> operation) throws GdxRuntimeException{
		try{
			return operation.resultOf();
		}catch(Exception e){
			if(debug())
				throw new GdxRuntimeException("failed with exception: " + tag + ", " + e.getMessage());
			else throw new GdxRuntimeException(e);
		}
	}
	
	public static interface Operation <T>{
		T resultOf() throws Exception;
	}
	
	public void assertStringNotEmpty(String tag, String string){
		if(debug())
			if("".equals(string))
				throw new GdxRuntimeException("failed: " + tag);
	}
	
	public void assertEqualInt(String tag, int value, int expect){
		if(debug())
			if(value != expect)
				throw new GdxRuntimeException("failed: " + tag);
	}
	
	public void assertGreaterEqualInt(String tag, int value, int leastExpect){
		if(debug())
			if(value < leastExpect)
				throw new GdxRuntimeException("failed: " + tag);
	}
	
	public void assertEqual(String tag, Object value, Object expect){
		if(debug())
			if(!value.equals(expect))
				throw new GdxRuntimeException("failed: " + tag);
	}
	
	public void report(String tag, String message){
		if(debug())
			Gdx.app.log(tag, message);
	}
}
