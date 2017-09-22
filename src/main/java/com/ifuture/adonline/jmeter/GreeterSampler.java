package com.ifuture.adonline.jmeter;

import com.ifuture.adonline.grpc.GreeterClient;
import io.grpc.StatusRuntimeException;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

public class GreeterSampler extends AbstractJavaSamplerClient {

  GreeterClient bclient = null;

  @Override
  public void setupTest(JavaSamplerContext context) {

    String host = context.getParameter("host");
    String port = context.getParameter("port");

    this.bclient = new GreeterClient(host, Integer.parseInt(port));

    super.setupTest(context);
  }

  @Override
  public Arguments getDefaultParameters() {
    Arguments defaultParameters = new Arguments();
    defaultParameters.addArgument("host", "192.168.220.5");
    defaultParameters.addArgument("port", "6565");
    return defaultParameters;
  }

  public SampleResult runTest(JavaSamplerContext context) {

    SampleResult result = new SampleResult();
    boolean success = true;
    String response = "";
    result.sampleStart();

    try {
      response = this.bclient.healthcheck();
      result.sampleEnd();
      result.setSuccessful(success);
      result.setResponseData(response.getBytes());
      result.setResponseMessage("Successfully performed backup healthcheck");
      result.setResponseCodeOK(); // 200 code
    } catch (StatusRuntimeException e) {
      result.sampleEnd(); // stop stopwatch
      result.setSuccessful(false);
      result.setResponseMessage("Exception: " + e);
      success = false;
      result.setSuccessful(success);
      // get stack trace as a String to return as document data
      java.io.StringWriter stringWriter = new java.io.StringWriter();
      e.printStackTrace(new java.io.PrintWriter(stringWriter));
      result.setResponseData(stringWriter.toString().getBytes());
      result.setDataType(SampleResult.TEXT);
      result.setResponseCode("500");
    }

    return result;
  }

  @Override
  public void teardownTest(JavaSamplerContext context) {
    try {
      bclient.shutdown();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    super.teardownTest(context);
  }

}
