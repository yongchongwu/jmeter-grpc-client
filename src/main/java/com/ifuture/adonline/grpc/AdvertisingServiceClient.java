package com.ifuture.adonline.grpc;

import com.google.protobuf.ProtocolStringList;
import fpay.bills.AdvertisingServiceGrpc;
import fpay.bills.Ifuture.AdvRequest;
import fpay.bills.Ifuture.AdvResponse;
import io.grpc.ClientInterceptors;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AdvertisingServiceClient {

  private final ManagedChannel channel;
  private final AdvertisingServiceGrpc.AdvertisingServiceBlockingStub blockingStub;
  private final AdvertisingServiceGrpc.AdvertisingServiceStub asyncStub;
  private long totalBytesPut = 0;

  public AdvertisingServiceClient(String host, int port) {
    this(ManagedChannelBuilder.forAddress(host, port)
        .usePlaintext(true));
  }

  public AdvertisingServiceClient(ManagedChannelBuilder<?> channelBuilder) {

    channel = channelBuilder.build();
    blockingStub = AdvertisingServiceGrpc
        .newBlockingStub(
            ClientInterceptors.intercept(channel, new AdvertisingClientInterceptor()));
    asyncStub = AdvertisingServiceGrpc
        .newStub(
            ClientInterceptors.intercept(channel, new AdvertisingClientInterceptor()));
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  public AdvRequest getTestAdvRequest() {
    AdvRequest.Builder builder = AdvRequest.newBuilder();
    builder.setMaid(getRandomVal(new String[]{"00052","00062","0009g"}));// 用户ID
    builder.setBussinessId("2");// 商家ID
    builder.setUa(getRandomVal(new String[]{"Android","iOS"}));// User-Agent的信息
    builder.setIp("127.0.0.1");// 交易时的IP
    builder.setPayMethond("4JBo");// 交易的付账方式
    builder.setPay(0);// 交易金额，单位为分
    builder.setNetworkId(getRandomVal(new String[]{"4g","3g","wifi"}));// 用户的网络
    AdvRequest request = builder.build();
    return request;
  }

  public String getRandomVal(String[] arr){
    int index = (int) (Math.random() * arr.length);
    return arr[index];
  }

  public List<String> getTestAdvertisement(AdvRequest request) {
    try {
      AdvResponse response = blockingStub.getAdvertisement(request);
      ProtocolStringList list = response.getAdidList();
      System.out.println("grpc请求返回结果如下:");
      for (String adid : list) {
        System.out.println(adid);
      }
      return list;
    } catch (StatusRuntimeException e) {
      throw e;
    }
  }

  public static void main(String[] args) throws Exception {
    AdvertisingServiceClient client = new AdvertisingServiceClient("localhost", 6565);
    try {
      AdvRequest request = client.getTestAdvRequest();
      client.getTestAdvertisement(request);
    } finally {
      client.shutdown();
    }
  }

}
