public class Enemy extends Being {
  public Enemy(int h, int a, int x, int y) {
    super(h,a,x,y);
  }
  public boolean isOnEdge() {
    return true;
  }
  public void move() {}
    public boolean isDead() {
      return (health == 0);
    }
}
