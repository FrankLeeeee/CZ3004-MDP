package communication;


import com.google.protobuf.ByteString;
import grpc.GRPCDataServiceGrpc;
import grpc.GrpcService.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class GrpcDataClient {
	private ManagedChannel channel;
	private GRPCDataServiceGrpc.GRPCDataServiceBlockingStub blockingStub;
	private boolean isConnected;
	private static final Logger logger = Logger.getLogger("src/main/java/communication");
	private static final String host = "127.0.0.1";
	private static final int port = 50051;
	private static GrpcDataClient client;


	// Singleton class is used. Only one client is present at any time
	public static GrpcDataClient getInstance() {
		if (client == null)
			client = new GrpcDataClient();
		return client;
	}

	public GrpcDataClient() {
		isConnected = false;
	}

	public void connect(String host, int port) {
		assert !isConnected : "Data client has already connected to the gRPC server";
		channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
		blockingStub = GRPCDataServiceGrpc.newBlockingStub(channel);
		isConnected = true;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void shutdown() throws InterruptedException {
		channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
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