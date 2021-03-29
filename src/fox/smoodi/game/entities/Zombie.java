package fox.smoodi.game.entities;
import fox.smoodi.Util;
import fox.smoodi.engine.SFoxEngine;
import fox.smoodi.engine.collision.CollisionHandler;
import fox.smoodi.engine.collision.colliders.AABBCollider2D;
import fox.smoodi.engine.projection.location.Location2D;
import fox.smoodi.engine.shader.NoShaderShader;
import fox.smoodi.engine.visual.sprite.Sprite;
import fox.smoodi.engine.world.objects.DynamicWorldObject;
import fox.smoodi.game.Game;

public class Zombie extends DynamicWorldObject{

	/*
	 * Once again we extend the DynamicWorldObject object to automatically add it to our current world.
	 */
	
	//Constructing our field-variables.
	private Sprite spr;
	private float x;
	private float y;
	private int directionFacing = 1;
	private int redTimer = -1;

	//
	private float speed = Util.inRange(40,480) / 10000f;
	private int health = Util.inRange(40,180);
	
	
	public Zombie() {
		
		//We generate a new random x spawn position.
		float spawnX = (float)Util.inRange(-700, 700)/100;
		
		//We create a new sprite at that position and create a new collider with their position at the new spawn.
		spr = new Sprite(new Location2D(spawnX,0.75f), 32, 64, Game.textures[2], true);
		spr.collider.position = new Location2D(spawnX,0.75f);
		x = spawnX;
		y = 0.75f;
		
		//We add our zombie collider to the zombie collider list.
		Game.zombies.add(spr.collider);
		
	}
	
	@Override
	public void syncedUpdate() {
				
		/*
		 * Entire vertical movement
		 */

		
		//Same as in the Player object. We reduce our cooldown timer if it is not -1.
		if(redTimer != -1) {
			redTimer --;
			
		}
		
		//If we hit 0 we reset our shader.
		if(redTimer == 0) {
			spr.setShader(NoShaderShader.object);
		}
		
		//We get our players x position...
		float p_x = Game.player.x;
		
		//...to check in which direction we need to walk in order to get to the player.
		if(p_x > x) { directionFacing = 1; }
		else { directionFacing = -1; }
		
		//If our distance between the player and the zombie is smaller than our speed we move per frame we do not move anymore to
		//avoid swapping direction permanently when near the player.
		if(Math.abs(p_x - x) >= speed) {
			x += directionFacing * speed;
		}
		
		//Now we check if we are colliding with a bullet
		Bullet c = checkBullets();
		
		//If our bullet we collide with is not null we collide with a bullet.
		if(c != null && redTimer < 1) {
			
			
			//We now reduce our health by a random amount...
			health -= Util.inRange(40, 80);
			
			//... and set the zombie cooldown on a half second in order to prevent from future damage.
			redTimer = 30;


			//... now we tell the engine it can try to destroy the object if possible. (will not always destroy it if the engine
			//has not been running it's garbage collector yet. It will be put on a list to get destroyed tho).
			c.die();
			
			//We set our shader and check whether the health is 0 or even below. If so the entity has to be destroyed.
			spr.setShader(Game.redShader);
			if(health <= 0)
				die();
		}
		
		//Flip our sprite.
		spr.scale.setX(32 * -directionFacing);
		
		//Move our sprite according to our direction.
		this.spr.position.setX(x * -directionFacing);
		this.spr.position.setY(y);
		
		//Move our collider.
		this.spr.collider.position.setX(x);
		this.spr.collider.position.setY(y);

		
	}
	
	private void die() {
		
		//If our zombie dies we add 10 points to our score.
		
		Game.score += 10;
		
		//We remove our zombie from the collider list.
		Game.zombies.remove(spr.collider);
		
		//Now we destory the sprite and the object.
		spr.destroy();
		this.destroy();
	}

	
	/**
	 * Checks if the Zombie is colliding with a bullet.
	 * @return The bullet object it is colliding with - otherwise null.
	 */
	private Bullet checkBullets() {
		for(Bullet c : Game.bullets) {
			boolean i = CollisionHandler.collide(spr.collider, c.spr.collider);
			if(i) {
				return c;
				
			}
		}
		return null;
	}
}
