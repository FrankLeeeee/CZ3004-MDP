package communication;

import org.apache.log4j.Logger;

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

	private static Logger logger = Logger.getLogger(CommunicationManager.class);

	private CommunicationManager() {
	}

	// Singleton class is used. Only one CommMgr is present at any time
	public static CommunicationManager getCommMgr() {
		if (_communicationManager == null)
			_communicationManager = new CommunicationManager();

		return _communicationManager;
	}

	public void openConnection() {
		logger.info("Connecting to RPi....");

		try {
			_connection = new Socket(HOST, PORT);

			this._reader = new BufferedReader(new InputStreamReader(_connection.getInputStream()));
			this._writer = new BufferedWriter(
					new OutputStreamWriter(new BufferedOutputStream(_connection.getOutputStream())));

			logger.info("Connection established.");

			return;
		} catch (UnknownHostException e) {
			logger.info("Error while connecting: UnknownHostException");
		} catch (IOException e) {
			logger.info("Error while connecting: IOException");
		} catch (Exception e) {
			logger.info("Error while connecting: " + e.toString());
		}

		logger.info("Failed to establish connection!");
	}

	public void closeConnection() {
		logger.info("Closing connection to RPi...");

		try {
			this._reader.close();

			if (_connection != null) {
				_connection.close();
				_connection = null;
			}

			logger.info("Connection closed.");
		} catch (IOException e) {
			logger.info("Error while closing: IOException");
		} catch (NullPointerException e) {
			logger.info("Error while closing: NullPointerException");
		} catch (Exception e) {
			logger.info("Error while closing: " + e.toString());
		}

		logger.info("Failed to close connection!");
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

			logger.info("Sending out message: " + outputMsg);
			this._writer.write(outputMsg);
			this._writer.flush();
			logger.info("Message sent!");
		} catch (IOException e) {
			logger.info("Error while sending message: IOException");
		} catch (Exception e) {
			logger.info("Error while sending message: " + e.toString());
		}
		logger.info("===========");
	}

	public String receiveMsg() {
		logger.info("Receiving message....");

		try {

			StringBuilder strBuilder = new StringBuilder();
			String input = _reader.readLine();

			if (input != null && input.length() > 0) {
				strBuilder.append(input);
				logger.info("Message received: " + strBuilder.toString());
				return strBuilder.toString();
			}
		} catch (IOException e) {
			logger.info("Error while receiving message: IOException");
		} catch (Exception e) {
			logger.info("Error while receiving message: Exception");
			logger.info(e.toString());
		}
		logger.info("===========");
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
