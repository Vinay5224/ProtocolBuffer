package com.sotero.proto.test;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Base64;


import org.json.JSONException;
import org.json.JSONObject;

import com.sotero.proto.AddSbtest;
import com.sotero.proto.SbTestProtos.SbTestMsgs;

public class ProtoBufferTest {
	static JSONObject jsontxtarr = new JSONObject();
	static byte[] sbtestResult;
	static String compare;
	
	
	public static void main(String[] args) throws Exception {
		
/*		if(args.length <  1){
			
			System.out.println("Please Enter two arguments \n Json File & jsonobj or byteobj");
			System.exit(-1);
		}*/
		
		try{
		//JSON File is here
		String content = new String(Files.readAllBytes(Paths.get("/home/exa1/Downloads/Protos/100.txt")));//   ("/home/exa1/Downloads/Protos/100.txt"
        jsontxtarr = new JSONObject(content);
        compare =  "byteobj";// args[1].trim().toLowerCase(); //
        
        AddSbtest sbtestClass = new AddSbtest();
		SbTestMsgs.Builder sbtest = SbTestMsgs.newBuilder();
		
		
		sbtest.setAid(jsontxtarr.getString("aid"));
		sbtest.setTid(jsontxtarr.getString("tid"));
		sbtest.setCols(sbtestClass.addColumns(jsontxtarr.getJSONArray("COLUMNS")));
		sbtest.setRecords(sbtestClass.addRecords(jsontxtarr.getJSONArray("RECORDS"), jsontxtarr.getJSONArray("COLUMNS").length()));


		sbtestResult = sbtest.build().toByteArray();
	
		long startTime = System.nanoTime();
		if(compare.equalsIgnoreCase("jsonobj")){			
			JSONObject outputJson = readJsonFromUrl("http://localhost:45670/path5");
		//	System.out.println(outputJson);
		}else{			
			byte[] outputByte = readByteFromUrl("http://18.232.16.34:45670/path9");
		//	System.out.println(outputByte.toString());
			//byte[] sbtestResult = sbtest.build().toByteArray();
		
			//System.out.println(sbtest.build().toString());
		/*	SbTestMsgs sbtestParse1 = SbTestMsgs.parseFrom(sbtestResult);
			sbtestClass.ReadSbtest(sbtestParse1);*/
		//	SbTestMsgs sbtestParse = SbTestMsgs.parseFrom(outputByte);
		//	sbtestClass.ReadSbtest(sbtestParse);
			
		}
		System.out.println(((System.nanoTime() - startTime) / 1000000)+" ms");
		

		
		}catch(Exception e){
			throw e;	
			
		}
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException, ParseException {
		// InputStream is = new URL(url).openStream();
		URL object = new URL(url);
		HttpURLConnection contxt = (HttpURLConnection) object.openConnection();
		try {
			
			contxt.setDoOutput(true);
			contxt.setDoInput(true);
			contxt.setRequestProperty("Content-Type", "application/json");
			contxt.setRequestProperty("Accept", "application/json");
			contxt.setRequestMethod("POST");
			OutputStream os = contxt.getOutputStream();
			os.write(jsontxtarr.toString().getBytes());
			os.flush();
	
			BufferedReader rd = new BufferedReader(new InputStreamReader(contxt.getInputStream()));

			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			contxt.disconnect();
		}
	}
	
	
	public static byte[] readByteFromUrl(String url) throws IOException{
		
		URL object = new URL(url);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		HttpURLConnection contxt = (HttpURLConnection) object.openConnection();
		String jsonText="";
		try {
		
			contxt.setDoOutput(true);
			contxt.setDoInput(true);
			contxt.setRequestProperty("Content-Type", "text/plain");
			contxt.setRequestProperty("Accept", "text/plain");
			contxt.setRequestMethod("POST");
			//String str = new String(sbtestResult,"UTF-8");
			String s =Base64.getEncoder().encodeToString(sbtestResult);
			//System.out.println(s.getBytes().length);
			OutputStream os = contxt.getOutputStream();
			os.write(s.getBytes()); //str.getBytes(StandardCharsets.UTF_8
			os.flush();
			
			
			BufferedReader rd = new BufferedReader(new InputStreamReader(contxt.getInputStream()));

			jsonText = readAll(rd);
			//System.out.println(jsonText);
	/*		InputStream is = contxt.getInputStream();
			int reads = is.read();
			while (reads != -1) {
				baos.write(reads);
				reads = is.read();
			}*/
			
		} finally {
			contxt.disconnect();
		}
		return  jsonText.getBytes();
	}
	
	
	
	
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}
	


}
