public class User extends Being {
  private int lives;
  public User(int h, int a, int x, int y, int l) {
    super(h,a,x,y);
    lives = l;
  }
  public boolean isDead() {
    return (lives == 0 && getHealth() == 0);
  }
  public void move(int dir) {
    if (dir == 1) {//moves right
      setXPos(getXPos()+1);
    }
    if (dir == 3) {//moves left
      setXPos(getXPos()-1);
    }
  }
  public void changeLife(int num) {
    lives += num;
  }

  public int getLives(){
    return lives;
  }

  public void loselife(){
    lives--;
  }
}
