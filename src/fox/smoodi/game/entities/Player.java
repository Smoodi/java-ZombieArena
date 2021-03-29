package fox.smoodi.game.entities;
import fox.smoodi.Util;
import fox.smoodi.engine.SFoxEngine;
import fox.smoodi.engine.collision.CollisionHandler;
import fox.smoodi.engine.collision.colliders.AABBCollider2D;
import fox.smoodi.engine.input.InputManager;
import fox.smoodi.engine.input.modules.KeyboardKey;
import fox.smoodi.engine.projection.location.Location2D;
import fox.smoodi.engine.shader.NoShaderShader;
import fox.smoodi.engine.visual.sprite.Sprite;
import fox.smoodi.engine.world.objects.DynamicWorldObject;
import fox.smoodi.game.Game;

public class Player extends DynamicWorldObject {

	/*
	 * Once again we extend the DynamicWorldObject object to automatically add it to our current world.
	 */
	
	//We initialize all private stuff and set our configuration of the gravity and stuff.
	private Sprite spr;
	private float last_x;
	private float last_y;
	private float speed_x = 0;
	private float speed_y = 0;
	private float speed = 0.075f;
	private float gravityForce = 0.00125f;
	private float maxGravity = 0.05f;
	private float jumpForce = 0.055f;
	private int redTimer = -1;
	private boolean wasAlreadyLow;
	
	//Those values need to be public because other entities need to check them.
	//I personally prefer using field instances for values like health, position and directionFacing,
	//instead of getters.
	public float x;
	public float y;
	public int directionFacing = 1;
	public int health = 100;
	
	public Player() {
		
		/*
		 * Load player textures
		 */
		
		//Our does not start low when he spawns.
		wasAlreadyLow = false;
		
		//We create a new sprite with an automatic collider generation. (spr.collider)
		spr = new Sprite(new Location2D(0,2), 32, 64, Game.textures[4], true);
		spr.collider.position = new Location2D(0,2);
		x = 0;
		y = 2;
		last_x = 0;
		last_y = 2;
	}
	
	@Override
	public void syncedUpdate() {
		
		
		//If our red timer (our hit cooldown) is not undefined (-1) we reduce it every frame.
		if(redTimer != -1) {
			redTimer --;
			
		}
		
		//If we hit 0 on our counter we set our shader back to normal so we do not get drawn red anymore.
		if(redTimer == 0) {
			spr.setShader(NoShaderShader.object);
		}
		
		/*
		 * Entire vertical movement
		 */
		

		speed_y -= gravityForce;
		//Make sure we are not falling through blocks
		float final_y;

		//We move our collider up a bit so we check vertically. I only implemented to ask whether two colliders intercept each other.
		this.spr.collider.position.setY(this.spr.collider.position.getY()-0.05f);
		
		//We check for collision.
		boolean collision_ground = checkCollision();

		//If we are standing on the ground...
		if(collision_ground) {
			
			//...we set our own speed on the y axis on 0.
			speed_y = 0;
			
			//And we can check whether we should jump.
			if(InputManager.isKeyActive(KeyboardKey.W)) 
			{
				speed_y += jumpForce;
			}
		}
		
	
		final_y = last_y + speed_y;
		
		this.y = final_y;
		
		this.spr.position.setY(y);

		/**
		 * Entire horizontal movement
		 */
		
		//We calculate which direction we need and generate a factor out of it to multiply our speed with.
		
		int d_pressed = (InputManager.isKeyActive(KeyboardKey.D)) ? 1 : 0;
		int a_pressed = (InputManager.isKeyActive(KeyboardKey.A)) ? 1 : 0;
		
		//This can become -1,0 and 1.
		int directionFactor = -a_pressed + d_pressed;
		
		//If our DirectionFactor is not 0 we set our new direction we face in to the direction we also move to.
		if(directionFactor != 0) { directionFacing = directionFactor; }
		
		//Now we set our speed to our direction factor.
		speed_x = directionFactor * speed;
		
		//Our new position is our old + our speed.
		float final_x = x + speed_x;
		this.x = final_x;
		
		//Now we check for horizontal wall collision by moving him up in the air and check for a collision with a world collider.
		this.spr.collider.position.setX(x);
		this.spr.position.setX(x * directionFacing);
		this.spr.position.setY(0.751375f);
		this.spr.collider.position.setY(0.751375f);
		//If he is stuck in a wall now we move him out of the wall.
		while(checkCollision()) {
			this.x -= 0.005f*directionFacing;
			this.spr.position.setX(x * directionFacing);
			this.spr.collider.position.setX(x);
		}
		this.spr.position.setY(y);
		
		//Once again we flip our player.
		spr.scale.setX(32 * directionFacing);
		
		
		/*
		 * Make our Camera follow our player.
		 */
		
		if(!Game.gameOver) {
			SFoxEngine.gameCamera.getPosition().setX(-x*64f);
			SFoxEngine.gameCamera.getPosition().setY(-y*129);
		}
		
		
		//Choose animation
		
		//If our player is moving we need to make him walk. 
		if(speed_x != 0) {
			spr.setTexture(Game.textures[3]);
		} else {
			spr.setTexture(Game.textures[4]);
		}
		
		//This are our positions. Give them to the collider.
		this.spr.collider.position.setX(x);
		this.spr.collider.position.setY(y);

		
		//Now we check if he get damage and are not on cooldown.
		if(checkDamage() && redTimer < 1) {
			
			//We reduce our health by a random amount.
			health -= Util.inRange(1, 10);
			
			//And give the player 1 second cooldown.
			redTimer = 60;
			
			//And play a random sound.
			switch(Util.inRange(1, 3)) {
				case 1:
					SFoxEngine.world.audio.playSound("hit1", "hitSource");
				break;
				case 2:
					SFoxEngine.world.audio.playSound("hit2", "hitSource");
				break;
				case 3:
					SFoxEngine.world.audio.playSound("hit3", "hitSource");
				break;
			}

			//We set our shader.
			spr.setShader(Game.redShader);
			
			//If our player's new health is 0 or even below we tell the game to be over.
			if(health <= 0)
				Game.gameOver();
		}
				
		last_x = x;
		last_y = y;
		
		
		//If our player is getting low now we start playing a heartbeat sound.
		if(!wasAlreadyLow && health < 45) {
			SFoxEngine.world.audio.playSound("low", "lowHealth");
			SFoxEngine.world.audio.getSource("msc").setGain(0.05f*Game.myGame.masterVolume);
		}
	}
	
	/**
	 * Checks for collision.
	 * @return true if the entity is colliding with the world. Otherwise false.
	 */
	private boolean checkCollision( ) {
		for(AABBCollider2D c : Game.playerCollisions) {
			boolean i = CollisionHandler.collide(spr.collider, c);
			if(i) {
				return i;
				
			}
		}
		return false;
	}
	

	/**
	 * Checking for zombie collsion.
	 * @return true if the entity is colliding with a zombie. Otherwise false.
	 */
	private boolean checkDamage() {
		for(AABBCollider2D c : Game.zombies) {
			boolean i = CollisionHandler.collide(spr.collider, c);
			if(i) {
				return i;
				
			}
		}
		return false;
	}
	
	//We destroy our entity and sprite.
	public void die() {
		spr.destroy();
		this.destroy();
	}
}
