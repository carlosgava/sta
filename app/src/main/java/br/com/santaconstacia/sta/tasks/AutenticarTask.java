package br.com.santaconstacia.sta.tasks;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.conn.ConnectTimeoutException;

import android.os.AsyncTask;
import br.com.santaconstacia.sta.Constantes;
import br.com.santaconstacia.sta.tasks.interfaces.ResultadoAutenticacaoCallback;
import br.com.santaconstacia.sta.utils.HttpUtils;
import br.com.santaconstacia.sta.utils.StringUtils;

public class AutenticarTask extends AsyncTask<Void, Long, String> {

	String mUsuario = null;
	String mSenha = null;
	ResultadoAutenticacaoCallback mCallBackResultado = null;
	
	public AutenticarTask( String usuario, String senha, ResultadoAutenticacaoCallback callBackResultado ) {
		mUsuario = usuario;
		mSenha = senha;
		mCallBackResultado = callBackResultado;
	}
	
	@Override
	protected String doInBackground(Void... params) {
		String resultado = Constantes.ERRO;
		
		InputStream inputStream = null;
		
		try {
			inputStream = HttpUtils.get("/ServerEstampas2/authenticate", true, "user", mUsuario, "pass", mSenha, "udid", Constantes.IMEI_TESTE);
			resultado = StringUtils.toString(inputStream);
		} catch (ConnectTimeoutException e) {
			resultado += " - " + Constantes.CONEXAO;
		} catch (SecurityException e) {
			resultado += " - " + Constantes.AUTENTICACAO;
		} catch (IOException e) {
			resultado += " - " + Constantes.CONEXAO;
		}
		
		return resultado;
	}
	
	@Override
	protected void onPostExecute(String result) {
		mCallBackResultado.onResultadoAutenticacao( result );
	}

}
