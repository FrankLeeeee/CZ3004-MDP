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
public final class GRPCDataServiceGrpc {

  private GRPCDataServiceGrpc() {}

  public static final String SERVICE_NAME = "GRPCDataService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<grpc.GrpcService.MetricRequest,
      grpc.GrpcService.MetricResponse> getGetMetricsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetMetrics",
      requestType = grpc.GrpcService.MetricRequest.class,
      responseType = grpc.GrpcService.MetricResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<grpc.GrpcService.MetricRequest,
      grpc.GrpcService.MetricResponse> getGetMetricsMethod() {
    io.grpc.MethodDescriptor<grpc.GrpcService.MetricRequest, grpc.GrpcService.MetricResponse> getGetMetricsMethod;
    if ((getGetMetricsMethod = GRPCDataServiceGrpc.getGetMetricsMethod) == null) {
      synchronized (GRPCDataServiceGrpc.class) {
        if ((getGetMetricsMethod = GRPCDataServiceGrpc.getGetMetricsMethod) == null) {
          GRPCDataServiceGrpc.getGetMetricsMethod = getGetMetricsMethod = 
              io.grpc.MethodDescriptor.<grpc.GrpcService.MetricRequest, grpc.GrpcService.MetricResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "GRPCDataService", "GetMetrics"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.MetricRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.MetricResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new GRPCDataServiceMethodDescriptorSupplier("GetMetrics"))
                  .build();
          }
        }
     }
     return getGetMetricsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<grpc.GrpcService.Position,
      grpc.GrpcService.Position> getSetPositionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SetPosition",
      requestType = grpc.GrpcService.Position.class,
      responseType = grpc.GrpcService.Position.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<grpc.GrpcService.Position,
      grpc.GrpcService.Position> getSetPositionMethod() {
    io.grpc.MethodDescriptor<grpc.GrpcService.Position, grpc.GrpcService.Position> getSetPositionMethod;
    if ((getSetPositionMethod = GRPCDataServiceGrpc.getSetPositionMethod) == null) {
      synchronized (GRPCDataServiceGrpc.class) {
        if ((getSetPositionMethod = GRPCDataServiceGrpc.getSetPositionMethod) == null) {
          GRPCDataServiceGrpc.getSetPositionMethod = getSetPositionMethod = 
              io.grpc.MethodDescriptor.<grpc.GrpcService.Position, grpc.GrpcService.Position>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "GRPCDataService", "SetPosition"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.Position.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.Position.getDefaultInstance()))
                  .setSchemaDescriptor(new GRPCDataServiceMethodDescriptorSupplier("SetPosition"))
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
    if ((getSetMapMethod = GRPCDataServiceGrpc.getSetMapMethod) == null) {
      synchronized (GRPCDataServiceGrpc.class) {
        if ((getSetMapMethod = GRPCDataServiceGrpc.getSetMapMethod) == null) {
          GRPCDataServiceGrpc.getSetMapMethod = getSetMapMethod = 
              io.grpc.MethodDescriptor.<grpc.GrpcService.MapDescription, grpc.GrpcService.Status>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "GRPCDataService", "SetMap"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.MapDescription.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.Status.getDefaultInstance()))
                  .setSchemaDescriptor(new GRPCDataServiceMethodDescriptorSupplier("SetMap"))
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
    if ((getGetWayPointMethod = GRPCDataServiceGrpc.getGetWayPointMethod) == null) {
      synchronized (GRPCDataServiceGrpc.class) {
        if ((getGetWayPointMethod = GRPCDataServiceGrpc.getGetWayPointMethod) == null) {
          GRPCDataServiceGrpc.getGetWayPointMethod = getGetWayPointMethod = 
              io.grpc.MethodDescriptor.<grpc.GrpcService.EmptyRequest, grpc.GrpcService.Position>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "GRPCDataService", "GetWayPoint"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.EmptyRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GrpcService.Position.getDefaultInstance()))
                  .setSchemaDescriptor(new GRPCDataServiceMethodDescriptorSupplier("GetWayPoint"))
                  .build();
          }
        }
     }
     return getGetWayPointMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static GRPCDataServiceStub newStub(io.grpc.Channel channel) {
    return new GRPCDataServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static GRPCDataServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new GRPCDataServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static GRPCDataServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new GRPCDataServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class GRPCDataServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void getMetrics(grpc.GrpcService.MetricRequest request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.MetricResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getGetMetricsMethod(), responseObserver);
    }

    /**
     */
    public void setPosition(grpc.GrpcService.Position request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.Position> responseObserver) {
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

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetMetricsMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                grpc.GrpcService.MetricRequest,
                grpc.GrpcService.MetricResponse>(
                  this, METHODID_GET_METRICS)))
          .addMethod(
            getSetPositionMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                grpc.GrpcService.Position,
                grpc.GrpcService.Position>(
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
          .build();
    }
  }

  /**
   */
  public static final class GRPCDataServiceStub extends io.grpc.stub.AbstractStub<GRPCDataServiceStub> {
    private GRPCDataServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GRPCDataServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GRPCDataServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GRPCDataServiceStub(channel, callOptions);
    }

    /**
     */
    public void getMetrics(grpc.GrpcService.MetricRequest request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.MetricResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetMetricsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void setPosition(grpc.GrpcService.Position request,
        io.grpc.stub.StreamObserver<grpc.GrpcService.Position> responseObserver) {
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
  }

  /**
   */
  public static final class GRPCDataServiceBlockingStub extends io.grpc.stub.AbstractStub<GRPCDataServiceBlockingStub> {
    private GRPCDataServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GRPCDataServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GRPCDataServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GRPCDataServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public grpc.GrpcService.MetricResponse getMetrics(grpc.GrpcService.MetricRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetMetricsMethod(), getCallOptions(), request);
    }

    /**
     */
    public grpc.GrpcService.Position setPosition(grpc.GrpcService.Position request) {
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
  }

  /**
   */
  public static final class GRPCDataServiceFutureStub extends io.grpc.stub.AbstractStub<GRPCDataServiceFutureStub> {
    private GRPCDataServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GRPCDataServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GRPCDataServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GRPCDataServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.GrpcService.MetricResponse> getMetrics(
        grpc.GrpcService.MetricRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetMetricsMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.GrpcService.Position> setPosition(
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
  }

  private static final int METHODID_GET_METRICS = 0;
  private static final int METHODID_SET_POSITION = 1;
  private static final int METHODID_SET_MAP = 2;
  private static final int METHODID_GET_WAY_POINT = 3;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final GRPCDataServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(GRPCDataServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_METRICS:
          serviceImpl.getMetrics((grpc.GrpcService.MetricRequest) request,
              (io.grpc.stub.StreamObserver<grpc.GrpcService.MetricResponse>) responseObserver);
          break;
        case METHODID_SET_POSITION:
          serviceImpl.setPosition((grpc.GrpcService.Position) request,
              (io.grpc.stub.StreamObserver<grpc.GrpcService.Position>) responseObserver);
          break;
        case METHODID_SET_MAP:
          serviceImpl.setMap((grpc.GrpcService.MapDescription) request,
              (io.grpc.stub.StreamObserver<grpc.GrpcService.Status>) responseObserver);
          break;
        case METHODID_GET_WAY_POINT:
          serviceImpl.getWayPoint((grpc.GrpcService.EmptyRequest) request,
              (io.grpc.stub.StreamObserver<grpc.GrpcService.Position>) responseObserver);
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

  private static abstract class GRPCDataServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    GRPCDataServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return grpc.GrpcService.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("GRPCDataService");
    }
  }

  private static final class GRPCDataServiceFileDescriptorSupplier
      extends GRPCDataServiceBaseDescriptorSupplier {
    GRPCDataServiceFileDescriptorSupplier() {}
  }

  private static final class GRPCDataServiceMethodDescriptorSupplier
      extends GRPCDataServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    GRPCDataServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (GRPCDataServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new GRPCDataServiceFileDescriptorSupplier())
              .addMethod(getGetMetricsMethod())
              .addMethod(getSetPositionMethod())
              .addMethod(getSetMapMethod())
              .addMethod(getGetWayPointMethod())
              .build();
        }
      }
    }
    return result;
  }
}
