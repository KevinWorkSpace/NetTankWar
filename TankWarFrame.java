import java.awt.*;
import java.awt.event.*;
import java.util.List;

import java.util.*;
public class TankWarFrame extends Frame {
	
	public static final int GAME_WIDTH = 800;
	public static final int GAME_HEIGHT = 600;

	Tank myTank = new Tank(50,50,true,direction.STOP,this);
	List<Tank> enemyTanks = new ArrayList<Tank>();
	List<Explode> explodes = new ArrayList<Explode>();
	List<Missile> missiles = new ArrayList<Missile>();
	NetClient nc = new NetClient(this);
	MyDialog md = new MyDialog();
	Image OffScreenImage;
	public void LanuchFrame() {
		this.setBounds(400,0,GAME_WIDTH,GAME_HEIGHT);
		setVisible(true);
		setResizable(false);
		setTitle("TankWar");
		this.setBackground(Color.GREEN);
		/*for(int i=0; i<10; i++) {
			Tank t = new Tank(50+(i+1)*40,50,false,direction.D,this);
			enemyTanks.add(t);
		}*/
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				 setVisible(false);
				 System.exit(0);
			 }
		});
		addKeyListener(new KeyMonitor());
		TankThread tt = new TankThread();
		new Thread(tt).start();
		//nc.connectServer("127.0.0.1", TankServer.TCP_PORT);
	}
	
	@Override
	public void update(Graphics g) {
		if(OffScreenImage == null) {
			OffScreenImage = this.createImage(GAME_WIDTH,GAME_HEIGHT);
		}
		Graphics gOffScreen = OffScreenImage.getGraphics();
		Color c = gOffScreen.getColor();
		gOffScreen.setColor(Color.GREEN);
		gOffScreen.fillRect(0, 0, GAME_WIDTH,GAME_HEIGHT);
		gOffScreen.setColor(c);
		paint(gOffScreen);
		g.drawImage(OffScreenImage, 0, 0, null);
	}
	
	public void paint(Graphics g) {
		g.drawString("The missiles number is :" + missiles.size(), 10, 50);
		g.drawString("The Explode number is :" + explodes.size(), 10, 70);
		g.drawString("The enemy tanks number is :" + enemyTanks.size(), 10, 90);
		myTank.draw(g);
		//if(enemyTanks.size() == 0) g.drawString("You Win !!!", 200, 200);
		for(int i=0; i<enemyTanks.size(); i++) {
			Tank t = enemyTanks.get(i);
			t.draw(g);
		}
		for(int i=0; i<explodes.size(); i++) {
			Explode e = explodes.get(i);
			e.draw(g);
		}
		for(int i=0; i<missiles.size(); i++) {
			Missile m = missiles.get(i);
			m.hitTanks(enemyTanks);
			m.hitTank(myTank);
			m.draw(g);
		}
	}
	
	
	public static void main(String[] args) {
		TankWarFrame twf = new TankWarFrame();
		twf.LanuchFrame();
	}
	
	class TankThread implements Runnable {
		@Override
		public void run() {
			while(true) {
				repaint();	
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
  
	private class KeyMonitor extends KeyAdapter {

		@Override
		public void keyReleased(KeyEvent e) {
			myTank.keyReleased(e);
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_C) {
				md.setVisible(true);
			}
			else myTank.keyPressed(e);
		}
	}
	
	private class MyDialog extends Dialog {
		public MyDialog() {
			super(TankWarFrame.this, true);
			Button b = new Button("Enter");
			TextField tfIP = new TextField("127.0.0.1", 12);
			TextField tfPort = new TextField("8888", 4);
			TextField tfMyUDPPort = new TextField("2223", 4);
			this.setLayout(new FlowLayout());
			this.add(new Label("IP:"));
			this.add(tfIP);
			this.add(new Label("Port:"));
			this.add(tfPort);
			this.add(new Label("MyUDPPort:"));
			this.add(tfMyUDPPort);
			this.add(b);
			this.setLocation(300,300);
			this.pack();
			b.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					MyDialog.this.setVisible(false);
					String IP = tfIP.getText().trim();
					int port = Integer.parseInt(tfPort.getText().trim());
					int udpPort = Integer.parseInt(tfMyUDPPort.getText().trim());
					nc.udpPort = udpPort;
					nc.connectServer(IP, port);
				}
				
			});
		}
	}
}



