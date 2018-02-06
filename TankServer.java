import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class TankServer {
	public static final int TCP_PORT = 8888;
	public static final int UDP_PORT = 6667;
	public List<Client> clients = new ArrayList<Client>();
	private static int id = 100;
	
	public void start() {
		new Thread(new UDPThread()).start();
		try {
			ServerSocket ss = new ServerSocket(TCP_PORT);;
			while(true) {
				Socket s = ss.accept();
				DataInputStream dis = new DataInputStream(s.getInputStream());
				int udpPort = dis.readInt();
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				dos.writeInt(id++);
				String ip = s.getInetAddress().getHostAddress();
				Client c = new Client(ip, udpPort);
				clients.add(c);
				s.close();
	System.out.println("a client is connected, address: "+s.getInetAddress()+":"+s.getPort());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new TankServer().start();
	}
	
	private class Client {
		public String ip;
		public int udpPort;
		
		public Client(String ip, int udpPort) {
			this.ip = ip;
			this.udpPort = udpPort;
		}
	}
	
	class UDPThread implements Runnable {

		@Override
		public void run() {
			byte[] buf = new byte[1024];
			try {
				DatagramSocket ds = new DatagramSocket(UDP_PORT);;
System.out.println("The UDPThread has started at port: " + UDP_PORT);
				DatagramPacket dp = new DatagramPacket(buf, buf.length);
				while(true) {
					ds.receive(dp);
System.out.println("a packet received");
					for(int i=0; i<clients.size(); i++) {
						dp.setSocketAddress(new InetSocketAddress(clients.get(i).ip, clients.get(i).udpPort));
						ds.send(dp);
					}
				}
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
