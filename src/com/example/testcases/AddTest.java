package com.example.testcases;

import com.example.test.testclass.FIELDS;
import com.example.test.testclass.Msg;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintStream;

class AddTest {

  static FIELDS PromptForAddress(BufferedReader stdin,
                                 PrintStream stdout) throws IOException {
    FIELDS.Builder fields = FIELDS.newBuilder();


    while (true) {
      stdout.print("Enter a phone number (or leave blank to finish): ");
      String number = stdin.readLine();
      if (number.length() == 0) {
        break;
      }
	//fields.setRow(number);



   //   fields.addField(fieldRow);
    }

    return fields.build();
  }
  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.err.println("Usage:  AddTest Testing Rows");
      System.exit(-1);
    }

    Msg.Builder msgCheck = Msg.newBuilder();

    // Read the existing address book.
    try {
      FileInputStream input = new FileInputStream(args[0]);
      try {
    	  msgCheck.mergeFrom(input);
      } finally {
        try { input.close(); } catch (Throwable ignore) {}
      }
    } catch (FileNotFoundException e) {
      System.out.println(args[0] + ": File not found.  Creating a new file.");
    }

    // Add an address.
    msgCheck.addField(
      PromptForAddress(new BufferedReader(new InputStreamReader(System.in)),
                       System.out));

    // Write the new address book back to disk.
    FileOutputStream output = new FileOutputStream(args[0]);
    try {
    	msgCheck.build().writeTo(output);
    } finally {
      output.close();
    }
  }
}
