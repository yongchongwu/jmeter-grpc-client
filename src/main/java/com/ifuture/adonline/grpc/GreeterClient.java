package com.ifuture.adonline.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;


public class GreeterClient {

  private final ManagedChannel channel;
  private final GreeterServiceGrpc.GreeterServiceBlockingStub blockingStub;
  private final GreeterServiceGrpc.GreeterServiceStub asyncStub;
  private long totalBytesPut = 0;

  public GreeterClient(String host, int port) {
    this(ManagedChannelBuilder.forAddress(host, port)
        .usePlaintext(true));
  }

  GreeterClient(ManagedChannelBuilder<?> channelBuilder) {
    channel = channelBuilder.build();
    blockingStub = GreeterServiceGrpc.newBlockingStub(channel);
    asyncStub = GreeterServiceGrpc.newStub(channel);

  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  public String healthcheck() {
    HelloRequest request = HelloRequest.newBuilder().setName("Jack").build();
    HelloReply response = null;
    try {
      response = blockingStub.sayHello(request);
      System.out.println(response.getMessage());
    } catch (StatusRuntimeException e) {
      throw e;
    }
    return response.getMessage();
  }

  public static void main(String[] args) throws Exception {
    GreeterClient client = new GreeterClient("192.168.220.5", 6565);
    try {
      client.healthcheck();
    } finally {
      client.shutdown();
    }
  }

}