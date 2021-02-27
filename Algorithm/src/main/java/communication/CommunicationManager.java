package communication;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class CommunicationManager {
	private static final String HOST = "192.168.16.16";
	private static final int PORT = 5555;

	private static CommunicationManager _communicationManager = null;
	private static Socket _connection = null;

	private BufferedReader _reader;
	private BufferedWriter _writer;

	public static final String SEPARATOR = ":";
	public static final String EX_START = "EX_START"; // Android --> PC "EX_START"
	public static final String FP_START = "FP_START"; // Android --> PC "FP_START"
	public static final String WAY_POINT = "WP"; // Android --> PC "WP:c:r"
	public static final String MAP_STRINGS = "MAP"; // PC --> Android "MAP:mapDesc1:mapDesc2"
	public static final String ROBOT_POS = "BOT_POS"; // PC --> Android "BOT_POS:c:r:dir"
	public static final String ROBOT_START = "BOT_START"; // PC --> Arduino "BOT_START:S"
	public static final String INSTRUCTIONS = "INSTR"; // PC --> Arduino "INSTR:1"
	public static final String SENSOR_DATA = "SDATA"; // Arduino --> PC "SDATA:SRFL:SRFC:SRFR:SRL:SRR:LRL"
	public static final String C_DONE = "CALIDONE";
	public static final String ROBOT_INFO = "ROBOT"; // PC --> Android [Refer to parseToJSON method below]
	public static final String EX_DONE = "EX_DONE";

	private CommunicationManager() {
	}

	// Singleton class is used. Only one CommMgr is present at any time
	public static CommunicationManager getCommMgr() {
		if (_communicationManager == null)
			_communicationManager = new CommunicationManager();

		return _communicationManager;
	}

	public void openConnection() {
		System.out.println("Connecting to RPi....");

		try {
			_connection = new Socket(HOST, PORT);

			this._reader = new BufferedReader(new InputStreamReader(_connection.getInputStream()));
			this._writer = new BufferedWriter(
					new OutputStreamWriter(new BufferedOutputStream(_connection.getOutputStream())));

			System.out.println("Connection established.");

			return;
		} catch (UnknownHostException e) {
			System.out.println("Error while connecting: UnknownHostException");
		} catch (IOException e) {
			System.out.println("Error while connecting: IOException");
		} catch (Exception e) {
			System.out.println("Error while connecting: " + e.toString());
		}

		System.out.println("Failed to establish connection!");
	}

	public void closeConnection() {
		System.out.println("Closing connection to RPi...");

		try {
			this._reader.close();

			if (_connection != null) {
				_connection.close();
				_connection = null;
			}

			System.out.println("Connection closed.");
		} catch (IOException e) {
			System.out.println("Error while closing: IOException");
		} catch (NullPointerException e) {
			System.out.println("Error while closing: NullPointerException");
		} catch (Exception e) {
			System.out.println("Error while closing: " + e.toString());
		}

		System.out.println("Failed to close connection!");
	}

	public void sendMsg(String msg, String msgType) {

		try {
			String outputMsg;
			if (msg == null) {
				outputMsg = msgType + "\n";
			} else if (msgType.equals(MAP_STRINGS) || msgType.equals(ROBOT_POS)) {
				outputMsg = msgType + SEPARATOR + msg + "\n";
			} else {
				outputMsg = msgType + SEPARATOR + msg + "\n";
			}

			System.out.println("Sending out message: " + outputMsg);
			this._writer.write(outputMsg);
			this._writer.flush();
			System.out.println("Message sent!");
		} catch (IOException e) {
			System.out.println("Error while sending message: IOException");
		} catch (Exception e) {
			System.out.println("Error while sending message: " + e.toString());
		}
		System.out.println("===========");
	}

	public String receiveMsg() {
		System.out.println("Receiving message....");

		try {

			StringBuilder strBuilder = new StringBuilder();
			String input = _reader.readLine();

			if (input != null && input.length() > 0) {
				strBuilder.append(input);
				System.out.println("Message received: " + strBuilder.toString());
				return strBuilder.toString();
			}
		} catch (IOException e) {
			System.out.println("Error while receiving message: IOException");
		} catch (Exception e) {
			System.out.println("Error while receiving message: Exception");
			System.out.println(e.toString());
		}
		System.out.println("===========");
		return null;
	}

	public static boolean isConnected() {
		if (_connection != null)
			return _connection.isConnected();
		else
			return false;
	}

	public static String parseToJSON(String map, String obstacle, String pos) {

		String robotHead = null, robotCenter = null;
		String[] arr = pos.split(":");
		String Center = "(" + arr[1] + ", " + arr[2] + ")";
		String test = arr[3];
		String dir = "d" + test.charAt(0);
		int y;
		int x;
		String Head = new String();

		switch (dir) {
			case ("dN"):
				y = Integer.valueOf((String) arr[2]);
				y = y + 1;
				Head = "(" + arr[1] + ", " + y + ")";
				break;
			case ("dW"):
				x = Integer.valueOf((String) arr[1]);
				x = x - 1;
				Head = "(" + x + ", " + arr[2] + ")";
				break;
			case ("dE"):
				x = Integer.valueOf((String) arr[1]);
				x = x + 1;
				Head = "(" + x + ", " + arr[2] + ")";
				break;
			case ("dS"):
				y = Integer.valueOf((String) arr[2]);
				y = y - 1;
				Head = "(" + arr[1] + ", " + y + ")";
				break;
		}

		robotHead = Head; // Set the first name/pair
		robotCenter = Center;

		String jsonRes =
				"{" + "\"map\": \"" + map + "\"" + "," +
						"\"obstacle\": \"" + obstacle + "\"" + "," +
						"\"robotCenter\": \"" + robotCenter + "\"" + "," +
						"\"robotHead\": \"" + robotHead + "\"" + "," +
						"\"heading\": \"" + test.charAt(0) + "\"" +
						"}";

		return jsonRes;
	}
}
