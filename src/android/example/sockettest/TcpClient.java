package example.sockettest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import android.util.Log;

// TCP客户端委托接口类
interface ITcpClientDelegate {
	void processNetworkData(byte[] data, int length);
}

// TCP客户端
public class TcpClient {
	private static final String TAG = "TcpClient";

	// private ITcpClientDelegate delegate = null;
	public static String readstr = null;
	private String ipAddress = null;
	private int ipPort = 0;

	// 是否需要连接服务器
	private boolean needConnectServer = false;
	// 是否正在连接服务器
	private boolean connectingServer = false;
	// 是否已经连上了服务器
	static boolean serverConnected = false;

	private Socket socket = null;
	private InputStream inputStream = null;
	private OutputStream outputStream = null;

	// 连接服务器线程
	private ConnectThread connectThread = null;
	// 接收网络数据线程
	private ReceiveThread receiveThread = null;

	/*
	 * 构造时需要委托类的对象 public TcpClient(ITcpClientDelegate delegate) { this.delegate
	 * = delegate; }
	 */

	// 连接服务器
	public boolean connect(String ipAddress, int ipPort) {
		this.ipAddress = ipAddress;
		this.ipPort = ipPort;

		needConnectServer = true;
		startConnectThread();

		return true;
	}

	// 断开和服务器的连接
	public void disconnect() {
		needConnectServer = false;
		if (connectThread != null) {
			try {
				for (int i = 0; i < 1800; i++) {
					if (!connectThread.isAlive()) {
						break;
					}
					Thread.sleep(100);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			connectThread = null;
		}

		disconnectFromServer();
	}

	// 重新连接服务器
	public void reconnect() {
		disconnect();
		needConnectServer = true;
		startConnectThread();
	}

	// 是否连上服务器
	public boolean isConnected() {
		return serverConnected;
	}

	// 写数据
	public boolean write(byte[] data, int length) {
		if (outputStream == null) {
			return false;
		}
		try {
			outputStream.write(data, 0, length);
		} catch (Exception e) {
			Log.e(TAG, "Write network data failed.");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// 启动连接服务器的线程
	private boolean startConnectThread() {
		connectThread = new ConnectThread();
		connectThread.start();
		return true;
	}

	// 连接服务器
	private boolean connectServer() {
		try {
			socket = new Socket();
			SocketAddress address = new InetSocketAddress(ipAddress, ipPort);
			socket.connect(address, 30000);
			if (socket == null) {
				return false;
			}
			if (!socket.isConnected()) {
				socket.close();
				socket = null;
				return false;
			}
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
			startReceiveThread();
		} catch (SocketTimeoutException e) {
			Log.e(TAG, "Connect server failed.");
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			Log.e(TAG, "Connect server failed.");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// 断开和服务器的连接
	private void disconnectFromServer() {
		Log.i(TAG, "Disconnecting...");

		serverConnected = false;

		if (socket != null) {
			try {
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			socket = null;
		}
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			inputStream = null;
		}
		if (outputStream != null) {
			try {
				outputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			outputStream = null;
		}
		if (receiveThread != null) {
			try {
				for (int i = 0; i < 30; i++) {
					if (!receiveThread.isAlive()) {
						break;
					}
					Thread.sleep(100);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			receiveThread = null;
		}

		Log.i(TAG, "Disconnected.");
	}

	// 连接服务器线程
	private class ConnectThread extends Thread {
		public void run() {
			if (connectingServer) {
				return;
			}
			connectingServer = true;
			while (needConnectServer) {
				Log.i(TAG, "Connect to " + ipAddress + ":" + ipPort + "...");
				if (connectServer()) {
					Log.i(TAG, "Connect OK.");
					serverConnected = true;
					break;
				} else {
					Log.i(TAG, "Connect failed.");
					disconnectFromServer();
				}
			}
			connectingServer = false;
		}
	}

	// 启动接收数据的线程
	private boolean startReceiveThread() {
		receiveThread = new ReceiveThread();
		receiveThread.start();
		return true;
	}

	// 接收数据线程
	private class ReceiveThread extends Thread {
		public void run() {
			byte buffer[] = new byte[1024];
			int length = 0;
			while (true) {
				try {
					length = inputStream.read(buffer);
					if (length == -1) {
						Log.w(TAG, "Disconnect from server.");
						reconnect(); // 重新连接服务器
						break;
					}
					readstr = new String(buffer, 0, length);
					Log.i("tcpClient", readstr);
					// delegate.processNetworkData(buffer, length);
				} catch (IOException e) {
					Log.e(TAG, "Read network data failed.");
					break;
				}
			}
		}
	}
}
