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
public final class GRPCServiceGrpc {

  private GRPCServiceGrpc() {}

  public static final String SERVICE_NAME = "GRPCService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<grpc.GrpcService.EchoRequest,
      grpc.GrpcService.EchoResponse> getEchoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Echo",
      requestType = grpc.GrpcService.EchoRequest.class,
      responseType = grpc.GrpcService.EchoResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<grpc.GrpcService.EchoRequest,
      grpc.GrpcService.EchoResponse> getEchoMethod() {
    io.grpc.MethodDescriptor<grpc.GrpcService.EchoRequest, grpc.GrpcService.EchoResponse> getEchoMethod;
    if ((getEchoMethod = GRPCServiceGrpc.getEchoMethod) == null) {
      synchronized (GRPCServiceGrpc.class) {
        if ((getEchoMethod = GRPCServiceGrpc.getEchoMethod) == null) {
          GRPCServiceGrpc.getEchoMethod = getEchoMethod = 
              io.grpc.MethodDescriptor.<grpc.GrpcService.EchoRequest, grpc.GrpcService.EchoResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "GRPCService", "Echo"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.EchoRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.EchoResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new GRPCServiceMethodDescriptorSupplier("Echo"))
                  .build();
          }
        }
     }
     return getEchoMethod;
  }

  private static volatile io.grpc.MethodDescriptor<grpc.GrpcService.MoveRequest,
      grpc.GrpcService.MetricResponse> getForwardMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Forward",
      requestType = grpc.GrpcService.MoveRequest.class,
      responseType = grpc.GrpcService.MetricResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<grpc.GrpcService.MoveRequest,
      grpc.GrpcService.MetricResponse> getForwardMethod() {
    io.grpc.MethodDescriptor<grpc.GrpcService.MoveRequest, grpc.GrpcService.MetricResponse> getForwardMethod;
    if ((getForwardMethod = GRPCServiceGrpc.getForwardMethod) == null) {
      synchronized (GRPCServiceGrpc.class) {
        if ((getForwardMethod = GRPCServiceGrpc.getForwardMethod) == null) {
          GRPCServiceGrpc.getForwardMethod = getForwardMethod = 
              io.grpc.MethodDescriptor.<grpc.GrpcService.MoveRequest, grpc.GrpcService.MetricResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "GRPCService", "Forward"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.MoveRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.MetricResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new GRPCServiceMethodDescriptorSupplier("Forward"))
                  .build();
          }
        }
     }
     return getForwardMethod;
  }

  private static volatile io.grpc.MethodDescriptor<grpc.GrpcService.TurnRequest,
      grpc.GrpcService.MetricResponse> getTurnLeftMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "TurnLeft",
      requestType = grpc.GrpcService.TurnRequest.class,
      responseType = grpc.GrpcService.MetricResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<grpc.GrpcService.TurnRequest,
      grpc.GrpcService.MetricResponse> getTurnLeftMethod() {
    io.grpc.MethodDescriptor<grpc.GrpcService.TurnRequest, grpc.GrpcService.MetricResponse> getTurnLeftMethod;
    if ((getTurnLeftMethod = GRPCServiceGrpc.getTurnLeftMethod) == null) {
      synchronized (GRPCServiceGrpc.class) {
        if ((getTurnLeftMethod = GRPCServiceGrpc.getTurnLeftMethod) == null) {
          GRPCServiceGrpc.getTurnLeftMethod = getTurnLeftMethod = 
              io.grpc.MethodDescriptor.<grpc.GrpcService.TurnRequest, grpc.GrpcService.MetricResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "GRPCService", "TurnLeft"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.TurnRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.MetricResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new GRPCServiceMethodDescriptorSupplier("TurnLeft"))
                  .build();
          }
        }
     }
     return getTurnLeftMethod;
  }

  private static volatile io.grpc.MethodDescriptor<grpc.GrpcService.TurnRequest,
      grpc.GrpcService.MetricResponse> getTurnRightMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "TurnRight",
      requestType = grpc.GrpcService.TurnRequest.class,
      responseType = grpc.GrpcService.MetricResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<grpc.GrpcService.TurnRequest,
      grpc.GrpcService.MetricResponse> getTurnRightMethod() {
    io.grpc.MethodDescriptor<grpc.GrpcService.TurnRequest, grpc.GrpcService.MetricResponse> getTurnRightMethod;
    if ((getTurnRightMethod = GRPCServiceGrpc.getTurnRightMethod) == null) {
      synchronized (GRPCServiceGrpc.class) {
        if ((getTurnRightMethod = GRPCServiceGrpc.getTurnRightMethod) == null) {
          GRPCServiceGrpc.getTurnRightMethod = getTurnRightMethod = 
              io.grpc.MethodDescriptor.<grpc.GrpcService.TurnRequest, grpc.GrpcService.MetricResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "GRPCService", "TurnRight"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.TurnRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.MetricResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new GRPCServiceMethodDescriptorSupplier("TurnRight"))
                  .build();
          }
        }
     }
     return getTurnRightMethod;
  }

  private static volatile io.grpc.MethodDescriptor<grpc.GrpcService.EmptyRequest,
      grpc.GrpcService.Status> getCalibrateMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Calibrate",
      requestType = grpc.GrpcService.EmptyRequest.class,
      responseType = grpc.GrpcService.Status.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<grpc.GrpcService.EmptyRequest,
      grpc.GrpcService.Status> getCalibrateMethod() {
    io.grpc.MethodDescriptor<grpc.GrpcService.EmptyRequest, grpc.GrpcService.Status> getCalibrateMethod;
    if ((getCalibrateMethod = GRPCServiceGrpc.getCalibrateMethod) == null) {
      synchronized (GRPCServiceGrpc.class) {
        if ((getCalibrateMethod = GRPCServiceGrpc.getCalibrateMethod) == null) {
          GRPCServiceGrpc.getCalibrateMethod = getCalibrateMethod = 
              io.grpc.MethodDescriptor.<grpc.GrpcService.EmptyRequest, grpc.GrpcService.Status>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "GRPCService", "Calibrate"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.EmptyRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.Status.getDefaultInstance()))
                  .setSchemaDescriptor(new GRPCServiceMethodDescriptorSupplier("Calibrate"))
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
    if ((getWaitForRobotStartMethod = GRPCServiceGrpc.getWaitForRobotStartMethod) == null) {
      synchronized (GRPCServiceGrpc.class) {
        if ((getWaitForRobotStartMethod = GRPCServiceGrpc.getWaitForRobotStartMethod) == null) {
          GRPCServiceGrpc.getWaitForRobotStartMethod = getWaitForRobotStartMethod = 
              io.grpc.MethodDescriptor.<grpc.GrpcService.RobotStatus, grpc.GrpcService.Status>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "GRPCService", "WaitForRobotStart"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.RobotStatus.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.Status.getDefaultInstance()))
                  .setSchemaDescriptor(new GRPCServiceMethodDescriptorSupplier("WaitForRobotStart"))
                  .build();
          }
        }
     }
     return getWaitForRobotStartMethod;
  }

  private static volatile io.grpc.MethodDescriptor<grpc.GrpcService.EmptyRequest,
      grpc.GrpcService.Status> getStopRobotMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "StopRobot",
      requestType = grpc.GrpcService.EmptyRequest.class,
      responseType = grpc.GrpcService.Status.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<grpc.GrpcService.EmptyRequest,
      grpc.GrpcService.Status> getStopRobotMethod() {
    io.grpc.MethodDescriptor<grpc.GrpcService.EmptyRequest, grpc.GrpcService.Status> getStopRobotMethod;
    if ((getStopRobotMethod = GRPCServiceGrpc.getStopRobotMethod) == null) {
      synchronized (GRPCServiceGrpc.class) {
        if ((getStopRobotMethod = GRPCServiceGrpc.getStopRobotMethod) == null) {
          GRPCServiceGrpc.getStopRobotMethod = getStopRobotMethod = 
              io.grpc.MethodDescriptor.<grpc.GrpcService.EmptyRequest, grpc.GrpcService.Status>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "GRPCService", "StopRobot"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.EmptyRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.Status.getDefaultInstance()))
                  .setSchemaDescriptor(new GRPCServiceMethodDescriptorSupplier("StopRobot"))
                  .build();
          }
        }
     }
     return getStopRobotMethod;
  }

  private static volatile io.grpc.MethodDescriptor<grpc.GrpcService.EmptyRequest,
      grpc.GrpcService.MetricResponse> getGetMetricsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetMetrics",
      requestType = grpc.GrpcService.EmptyRequest.class,
      responseType = grpc.GrpcService.MetricResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<grpc.GrpcService.EmptyRequest,
      grpc.GrpcService.MetricResponse> getGetMetricsMethod() {
    io.grpc.MethodDescriptor<grpc.GrpcService.EmptyRequest, grpc.GrpcService.MetricResponse> getGetMetricsMethod;
    if ((getGetMetricsMethod = GRPCServiceGrpc.getGetMetricsMethod) == null) {
      synchronized (GRPCServiceGrpc.class) {
        if ((getGetMetricsMethod = GRPCServiceGrpc.getGetMetricsMethod) == null) {
          GRPCServiceGrpc.getGetMetricsMethod = getGetMetricsMethod = 
              io.grpc.MethodDescriptor.<grpc.GrpcService.EmptyRequest, grpc.GrpcService.MetricResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "GRPCService", "GetMetrics"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.EmptyRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.MetricResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new GRPCServiceMethodDescriptorSupplier("GetMetrics"))
                  .build();
          }
        }
     }
     return getGetMetricsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<grpc.GrpcService.Position,
      grpc.GrpcService.Status> getSetPositionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SetPosition",
      requestType = grpc.GrpcService.Position.class,
      responseType = grpc.GrpcService.Status.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<grpc.GrpcService.Position,
      grpc.GrpcService.Status> getSetPositionMethod() {
    io.grpc.MethodDescriptor<grpc.GrpcService.Position, grpc.GrpcService.Status> getSetPositionMethod;
    if ((getSetPositionMethod = GRPCServiceGrpc.getSetPositionMethod) == null) {
      synchronized (GRPCServiceGrpc.class) {
        if ((getSetPositionMethod = GRPCServiceGrpc.getSetPositionMethod) == null) {
          GRPCServiceGrpc.getSetPositionMethod = getSetPositionMethod = 
              io.grpc.MethodDescriptor.<grpc.GrpcService.Position, grpc.GrpcService.Status>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "GRPCService", "SetPosition"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.Position.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.Status.getDefaultInstance()))
                  .setSchemaDescriptor(new GRPCServiceMethodDescriptorSupplier("SetPosition"))
                  .build();
          }
        }
     }
     return getSetPositionMethod;
  }

  private static volatile io.grpc.MethodDescriptor<grpc.GrpcService.MapDescription,
      grpc.GrpcService.Status> getSetMapMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SetMap",
      requestType = grpc.GrpcService.MapDescription.class,
      responseType = grpc.GrpcService.Status.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<grpc.GrpcService.MapDescription,
      grpc.GrpcService.Status> getSetMapMethod() {
    io.grpc.MethodDescriptor<grpc.GrpcService.MapDescription, grpc.GrpcService.Status> getSetMapMethod;
    if ((getSetMapMethod = GRPCServiceGrpc.getSetMapMethod) == null) {
      synchronized (GRPCServiceGrpc.class) {
        if ((getSetMapMethod = GRPCServiceGrpc.getSetMapMethod) == null) {
          GRPCServiceGrpc.getSetMapMethod = getSetMapMethod = 
              io.grpc.MethodDescriptor.<grpc.GrpcService.MapDescription, grpc.GrpcService.Status>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "GRPCService", "SetMap"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.MapDescription.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.Status.getDefaultInstance()))
                  .setSchemaDescriptor(new GRPCServiceMethodDescriptorSupplier("SetMap"))
                  .build();
          }
        }
     }
     return getSetMapMethod;
  }

  private static volatile io.grpc.MethodDescriptor<grpc.GrpcService.EmptyRequest,
      grpc.GrpcService.Position> getGetWayPointMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetWayPoint",
      requestType = grpc.GrpcService.EmptyRequest.class,
      responseType = grpc.GrpcService.Position.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<grpc.GrpcService.EmptyRequest,
      grpc.GrpcService.Position> getGetWayPointMethod() {
    io.grpc.MethodDescriptor<grpc.GrpcService.EmptyRequest, grpc.GrpcService.Position> getGetWayPointMethod;
    if ((getGetWayPointMethod = GRPCServiceGrpc.getGetWayPointMethod) == null) {
      synchronized (GRPCServiceGrpc.class) {
        if ((getGetWayPointMethod = GRPCServiceGrpc.getGetWayPointMethod) == null) {
          GRPCServiceGrpc.getGetWayPointMethod = getGetWayPointMethod = 
              io.grpc.MethodDescriptor.<grpc.GrpcService.EmptyRequest, grpc.GrpcService.Position>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "GRPCService", "GetWayPoint"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.EmptyRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.Position.getDefaultInstance()))
                  .setSchemaDescriptor(new GRPCServiceMethodDescriptorSupplier("GetWayPoint"))
                  .build();
          }
        }
     }
     return getGetWayPointMethod;
  }

  private static volatile io.grpc.MethodDescriptor<grpc.GrpcService.EmptyRequest,
      grpc.GrpcService.Status> getTakePhotoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "TakePhoto",
      requestType = grpc.GrpcService.EmptyRequest.class,
      responseType = grpc.GrpcService.Status.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<grpc.GrpcService.EmptyRequest,
      grpc.GrpcService.Status> getTakePhotoMethod() {
    io.grpc.MethodDescriptor<grpc.GrpcService.EmptyRequest, grpc.GrpcService.Status> getTakePhotoMethod;
    if ((getTakePhotoMethod = GRPCServiceGrpc.getTakePhotoMethod) == null) {
      synchronized (GRPCServiceGrpc.class) {
        if ((getTakePhotoMethod = GRPCServiceGrpc.getTakePhotoMethod) == null) {
          GRPCServiceGrpc.getTakePhotoMethod = getTakePhotoMethod = 
              io.grpc.MethodDescriptor.<grpc.GrpcService.EmptyRequest, grpc.GrpcService.Status>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "GRPCService", "TakePhoto"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.EmptyRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.Status.getDefaultInstance()))
                  .setSchemaDescriptor(new GRPCServiceMethodDescriptorSupplier("TakePhoto"))
                  .build();
          }
        }
     }
     return getTakePhotoMethod;
  }

  private static volatile io.grpc.MethodDescriptor<grpc.GrpcService.EmptyRequest,
      grpc.GrpcService.ImageResponse> getGetImageResultMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetImageResult",
      requestType = grpc.GrpcService.EmptyRequest.class,
      responseType = grpc.GrpcService.ImageResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<grpc.GrpcService.EmptyRequest,
      grpc.GrpcService.ImageResponse> getGetImageResultMethod() {
    io.grpc.MethodDescriptor<grpc.GrpcService.EmptyRequest, grpc.GrpcService.ImageResponse> getGetImageResultMethod;
    if ((getGetImageResultMethod = GRPCServiceGrpc.getGetImageResultMethod) == null) {
      synchronized (GRPCServiceGrpc.class) {
        if ((getGetImageResultMethod = GRPCServiceGrpc.getGetImageResultMethod) == null) {
          GRPCServiceGrpc.getGetImageResultMethod = getGetImageResultMethod = 
              io.grpc.MethodDescriptor.<grpc.GrpcService.EmptyRequest, grpc.GrpcService.ImageResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "GRPCService", "GetImageResult"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.EmptyRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.ImageResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new GRPCServiceMethodDescriptorSupplier("GetImageResult"))
                  .build();
          }
        }
     }
     return getGetImageResultMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static GRPCServiceStub newStub(io.grpc.Channel channel) {
    return new GRPCServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static GRPCServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new GRPCServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static GRPCServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new GRPCServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class GRPCServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void echo(grpc.GrpcService.EchoRequest request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.EchoResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getEchoMethod(), responseObserver);
    }

    /**
     */
    public void forward(grpc.GrpcService.MoveRequest request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.MetricResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getForwardMethod(), responseObserver);
    }

    /**
     */
    public void turnLeft(grpc.GrpcService.TurnRequest request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.MetricResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getTurnLeftMethod(), responseObserver);
    }

    /**
     */
    public void turnRight(grpc.GrpcService.TurnRequest request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.MetricResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getTurnRightMethod(), responseObserver);
    }

    /**
     */
    public void calibrate(grpc.GrpcService.EmptyRequest request,
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
    public void stopRobot(grpc.GrpcService.EmptyRequest request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.Status> responseObserver) {
      asyncUnimplementedUnaryCall(getStopRobotMethod(), responseObserver);
    }

    /**
     */
    public void getMetrics(grpc.GrpcService.EmptyRequest request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.MetricResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getGetMetricsMethod(), responseObserver);
    }

    /**
     */
    public void setPosition(grpc.GrpcService.Position request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.Status> responseObserver) {
      asyncUnimplementedUnaryCall(getSetPositionMethod(), responseObserver);
    }

    /**
     */
    public void setMap(grpc.GrpcService.MapDescription request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.Status> responseObserver) {
      asyncUnimplementedUnaryCall(getSetMapMethod(), responseObserver);
    }

    /**
     */
    public void getWayPoint(grpc.GrpcService.EmptyRequest request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.Position> responseObserver) {
      asyncUnimplementedUnaryCall(getGetWayPointMethod(), responseObserver);
    }

    /**
     */
    public void takePhoto(grpc.GrpcService.EmptyRequest request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.Status> responseObserver) {
      asyncUnimplementedUnaryCall(getTakePhotoMethod(), responseObserver);
    }

    /**
     */
    public void getImageResult(grpc.GrpcService.EmptyRequest request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.ImageResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getGetImageResultMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getEchoMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                grpc.GrpcService.EchoRequest,
                grpc.GrpcService.EchoResponse>(
                  this, METHODID_ECHO)))
          .addMethod(
            getForwardMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                grpc.GrpcService.MoveRequest,
                grpc.GrpcService.MetricResponse>(
                  this, METHODID_FORWARD)))
          .addMethod(
            getTurnLeftMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                grpc.GrpcService.TurnRequest,
                grpc.GrpcService.MetricResponse>(
                  this, METHODID_TURN_LEFT)))
          .addMethod(
            getTurnRightMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                grpc.GrpcService.TurnRequest,
                grpc.GrpcService.MetricResponse>(
                  this, METHODID_TURN_RIGHT)))
          .addMethod(
            getCalibrateMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                grpc.GrpcService.EmptyRequest,
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
                grpc.GrpcService.EmptyRequest,
                grpc.GrpcService.Status>(
                  this, METHODID_STOP_ROBOT)))
          .addMethod(
            getGetMetricsMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                grpc.GrpcService.EmptyRequest,
                grpc.GrpcService.MetricResponse>(
                  this, METHODID_GET_METRICS)))
          .addMethod(
            getSetPositionMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                grpc.GrpcService.Position,
                grpc.GrpcService.Status>(
                  this, METHODID_SET_POSITION)))
          .addMethod(
            getSetMapMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                grpc.GrpcService.MapDescription,
                grpc.GrpcService.Status>(
                  this, METHODID_SET_MAP)))
          .addMethod(
            getGetWayPointMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                grpc.GrpcService.EmptyRequest,
                grpc.GrpcService.Position>(
                  this, METHODID_GET_WAY_POINT)))
          .addMethod(
            getTakePhotoMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                grpc.GrpcService.EmptyRequest,
                grpc.GrpcService.Status>(
                  this, METHODID_TAKE_PHOTO)))
          .addMethod(
            getGetImageResultMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                grpc.GrpcService.EmptyRequest,
                grpc.GrpcService.ImageResponse>(
                  this, METHODID_GET_IMAGE_RESULT)))
          .build();
    }
  }

  /**
   */
  public static final class GRPCServiceStub extends io.grpc.stub.AbstractStub<GRPCServiceStub> {
    private GRPCServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GRPCServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GRPCServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GRPCServiceStub(channel, callOptions);
    }

    /**
     */
    public void echo(grpc.GrpcService.EchoRequest request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.EchoResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getEchoMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void forward(grpc.GrpcService.MoveRequest request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.MetricResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getForwardMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void turnLeft(grpc.GrpcService.TurnRequest request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.MetricResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getTurnLeftMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void turnRight(grpc.GrpcService.TurnRequest request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.MetricResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getTurnRightMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void calibrate(grpc.GrpcService.EmptyRequest request,
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
    public void stopRobot(grpc.GrpcService.EmptyRequest request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.Status> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getStopRobotMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getMetrics(grpc.GrpcService.EmptyRequest request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.MetricResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetMetricsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void setPosition(grpc.GrpcService.Position request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.Status> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSetPositionMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void setMap(grpc.GrpcService.MapDescription request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.Status> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSetMapMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getWayPoint(grpc.GrpcService.EmptyRequest request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.Position> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetWayPointMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void takePhoto(grpc.GrpcService.EmptyRequest request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.Status> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getTakePhotoMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getImageResult(grpc.GrpcService.EmptyRequest request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.ImageResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetImageResultMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class GRPCServiceBlockingStub extends io.grpc.stub.AbstractStub<GRPCServiceBlockingStub> {
    private GRPCServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GRPCServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GRPCServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GRPCServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public grpc.GrpcService.EchoResponse echo(grpc.GrpcService.EchoRequest request) {
      return blockingUnaryCall(
          getChannel(), getEchoMethod(), getCallOptions(), request);
    }

    /**
     */
    public grpc.GrpcService.MetricResponse forward(grpc.GrpcService.MoveRequest request) {
      return blockingUnaryCall(
          getChannel(), getForwardMethod(), getCallOptions(), request);
    }

    /**
     */
    public grpc.GrpcService.MetricResponse turnLeft(grpc.GrpcService.TurnRequest request) {
      return blockingUnaryCall(
          getChannel(), getTurnLeftMethod(), getCallOptions(), request);
    }

    /**
     */
    public grpc.GrpcService.MetricResponse turnRight(grpc.GrpcService.TurnRequest request) {
      return blockingUnaryCall(
          getChannel(), getTurnRightMethod(), getCallOptions(), request);
    }

    /**
     */
    public grpc.GrpcService.Status calibrate(grpc.GrpcService.EmptyRequest request) {
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
    public grpc.GrpcService.Status stopRobot(grpc.GrpcService.EmptyRequest request) {
      return blockingUnaryCall(
          getChannel(), getStopRobotMethod(), getCallOptions(), request);
    }

    /**
     */
    public grpc.GrpcService.MetricResponse getMetrics(grpc.GrpcService.EmptyRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetMetricsMethod(), getCallOptions(), request);
    }

    /**
     */
    public grpc.GrpcService.Status setPosition(grpc.GrpcService.Position request) {
      return blockingUnaryCall(
          getChannel(), getSetPositionMethod(), getCallOptions(), request);
    }

    /**
     */
    public grpc.GrpcService.Status setMap(grpc.GrpcService.MapDescription request) {
      return blockingUnaryCall(
          getChannel(), getSetMapMethod(), getCallOptions(), request);
    }

    /**
     */
    public grpc.GrpcService.Position getWayPoint(grpc.GrpcService.EmptyRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetWayPointMethod(), getCallOptions(), request);
    }

    /**
     */
    public grpc.GrpcService.Status takePhoto(grpc.GrpcService.EmptyRequest request) {
      return blockingUnaryCall(
          getChannel(), getTakePhotoMethod(), getCallOptions(), request);
    }

    /**
     */
    public grpc.GrpcService.ImageResponse getImageResult(grpc.GrpcService.EmptyRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetImageResultMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class GRPCServiceFutureStub extends io.grpc.stub.AbstractStub<GRPCServiceFutureStub> {
    private GRPCServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GRPCServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GRPCServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GRPCServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.GrpcService.EchoResponse> echo(
        grpc.GrpcService.EchoRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getEchoMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.GrpcService.MetricResponse> forward(
        grpc.GrpcService.MoveRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getForwardMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.GrpcService.MetricResponse> turnLeft(
        grpc.GrpcService.TurnRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getTurnLeftMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.GrpcService.MetricResponse> turnRight(
        grpc.GrpcService.TurnRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getTurnRightMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.GrpcService.Status> calibrate(
        grpc.GrpcService.EmptyRequest request) {
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
        grpc.GrpcService.EmptyRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getStopRobotMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.GrpcService.MetricResponse> getMetrics(
        grpc.GrpcService.EmptyRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetMetricsMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.GrpcService.Status> setPosition(
        grpc.GrpcService.Position request) {
      return futureUnaryCall(
          getChannel().newCall(getSetPositionMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.GrpcService.Status> setMap(
        grpc.GrpcService.MapDescription request) {
      return futureUnaryCall(
          getChannel().newCall(getSetMapMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.GrpcService.Position> getWayPoint(
        grpc.GrpcService.EmptyRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetWayPointMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.GrpcService.Status> takePhoto(
        grpc.GrpcService.EmptyRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getTakePhotoMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.GrpcService.ImageResponse> getImageResult(
        grpc.GrpcService.EmptyRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetImageResultMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_ECHO = 0;
  private static final int METHODID_FORWARD = 1;
  private static final int METHODID_TURN_LEFT = 2;
  private static final int METHODID_TURN_RIGHT = 3;
  private static final int METHODID_CALIBRATE = 4;
  private static final int METHODID_WAIT_FOR_ROBOT_START = 5;
  private static final int METHODID_STOP_ROBOT = 6;
  private static final int METHODID_GET_METRICS = 7;
  private static final int METHODID_SET_POSITION = 8;
  private static final int METHODID_SET_MAP = 9;
  private static final int METHODID_GET_WAY_POINT = 10;
  private static final int METHODID_TAKE_PHOTO = 11;
  private static final int METHODID_GET_IMAGE_RESULT = 12;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final GRPCServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(GRPCServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_ECHO:
          serviceImpl.echo((grpc.GrpcService.EchoRequest) request,
              (io.grpc.stub.StreamObserver<grpc.GrpcService.EchoResponse>) responseObserver);
          break;
        case METHODID_FORWARD:
          serviceImpl.forward((grpc.GrpcService.MoveRequest) request,
              (io.grpc.stub.StreamObserver<grpc.GrpcService.MetricResponse>) responseObserver);
          break;
        case METHODID_TURN_LEFT:
          serviceImpl.turnLeft((grpc.GrpcService.TurnRequest) request,
              (io.grpc.stub.StreamObserver<grpc.GrpcService.MetricResponse>) responseObserver);
          break;
        case METHODID_TURN_RIGHT:
          serviceImpl.turnRight((grpc.GrpcService.TurnRequest) request,
              (io.grpc.stub.StreamObserver<grpc.GrpcService.MetricResponse>) responseObserver);
          break;
        case METHODID_CALIBRATE:
          serviceImpl.calibrate((grpc.GrpcService.EmptyRequest) request,
              (io.grpc.stub.StreamObserver<grpc.GrpcService.Status>) responseObserver);
          break;
        case METHODID_WAIT_FOR_ROBOT_START:
          serviceImpl.waitForRobotStart((grpc.GrpcService.RobotStatus) request,
              (io.grpc.stub.StreamObserver<grpc.GrpcService.Status>) responseObserver);
          break;
        case METHODID_STOP_ROBOT:
          serviceImpl.stopRobot((grpc.GrpcService.EmptyRequest) request,
              (io.grpc.stub.StreamObserver<grpc.GrpcService.Status>) responseObserver);
          break;
        case METHODID_GET_METRICS:
          serviceImpl.getMetrics((grpc.GrpcService.EmptyRequest) request,
              (io.grpc.stub.StreamObserver<grpc.GrpcService.MetricResponse>) responseObserver);
          break;
        case METHODID_SET_POSITION:
          serviceImpl.setPosition((grpc.GrpcService.Position) request,
              (io.grpc.stub.StreamObserver<grpc.GrpcService.Status>) responseObserver);
          break;
        case METHODID_SET_MAP:
          serviceImpl.setMap((grpc.GrpcService.MapDescription) request,
              (io.grpc.stub.StreamObserver<grpc.GrpcService.Status>) responseObserver);
          break;
        case METHODID_GET_WAY_POINT:
          serviceImpl.getWayPoint((grpc.GrpcService.EmptyRequest) request,
              (io.grpc.stub.StreamObserver<grpc.GrpcService.Position>) responseObserver);
          break;
        case METHODID_TAKE_PHOTO:
          serviceImpl.takePhoto((grpc.GrpcService.EmptyRequest) request,
              (io.grpc.stub.StreamObserver<grpc.GrpcService.Status>) responseObserver);
          break;
        case METHODID_GET_IMAGE_RESULT:
          serviceImpl.getImageResult((grpc.GrpcService.EmptyRequest) request,
              (io.grpc.stub.StreamObserver<grpc.GrpcService.ImageResponse>) responseObserver);
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

  private static abstract class GRPCServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    GRPCServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return grpc.GrpcService.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("GRPCService");
    }
  }

  private static final class GRPCServiceFileDescriptorSupplier
      extends GRPCServiceBaseDescriptorSupplier {
    GRPCServiceFileDescriptorSupplier() {}
  }

  private static final class GRPCServiceMethodDescriptorSupplier
      extends GRPCServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    GRPCServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (GRPCServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new GRPCServiceFileDescriptorSupplier())
              .addMethod(getEchoMethod())
              .addMethod(getForwardMethod())
              .addMethod(getTurnLeftMethod())
              .addMethod(getTurnRightMethod())
              .addMethod(getCalibrateMethod())
              .addMethod(getWaitForRobotStartMethod())
              .addMethod(getStopRobotMethod())
              .addMethod(getGetMetricsMethod())
              .addMethod(getSetPositionMethod())
              .addMethod(getSetMapMethod())
              .addMethod(getGetWayPointMethod())
              .addMethod(getTakePhotoMethod())
              .addMethod(getGetImageResultMethod())
              .build();
        }
      }
    }
    return result;
  }
}
