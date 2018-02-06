import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TankNewMsg implements Msg {
	Tank t;
	TankWarFrame twf;
	public TankNewMsg(Tank t) {
		this.t= t;
	}
	public TankNewMsg(TankWarFrame twf) {
		this.twf = twf;
	}
	public void send(DatagramSocket ds, String IP, int udpPort) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		int msgType = Msg.TankNewMsg;
		try {
			dos.writeInt(msgType);
			dos.writeInt(t.id);
			dos.writeInt(t.x);
			dos.writeInt(t.y);
			dos.writeInt(t.dir.ordinal());
			dos.writeBoolean(t.good);
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
	public void parse(DataInputStream dis) {
		try {
			int id = dis.readInt();
			if(id == twf.myTank.id) {
				return;
			}
			boolean exit = false;
			for(int i=0; i<twf.enemyTanks.size(); i++) {
				Tank t = twf.enemyTanks.get(i);
				if(t.id == id) {
					exit = true;
					return;
				}
			}
			
			int x = dis.readInt();
			int y = dis.readInt();
			direction dir = direction.values()[dis.readInt()];
			boolean good = dis.readBoolean();
			if(!exit) {
				Tank t = new Tank(x, y, good, dir, twf);
				t.id = id;
				twf.enemyTanks.add(t);
				Msg msg = new TankNewMsg(twf.myTank); 
				twf.nc.send(msg);
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
