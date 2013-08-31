package com.stronglinksolutions.httphelper;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class HTTPHelper {

	private int _statusCode = -99;
	private long _responseLength = -99;
	private String _responseBody = null;
	private HashMap<String, String> _responseHeaders = null;
	
	private static final String ISO_8859_1 = "ISO-8859-1";
	private static final String CONTENT_ENCODING_HEADER_KEY = "content-encoding";
//	private static final String CONTENT_TYPE_HEADER_KEY = "content-type";
	private static final String GZIP_CONTENT_ENCODING_VALUE = "gzip";

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
	
	public void makeRequest(String targetURL, String httpMethod, HashMap<String, String> requestHeaders, String requestBody) throws MalformedURLException, ProtocolException, IOException{
		makeRequest(targetURL, httpMethod, requestHeaders, requestBody, false);
	}
	
	@SuppressWarnings("rawtypes")
	public void makeRequest(String targetURL, String httpMethod, HashMap<String, String> requestHeaders, String requestBody, boolean useGZipCompression) throws MalformedURLException, ProtocolException, IOException{
		try{
			int contentLength = 0;
			boolean requestBodyIsNull = (requestBody == null);
			URL url = new URL(targetURL);
			//Build Connection Object
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			//Set HTTP Method of Request
			connection.setRequestMethod(httpMethod);
			if(useGZipCompression && !requestBodyIsNull && requestBody.length()> 0){
				if(requestHeaders == null){
					requestHeaders= new HashMap<String, String>();
				}
				if(useGZipCompression){
					requestBody = gZipCompressRequestBody(requestBody);
					if(!requestHeaders.containsKey(CONTENT_ENCODING_HEADER_KEY)){
						requestHeaders.put(CONTENT_ENCODING_HEADER_KEY, GZIP_CONTENT_ENCODING_VALUE);
					}					
				}
			}
			
			contentLength = (requestBodyIsNull) ? 0 : requestBody.length();
			
			//Set HTTP Headers
			if(requestHeaders != null){
				Set headers = requestHeaders.entrySet();
				for(Iterator i = headers.iterator(); i.hasNext();){
					Map.Entry map = (Map.Entry)i.next();
					if(map.getValue() != null){
						String key = map.getKey().toString();
						String value = map.getValue().toString();
						connection.setRequestProperty(key, value);	
					}
				}
			}
			boolean doOutput = (!requestBodyIsNull) ? true : false;
			//Set HTTP Request Body
			connection.setRequestProperty("Content-Length", String.format("%d", contentLength));//" + Integer.toString(requestBody.getBytes().length));
			connection.setDoOutput(doOutput);
			if(!requestBodyIsNull){
			    DataOutputStream wr = new DataOutputStream (connection.getOutputStream ());
				wr.writeBytes (requestBody);
				wr.flush ();
				wr.close ();
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
					_responseHeaders.put(key.toLowerCase(), value);				
				}
			}	
			
			_statusCode = connection.getResponseCode();
		    
		    if(_responseHeaders.containsKey(CONTENT_ENCODING_HEADER_KEY)){
		    	String encoding = _responseHeaders.get(CONTENT_ENCODING_HEADER_KEY).replace("[", "").replace("]", "");
		    	if(encoding.equals(GZIP_CONTENT_ENCODING_VALUE)){
		    		_responseBody = gZipDecompressRequestBody(connection.getInputStream());
		    		_responseLength = _responseBody.length();	
		    	}
		    }else{
				//Read Response Body
				InputStream is = connection.getInputStream();		
				_responseLength = connection.getContentLength();		
			    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			    String line;
			    StringBuffer response = new StringBuffer(); 
			    while((line = rd.readLine()) != null) {
			    	response.append(line);
			    }
			    is.close();
			    rd.close();
		    	_responseBody = response.toString();
		    }
		    
		    connection.disconnect();
		}catch(UnknownHostException e){
			//DNS Lookup Failed!
			_statusCode = 502;
		}
	}
	
	private String gZipCompressRequestBody(String requestBody) throws IOException{
		String gZippedBody = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzipout = new GZIPOutputStream(out);
		gzipout.write(requestBody.getBytes());
		gzipout.close();
		gZippedBody = out.toString(ISO_8859_1);
		return gZippedBody;
	}
	
	private String gZipDecompressRequestBody(InputStream response) throws IOException{
		StringBuilder decompressedBody = new StringBuilder(); 
		InputStream gzipin = new GZIPInputStream(response);
		BufferedReader in = new BufferedReader(new InputStreamReader(gzipin));
		String read = null;
		while((read = in.readLine()) != null){
			decompressedBody.append(read);
		}
		in.close();
		gzipin.close();
		return decompressedBody.toString();
	}
}
