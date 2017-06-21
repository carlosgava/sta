package br.com.santaconstacia.sta.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class UriBuilder {

	private String scheme = "http";
	private String host = "localhost";
	private String port = "80";
	private String path = "";
	private List<NameValuePair> parameters;
	
	public UriBuilder() {
		parameters = new ArrayList<NameValuePair>();
	}
	
	public String build() {
		StringBuilder sb = new StringBuilder();
		sb.append(scheme);
		sb.append("://");
		sb.append( host );
		sb.append(":");
		sb.append( port );
		sb.append("/");
		sb.append( path );
		
		for( int i=0; i < parameters.size(); i++ ) {
			if ( i == 0 )
				sb.append( "?" );
			else
				sb.append( ";" );
			
			sb.append( parameters.get(i).toString() );
		}
		
		return sb.toString();
	}
	
	public UriBuilder setScheme( String scheme ) {
		this.scheme = scheme;
		return this;
	}
	
	public UriBuilder setHost( String host ) {
		this.host = host;
		return this;
	}
	
	public UriBuilder setPort( int port ) {
		this.port = String.valueOf(port);
		return this;
	}
	
	public UriBuilder setPath( String path ) {
		this.path = path;
		return this;
	}
	
	public UriBuilder addParamter( String param, String value) {
		parameters.add( new BasicNameValuePair(param, value) );
		return this;
	}
	
}
