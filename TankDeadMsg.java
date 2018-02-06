import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TankDeadMsg implements Msg {
	int id;
	TankWarFrame twf;
	int msgType = Msg.TankDeadMsg;
	
	public TankDeadMsg(int id) {
		this.id = id;
	}
	
	public TankDeadMsg(TankWarFrame twf) {
		this.twf = twf;
	}
	
	@Override
	public void send(DatagramSocket ds, String IP, int udpPort) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		int msgType = Msg.TankNewMsg;
		try {
			dos.writeInt(msgType);
			dos.writeInt(id);
			
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
			for(int i=0; i<twf.enemyTanks.size(); i++) {
				Tank t = twf.enemyTanks.get(i);
				if(t.id == id) {
					twf.enemyTanks.remove(t);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
