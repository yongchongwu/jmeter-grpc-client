package com.ifuture.adonline.grpc;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.grpc.ForwardingClientCallListener.SimpleForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdvertisingClientInterceptor implements ClientInterceptor {

  private final Logger logger = LoggerFactory.getLogger(AdvertisingClientInterceptor.class);

  private final Metadata.Key<String> token = Metadata.Key.of("access_token", Metadata.ASCII_STRING_MARSHALLER);

  @Override
  public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
      MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {
    logger.info("AdvertisingClientInterceptor...");
    //return channel.newCall(methodDescriptor, callOptions);
    return new SimpleForwardingClientCall<ReqT, RespT>(
        channel.newCall(methodDescriptor, callOptions)) {
      @Override
      public void start(Listener<RespT> responseListener, Metadata headers) {
        headers.put(token, "A2D05E5ED2414B1F8C6AEB19F40EF77C");
        super.start(new SimpleForwardingClientCallListener<RespT>(responseListener) {
          @Override
          public void onHeaders(Metadata headers) {
            logger.info("header received from server:" + headers);
            super.onHeaders(headers);
          }
        }, headers);
      }
    };
  }
}
