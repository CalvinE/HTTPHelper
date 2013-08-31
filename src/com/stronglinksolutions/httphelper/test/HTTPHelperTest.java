package com.stronglinksolutions.httphelper.test;

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
	
	@Test
	public void testMakeGetRequestWithGZIP() {
		boolean success = false;
		HTTPHelper con = new HTTPHelper();
		
		try {
			HashMap<String, String> headers = new HashMap<String, String>();
			headers.put("accept-encoding", "gzip,deflate,sdch");
			headers.put("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			headers.put("accept-language", "en-US,en;q=0.8");
			headers.put("user-agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.95 Safari/537.36");
			
			con.makeRequest("https://www.google.com/", "GET", headers, null);
			
			if(con.getStatusCode() == 200 && con.getResponseHeaders().containsKey("content-encoding") && con.getResponseHeaders().get("content-encoding").replace("[", "").replace("]", "").equals("gzip")){
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
