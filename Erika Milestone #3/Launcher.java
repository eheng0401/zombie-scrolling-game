//Contains main... run me to launch the game!
public class Launcher{
	
    //Initializes and launches the game
    public static void main(String[] args){              
        AbstractGame gamePartOne = new HengGameLevelOne();
        gamePartOne.setDebugMode(false);
        gamePartOne.play();  
    }        
    
}
