package communication;

import com.google.protobuf.ByteString;
import config.RobotConst;
import grpc.GRPCServiceGrpc;
import grpc.GrpcService;
import grpc.GrpcService.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.nio.charset.Charset;
import java.security.InvalidParameterException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GrpcClient {
	private ManagedChannel channel;
	private GRPCServiceGrpc.GRPCServiceBlockingStub blockingStub;
	private boolean isConnected;
	private static GrpcClient client;
	private static Logger logger = Logger.getLogger("io.grpc");

	// Singleton class is used. Only one client is present at any time
	public static GrpcClient getInstance() {
		if (client == null)
			client = new GrpcClient();
		logger.setLevel(Level.INFO);
		return client;
	}

	public GrpcClient() {
		isConnected = false;
	}

	public void connect(String host, int port) {
		assert !isConnected : "Control client has already connected to the gRPC server";
		channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
		blockingStub = GRPCServiceGrpc.newBlockingStub(channel);
		isConnected = true;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void shutdown() throws InterruptedException {
		channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	}

	public Map<Integer, Double> moveRobot(RobotConst.MOVE move, int step) {
		MetricResponse response;
		MoveRequest req = MoveRequest.newBuilder().setStep(step).build();

		switch (move) {
			case FORWARD:
				response = blockingStub.forward(req);
				break;
			case TURN_LEFT:
				response = blockingStub.turnLeft(req);
				break;
			case TURN_RIGHT:
				response = blockingStub.turnRight(req);
				break;
			default:
				throw new InvalidParameterException("Invalid movement");
		}

		return response.getValuesMap();
	}

	public boolean calibrate() {
		CalibrateRequest req = CalibrateRequest.newBuilder().build();
		Status response = blockingStub.calibrate(req);
		return response.getStatus();
	}

	public boolean waitForRobotStart(GrpcService.RobotStatus.Mode mode) {
		RobotStatus req = RobotStatus.newBuilder().setMode(mode).build();
		Status response = blockingStub.waitForRobotStart(req);
		return response.getStatus();
	}

	public boolean stopRobot(GrpcService.RobotStatus.Mode mode) {
		RobotStatus req = RobotStatus.newBuilder().setMode(mode).build();
		Status response = blockingStub.stopRobot(req);
		return response.getStatus();
	}

	public EchoResponse echo() {
//		byte[] CDRIVES = hexStringToByteArray("e04fd020ea3a6910a2");
//		ByteString str = ByteString.copyFrom(CDRIVES);
		ByteString testStr = ByteString.copyFrom("F", Charset.forName("utf-8"));
		logger.info(testStr);
		EchoRequest req = EchoRequest.newBuilder().setMessage(testStr).build();
		logger.info(req);
		EchoResponse response = blockingStub.echo(req);
		return response;
	}

	private static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public Map<Integer, Double> getMetrics() {
		// set id to be 0 by default to fetch all sensor reading
		MetricRequest request = MetricRequest.newBuilder().setId(0).build();
		MetricResponse response = blockingStub.getMetrics(request);
		return response.getValuesMap();
	}

	public Position setPosition(int x, int y) {
		Position request = Position.newBuilder().setX(x).setY(y).build();
		Position response = blockingStub.setPosition(request);
		return response;
	}

	public boolean setMap(String mapString) {
		byte mapByte = Byte.parseByte(mapString, 2);
		byte[] mapByteList = new byte[]{mapByte};
		ByteString mapByteString = ByteString.copyFrom(mapByteList);
		MapDescription request = MapDescription.newBuilder().setDescription(mapByteString).build();
		Status response = blockingStub.setMap(request);
		return response.getStatus();
	}

	public Position getWayPoint() {
		EmptyRequest request = EmptyRequest.newBuilder().build();
		Position response = blockingStub.getWayPoint(request);
		return response;
	}
}

