import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class HengGameLevelThree extends AbstractGame{
	//Dimensions of game window
    private static final int DEFAULT_WIDTH = 900;
    private static final int DEFAULT_HEIGHT = 600;  
    
    //Starting Player coordinates
    private static final int STARTING_PLAYER_X = 0;
    private static final int STARTING_PLAYER_Y = 100;
    
    //Score needed to win the game
    private static final int SCORE_TO_WIN = 50;
    
    //Maximum that the game speed can be increased to
    //(a percentage, ex: a value of 300 = 300% speed, or 3x regular speed)
    private static final int MAX_GAME_SPEED = 300;
    //Interval that the speed changes when pressing speed up/down keys
    private static final int SPEED_CHANGE = 20;    
 
    private static final String INTRO_SPLASH_FILE = "assets/day_two_finished.png";        
    private static final String WIN_SCREEN = "assets/win_screen.png";   
    private static final String LOSE_SCREEN = "assets/lose_screen.png";
    private static final String BACKGROUND_THREE = "assets/background_three.png";
    private static final String GREEN_SMOKE = "assets/green_smoke.png";
    //Key pressed to advance past the splash screen
    public static final int ADVANCE_SPLASH_KEY = KeyEvent.VK_ENTER;
    
    //Interval that Entities get spawned in the game window
    //ie: once every how many ticks does the game attempt to spawn new Entities
    private static final int SPAWN_INTERVAL = 45;
    //Maximum Entities that can be spawned on a single call to spawnEntities
    private static final int MAX_SPAWNS = 3;
                                  
   
    //A Random object for all your random number generation needs!
    public static final Random rand = new Random();

    //Player's current score
    private int score;
    
    //Stores a reference to game's Player object for quick reference
    //(This Player will also be in the displayList)
    private Player player;
    
    public HengGameLevelThree(){
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
    
    public HengGameLevelThree(int gameWidth, int gameHeight){
        super(gameWidth, gameHeight);
    }
    
    //Performs all of the initialization operations that need to be done before the game starts
    protected void preGame(){
    	this.setBackgroundImage(BACKGROUND_THREE);
        this.setBackgroundColor(Color.BLACK);
        player = new Player(STARTING_PLAYER_X, STARTING_PLAYER_Y);
        displayList.add(player); 
        score = 0;
        setTitleText("~ DAY TWO COMPLETED! ~");	// change title bar
        setSplashImage(INTRO_SPLASH_FILE);	// insert splash page
    }
    
    //Called on each game tick
    protected void updateGame(){
        //scroll all scrollable Entities on the game board
        scrollEntities();   
        //Spawn new entities only at a certain interval
        if (ticksElapsed % SPAWN_INTERVAL == 0)            
        {
        	scrollEntities();
        	spawnEntities();
        }
        //Update the title text on the top of the window
        setTitleText("~ DAY THREE ~ HP: " + player.getHP() + ", Points: " + score);
        
    }
    
    //Scroll all scrollable entities per their respective scroll speeds
    //How do you know what Entities to scroll?
    //finish me!
    protected void scrollEntities()
    {
        for (int i = 0; i < displayList.size(); i++)
		{
			if (displayList.get(i) instanceof Scrollable)
			{
				((Scrollable)displayList.get(i)).scroll();
			}
			if (displayList.get(i) instanceof Consumable && player.isCollidingWith(displayList.get(i)))
			{
				handleCollision((Consumable) displayList.get(i));
			}
		}
	}
    
    //Spawn new Entities on the right edge of the game board
    //implement me!  
    protected void spawnEntities()
    {
        Entity newSpawn;
        int numSpawn = rand.nextInt(MAX_SPAWNS)+1; // rando int from 0 to 3
        boolean collides = false;
       
        for (int i = 0; i<numSpawn; i++) // spawning entities based on numSpawn
        {
        	int type = rand.nextInt(10)+1;
			if (type < 2)
			{
				newSpawn = new RareGet(DEFAULT_WIDTH, rand.nextInt(DEFAULT_HEIGHT-50-50)+50);
			}
		   
			else if (type < 7)
			{
				newSpawn = new Get(DEFAULT_WIDTH, rand.nextInt(DEFAULT_HEIGHT-50-50)+50);
			}
			else if (type <9)
			{
				newSpawn = new Avoid(DEFAULT_WIDTH, rand.nextInt(DEFAULT_HEIGHT-110-110)+110);
			}
			else
			{
				newSpawn = new Avoid(DEFAULT_WIDTH, rand.nextInt(DEFAULT_HEIGHT-100-100)+100, 100, 100, GREEN_SMOKE);
				((Avoid)newSpawn).setScrollSpeed(10);
			}
		   
			while (checkCollision(newSpawn) != null)
			{
				newSpawn.setY(rand.nextInt(DEFAULT_HEIGHT-newSpawn.getHeight()-100)+80);
			}
		   
			displayList.add(newSpawn); 
        }
    }
   
    
    /** make arraylist of consumables instead of entity? */
    //Called whenever it has been determined that the Player collided with a consumable
    protected void handleCollision(Consumable collidedWith){
        //throw new IllegalStateException("Hey 102 Student! You need to implement handleCollision in BasicGame!"); 
        
        player.modifyHP(collidedWith.getDamageValue());
        score = score + collidedWith.getPointsValue();
        displayList.remove(displayList.indexOf(collidedWith));
    }
    
    
    //Called once the game is over, performs any end-of-game operations
    protected void postGame(){
        if (player.getHP()<1)
        {
        	super.setSplashImage(LOSE_SCREEN);
        	super.setTitleText("GAME OVER! - YOU LOSE!");
        }
        else
        {
        	super.setSplashImage(WIN_SCREEN);
        	super.setTitleText("GAME OVER! - YOU WIN!");
        }
    }
    
    //Determines if the game is over or not
    //Game can be over due to either a win or lose state
    protected boolean isGameOver(){
    	if (player.getHP()<1 || score>= SCORE_TO_WIN)
    		return true;
        return false;
    }
     
    //Reacts to a single key press on the keyboard
    //Override's AbstractGame's handleKey
    protected void handleKey(int key){
        //first, call AbstractGame's handleKey to deal with any of the 
        //fundamental key press operations
        super.handleKey(key);
        setDebugText("Key Pressed!: " + KeyEvent.getKeyText(key));
        //if a splash screen is up, only react to the advance splash key
        if (getSplashImage() != null){
            if (key == ADVANCE_SPLASH_KEY)
                super.setSplashImage(null);
            return;
        }
        else // (getSplashImage() == null) 
        {
        	if (key == KEY_PAUSE_GAME)	// pauses/unpauses game
        	{
        		if (isPaused)
        			isPaused = false;
        		else
        			isPaused = true;
        	}
        	if (!isPaused)
        	{
				if (key == UP_KEY && player.getY()>0)	// player movement
				{
					if (player.getY()-player.getMovementSpeed() > 50-player.getMovementSpeed()) // ensures player does not move out of frame of gamewindow
						player.setY(player.getY()-player.getMovementSpeed());
				}
				if (key == DOWN_KEY && player.getY() < getWindowHeight()-player.getHeight())
				{
					if (player.getY()+player.getMovementSpeed() > getWindowHeight()-player.getHeight())
						player.setY(getWindowHeight()-player.getHeight());	// ensures player does not move out of frame of gamewindow
					else
						player.setY(player.getY()+player.getMovementSpeed());
				}
				if (key == LEFT_KEY && player.getX()>0)
				{
					if(player.getX()-player.getMovementSpeed()<0)	// ensures player does not move out of frame of gamewindow
						player.setX(0);
					else
						player.setX(player.getX()-player.getMovementSpeed());
				}
				if (key == RIGHT_KEY && player.getX()<getWindowWidth()-player.getWidth())
				{
					if (player.getX()+player.getMovementSpeed()> getWindowWidth()-player.getWidth())
						player.setX(getWindowWidth()-player.getWidth());	// ensures player does not move out of frame of gamewindow
					else	
						player.setX(player.getX()+player.getMovementSpeed());
				}
				
				if (key == SPEED_DOWN_KEY && getGameSpeed()-SPEED_CHANGE >0)  // THIS SHIT MAKES NO SENSE
				{
					setGameSpeed(getGameSpeed()-SPEED_CHANGE);
				}
				if (key == SPEED_UP_KEY && getGameSpeed()+SPEED_CHANGE<=MAX_GAME_SPEED)
				{
					setGameSpeed(getGameSpeed()+SPEED_CHANGE);
				}
			}
		}
    }    

	
	
}