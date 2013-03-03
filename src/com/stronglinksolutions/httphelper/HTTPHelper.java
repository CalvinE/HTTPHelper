package com.stronglinksolutions.httphelper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class HTTPHelper {

	private int _statusCode = -99;
	private long _responseLength = -99;
	private String _responseBody = null;
	private HashMap<String, String> _responseHeaders = null;

	public HTTPHelper(){
		_responseHeaders = new HashMap<String, String>();
	}
	
	public int getStatusCode() {
		return _statusCode;
	}


	public long getResponseLength() {
		return _responseLength;
	}


	public String getResponseBody() {
		return _responseBody;
	}


	public HashMap<String, String> getResponseHeaders() {
		return _responseHeaders;
	}	
	
	@SuppressWarnings("rawtypes")
	public void makeRequest(String targetURL, String httpMethod, HashMap<String, String> requestProperties, String requestBody) throws MalformedURLException, ProtocolException, IOException{
		try{
			URL url = new URL(targetURL);
			//Build Connection Object
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			//Set HTTP Method of Request
			connection.setRequestMethod(httpMethod);
			//Set HTTP Headers
			if(requestProperties != null){
				Set headers = requestProperties.entrySet();
				for(Iterator i = headers.iterator(); i.hasNext();){
					Map.Entry map = (Map.Entry)i.next();
					if(map.getValue() != null){
						String key = map.getKey().toString();
						String value = map.getValue().toString();
						connection.setRequestProperty(key, value);	
					}
				}
			}
			//Set HTTP Request Body
			if(requestBody != null){
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Length", "" + Integer.toString(requestBody.getBytes().length));
			    DataOutputStream wr = new DataOutputStream (connection.getOutputStream ());
				wr.writeBytes (requestBody);
				wr.flush ();
				wr.close ();
			}else{
				connection.setDoOutput(false);
				connection.setRequestProperty("Content-Length", "" + Integer.toString(0));
			}
			
			//Read Response headers
			Map headersFields = connection.getHeaderFields();
			if(headersFields != null && headersFields.size() != 0){
				Set headers = headersFields.entrySet();
				int j = 1;
				for(Iterator i = headers.iterator(); i.hasNext();){
					Map.Entry map = (Map.Entry)i.next();
					Object keyObj = map.getKey();
					String key = null;
					if(keyObj == null){
						key = String.format("No_Header_Key_Provided_%d", j);
						j++;
					}else{
						key = keyObj.toString();
					}
					String value = map.getValue().toString();
					_responseHeaders.put(key, value);				
				}
			}
			
			//Read Response Body
			InputStream is = connection.getInputStream();		
			_statusCode = connection.getResponseCode();
			_responseLength = connection.getContentLength();		
		    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		    String line;
		    StringBuffer response = new StringBuffer(); 
		    while((line = rd.readLine()) != null) {
		    	response.append(line);
		    }
		    is.close();
		    rd.close();
		    connection.disconnect();
		    
		    _responseBody = response.toString();
		}catch(UnknownHostException e){
			//DNS Lookup Failed!
			_statusCode = 502;
		}
	}
}
