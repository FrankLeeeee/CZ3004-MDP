package grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.15.0)",
    comments = "Source: grpc_service.proto")
public final class GRPCControlServiceGrpc {

  private GRPCControlServiceGrpc() {}

  public static final String SERVICE_NAME = "GRPCControlService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<grpc.GrpcService.MoveRequest,
      grpc.GrpcService.Status> getForwardMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Forward",
      requestType = grpc.GrpcService.MoveRequest.class,
      responseType = grpc.GrpcService.Status.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<grpc.GrpcService.MoveRequest,
      grpc.GrpcService.Status> getForwardMethod() {
    io.grpc.MethodDescriptor<grpc.GrpcService.MoveRequest, grpc.GrpcService.Status> getForwardMethod;
    if ((getForwardMethod = GRPCControlServiceGrpc.getForwardMethod) == null) {
      synchronized (GRPCControlServiceGrpc.class) {
        if ((getForwardMethod = GRPCControlServiceGrpc.getForwardMethod) == null) {
          GRPCControlServiceGrpc.getForwardMethod = getForwardMethod = 
              io.grpc.MethodDescriptor.<grpc.GrpcService.MoveRequest, grpc.GrpcService.Status>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "GRPCControlService", "Forward"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.MoveRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.Status.getDefaultInstance()))
                  .setSchemaDescriptor(new GRPCControlServiceMethodDescriptorSupplier("Forward"))
                  .build();
          }
        }
     }
     return getForwardMethod;
  }

  private static volatile io.grpc.MethodDescriptor<grpc.GrpcService.MoveRequest,
      grpc.GrpcService.Status> getTurnClockwiseMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "TurnClockwise",
      requestType = grpc.GrpcService.MoveRequest.class,
      responseType = grpc.GrpcService.Status.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<grpc.GrpcService.MoveRequest,
      grpc.GrpcService.Status> getTurnClockwiseMethod() {
    io.grpc.MethodDescriptor<grpc.GrpcService.MoveRequest, grpc.GrpcService.Status> getTurnClockwiseMethod;
    if ((getTurnClockwiseMethod = GRPCControlServiceGrpc.getTurnClockwiseMethod) == null) {
      synchronized (GRPCControlServiceGrpc.class) {
        if ((getTurnClockwiseMethod = GRPCControlServiceGrpc.getTurnClockwiseMethod) == null) {
          GRPCControlServiceGrpc.getTurnClockwiseMethod = getTurnClockwiseMethod = 
              io.grpc.MethodDescriptor.<grpc.GrpcService.MoveRequest, grpc.GrpcService.Status>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "GRPCControlService", "TurnClockwise"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.MoveRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.Status.getDefaultInstance()))
                  .setSchemaDescriptor(new GRPCControlServiceMethodDescriptorSupplier("TurnClockwise"))
                  .build();
          }
        }
     }
     return getTurnClockwiseMethod;
  }

  private static volatile io.grpc.MethodDescriptor<grpc.GrpcService.MoveRequest,
      grpc.GrpcService.Status> getTurnAntiClockwiseMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "TurnAntiClockwise",
      requestType = grpc.GrpcService.MoveRequest.class,
      responseType = grpc.GrpcService.Status.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<grpc.GrpcService.MoveRequest,
      grpc.GrpcService.Status> getTurnAntiClockwiseMethod() {
    io.grpc.MethodDescriptor<grpc.GrpcService.MoveRequest, grpc.GrpcService.Status> getTurnAntiClockwiseMethod;
    if ((getTurnAntiClockwiseMethod = GRPCControlServiceGrpc.getTurnAntiClockwiseMethod) == null) {
      synchronized (GRPCControlServiceGrpc.class) {
        if ((getTurnAntiClockwiseMethod = GRPCControlServiceGrpc.getTurnAntiClockwiseMethod) == null) {
          GRPCControlServiceGrpc.getTurnAntiClockwiseMethod = getTurnAntiClockwiseMethod = 
              io.grpc.MethodDescriptor.<grpc.GrpcService.MoveRequest, grpc.GrpcService.Status>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "GRPCControlService", "TurnAntiClockwise"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.MoveRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.Status.getDefaultInstance()))
                  .setSchemaDescriptor(new GRPCControlServiceMethodDescriptorSupplier("TurnAntiClockwise"))
                  .build();
          }
        }
     }
     return getTurnAntiClockwiseMethod;
  }

  private static volatile io.grpc.MethodDescriptor<grpc.GrpcService.CalibrateRequest,
      grpc.GrpcService.Status> getCalibrateMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Calibrate",
      requestType = grpc.GrpcService.CalibrateRequest.class,
      responseType = grpc.GrpcService.Status.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<grpc.GrpcService.CalibrateRequest,
      grpc.GrpcService.Status> getCalibrateMethod() {
    io.grpc.MethodDescriptor<grpc.GrpcService.CalibrateRequest, grpc.GrpcService.Status> getCalibrateMethod;
    if ((getCalibrateMethod = GRPCControlServiceGrpc.getCalibrateMethod) == null) {
      synchronized (GRPCControlServiceGrpc.class) {
        if ((getCalibrateMethod = GRPCControlServiceGrpc.getCalibrateMethod) == null) {
          GRPCControlServiceGrpc.getCalibrateMethod = getCalibrateMethod = 
              io.grpc.MethodDescriptor.<grpc.GrpcService.CalibrateRequest, grpc.GrpcService.Status>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "GRPCControlService", "Calibrate"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.CalibrateRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.Status.getDefaultInstance()))
                  .setSchemaDescriptor(new GRPCControlServiceMethodDescriptorSupplier("Calibrate"))
                  .build();
          }
        }
     }
     return getCalibrateMethod;
  }

  private static volatile io.grpc.MethodDescriptor<grpc.GrpcService.RobotStatus,
      grpc.GrpcService.Status> getWaitForRobotStartMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "WaitForRobotStart",
      requestType = grpc.GrpcService.RobotStatus.class,
      responseType = grpc.GrpcService.Status.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<grpc.GrpcService.RobotStatus,
      grpc.GrpcService.Status> getWaitForRobotStartMethod() {
    io.grpc.MethodDescriptor<grpc.GrpcService.RobotStatus, grpc.GrpcService.Status> getWaitForRobotStartMethod;
    if ((getWaitForRobotStartMethod = GRPCControlServiceGrpc.getWaitForRobotStartMethod) == null) {
      synchronized (GRPCControlServiceGrpc.class) {
        if ((getWaitForRobotStartMethod = GRPCControlServiceGrpc.getWaitForRobotStartMethod) == null) {
          GRPCControlServiceGrpc.getWaitForRobotStartMethod = getWaitForRobotStartMethod = 
              io.grpc.MethodDescriptor.<grpc.GrpcService.RobotStatus, grpc.GrpcService.Status>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "GRPCControlService", "WaitForRobotStart"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.RobotStatus.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.Status.getDefaultInstance()))
                  .setSchemaDescriptor(new GRPCControlServiceMethodDescriptorSupplier("WaitForRobotStart"))
                  .build();
          }
        }
     }
     return getWaitForRobotStartMethod;
  }

  private static volatile io.grpc.MethodDescriptor<grpc.GrpcService.RobotStatus,
      grpc.GrpcService.Status> getStopRobotMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "StopRobot",
      requestType = grpc.GrpcService.RobotStatus.class,
      responseType = grpc.GrpcService.Status.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<grpc.GrpcService.RobotStatus,
      grpc.GrpcService.Status> getStopRobotMethod() {
    io.grpc.MethodDescriptor<grpc.GrpcService.RobotStatus, grpc.GrpcService.Status> getStopRobotMethod;
    if ((getStopRobotMethod = GRPCControlServiceGrpc.getStopRobotMethod) == null) {
      synchronized (GRPCControlServiceGrpc.class) {
        if ((getStopRobotMethod = GRPCControlServiceGrpc.getStopRobotMethod) == null) {
          GRPCControlServiceGrpc.getStopRobotMethod = getStopRobotMethod = 
              io.grpc.MethodDescriptor.<grpc.GrpcService.RobotStatus, grpc.GrpcService.Status>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "GRPCControlService", "StopRobot"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.RobotStatus.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.Status.getDefaultInstance()))
                  .setSchemaDescriptor(new GRPCControlServiceMethodDescriptorSupplier("StopRobot"))
                  .build();
          }
        }
     }
     return getStopRobotMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static GRPCControlServiceStub newStub(io.grpc.Channel channel) {
    return new GRPCControlServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static GRPCControlServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new GRPCControlServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static GRPCControlServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new GRPCControlServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class GRPCControlServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void forward(grpc.GrpcService.MoveRequest request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.Status> responseObserver) {
      asyncUnimplementedUnaryCall(getForwardMethod(), responseObserver);
    }

    /**
     */
    public void turnClockwise(grpc.GrpcService.MoveRequest request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.Status> responseObserver) {
      asyncUnimplementedUnaryCall(getTurnClockwiseMethod(), responseObserver);
    }

    /**
     */
    public void turnAntiClockwise(grpc.GrpcService.MoveRequest request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.Status> responseObserver) {
      asyncUnimplementedUnaryCall(getTurnAntiClockwiseMethod(), responseObserver);
    }

    /**
     */
    public void calibrate(grpc.GrpcService.CalibrateRequest request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.Status> responseObserver) {
      asyncUnimplementedUnaryCall(getCalibrateMethod(), responseObserver);
    }

    /**
     */
    public void waitForRobotStart(grpc.GrpcService.RobotStatus request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.Status> responseObserver) {
      asyncUnimplementedUnaryCall(getWaitForRobotStartMethod(), responseObserver);
    }

    /**
     */
    public void stopRobot(grpc.GrpcService.RobotStatus request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.Status> responseObserver) {
      asyncUnimplementedUnaryCall(getStopRobotMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getForwardMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                grpc.GrpcService.MoveRequest,
                grpc.GrpcService.Status>(
                  this, METHODID_FORWARD)))
          .addMethod(
            getTurnClockwiseMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                grpc.GrpcService.MoveRequest,
                grpc.GrpcService.Status>(
                  this, METHODID_TURN_CLOCKWISE)))
          .addMethod(
            getTurnAntiClockwiseMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                grpc.GrpcService.MoveRequest,
                grpc.GrpcService.Status>(
                  this, METHODID_TURN_ANTI_CLOCKWISE)))
          .addMethod(
            getCalibrateMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                grpc.GrpcService.CalibrateRequest,
                grpc.GrpcService.Status>(
                  this, METHODID_CALIBRATE)))
          .addMethod(
            getWaitForRobotStartMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                grpc.GrpcService.RobotStatus,
                grpc.GrpcService.Status>(
                  this, METHODID_WAIT_FOR_ROBOT_START)))
          .addMethod(
            getStopRobotMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                grpc.GrpcService.RobotStatus,
                grpc.GrpcService.Status>(
                  this, METHODID_STOP_ROBOT)))
          .build();
    }
  }

  /**
   */
  public static final class GRPCControlServiceStub extends io.grpc.stub.AbstractStub<GRPCControlServiceStub> {
    private GRPCControlServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GRPCControlServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GRPCControlServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GRPCControlServiceStub(channel, callOptions);
    }

    /**
     */
    public void forward(grpc.GrpcService.MoveRequest request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.Status> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getForwardMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void turnClockwise(grpc.GrpcService.MoveRequest request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.Status> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getTurnClockwiseMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void turnAntiClockwise(grpc.GrpcService.MoveRequest request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.Status> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getTurnAntiClockwiseMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void calibrate(grpc.GrpcService.CalibrateRequest request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.Status> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCalibrateMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void waitForRobotStart(grpc.GrpcService.RobotStatus request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.Status> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getWaitForRobotStartMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void stopRobot(grpc.GrpcService.RobotStatus request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.Status> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getStopRobotMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class GRPCControlServiceBlockingStub extends io.grpc.stub.AbstractStub<GRPCControlServiceBlockingStub> {
    private GRPCControlServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GRPCControlServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GRPCControlServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GRPCControlServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public grpc.GrpcService.Status forward(grpc.GrpcService.MoveRequest request) {
      return blockingUnaryCall(
          getChannel(), getForwardMethod(), getCallOptions(), request);
    }

    /**
     */
    public grpc.GrpcService.Status turnClockwise(grpc.GrpcService.MoveRequest request) {
      return blockingUnaryCall(
          getChannel(), getTurnClockwiseMethod(), getCallOptions(), request);
    }

    /**
     */
    public grpc.GrpcService.Status turnAntiClockwise(grpc.GrpcService.MoveRequest request) {
      return blockingUnaryCall(
          getChannel(), getTurnAntiClockwiseMethod(), getCallOptions(), request);
    }

    /**
     */
    public grpc.GrpcService.Status calibrate(grpc.GrpcService.CalibrateRequest request) {
      return blockingUnaryCall(
          getChannel(), getCalibrateMethod(), getCallOptions(), request);
    }

    /**
     */
    public grpc.GrpcService.Status waitForRobotStart(grpc.GrpcService.RobotStatus request) {
      return blockingUnaryCall(
          getChannel(), getWaitForRobotStartMethod(), getCallOptions(), request);
    }

    /**
     */
    public grpc.GrpcService.Status stopRobot(grpc.GrpcService.RobotStatus request) {
      return blockingUnaryCall(
          getChannel(), getStopRobotMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class GRPCControlServiceFutureStub extends io.grpc.stub.AbstractStub<GRPCControlServiceFutureStub> {
    private GRPCControlServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GRPCControlServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GRPCControlServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GRPCControlServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.GrpcService.Status> forward(
        grpc.GrpcService.MoveRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getForwardMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.GrpcService.Status> turnClockwise(
        grpc.GrpcService.MoveRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getTurnClockwiseMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.GrpcService.Status> turnAntiClockwise(
        grpc.GrpcService.MoveRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getTurnAntiClockwiseMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.GrpcService.Status> calibrate(
        grpc.GrpcService.CalibrateRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCalibrateMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.GrpcService.Status> waitForRobotStart(
        grpc.GrpcService.RobotStatus request) {
      return futureUnaryCall(
          getChannel().newCall(getWaitForRobotStartMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.GrpcService.Status> stopRobot(
        grpc.GrpcService.RobotStatus request) {
      return futureUnaryCall(
          getChannel().newCall(getStopRobotMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_FORWARD = 0;
  private static final int METHODID_TURN_CLOCKWISE = 1;
  private static final int METHODID_TURN_ANTI_CLOCKWISE = 2;
  private static final int METHODID_CALIBRATE = 3;
  private static final int METHODID_WAIT_FOR_ROBOT_START = 4;
  private static final int METHODID_STOP_ROBOT = 5;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final GRPCControlServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(GRPCControlServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_FORWARD:
          serviceImpl.forward((grpc.GrpcService.MoveRequest) request,
              (io.grpc.stub.StreamObserver<grpc.GrpcService.Status>) responseObserver);
          break;
        case METHODID_TURN_CLOCKWISE:
          serviceImpl.turnClockwise((grpc.GrpcService.MoveRequest) request,
              (io.grpc.stub.StreamObserver<grpc.GrpcService.Status>) responseObserver);
          break;
        case METHODID_TURN_ANTI_CLOCKWISE:
          serviceImpl.turnAntiClockwise((grpc.GrpcService.MoveRequest) request,
              (io.grpc.stub.StreamObserver<grpc.GrpcService.Status>) responseObserver);
          break;
        case METHODID_CALIBRATE:
          serviceImpl.calibrate((grpc.GrpcService.CalibrateRequest) request,
              (io.grpc.stub.StreamObserver<grpc.GrpcService.Status>) responseObserver);
          break;
        case METHODID_WAIT_FOR_ROBOT_START:
          serviceImpl.waitForRobotStart((grpc.GrpcService.RobotStatus) request,
              (io.grpc.stub.StreamObserver<grpc.GrpcService.Status>) responseObserver);
          break;
        case METHODID_STOP_ROBOT:
          serviceImpl.stopRobot((grpc.GrpcService.RobotStatus) request,
              (io.grpc.stub.StreamObserver<grpc.GrpcService.Status>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class GRPCControlServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    GRPCControlServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return grpc.GrpcService.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("GRPCControlService");
    }
  }

  private static final class GRPCControlServiceFileDescriptorSupplier
      extends GRPCControlServiceBaseDescriptorSupplier {
    GRPCControlServiceFileDescriptorSupplier() {}
  }

  private static final class GRPCControlServiceMethodDescriptorSupplier
      extends GRPCControlServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    GRPCControlServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (GRPCControlServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new GRPCControlServiceFileDescriptorSupplier())
              .addMethod(getForwardMethod())
              .addMethod(getTurnClockwiseMethod())
              .addMethod(getTurnAntiClockwiseMethod())
              .addMethod(getCalibrateMethod())
              .addMethod(getWaitForRobotStartMethod())
              .addMethod(getStopRobotMethod())
              .build();
        }
      }
    }
    return result;
  }
}
