package communication;

import config.RobotConst;
import grpc.GRPCControlServiceGrpc;
import grpc.GrpcService;
import grpc.GrpcService.CalibrateRequest;
import grpc.GrpcService.MoveRequest;
import grpc.GrpcService.RobotStatus;
import grpc.GrpcService.Status;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.security.InvalidParameterException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class GrpcControlClient {
	private ManagedChannel channel;
	private GRPCControlServiceGrpc.GRPCControlServiceBlockingStub blockingStub;
	private boolean isConnected;
	private static final Logger logger = Logger.getLogger("src/main/java/communication");
	private static GrpcControlClient client;

	// Singleton class is used. Only one client is present at any time
	public static GrpcControlClient getInstance() {
		if (client == null)
			client = new GrpcControlClient();
		return client;
	}

	public GrpcControlClient() {
		isConnected = false;
	}

	public void connect(String host, int port) {
		assert !isConnected : "Control client has already connected to the gRPC server";
		channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
		blockingStub = GRPCControlServiceGrpc.newBlockingStub(channel);
		isConnected = true;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void shutdown() throws InterruptedException {
		channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	}

	public boolean moveRobot(RobotConst.MOVE move, int step) {
		Status response;
		MoveRequest req = MoveRequest.newBuilder().setStep(step).build();

		switch (move) {
			case FORWARD:
				response = blockingStub.forward(req);
				break;
			case TURN_LEFT:
				response = blockingStub.turnAntiClockwise(req);
				break;
			case TURN_RIGHT:
				response = blockingStub.turnClockwise(req);
				break;
			default:
				throw new InvalidParameterException("Invalid movement");
		}

		return response.getStatus();
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
}

