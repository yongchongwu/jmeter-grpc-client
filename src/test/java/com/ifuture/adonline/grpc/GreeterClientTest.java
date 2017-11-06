package com.ifuture.adonline.grpc;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import io.grpc.StatusRuntimeException;
import java.util.ResourceBundle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GreeterClientTest {

  String host = null;
  int port;
  GreeterClient gclient;

  private void readProperties() {
    ResourceBundle unittestBundle = ResourceBundle.getBundle("unittest");
    this.host = unittestBundle.getString("host");
    this.port = Integer.parseInt(unittestBundle.getString("port"));
  }

  //@Before
  public void setUp() {
    readProperties();
    gclient = new GreeterClient(host, this.port);
  }

  //@Test
  public void testHealthcheck() {

    try {
      String response = gclient.healthcheck();
      assertTrue(response.contains("Jack"));
    } catch (StatusRuntimeException e) {
      fail("healthcheck threw StatusRuntimeException");
    }
  }

  //@After
  public void tearDown() {
    try {
      gclient.shutdown();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
