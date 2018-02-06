import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TankMovMsg implements Msg {
	int id;
	int x;
	int y;
	direction dir;
	TankWarFrame twf;
	
	public TankMovMsg(TankWarFrame twf) {
		this.twf = twf;
	}
	
	public TankMovMsg(int id, int x, int y, direction dir) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.dir = dir;
	}
	
	@Override
	public void send(DatagramSocket ds, String IP, int udpPort) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		int msgType = Msg.TankMovMsg;
		try {
			dos.writeInt(msgType);
			dos.writeInt(id);
			dos.writeInt(x);
			dos.writeInt(y);
			dos.writeInt(dir.ordinal());
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] b = baos.toByteArray();
		DatagramPacket dp = new DatagramPacket(b, b.length, new InetSocketAddress(IP, udpPort));
		try {
			ds.send(dp);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void parse(DataInputStream dis) {
		try {
			int id = dis.readInt();
			if(id == twf.myTank.id) {
				return;
			}
			int x = dis.readInt();
			int y = dis.readInt();
			direction dir = direction.values()[dis.readInt()];
			boolean exit = false;
			for(int i=0; i<twf.enemyTanks.size(); i++) {
				Tank t = twf.enemyTanks.get(i);
				if(t.id == id) {
					t.dir = dir;
					t.x = x;
					t.y = y;
					exit = true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
