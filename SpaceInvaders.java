import java.util.*;
import com.googlecode.lanterna.terminal.Terminal.SGR;
import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.input.Key.Kind;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.Terminal.Color;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.LanternaException;
import com.googlecode.lanterna.input.CharacterPattern;
import com.googlecode.lanterna.input.InputDecoder;
import com.googlecode.lanterna.input.InputProvider;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.input.KeyMappingProfile;

public class SpaceInvaders{

//from terminal demo
  public static void putString(int r, int c,Terminal t, String s){
		t.moveCursor(r,c);
		for(int i = 0; i < s.length();i++){
			t.putCharacter(s.charAt(i));
		}
	}

  private static void clearLine(int line, Terminal t, TerminalSize size){
    t.moveCursor(0,line);
    for(int i = 0; i < size.getColumns(); i++){
      t.putCharacter(' ');
    }
  }
  public static ArrayList<Enemy> enemyCreation() {
    ArrayList<Enemy> ans = new ArrayList<Enemy>();
    Enemy e;
    for (int p = 25; p <= 75; p+=2) {
      for (int m = 5; m < 11; m+=2) {
        e = new Enemy(1,1,p,m);
        ans.add(e);
      }
    }
    return ans;
  }
  public static int findexOf(ArrayList<Enemy> arr, int xp, int yp) {
    for (int p = 0; p < arr.size(); p++) {
      if (arr.get(p).getXPos() == xp && arr.get(p).getYPos() == yp) {
        return p;
      }
    }
    return -1;
  }
  public static void main(String[] args){
    //sets up new private terminal that the game is going to run on
    Terminal terminal = TerminalFacade.createTextTerminal();
		terminal.enterPrivateMode();
		TerminalSize size = terminal.getTerminalSize();
		terminal.setCursorVisible(false);
    //timekeeping: Laser and enemy movement is dependent on this
    long tStart = System.currentTimeMillis();
		long lastSecond = 0;
    long lastesecond = 0;

    //other variables (change later)
		boolean running = true;
    int x = 25; //x-coordinate of middle "="
    int y = 39;//y-coordinate of middle "="
    boolean mover = true;
    Random r = new Random();
    boolean toggleInvincible = false;

    User user = new User(1,1,x,y,1);
    ArrayList<Integer> lasers = new ArrayList<Integer>(); //keeps track of laser coordinates in the form of <x1,y1,x2,y2...>
    ArrayList<Integer> enemyLasers = new ArrayList<Integer>(); // same thing but for enemy lasers
    Barrier shields = new Barrier();
    ArrayList<Enemy> enemies = SpaceInvaders.enemyCreation();

    putString(0,0,terminal,"Press [esc] to exit");

  //creates barriers
  for(int p = 0; p < 40; p++){
      for(int w = 0; w < 100; w++){
        if(shields.barrierExists(w,p)){
          terminal.moveCursor(w,p);
          terminal.putCharacter('#');
        }
      }
    }

    //draws enemies
    for (int p = 0; p < enemies.size(); p++) {
      terminal.moveCursor(enemies.get(p).getXPos(),enemies.get(p).getYPos());
      terminal.putCharacter('E');
   }

    while(running){

      putString(x-2,y,terminal,"<===>");
      terminal.moveCursor(x,y-1);
      terminal.putCharacter('^');

      //key input reading and what to do on keypress (player movement/shooting)
      Key key = terminal.readInput();

      if (key != null) {
        if (key.getKind() == Key.Kind.Escape) {//closes program
          terminal.exitPrivateMode();
          System.exit(0);
        }
        if(key.getKind() == Key.Kind.ArrowRight){//moves right
          if (x <= 96) {
            clearLine(38,terminal,size);
            clearLine(39,terminal,size);
            user.move(1);
            x++;
          }
        }
        if(key.getKind() == Key.Kind.ArrowLeft){//moves left
          if (x >= 3) {
            clearLine(38,terminal,size);
            clearLine(39,terminal,size);
            user.move(3);
            x--;
          }
        }
        if(key.getKind() == Key.Kind.ArrowUp){//shoots laser upward
          lasers.add(user.getXPos());
          lasers.add(user.getYPos()-1);
        }
        if(key.getKind() == Key.Kind.F1){
          toggleInvincible = true;
        }
        if(key.getKind() == Key.Kind.PageUp){
          user.addLife();
        }
        if(key.getKind() == Key.Kind.PageDown){
          user.loseLife();
        }
      }



      long tEnd = System.currentTimeMillis();
      long millis = tEnd - tStart;

      //ENEMY SHOOTING CODE
      if (millis/150 > lastSecond) {
        lastSecond = millis/150;
        for (int p = 0; p < enemies.size(); p++) {
          if (enemies.get(p).isOnEdge(enemies)) {
            if (r.nextInt() % 25 == 0) {
              terminal.moveCursor(enemies.get(p).getXPos(),enemies.get(p).getYPos()+1);
              enemyLasers.add(enemies.get(p).getXPos());
              enemyLasers.add(enemies.get(p).getYPos());
            }
          }
        }

        //ENEMY LASER INTERACTIONS
        for (int i = 0; i < enemyLasers.size(); i+=2) {
          terminal.moveCursor(enemyLasers.get(i),enemyLasers.get(i+1));
          terminal.putCharacter(' ');

          //checks if laser is at top or touches barrier (destroys laser if true)
          if (enemyLasers.get(i+1) == 40 || shields.barrierExists(enemyLasers.get(i),enemyLasers.get(i+1))) {
            shields.destroy(lasers.get(i),lasers.get(i+1));
            enemyLasers.remove(i);
            enemyLasers.remove(i);
            i -= 2;
          }
          else if((enemyLasers.get(i) > x-3 && enemyLasers.get(i) < x+3) && enemyLasers.get(i+1) == y && toggleInvincible == false){
            user.loseLife();
          }
          else { //moves laser up
            terminal.moveCursor(enemyLasers.get(i),enemyLasers.get(i+1)+1);
            terminal.putCharacter('v');
            enemyLasers.set(i+1,enemyLasers.get(i+1)+1);
          }
        }

        //LASER INTERACTIONS
        for (int i = 0; i < lasers.size(); i+=2) {
          terminal.moveCursor(lasers.get(i),lasers.get(i+1));
          terminal.putCharacter(' ');
          int index = SpaceInvaders.findexOf(enemies,lasers.get(i),lasers.get(i+1));

          //checks if laser is at top or touches barrier (destroys laser if true)
          if (lasers.get(i+1) == 1 || shields.barrierExists(lasers.get(i),lasers.get(i+1)) || index != -1) {
            shields.destroy(lasers.get(i),lasers.get(i+1));
            lasers.remove(i);
            lasers.remove(i);
            i -= 2;
            if (index != -1) {
              enemies.remove(index);
            }
          }
          else { //moves laser up
            terminal.moveCursor(lasers.get(i),lasers.get(i+1)-1);
            terminal.putCharacter('^');
            lasers.set(i+1,lasers.get(i+1)-1);
          }
        }
      }

      //ENEMY MOVEMENT
      if (millis/1000 > lastesecond) {
        lastesecond = millis/1000;
        if (mover) {
          boolean isRight = false;
          for (int p = 0; p < enemies.size(); p++) {
            terminal.moveCursor(enemies.get(p).getXPos(),enemies.get(p).getYPos());
            terminal.putCharacter(' ');
            if (enemies.get(p).getXPos() == 99) {
              isRight = true;
            }
          }
          if (!isRight) {
            for (int p = 0; p < enemies.size(); p++) {
              enemies.get(p).setXPos(enemies.get(p).getXPos()+1);
            }
          }
          else {
            mover = false;
            for (int p = 0; p < enemies.size(); p++) {
              enemies.get(p).setYPos(enemies.get(p).getYPos()+1);
            }
          }
        }
        else {
          boolean isLeft = false;
          for (int p = 0; p < enemies.size(); p++) {
            terminal.moveCursor(enemies.get(p).getXPos(),enemies.get(p).getYPos());
            terminal.putCharacter(' ');
            if (enemies.get(p).getXPos() == 0) {
              isLeft = true;
            }
          }
          if (!isLeft) {
            for (int p = 0; p < enemies.size(); p++) {
              enemies.get(p).setXPos(enemies.get(p).getXPos()-1);
            }
          }
          else {
            mover = true;
            for (int p = 0; p < enemies.size(); p++) {
              enemies.get(p).setYPos(enemies.get(p).getYPos()+1);
            }
          }
        }

        //draws enemies after move
        for (int p = 0; p < enemies.size(); p++) {
          terminal.moveCursor(enemies.get(p).getXPos(),enemies.get(p).getYPos());
          terminal.putCharacter('E');
        }
      }

      //stuff that goes at the top
      SpaceInvaders.putString(30,0,terminal,"Time elapsed: "+millis/1000);
      SpaceInvaders.putString(30,1,terminal,"Lives: "+ user.getLives());

      if(user.getLives() == 0){
        running = false;
        for(int i = 0; i < 40; i++){
          clearLine(i,terminal,size);
        }
        SpaceInvaders.putString(30,0,terminal,"You lost!");
      }

    }
  }
}
