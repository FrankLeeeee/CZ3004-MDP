package communication;

import com.google.protobuf.ByteString;
import config.RobotConst;
import grpc.GRPCServiceGrpc;
import grpc.GrpcService;
import grpc.GrpcService.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import map.Map;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;
import java.security.InvalidParameterException;
import java.util.HashMap;
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

	public java.util.Map<Integer, Float> moveRobot(RobotConst.MOVE move, int val) {
		MetricResponse response;

		try {
			switch (move) {
				case FORWARD:
					MoveRequest fwdReq = MoveRequest.newBuilder().setStep(val).build();
					response = blockingStub.withDeadlineAfter(5000, TimeUnit.MILLISECONDS).forward(fwdReq);
					break;
				case TURN_LEFT:
					TurnRequest turnLeftReq = TurnRequest.newBuilder().setAngle(90).build();
					response = blockingStub.withDeadlineAfter(10000, TimeUnit.MILLISECONDS).turnLeft(turnLeftReq);
					break;
				case TURN_RIGHT:
					TurnRequest turnRightReq = TurnRequest.newBuilder().setAngle(90).build();
					response = blockingStub.withDeadlineAfter(10000, TimeUnit.MILLISECONDS).turnRight(turnRightReq);
					break;
				default:
					throw new InvalidParameterException("Invalid movement");
			}
		} catch (Exception e) {
			java.util.Map<Integer, Float> res = new HashMap();
			res.put(1, (float) 0.0);
			res.put(2, (float) 0.0);
			res.put(3, (float) 0.0);
			res.put(4, (float) 0.0);
			res.put(5, (float) 0.0);
			res.put(6, (float) 0.0);
			return res;
		}

		return response.getValuesMap();
	}

	public boolean calibrate(int mode) {
		CalibrationRequest req = CalibrationRequest.newBuilder().setMode(mode).build();
		try {
			Status response = blockingStub.withDeadlineAfter(10000, TimeUnit.MILLISECONDS).calibrate(req);
			return response.getStatus();
		} catch (Exception e) {
			return true;
		}
	}

	public boolean waitForRobotStart(GrpcService.RobotStatus.Mode mode) {
		RobotStatus req = RobotStatus.newBuilder().setMode(mode).build();
		Status response = blockingStub.waitForRobotStart(req);
		return response.getStatus();
	}

	public boolean stopRobot() {
		EmptyRequest req = EmptyRequest.newBuilder().build();
		Status response = blockingStub.stopRobot(req);
		return response.getStatus();
	}

	public EchoResponse echo() {
		ByteString testStr = ByteString.copyFrom("F", Charset.forName("utf-8"));
		logger.info(testStr);
		EchoRequest req = EchoRequest.newBuilder().setMessage(testStr).build();
		logger.info(req);
		EchoResponse response = blockingStub.echo(req);
		return response;
	}

	public java.util.Map<Integer, Float> getMetrics() {
		// set id to be 0 by default to fetch all sensor reading
		EmptyRequest request = EmptyRequest.newBuilder().build();
		MetricResponse response = blockingStub.getMetrics(request);
		return response.getValuesMap();
	}

	public boolean setPosition(int x, int y, Position.Direction dir) {
		return true;
//		Position request = Position.newBuilder().setX(x).setY(y).setDir(dir).build();
//		Status response = blockingStub.setPosition(request);
//		return response.getStatus();
	}

	public boolean setMap(Map map) {
		String[] mapDescriptor = MapDescriptor.generateMapDescriptor(map);
		MapDescription request = MapDescription.newBuilder().setP1(mapDescriptor[0]).setP2(mapDescriptor[1]).build();
		Status response = blockingStub.setMap(request);
		return response.getStatus();
	}

	public Position getWayPoint() {
		EmptyRequest request = EmptyRequest.newBuilder().build();
		Position response = blockingStub.getWayPoint(request);
		return response;
	}

	public boolean takePhoto() {
		EmptyRequest request = EmptyRequest.newBuilder().build();
		Status status = blockingStub.takePhoto(request);
		return status.getStatus();
	}

	public void getFinalImageResults(String filePath) throws IOException {
		EmptyRequest request = EmptyRequest.newBuilder().build();
		ImageResponse response = blockingStub.getImageResult(request);
		ByteString imageBytes = response.getRawImage();

		if (imageBytes.size() == 0) {
			logger.info("no image is found");
			return;
		}

		ByteArrayOutputStream bis = new ByteArrayOutputStream();
		imageBytes.writeTo(bis);
		InputStream is = new ByteArrayInputStream(bis.toByteArray());

		BufferedImage newBi = ImageIO.read(is);
		ImageIO.write(newBi, "jpg", new File(filePath));
	}
}

