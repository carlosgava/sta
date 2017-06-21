package br.com.santaconstacia.sta.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;
import br.com.santaconstacia.sta.Constantes;

import com.google.gson.JsonSyntaxException;

public class HttpUtils {
    
	private static final String LOG_CATEGORY = "HTTPUTILS";

	public static DefaultHttpClient buildHttpClient(boolean shortTimeOut) {
    	int timeOut = Constantes.SERVER_TIMEOUT;
		
		if(shortTimeOut)
			timeOut = Constantes.SHORT_TIMEOUT;
		
    	DefaultHttpClient client = new DefaultHttpClient();
        HttpParams params = client.getParams();
        HttpConnectionParams.setConnectionTimeout(params, timeOut);
        HttpConnectionParams.setSoTimeout(params, timeOut);
        ConnManagerParams.setTimeout(params, timeOut);
        
        return client;
    }

    /**
     * Executes the network requests on a separate thread.
     * 
     * @param runnable The runnable instance containing network mOperations to
     *        be executed.
     */
    public static Thread performOnBackground(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                runnable.run();
            }
        };
        t.start();
        return t;
    }
    
    public static void flush(HttpResponse response) throws IllegalStateException, IOException{
    	flush(response.getEntity().getContent());    	
    }
    
    public static void flush(InputStream response) throws IOException{
    	Log.v(LOG_CATEGORY, "Flushing response");
    	response.close();
    }
    
	public static <T> T get(Class<T> clazz, String url, boolean shortTimeOut, Object ... parameters) throws AuthenticationException, ConnectTimeoutException, IOException{
		InputStream input = get(url, shortTimeOut, parameters);
		
		String payLoad = StringUtils.toString(input);
		
	 	T result;
		try {
			result = JSONUtils.newGson().fromJson(payLoad, clazz);
		} catch (JsonSyntaxException e) {
			Log.e(LOG_CATEGORY, "Erro ao tentar decodificar a resposta: " + payLoad, e);
			throw e;
		}
	 	
		return result;
	}
	
	private static String buildURL(String url, Object ... parameters){
		StringBuilder fullURL = new StringBuilder(Constantes.SERVER_BASE_URL);
		fullURL.append(url);
		buildQueryString(fullURL, parameters);
		System.out.println("buildURL > " + fullURL.toString());
		return fullURL.toString();
	}
	
	private static void buildQueryString(StringBuilder url, Object ... parameters){
		if(parameters != null && parameters.length > 0){
			String connector = "?";
			
			for(int i=0; i < parameters.length; i++)
				if(i + 1 < parameters.length && parameters[i+1] != null){
					url.append(connector);
					url.append(parameters[i].toString());
					url.append('=');
					url.append(URLEncoder.encode(parameters[++i].toString()));
					connector="&";
				}
				else i++;
		}
	}
	
	public static String buildQueryString(Object ... parameters){
		StringBuilder fullURL = new StringBuilder();
		buildQueryString(fullURL, parameters);
		return fullURL.toString();
	}
    
	public static InputStream get(String url, boolean shortTimeOut, Object ... parameters) throws SecurityException, ConnectTimeoutException, IOException{
		DefaultHttpClient httpClient =  HttpUtils.buildHttpClient(shortTimeOut);
		HttpGet httpget = new HttpGet(buildURL(url, parameters));
		InputStream inputStream = null;
		
		try {
		 	HttpResponse response = httpClient.execute(httpget);
		 	int statusCode = response.getStatusLine().getStatusCode(); 
		 	if(statusCode == HttpStatus.SC_UNAUTHORIZED || statusCode == HttpStatus.SC_FORBIDDEN)
		 		throw new SecurityException(response.getStatusLine().getReasonPhrase());
		 	
		 	if(statusCode != HttpStatus.SC_OK){
		 		String errorMessage = "Erro na requisi????o ao servidor " + 
		 			httpget.getRequestLine() + ": " + response.getStatusLine();
				Log.e(LOG_CATEGORY, errorMessage);
				throw new IOException(errorMessage);
		 	}
		 	
		 	inputStream =  response.getEntity().getContent();
		} catch (ClientProtocolException e) {
			throw new RuntimeException(e); 
		}
		
		return inputStream;
	}
	
    private static HttpPost newJSONPost(String url){
    	HttpPost post = new HttpPost(url);

		post.setHeader("Content-Type", "application/json");
		post.setHeader("Accept", "application/json");
		
		return post;
    }
    
	public static Object post(Type clazz, String url, boolean shortTimeOut, Object body, Object ... headers) 
							throws AuthenticationException, ConnectTimeoutException, IOException, HttpRemoteRuntimeException{
		String bodyString = JSONUtils.newGson().toJson(body);
		return post(clazz, url, shortTimeOut, bodyString, headers);
	}
    
	public static Object post(Type clazz, String url, boolean shortTimeOut, String body, Object ... headers) 
							throws AuthenticationException, ConnectTimeoutException, IOException, HttpRemoteRuntimeException{
		
		String payLoad = post(url, shortTimeOut, body, headers);
		
	 	Object result;
		try {
			result = JSONUtils.newGson().fromJson(payLoad, clazz);
		} catch (JsonSyntaxException e) {
			Log.e(LOG_CATEGORY, "Erro ao tentar decodificar a resposta: " + payLoad, e);
			throw e;
		}
	 	
		return result;
	}
    
	public static String post(String url, boolean shortTimeOut, String body, Object ... headers) 
							throws AuthenticationException, ConnectTimeoutException, IOException, HttpRemoteRuntimeException{
		InputStream input = null;
		
		DefaultHttpClient httpClient =  HttpUtils.buildHttpClient(shortTimeOut);
		StringBuilder fullURL = new StringBuilder(Constantes.SERVER_BASE_URL);
		fullURL.append(url);
		
		HttpPost httppost = newJSONPost(fullURL.toString());
		httppost.setEntity(new StringEntity(body));
		addHeaders(httppost, headers);
		
		try {
		 	HttpResponse response = httpClient.execute(httppost);
		 	int statusCode = response.getStatusLine().getStatusCode(); 
		 	if(statusCode == HttpStatus.SC_UNAUTHORIZED || statusCode == HttpStatus.SC_FORBIDDEN)
		 		throw new AuthenticationException(response.getStatusLine().getReasonPhrase());
		 	
		 	if(statusCode != HttpStatus.SC_OK && statusCode != HttpStatus.SC_NO_CONTENT){
		 		String errorMessage = "Erro na requisi????o ao servidor " + 
		 			httppost.getRequestLine() + ": " + response.getStatusLine();
				Log.e(LOG_CATEGORY, errorMessage);
				throw new HttpRemoteRuntimeException(errorMessage);
		 	}
		 	
		 	if(statusCode != HttpStatus.SC_NO_CONTENT)
			 	input = response.getEntity().getContent();
		} catch (ClientProtocolException e) {
			throw new RuntimeException(e); 
		}
		
		if(input == null)
			return null;
		
		return StringUtils.toString(input);
	}
    
	private static void addHeaders(HttpPost post, Object ... headers){
		if(headers != null && headers.length > 0)
			for(int i=0; i < headers.length; i++)
				if(i + 1 < headers.length && headers[i+1] != null)
					post.setHeader(headers[i].toString(), headers[++i].toString());
				else i++;
	}
}