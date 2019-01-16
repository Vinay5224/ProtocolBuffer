package com.example.testcases;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.tutorial.AddressBookProtos.Person;
import com.example.tutorial.sotero.SbTestProtos.Columns;
import com.example.tutorial.sotero.SbTestProtos.Columns.ColumnsValues;
import com.example.tutorial.sotero.SbTestProtos.RECORDS;
import com.example.tutorial.sotero.SbTestProtos.RECORDS.ROWS;
import com.example.tutorial.sotero.SbTestProtos.SbTestMsgs;

public class AddSbtest {

	/**
	 * 
	 * @param jsonRecords
	 * @param colLength
	 *            is the Columns length in a JSONObject (Note)
	 * @return
	 */
	static RECORDS addRecords(JSONArray jsonRecords, int colLength) {

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

	// Columns is Building here
	static Columns addColumns(JSONArray jsonColumns) {

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

	public static void main(String[] args) throws IOException {

		SbTestMsgs.Builder sbtest = SbTestMsgs.newBuilder();

		String content = new String(Files.readAllBytes(Paths.get("/home/exa1/Downloads/Protos/test.txt")));
		JSONObject sbtestMessage = new JSONObject(content);
		sbtest.setAid(sbtestMessage.getString("aid"));
		sbtest.setTid(sbtestMessage.getString("tid"));
		sbtest.setCols(addColumns(sbtestMessage.getJSONArray("COLUMNS")));
		sbtest.setRecords(
				addRecords(sbtestMessage.getJSONArray("RECORDS"), sbtestMessage.getJSONArray("COLUMNS").length()));

		byte[] sbtestResult = sbtest.build().toByteArray();

		//System.out.println(sbtest.build().toString());
		
		SbTestMsgs sbtestParse = SbTestMsgs.parseFrom(sbtestResult);
		ReadSbtest(sbtestParse);
		


	}

	 static void ReadSbtest(SbTestMsgs sbtestParse) {
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
