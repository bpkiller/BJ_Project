package entity;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Utility.InputUtility;
import Utility.RandomUtility;
import render.IRenderable;
import render.RenderManager;



public class MainLogic {

	//All renderable objectss
	private List<CollidableEntity> onScreenObject = new ArrayList<CollidableEntity>();

	private int zCounter = Integer.MIN_VALUE+1;
	//InitialDelay: ?, Rosen, F, Drug, Cartoon, Music, Bomb
	private int[] nextObjectCreationDelay = {30, 60, 600, 350, 650, 800, 1200};	
	private boolean readyToRender = false; //For dealing with synchronization issue
	private RenderManager renderManager;
	public MainLogic(RenderManager renderManager) {
		this.renderManager = renderManager;
	}
	//Called before enter the game loop
	public synchronized void onStart(){
		
	}
	
	//Called after exit the game loop
	public synchronized void onExit(){
		readyToRender = false;
		onScreenObject.clear();
	}
	
	public void logicUpdate(){
		//Paused
		if(InputUtility.getKeyTriggered(KeyEvent.VK_ENTER)){
			Player.setPause(!Player.isPause());
		}
		
		if(Player.isPause()){
			return;
		}
				
		//Create random target
		createTarget();
		
		//Attack
		if(InputUtility.isMouseLeftDown()) {
			if(getTopEntity() != null) {
				getTopEntity().onClick();
				InputUtility.setMouseLeftDown(false);
			}
			
		}
		System.out.println(Player.getStressLevel());
		//Update target object
		for(CollidableEntity obj : onScreenObject){
			obj.move();
			obj.upSpeed();
		}
		
		//Remove unused image
		for(int i=onScreenObject.size()-1; i>=0; i--){
			if(onScreenObject.get(i).isDestroyed())
				onScreenObject.remove(i);
		}
	}
	
	private void createTarget(){
		for (int k = 0; k < nextObjectCreationDelay.length; k++) {
			if (nextObjectCreationDelay[k] > 0) {
				nextObjectCreationDelay[k]--;
			} 
		}	

				// Random next creation delay
				// set nextObjectCreationDelay
		if (nextObjectCreationDelay[0] <= 0) {
			nextObjectCreationDelay[0] = RandomUtility.random(10, 70);
			QuestionMark x = new QuestionMark(0, 450, zCounter, 3);
			onScreenObject.add(x);
			renderManager.add(x);
		}
		if (nextObjectCreationDelay[1] <= 0) {
			nextObjectCreationDelay[1] = RandomUtility.random(40, 90);
			Rosen y = new Rosen(0, RandomUtility.random(100, 400), zCounter, 5);
			onScreenObject.add(y);
			renderManager.add(y);
		}

		if (nextObjectCreationDelay[2] <= 0) {
			nextObjectCreationDelay[2] = RandomUtility.random(500, 850);
			FThrow f[] = new FThrow[5];
			for (int i = 0; i < f.length; i++) {
				f[i] = new FThrow(0, 90 * i, zCounter, 4);
			}
			for (int i = 0; i < f.length; i++) {
				onScreenObject.add(f[i]);
				renderManager.add(f[i]);
			}
		}
		
			//Increase z counter (so the next object will be created on top of the previous one)
			zCounter++;
			if(zCounter == Integer.MAX_VALUE-1){
				zCounter = Integer.MIN_VALUE+1;
			}
		}

	public CollidableEntity getTopEntity() {
		int z =Integer.MIN_VALUE;
		CollidableEntity entity = null;
		for(CollidableEntity e : onScreenObject) {
			if(e.getZ()>z && e.isMouseOver()) {
				z= e.getZ();
				entity = e;
			}
		}
		return entity;
	}
	
//	public synchronized List<IRenderable> getSortedRenderableObject() {
//		List<IRenderable> sortedRenderable = new ArrayList<IRenderable>();
//		if(!readyToRender) return sortedRenderable;
//		for(CollidableEntity object : onScreenObject){
//			sortedRenderable.add(object);
//		}
//
//		
//		Collections.sort(sortedRenderable, new Comparator<IRenderable>() {
//			@Override
//			public int compare(IRenderable o1, IRenderable o2) {
//				if(o1.getZ() > o2.getZ())
//					return 1;
//				else if(o1.getZ() < o2.getZ())
//					return -1;
//				else
//					return 0;
//			}
//		});
//		return sortedRenderable;
//	}
}
