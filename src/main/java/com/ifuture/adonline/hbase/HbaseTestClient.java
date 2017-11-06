package com.ifuture.adonline.hbase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.RowMapper;

public class HbaseTestClient {

  private HbaseTemplate hbaseTemplate;

  public HbaseTestClient(String host_name, String port) {
    Configuration conf = HBaseConfiguration.create();
    conf.set("hbase.zookeeper.quorum", host_name);
    conf.set("hbase.zookeeper.property.clientPort", port);
    hbaseTemplate = new HbaseTemplate();
    hbaseTemplate.setConfiguration(conf);
    hbaseTemplate.setEncoding("UTF-8");
  }

  public Map<String, String> getMapsFromHbaseByRowKey(String tableName, String rowName) {
    return hbaseTemplate.get(tableName, rowName, new RowMapper<Map<String, String>>() {
      @Override
      public Map<String, String> mapRow(Result result, int i) throws Exception {
        List<Cell> ceList = result.listCells();
        Map<String, String> map = new HashMap<String, String>();
        if (ceList != null && ceList.size() > 0) {
          for (Cell cell : ceList) {
            map.put(Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(),
                cell.getQualifierLength()),
                Bytes.toString(cell.getValueArray(), cell.getValueOffset(),
                    cell.getValueLength()));
          }
        }
        return map;
      }
    });
  }

  public List<Map<String, Object>> scanTable(String tableName) {
    Scan scan = new Scan();
    return hbaseTemplate.find(tableName, scan, new RowMapper<Map<String, Object>>() {
      @Override
      public Map<String, Object> mapRow(Result result, int i) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("row" + i, Bytes.toString(result.getRow()));
        List<Cell> ceList = result.listCells();
        if (ceList != null && ceList.size() > 0) {
          for (Cell cell : ceList) {
            String value = Bytes
                .toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
            String family = Bytes
                .toString(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength());
            String quali = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(),
                cell.getQualifierLength());
            map.put(family + "_" + quali, value);
          }
        }
        return map;
      }
    });
  }

  public static void main(String[] args) {
    HbaseTestClient client = new HbaseTestClient("lehui-demo-cdh001", "2181");
    List<Map<String, Object>> list = client.scanTable("ads");
    for (Map<String, Object> map : list) {
      for (Map.Entry<String, Object> entry : map.entrySet()) {
        System.out.println(entry.getKey() + "=" + entry.getValue());
      }
    }
  }

}
