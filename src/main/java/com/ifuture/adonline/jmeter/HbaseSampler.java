package com.ifuture.adonline.jmeter;

import com.ifuture.adonline.hbase.HbaseTestClient;
import io.grpc.StatusRuntimeException;
import java.util.List;
import java.util.Map;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

public class HbaseSampler extends AbstractJavaSamplerClient {

  HbaseTestClient client = null;

  @Override
  public void setupTest(JavaSamplerContext context) {
    String host_name = context.getParameter("host_name");
    String host_port = context.getParameter("host_port");
    this.client = new HbaseTestClient(host_name, host_port);
    super.setupTest(context);
  }

  @Override
  public Arguments getDefaultParameters() {
    Arguments defaultParameters = new Arguments();
    defaultParameters.addArgument("host_name", "lehui-demo-cdh001");
    defaultParameters.addArgument("host_port", "2181");
    return defaultParameters;
  }

  @Override
  public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
    SampleResult result = new SampleResult();
    boolean success = true;
    result.sampleStart();

    try {
      List<Map<String, Object>> list = this.client.scanTable("ads");
      System.out.println("ok"+result.getThreadName());
      result.sampleEnd();
      result.setSuccessful(success);
      result.setResponseData("ok".getBytes());
      result.setResponseMessage("Successfully");
      result.setResponseCodeOK(); // 200 code
    } catch (StatusRuntimeException e) {
      result.sampleEnd(); // stop stopwatch
      result.setSuccessful(false);
      result.setResponseMessage("Exception: " + e);
      // get stack trace as a String to return as document data
      java.io.StringWriter stringWriter = new java.io.StringWriter();
      e.printStackTrace(new java.io.PrintWriter(stringWriter));
      result.setResponseData(stringWriter.toString().getBytes());
      result.setDataType(SampleResult.TEXT);
      result.setResponseCode("500");
    }
    return result;
  }
}
