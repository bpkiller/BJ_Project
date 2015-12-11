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

	//All renderable objects
	private List<CollidableEntity> onScreenObject = new ArrayList<CollidableEntity>();

	private int zCounter = Integer.MIN_VALUE+1;
	private int nextObjectCreationDelay;	
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
		
		
		//Update target object
		for(CollidableEntity obj : onScreenObject){
			obj.move();
		}
		
		//Remove unused image
		for(int i=onScreenObject.size()-1; i>=0; i--){
			if(onScreenObject.get(i).isDestroyed())
				onScreenObject.remove(i);
		}
	}
	
	private void createTarget(){
		if(nextObjectCreationDelay > 0){
			nextObjectCreationDelay--;
		}else{
			//Random next creation delay
			// set nextObjectCreationDelay 
			nextObjectCreationDelay = RandomUtility.random(10, 100);
			QuestionMark x =  new QuestionMark(0, 0, zCounter, 10);
			onScreenObject.add(x);
			renderManager.add(x);
			Player.setStressLevel(Player.getStressLevel()+20);
			System.out.println(Player.getStressLevel());
			//Increase z counter (so the next object will be created on top of the previous one)
			zCounter++;
			if(zCounter == Integer.MAX_VALUE-1){
				zCounter = Integer.MIN_VALUE+1;
			}
		}
	}
	
	public synchronized List<IRenderable> getSortedRenderableObject() {
		List<IRenderable> sortedRenderable = new ArrayList<IRenderable>();
		if(!readyToRender) return sortedRenderable;
		for(CollidableEntity object : onScreenObject){
			sortedRenderable.add(object);
		}

		
		Collections.sort(sortedRenderable, new Comparator<IRenderable>() {
			@Override
			public int compare(IRenderable o1, IRenderable o2) {
				if(o1.getZ() > o2.getZ())
					return 1;
				else if(o1.getZ() < o2.getZ())
					return -1;
				else
					return 0;
			}
		});
		return sortedRenderable;
	}
}