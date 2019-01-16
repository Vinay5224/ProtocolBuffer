package com.sotero.proto;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.protobuf.Descriptors.Descriptor;
import com.sotero.proto.SbTestProtos.Columns;
import com.sotero.proto.SbTestProtos.RECORDS;
import com.sotero.proto.SbTestProtos.SbTestMsgs;
import com.sotero.proto.SbTestProtos.Columns.ColumnsValues;
import com.sotero.proto.SbTestProtos.RECORDS.ROWS;


/**
 * @Description: This class is reading the Json input and using the SbTestProtos.java
 *               we are building the proto buffer(ByteArray).
 *               We also have methods to parse the ByteArray and build the Json Object.  
 *               
 * @author exa1
 *
 */
public class AddSbtest {
	
	

	public static void main(String[] args) throws IOException {

		SbTestMsgs.Builder sbtest = SbTestMsgs.newBuilder();

		String content = new String(Files.readAllBytes(Paths.get("/home/exa1/Downloads/Protos/json.txt")));
		JSONObject sbtestMessage = new JSONObject(content);
		
		sbtest.setAid(sbtestMessage.getString("aid")); 
		sbtest.setTid(sbtestMessage.getString("tid"));
		sbtest.setCols(addColumns(sbtestMessage.getJSONArray("COLUMNS")));
		sbtest.setRecords(
				addRecords(sbtestMessage.getJSONArray("RECORDS"), sbtestMessage.getJSONArray("COLUMNS").length()));

		byte[] sbtestResult = sbtest.build().toByteArray();

		//System.out.println(sbtest.build().toString());
		
		
		SbTestMsgs sbtestParse = SbTestMsgs.parseFrom(sbtestResult);  //Parsing ByteArray to SbTestMsgs(Actual Message JavaObject)
		ReadSbtest(sbtestParse); 
		


	}

	/**
	 * Description: Adding Records to Message Object 
	 * 
	 * @param jsonRecords
	 * @param colLength is the Columns length in a JSONObject
	 * @return
	 */
	public static RECORDS addRecords(JSONArray jsonRecords, int colLength) {

		RECORDS.Builder records = RECORDS.newBuilder();
		int rowsLength = jsonRecords.length();
		for (int i = 0; i < rowsLength; i++) {
			JSONObject jsonRows = jsonRecords.getJSONObject(i);
			JSONArray jsonRow = jsonRows.getJSONArray("ROWS");
			RECORDS.ROWS.Builder rows = RECORDS.ROWS.newBuilder();
			for (int j = 0; j < colLength; j++) {
				rows.addValue(jsonRow.getString(j));

			}
			records.addRows(rows);
		}

		return records.build();
	}

	/**
	 * Description: Adding Columns to Message Object
	 * @param jsonColumns
	 * @return
	 */
	public static Columns addColumns(JSONArray jsonColumns) {

		Columns.Builder cols = Columns.newBuilder();
		int colLength = jsonColumns.length();

		for (int i = 0; i <colLength; i++) {
			JSONObject table = jsonColumns.getJSONObject(i);
			Columns.ColumnsValues.Builder colValues = Columns.ColumnsValues.newBuilder();
			colValues.setName(table.getString("name"));
			colValues.setTablename(table.getString("tablename"));
			cols.addColval(colValues);

		}

		return cols.build();
	}


	/**
	 * Description:  This method builds the JsonObject from SbTestMsgs java object,
	 *               which parsed from ByteArray.
	 * @param sbtestParse
	 */
	 public static void ReadSbtest(SbTestMsgs sbtestParse) {
		JSONObject writeObj = new JSONObject();
		writeObj.put("aid",sbtestParse.getAid());
		writeObj.put("tid",sbtestParse.getTid());
		
		RECORDS record = sbtestParse.getRecords();
		JSONArray recordArray = new JSONArray();
		

			for(ROWS row : record.getRowsList()){	
				JSONObject rowObj = new JSONObject();
				JSONArray rowArray = new JSONArray();
				for(String val : row.getValueList()){
					rowArray.put(val);
				}
				rowObj.put("ROWS", rowArray);
				recordArray.put(rowObj);
			}
			
			writeObj.put("RECORDS", recordArray);
		
			
			Columns column = sbtestParse.getCols();
			JSONArray colArray = new JSONArray();
			
			for(ColumnsValues cols : column.getColvalList()){
				JSONObject columnObjects = new JSONObject();
				columnObjects.put("name", cols.getName());
				columnObjects.put("tablename", cols.getTablename());
				
				colArray.put(columnObjects);
				
			}
			
			writeObj.put("COLUMNS", colArray);
			System.out.println(writeObj);
	}


}
