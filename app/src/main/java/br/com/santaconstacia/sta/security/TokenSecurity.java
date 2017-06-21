package br.com.santaconstacia.sta.security;

public class TokenSecurity {

	private int mPeriodo = 4 * 60 * 60 * 1000; // 4 Hroas
	private String mUsuario = "";
	private String mSenha = "";
	
	private static TokenSecurity mInstance = null;

	private TokenSecurity() {
		
	}
	
	public static synchronized TokenSecurity instance() {
		if ( mInstance == null ) {
			mInstance = new TokenSecurity();
		}
		return mInstance;
	}
	
	public void generateToken( String user, String password ) {
		mUsuario = user;
		mSenha = password;
	}

	public String getUsuario() {
		return mUsuario;
	}

	public String getSenha() {
		return mSenha;
	}
}
