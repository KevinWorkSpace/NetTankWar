import java.awt.*;
public class Explode {
	int x,y;
	int[] diameter ={3,9,12,16,22,26,32,49,72,60,30,22,13,6};
	int step = 0;
	TankWarFrame twf;
	boolean live = true;
	
	public Explode(int x, int y, TankWarFrame twf) {
		this.x = x;
		this.y = y;
		this.twf = twf;
	}
	
	public void draw(Graphics g) {
		if(!live) return;
		if(step == diameter.length) {
			live = false;
			twf.explodes.remove(this);
			step = 0;
			return;
		}
		Color c = g.getColor();
		g.setColor(Color.BLACK);
		g.fillOval(x, y, diameter[step],diameter[step] );
		step ++;
		g.setColor(c);
	}
	
}
