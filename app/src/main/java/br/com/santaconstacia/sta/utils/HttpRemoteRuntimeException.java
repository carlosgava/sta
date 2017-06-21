package br.com.santaconstacia.sta.utils;

public class HttpRemoteRuntimeException extends Exception{

	private static final long serialVersionUID = 7069397968595941236L;

	public HttpRemoteRuntimeException() {
		super();
	}

	public HttpRemoteRuntimeException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public HttpRemoteRuntimeException(String detailMessage) {
		super(detailMessage);
	}

	public HttpRemoteRuntimeException(Throwable throwable) {
		super(throwable);
	}	
}