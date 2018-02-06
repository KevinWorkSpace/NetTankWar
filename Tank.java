import java.awt.*;
import java.awt.event.*;
import java.util.*;
public class Tank {
	public int id;
	public static final int XSPEED = 5;
	public static final int YSPEED = 5 ;
	public static final int WIDTH = 30;
	public static final int HEIGHT = 30;
	int x,y;
	TankWarFrame twf;
	boolean u = false, d = false, l = false, r = false;
	boolean good = true;
	direction dir = direction.STOP;
	direction ptdir = direction.D;
	boolean live = true;
	private static Random r1 = new Random();
	public static int step = r1.nextInt(12) + 3;
	direction[] dirs = dir.values();
	
	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}

	public Tank(int x, int y,boolean good) {
		super();
		this.x = x;
		this.y = y;
		this.good = good;
	}
	
	public Tank(int x, int y, boolean good, direction dir, TankWarFrame twf) {
		this(x,y,good);
		this.dir = dir;
		this.twf = twf;
	}
	
	public void draw(Graphics g) {
		if(id%2 == 0) {
			this.good = false;
		}
		else this.good = true;
		if(!isLive()) {
			twf.enemyTanks.remove(this);
			return;
		}
		
		/*if(!good) {
			if(step==0) {
			step = r1.nextInt(12) +3;
			int rn = r1.nextInt(8);
			dir = dirs[rn]; 
			}
			step--;
			if(r1.nextInt(40) >38) this.fire();
		}*/
		Color c = g.getColor();
		if(good == true) {
			g.setColor(Color.RED);
		}
		else {
			g.setColor(Color.BLUE);
		}
		
		g.fillOval(x, y, WIDTH, HEIGHT);
		g.drawString("id:" + id, x, y-10);
		g.setColor(c);
		move();
		if(dir != direction.STOP) {
			ptdir = dir;
		}
		drawPt(g);
	}
	
	public boolean isGood() {
		return good;
	}

	public void setGood(boolean good) {
		this.good = good;
	}

	public void drawPt(Graphics g) {
		switch(ptdir) {
		case L:
			g.drawLine(x + WIDTH/2, y + HEIGHT/2, x, y + HEIGHT/2);
			break;
		case LU:
			g.drawLine(x + WIDTH/2, y + HEIGHT/2, x, y);
			break;
		case U:
			g.drawLine(x + WIDTH/2, y + HEIGHT/2, x + WIDTH/2, y);
			break;
		case RU:
			g.drawLine(x + WIDTH/2, y + HEIGHT/2, x + WIDTH, y);
			break;
		case R:
			g.drawLine(x + WIDTH/2, y + HEIGHT/2, x + WIDTH, y + HEIGHT/2);
			break;
		case RD:
			g.drawLine(x + WIDTH/2, y + HEIGHT/2, x + WIDTH, y + HEIGHT);
			break;
		case D:
			g.drawLine(x + WIDTH/2, y + HEIGHT/2, x + WIDTH/2, y + HEIGHT);
			break;
		case LD:
			g.drawLine(x + WIDTH/2, y + HEIGHT/2, x, y + HEIGHT);
			break;
		}
		
		
	}
	
	public void move() {
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
		case STOP:
			break;
		}
		if(x < 0) {
			x = 0;
		}
		if(y < 30) {
			y = 30;
		}
		if(x + Tank.WIDTH > TankWarFrame.GAME_WIDTH) {
			x = TankWarFrame.GAME_WIDTH - Tank.WIDTH;
		}
		if(y + Tank.HEIGHT > TankWarFrame.GAME_HEIGHT) {
			y = TankWarFrame.GAME_HEIGHT - Tank.HEIGHT;
		}
		
		
	}
	
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		switch (key) {
			
			case KeyEvent.VK_RIGHT:
				r = true;
				break;
			case KeyEvent.VK_LEFT:
				l = true;
				break;
			case KeyEvent.VK_UP:
				u = true;
				break;
			case KeyEvent.VK_DOWN: 
				d = true;
				break;
			
		}
		locateDirection();
	}
	
	public Missile fire() {
		if(!live) return null;
		Missile m = new Missile(id, x, y, ptdir, good,twf);	
		twf.missiles.add(m);
		Msg msg = new MissileNewMsg(m);
		twf.nc.send(msg);
		return m;
	}

	public void locateDirection() {
		direction oldDir = this.dir;
		if(l && !r && !u && !d ) {
			dir = direction.L;
		}
		else if(l && !r && u && !d ) {
			dir = direction.LU;
		}
		else if(!l && !r && u && !d ) {
			dir = direction.U;
		}
		else if(!l && r && u && !d ) {
			dir = direction.RU;
		}
		else if(!l && r && !u && !d ) {
			dir = direction.R;
		}
		else if(!l && r && !u && d ) {
			dir = direction.RD;
		}
		else if(!l && !r && !u && d ) {
			dir = direction.D;
		}
		else if(l && !r && !u && d) {
			dir = direction.LD;
		}
		else if(!l && !r && !u && !d ) {
			dir = direction.STOP;
		}
		if(this.dir != oldDir) {
			Msg msg = new TankMovMsg(this.id, this.x, this.y, this.dir);
			twf.nc.send(msg);
		}
		
	}

	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		switch (key) {
			case KeyEvent.VK_CONTROL:
			    fire();
				break;
			case KeyEvent.VK_RIGHT:
				r = false;
				break;
			case KeyEvent.VK_LEFT:
				l = false;
				break;
			case KeyEvent.VK_UP:
				u = false;
				break;
			case KeyEvent.VK_DOWN: 
				d = false;
				break;
		}
		locateDirection();
		
	}
	public Rectangle getRect() {
		return new Rectangle(x,y,WIDTH,HEIGHT);
	}
	
}
