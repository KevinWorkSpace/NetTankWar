import java.net.DatagramSocket;
import java.io.*;
public interface Msg {
	public static final int TankNewMsg = 1;
	public static final int TankMovMsg = 2;
	public static final int MissileNewMsg = 3;
	public static final int TankDeadMsg = 4;
	public static final int MissileDeadMsg = 5;
	
	public void send(DatagramSocket ds, String ip, int port);
	public void parse(DataInputStream dis);
}
