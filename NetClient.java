import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

public class NetClient {
	int udpPort;
	TankWarFrame twf;
	DatagramSocket ds = null;
	
	public NetClient(TankWarFrame twf) {
		this.twf = twf;
		
	}
	public void connectServer(String IP, int port) {
		Socket s = null;
		try {
			ds = new DatagramSocket(udpPort);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		try {
			s = new Socket(IP,port);
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			dos.writeInt(udpPort);
			DataInputStream dis = new DataInputStream(s.getInputStream());
			int id = dis.readInt();
			this.twf.myTank.id = id;
System.out.println("connected!!");
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(s != null) {
				try {
					s.close();
					s = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		new Thread(new UDPRecvThread()).start();
		Msg msg = new TankNewMsg(twf.myTank);
		send(msg);
	}
	public void send(Msg msg) {
		msg.send(ds, "127.0.0.1", TankServer.UDP_PORT);
	}
	
	private class UDPRecvThread implements Runnable {
		byte[] buf = new byte[1024];
		@Override
		public void run() {
			try {
System.out.println("The UDPThread has started at port: " + udpPort);
				DatagramPacket dp = new DatagramPacket(buf, buf.length);
				while(true) {
					ds.receive(dp);
System.out.println("a packet received from server");
					parse(dp);
				}
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void parse(DatagramPacket dp) {
			Msg msg = null;
			ByteArrayInputStream bais = new ByteArrayInputStream(buf, 0, buf.length);
			DataInputStream dis = new DataInputStream(bais);
			int msgType = 0;
			try {
				msgType = dis.readInt();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(msgType == Msg.TankNewMsg) {
				msg = new TankNewMsg(twf);
				msg.parse(dis);
			}
			if(msgType == Msg.TankMovMsg) {
				msg = new TankMovMsg(twf);
				msg.parse(dis);
			}
			if(msgType == Msg.MissileNewMsg) {
				msg = new MissileNewMsg(twf);
				msg.parse(dis);
			}
			if(msgType == Msg.TankDeadMsg) {
				msg = new TankDeadMsg(twf);
				msg.parse(dis);
			}
			if(msgType == Msg.MissileDeadMsg) {
				msg = new MissileDeadMsg(twf);
				msg.parse(dis);
			}
		}
	}
}
