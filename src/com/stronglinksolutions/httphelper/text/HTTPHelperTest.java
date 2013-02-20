package com.stronglinksolutions.httphelper.text;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.HashMap;

import org.junit.Test;

import com.stronglinksolutions.httphelper.HTTPHelper;

public class HTTPHelperTest {

	@Test
	public void testMakeRequest() {
		boolean success = false;
		HTTPHelper con = new HTTPHelper();
		
		try {
			HashMap<String, String> headers = new HashMap<String, String>();
			headers.put("CustomHeader", "TestValue!");
			
			con.makeRequest("http://google.com", "GET", headers, null);
			
			if(con.getStatusCode() == 200){
				success = true;
			}
			
			con.makeRequest("http://madeup.duh", "POST", null, "This is a test!");
			
			if(con.getStatusCode() == 502){
				success = true;
			}
		} catch (MalformedURLException e) {
			success = false;
			e.printStackTrace();
		} catch (ProtocolException e) {
			success = false;
			e.printStackTrace();
		} catch (IOException e) {
			success = false;
			e.printStackTrace();
		} catch(Exception e){
			success = false;
			e.printStackTrace();
		}
		assertTrue(success);
	}

}
