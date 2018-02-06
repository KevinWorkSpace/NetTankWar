import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Missile {
	
	int x,y;
	direction dir;
	private TankWarFrame twf;
	boolean live = true;
	boolean good;
	int tankId;
	public boolean isLive() {
		return live;
	}

	public static final int XSPEED = 20;
	public static final int YSPEED = 20;
	
	public Missile(int tankId, int x, int y, direction dir) {
		this.tankId = tankId;
		this.x = x;
		this.y = y;
		this.dir = dir;
		
	}
	
	public Missile(int tankId, int x, int y, direction dir,boolean good,TankWarFrame twf) {
		this(tankId, x , y, dir);
		this.good = good;
		this.twf = twf;
		
	}
	public void draw(Graphics g) {
		if(!isLive()) return;
		Color c = g.getColor();
		g.setColor(Color.BLACK);
		g.fillOval(x, y, 10, 10);
		g.setColor(c);
		move();
	}

	private void move() {
		switch(dir) {
		case L:
			x -= XSPEED;
			break;
		case LU:
			x -= XSPEED;
			y -= YSPEED;
			break;
		case U:
			y -= YSPEED;
			break;
		case RU:
			x += XSPEED;
			y -= YSPEED;
			break;
		case R:
			x += XSPEED;
			break;
		case RD:
			x += XSPEED;
			y += YSPEED;
			break;
		case D:
			y += YSPEED;
			break;
		case LD:
			x -= XSPEED;
			y += YSPEED;
			break;
		}	
		if(x<0 || y <0 || x>TankWarFrame.GAME_WIDTH || y>TankWarFrame.GAME_HEIGHT) {
			live = false;
			twf.missiles.remove(this);
			MissileDeadMsg msg = new MissileDeadMsg(this);
			twf.nc.send(msg);
		}
	}
	public Rectangle getRect() {
		return new Rectangle(x,y,10,10);
	}
	public boolean hitTank(Tank t) {
		if(this.isGood() != t.isGood() && t.getRect().intersects(getRect()) && t.isLive() && live) {
			Explode e = new Explode(x,y,twf);
			twf.explodes.add(e);
			t.setLive(false);
			live = false;
			twf.missiles.remove(this);
			TankDeadMsg msg = new TankDeadMsg(t.id);
			twf.nc.send(msg);
			return true;
		}
		return false;
	}
	
	public boolean isGood() {
		return good;
	}

	public void setGood(boolean good) {
		this.good = good;
	}

	public void hitTanks(List<Tank> enemyTanks) {
		for(int i=0; i<enemyTanks.size(); i++) {
			if(enemyTanks.get(i).getRect().intersects(getRect())) {
				this.hitTank(enemyTanks.get(i));
			}
		}
	}
	
}
