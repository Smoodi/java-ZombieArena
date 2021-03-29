package fox.smoodi.game;
import java.awt.EventQueue;
import java.util.LinkedList;
import java.util.List;

import org.joml.Vector2f;

import fox.smoodi.Util;
import fox.smoodi.engine.SFoxEngine;
import fox.smoodi.engine.audio.AudioSource;
import fox.smoodi.engine.collision.colliders.AABBCollider2D;
import fox.smoodi.engine.game.GameClass;
import fox.smoodi.engine.input.InputManager;
import fox.smoodi.engine.input.modules.KeyboardKey;
import fox.smoodi.engine.model.transformation.scaling.TransformScaling2D;
import fox.smoodi.engine.projection.camera.Camera2D;
import fox.smoodi.engine.projection.location.Location2D;
import fox.smoodi.engine.shader.NoShaderShader;
import fox.smoodi.engine.shader.Shader;
import fox.smoodi.engine.visual.sprite.Sprite;
import fox.smoodi.engine.visual.texture.Texture;
import fox.smoodi.engine.visual.texture.TextureHandler;
import fox.smoodi.engine.world.World;
import fox.smoodi.engine.world.WorldMode;
import fox.smoodi.engine.world.tiles.TileSet;
import fox.smoodi.game.entities.Bullet;
import fox.smoodi.game.entities.Player;
import fox.smoodi.game.entities.Zombie;
import fox.smoodi.launcher.afterGameMenu;

/*
 * Our game class - its interface is the GameClass interface provided by the SFoxEngine which contains
 * a close(), init(), loop(), syncedLoop() and recieveWindow() method.
 */

public class Game implements GameClass {

	
	World world;
	
	//We create some public collider lists where we can iterate through to check collisions with other colliders.
	public static List<AABBCollider2D> playerCollisions = new LinkedList<AABBCollider2D>(); 
	public static List<AABBCollider2D> zombies = new LinkedList<AABBCollider2D>();
	
	//And a list of bullets we can check with.
	public static List<Bullet> bullets = new LinkedList<Bullet>();
	
	//We create some basic counter.
	private static int c_fps = 0;
	private int spawnTimer;
	
	//We create a public player object which is containing our player in the game.
	public static Player player;
	
	//We create a public texture array which is going to hold all textures loaded in the game.
	public static Texture[] textures = new Texture[7]; 
	
	//We create a public Shader object which is going to contain the shader used when a entity is being hit.
	public static Shader redShader;
	
	//We create a public Integer in order to save our score.
	public static int score = 0;
	
	//We create a boolean to check if the button has already been pressed.
	private boolean shot_already = false;
	
	//We create a boolean to check whether the game has ended. 
	public static boolean gameOver = false;
	
	//We create a float to save our master volume. This is going to be changed by the startup frame.
	public float masterVolume = 1f;
	
	//In order to access some non-static methods and stuff we add a static reference to ourself.
	public static Game myGame;
	
	
	/*
	 * This is being fired when the game is supposed to end.
	 * (non-Javadoc)
	 * @see fox.smoodi.engine.game.GameClass#close()
	 */
	@Override
	public void close() {
	
		//We tell the engine to end the game.
		SFoxEngine.endGame();
		
		//Now we create a new afterGame menu and tell the frame the score we reached.
		afterGameMenu frame = new afterGameMenu();
		frame.label.setText("Your score: "+score +"  ");
		frame.setVisible(true);
		
	}
	
	/*
	 * This is being fired when the player's hp <= 0;
	 */
	public static void gameOver() {
		System.out.println("");
		System.out.println("-------------------------------------------------");
		System.out.println("You died. Game over.");
		System.out.println("You scored "+score+" points.");
		
		c_fps = 0;
		player.die();
		
		gameOver = true;		

	}

	/*
	 * This is being fired when the game is being initialized by the engine. It is only
	 * fired once.
	 * It loads all necessary textures for the game to run and creates a new World object so the 
	 * engine knows what is going on.
	 * (non-Javadoc)
	 * @see fox.smoodi.engine.game.GameClass#init()
	 */
	@Override
	public void init() {
		
		//When initializing the game (basically fired in the engine when constructing the Game) we set our own static reference.
		myGame = this;
		
		//Now we need to load our textures in order to be able to see them.
		loadTextures();
		
		//We create a new world and tell the that this is the world it has to be working with.
		SFoxEngine.world = new World(WorldMode.BASIC2D, new TransformScaling2D(1,1), 32, TextureHandler.getTexture("background"));
		this.world = SFoxEngine.world;
			
			
		//Now we create a new camera and assign it to the engines game camera.
		//Our engine needs to know from what perspective it has to render the world it deals with. 
		SFoxEngine.setGameCamera(new Camera2D());
	
		//Because our world always uses a background when using 2D world with the scaling of x = 1 and y = 1 we need to adjust this in order to fit our
		//background to the camera.
		//Therefore we add additional world initialization settings containing transformation information for two-dimensional objects.
		//Additionally we need to tell the engine we don't want to use a shader on our background.
		//So we use the NoShaderShader. It is basically a shader without any color correction. It only draws the object according to it's perspective.
		this.world.bgd.addInitSettings(new TransformScaling2D(SFoxEngine.gameCamera.getProjectionMatrix().getWidth()*3,SFoxEngine.gameCamera.getProjectionMatrix().getHeight()*3), NoShaderShader.object);
		
		//Now we start playing music to the player.
		playMusic();
		
		//We create our terrain.
		createBlocks();
		
		//And create a new Shader object out of the shader files (shd/redHit.sfvs and shd/redHit.sffs) and compile it.
		redShader = new Shader("./shd/redHit");
		
		//Now we add a new player object.
		player = new Player();
		
		//And create a new spawnTimer for the first zombie to spawn. It is supposed to be random. 60frames (1 second) up to 240 frames (4 seconds)
		spawnTimer = Util.inRange(60, 240);
		
		//For debugging we print out our world object;
		System.out.println(world.toString());
	}

	private void playMusic() {
		
		//We load all our sounds in the memory by accessing the AudioManager in the world object.
		//The engine automatically stores the sound with a unique name we can choose.
		try {
			this.world.audio.loadSound("music", "./resources/audio/title.ogg");
			this.world.audio.loadSound("low", "./resources/audio/low.ogg");
			this.world.audio.loadSound("bullet", "./resources/audio/bullet.ogg");
			this.world.audio.loadSound("hit1", "./resources/audio/hit1.ogg");
			this.world.audio.loadSound("hit2", "./resources/audio/hit2.ogg");
			this.world.audio.loadSound("hit3", "./resources/audio/hit3.ogg");
			System.out.println("Loaded sounds successfully.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Now we need a source our camera (which automatically creates a listener) can listen to.
		//The engine automatically stores the source with a unique name we can choose.
		this.world.audio.createSource("msc", true, false);
		this.world.audio.createSource("lowHealth", true, false);
		this.world.audio.createSource("bulletSource", false, false);
		this.world.audio.createSource("hitSource", false, false);
		
		//Now we set our all sources volume to the master volume.
		this.world.audio.getSource("msc").setGain(masterVolume);;
		this.world.audio.getSource("lowHealth").setGain(masterVolume);;
		this.world.audio.getSource("bulletSource").setGain(masterVolume);;
		this.world.audio.getSource("hitSource").setGain(masterVolume);;
		
		//And we start playing the sound "music" on our source "msc".
		this.world.audio.playSound("music", "msc");
		System.out.println("Playing music.");
	}

	private void loadTextures() {
		
		//Now we load a bunch of new textures by accessing the engines texture handler.
		//Again our engine stores all the textures with a unique name to them we can choose.
		textures[0] = TextureHandler.loadTexture("background", "./resources/sprites/background.png");
		textures[1] = TextureHandler.loadTexture("dirt", "./resources/sprites/grass.png");
		textures[5] = TextureHandler.loadTexture("bullet", "./resources/sprites/bullet.png");
		textures[6] = TextureHandler.loadTexture("gameover", "./resources/sprites/gameover.png");
		
		//Now we load a bunch of new animated textures by loading multiple textures (engine intern) and returning
		//one AnimatedTexture (extending Texture) object we store in our texture array again.
		//We tell it to load e.g. 100 frames (0-99) of a zombie walking and tell him to animate it with 60fps
		//and we tell him the path and the filename with it's extension.
		textures[2] = TextureHandler.loadAnimatedTexture(100, 60, "./resources/sprites/zombie/", "zwalk", "png");
		textures[3] = TextureHandler.loadAnimatedTexture(60,60,"./resources/sprites/player/walk/", "walk", "png");
		textures[4] = TextureHandler.loadAnimatedTexture(60,60,"./resources/sprites/player/idle/", "idle", "png");
		
		
		System.out.println("Loading sprites successfully.");
		
	}

	@Override
	public void loop() {

		//Everytime the engine runs a new game tick we check if the player wants to force a game close by accessing our engines InputManager.
		if(InputManager.isKeyActive(KeyboardKey.ESCAPE)) {
			close();
		}
	}

	//Not needed in this game.
	@Override
	public void recieveWindow(long arg0) {

	}

	//The synced loop is basically run every time a new frame is being drawn. Therefore 60 times a second. It is used here to make the code not too complicated by using
	//delta time to provide the same speeds independent of the computers calculation speed.
	@Override
	public void syncedLoop() {
		
		if(!gameOver) {
			
			//As long as our game is not over we print out our FPS counter and HP and score in the console every 60 frames (1 second).
			
			c_fps++;
			if(c_fps > 60) {
				System.out.println("");
				System.out.println("-------------------------------------------------");
				System.out.println("FPS: "+SFoxEngine.getWindow().getFPS());
				System.out.println("-------------------------------------------------");
				System.out.println("HP: "+player.health);
				System.out.println("Score: "+score);
				c_fps = 0;
			}
			
			//We also reduce our spawn timer for the next zombie to spawn.
			
			spawnTimer--;
			
			//Once we hit 0 we create a new zombie and generate a new random spawn timer. 
			if(spawnTimer <= 0) {
				new Zombie();
				spawnTimer = Util.inRange(60, 240);
			}
			
			//Now we check if the player is currently pressing space to fire.
			boolean shot = InputManager.isKeyActive(KeyboardKey.SPACE);
			
			//If the player is pressing the button and has not been holding the button we shoot a bullet. 
			if(shot != shot_already && shot) {
				new Bullet();
				
				//Now we check if the source is already playing a sound and if so we stop it.
				AudioSource s = world.audio.getSource("bulletSource");
				if(s.isPlaying()) { s.stop(); }
				//And play a new bullet sound.
				world.audio.playSound("bullet", "bulletSource");
			}
			//Now we need to set our button holding value in order for the stuff to work.
			shot_already = shot;
		}
		else {
			
			//If the game is over we use our counter to wait a time before closing the game.
			
			c_fps++;
			if(c_fps == 1) {
				
				//Once we hit the first tick in our death we create a new sprite and set the camera to our new position.
				SFoxEngine.gameCamera.getPosition().setX(0);
				SFoxEngine.gameCamera.getPosition().setY(0);
				new Sprite(new Location2D(0, 0),
						256, 128, Game.textures[6], false);
				
			}
			if(c_fps == 180) {
				
				//After 3 seconds we close the game.
				close(); 
			}
		}
		
	}

	private void createBlocks() {
		
		//We create a new TileSet to imporove render performance and set it's origin to 0,0.
		//Every item has a scale of 32 by 32 in there. A tileSet can hold up to 16 items.

		TileSet ground = new TileSet(new Location2D( 0, 0 ), 32, 32);
				
		//Now we add our entire platform.
		ground.add("dirt", new Location2D(-1,0), false);
		ground.add("dirt", new Location2D(-2,0), false);
		ground.add("dirt", new Location2D(-3,0), false);
		ground.add("dirt", new Location2D(-4,0), false);
		ground.add("dirt", new Location2D(-5,0), false);
		ground.add("dirt", new Location2D(-6,0), false);
		ground.add("dirt", new Location2D(-7,0), false);
		ground.add("dirt", new Location2D(0,0), false);
		ground.add("dirt", new Location2D(1,0), false);
		ground.add("dirt", new Location2D(2,0), false);
		ground.add("dirt", new Location2D(3,0), false);
		ground.add("dirt", new Location2D(4,0), false);
		ground.add("dirt", new Location2D(5,0), false);
		ground.add("dirt", new Location2D(6,0), false);
		ground.add("dirt", new Location2D(7,0), false);
		
		ground.print();
		
		
		/**
		 * Now we create the colliders for the player.
		 * Because our platform is not containing any gaps we can create one collider for it instead of multiple for each block.
		 */
		playerCollisions.add(new AABBCollider2D(new Location2D(0,0), new Vector2f(64f, 0.25f)));
		
		//Now we add our side borders.
		playerCollisions.add(new AABBCollider2D(new Location2D(8,0), new Vector2f(0.25f, 64f)));
		playerCollisions.add(new AABBCollider2D(new Location2D(-8,0), new Vector2f(0.25f, 64f)));
	}
	
}
