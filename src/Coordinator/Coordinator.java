package Coordinator;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class Coordinator {
	
	
	
	private Coordinator(){
		
	}
	
	
	public static void startCoordinatorBroadcastService(){
		try {

			new Thread(new Runnable() {
				DatagramSocket socket;

				private InetAddress getOutboundAddress(SocketAddress remoteAddress) throws SocketException {
					DatagramSocket sock = new DatagramSocket();
					// connect is needed to bind the socket and retrieve the
					// local address
					// later (it would return 0.0.0.0 otherwise)
					sock.connect(remoteAddress);

					final InetAddress localAddress = sock.getLocalAddress();

					sock.disconnect();
					sock.close();
					sock = null;

					return localAddress;
				}

				@Override
				public void run() {
					try {
						// Keep a socket open to listen to all the UDP trafic
						// that is destined for this port
						socket = new DatagramSocket(33333, InetAddress.getByName("0.0.0.0"));
						socket.setBroadcast(true);

						while (true) {
							System.out.println(getClass().getName() + ">>>Ready to receive broadcast packets!");

							// Receive a packet
							byte[] recvBuf = new byte[15000];
							DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
							socket.receive(packet);

							// Packet received
							System.out.println(getClass().getName() + ">>>Discovery packet received from: "
									+ packet.getAddress().getHostAddress());
							System.out.println(
									getClass().getName() + ">>>Packet received; data: " + new String(packet.getData()));

							// See if the packet holds the right command
							// (message)
							String message = new String(packet.getData()).trim();
							if (message.equals("register")) {

								System.out.println("Local IP of this packet was: "
										+ getOutboundAddress(packet.getSocketAddress()).getHostAddress());
								

								byte[] sendData = getOutboundAddress(packet.getSocketAddress()).getHostAddress().getBytes();

								// Send a response
								DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
										packet.getAddress(), packet.getPort());
								socket.send(sendPacket);

								System.out.println(getClass().getName() + ">>>Sent packet to: "
										+ sendPacket.getAddress().getHostAddress());
							}
						}
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}

			}).start();

		} catch (Exception e) {
			System.out.println("Error" + e);
		}
		
	}	
	
}
