package com.algodal.gdxscreen.entity;

import com.algodal.gdxscreen.utils.GdxDebug;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * GdxEntity is a 2d game object that can be anything as long as
 * it follows the minimum rules: has at least a default entropy, position, size
 * and a input processor.
 */
public class GdxEntity {
	private InputProcessor inputProccessor;
	public final ArrayMap<String, Entropy> entropyMap;
	private Entropy nowEntropy;
	public static GdxDebug debug = new GdxDebug();//better than creating one for each instance
	private final Position position;
	private final Size size;
	
	public GdxEntity(){
		entropyMap = new ArrayMap<>();
		position = new Position();
		size = new Size();
	}
	
	public Position getPosition() {
		return position;
	}
	
	public Size getSize() {
		return size;
	}
	
	public final GdxEntity setPosition(float x, float y){
		position.setX(x);
		position.setY(y);
		return this;
	}
	
	public final GdxEntity setSize(float width, float height){
		size.setWidth(width);
		size.setHeight(height);
		return this;
	}
	
	public InputProcessor getInputProccessor() {
		debug.assertTrue("input processor is not null", inputProccessor != null);
		return inputProccessor;
	}

	public void setInputProccessor(InputProcessor inputProccessor) {
		this.inputProccessor = inputProccessor;
	}

	public final GdxEntity setEntropy(String entropyRef){
		debug.assertTrue("found entropy by reference", entropyMap.containsKey(entropyRef));
		nowEntropy = entropyMap.get(entropyRef);
		return this;
	}
	
	public final Entropy getEntropy(){
		debug.assertTrue("entropy is not null", nowEntropy != null);
		return nowEntropy;
	}
	
	public final GdxEntity putEntropy(String entropyRef, Entropy entropy){
		entropyMap.put(entropyRef, entropy);
		return this;
	}

	public static class Entropy{
		private Animation animation;
		private Sound sound;
		private float time;
		
		public Entropy(Animation animation, Sound sound){
			this.animation = animation;
			this.sound = sound;
			time = 0.0f;
		}
		
		private final boolean animating(){
			if(animation == null) return false;
			return true;
		}
		
		private final boolean sounding(){
			if(sound == null) return false;
			return true;
		}
		
		public final Entropy setPlayMode(PlayMode playMode){
			if(animating()) animation.setPlayMode(playMode);
			return this;
		}
		
		public final TextureRegion nextFrame(float delta){
			debug.assertTrue("animation is available", animating());
			return nextFrame(delta, animation.getAnimationDuration());
		}
		
		public final TextureRegion nextFrame(float delta, float soundLimit){
			TextureRegion frame = null;
			
			if(animating()){
				frame = animation.getKeyFrame(time);
				if(sounding()) if(time == 0.0f) sound.play();
				time += delta;
				if(time >= soundLimit) time = 0.0f;
			}
			
			if(frame == null) throw new GdxRuntimeException("No frame found!");
			return frame;
		}
	}
	
	public static class Position{
		private float x, y;

		public float getX() {
			return x;
		}

		public void setX(float x) {
			this.x = x;
		}

		public float getY() {
			return y;
		}

		public void setY(float y) {
			this.y = y;
		}
		
		public void addX(float dx){
			x += dx;
		}
		
		public void addY(float dy){
			y += dy;
		}
	}
	
	public static class Size{
		private float width, height;

		public float getWidth() {
			return width;
		}

		public void setWidth(float width) {
			this.width = width;
		}

		public float getHeight() {
			return height;
		}

		public void setHeight(float height) {
			this.height = height;
		}
		
		public void addWidth(float displace){
			width += displace;
		}
		
		public void addHeight(float displace){
			height += displace;
		}
	}
}












