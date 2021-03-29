package fox.smoodi.game.entities;
import org.joml.Vector2f;

import fox.smoodi.engine.collision.colliders.AABBCollider2D;
import fox.smoodi.engine.projection.location.Location2D;
import fox.smoodi.engine.visual.sprite.Sprite;
import fox.smoodi.engine.world.objects.DynamicWorldObject;
import fox.smoodi.game.Game;

public class Bullet extends DynamicWorldObject {

	/*
	 * Our class is extending the DynamicWorldObject object contained in our SFoxEngine.
	 * By doing so it automatically gets added to our current world and sent to the renderer.
	 */
	
	//Like every other entity we could need something to see. Therefore we add a sprite.
	public Sprite spr;
	
	//Add some actual positions
	private float x;
	private float y;
	
	//We define our bullet's speed.
	private float speed = 0.25f;
	
	//And a int which is either -1 or 1 to display the direction it is facing. We use a int instead of a bool to be able to multiply our scaling with it.
	private int directionFacing;
		
	public Bullet() {
		
		//When creating a new bullet we set our direction we face by our players facing direction.
		this.directionFacing = Game.player.directionFacing;
		
		//Now we create a new sprite at our players x and y postion and a scaling of 64, 64 with a texture of our bullet and no collider.
		spr = new Sprite(new Location2D(Game.player.x,Game.player.y), 64, 64, Game.textures[5], false);
		
		//Now we assign a new collider. This is not done by the engine because we want to use custom collision box size.
		spr.collider = new AABBCollider2D(spr.position, new Vector2f(0.1f,0.1f));
		
		//Now we tell the collider that we are it's corresponding parent DynamicWorldObject. 
		spr.collider.parent = this;
		
		//And now we set our location to our players one.
		spr.collider.position = new Location2D(Game.player.x,Game.player.y);
		x = Game.player.x;
		y = Game.player.y;
		
		//Now we add our bullet to the bullet list.
		Game.bullets.add(this);
		
	}
	
	@Override
	public void syncedUpdate() {
				
		/*
		 * Entire vertical movement
		 */
		
		x += directionFacing * speed;
		
		
		
		//We flip our sprite according to it's direction.
		spr.scale.setX(32 * directionFacing);
		
		//We set our sprite position according to our direction.
		this.spr.position.setX(x * directionFacing);
		this.spr.position.setY(y);
	
		//And our collider gets moved as well.
		this.spr.collider.position.setX(x);
		this.spr.collider.position.setY(y);

		//If the bullet moves out of sight we destroy it. 
		if(Math.abs(x) > 64) { die(); }
		
	}

	public void die() {
		
		//When this entity dies we remove it from our list and destroy it's sprite.
		//We also call DynamicWorldObject.destroy() a method used to remove this object from the world.
		Game.bullets.remove(this);
		this.spr.destroy();
		this.destroy();
	}
}
