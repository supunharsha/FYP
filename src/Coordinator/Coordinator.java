package Coordinator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.websocket.Session;

import org.json.JSONObject;

import MessageProtocol.Message;
import MessageProtocol.MessageBrocker;
import MessageProtocol.MessageCommonData;

public class Coordinator {

	public static Thread Coordinator;
	public static JSONObject personDetails;
	public static CoordinatorInterrupts interrupt;
	private static ExecutorService executor;
	public static Message msg;

	private static final short THREAD_POOL_SIZE = 10;

	public static enum CoordinatorInterrupts {
		NO_EVENT, CLIENT_ADD_A_REQUEST, BROCKER_SEND_A_MESSAGE;
	}

	private Coordinator() {

	}

	public static boolean startCoordinatorBroadcastService() {
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
								if(MessageCommonData.agentList.isEmpty()){
									MessageCommonData.agentList.put(getOutboundAddress(packet.getSocketAddress()).getHostAddress().toString(), null);
								}

								byte[] sendData = getOutboundAddress(packet.getSocketAddress()).getHostAddress()
										.getBytes();

								// Send a response
								DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
										packet.getAddress(), packet.getPort());
								socket.send(sendPacket);

								System.out.println(getClass().getName() + ">>>Sent packet to: "
										+ sendPacket.getAddress().getHostAddress());
							}
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

			}).start();

		} catch (Exception e) {
			System.out.println("Error" + e);
			return false;
		}

		return true;
	}

	public static void startCoordniatorMainProcess() {
		personDetails = new JSONObject();
		executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		Coordinator = new Thread(new Runnable() {

			@Override
			public void run() {
				interrupt = CoordinatorInterrupts.NO_EVENT;
				while (true) {
					try {						
						if (!MessageCommonData.agentList.isEmpty()) {
							if (interrupt == CoordinatorInterrupts.BROCKER_SEND_A_MESSAGE) {
								ServeMessage work = new ServeMessage(msg);
								executor.execute(work);
								interrupt = CoordinatorInterrupts.NO_EVENT;
							} else if (interrupt == CoordinatorInterrupts.CLIENT_ADD_A_REQUEST) {
								executor.execute(new Runnable() {
									@Override
									public void run() {

									}
								});
								interrupt = CoordinatorInterrupts.NO_EVENT;
							}
						}
						System.out.println("Cordinator start sleeping");
						Thread.sleep(Long.MAX_VALUE);
					} catch (Exception e) {
						System.out.println("Cordinator woke up from sleeping");
					}
				}

			}
		});
		Coordinator.start();
	}

}
