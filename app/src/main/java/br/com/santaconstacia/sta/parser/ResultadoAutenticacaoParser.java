package br.com.santaconstacia.sta.parser;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.util.Log;
import br.com.santaconstacia.sta.database.bean.Arquivo;
import br.com.santaconstacia.sta.database.bean.Comunicacao;
import br.com.santaconstacia.sta.database.bean.Configuracao;
import br.com.santaconstacia.sta.database.bean.Mascara;
import br.com.santaconstacia.sta.database.dao.ArquivoDAO;
import br.com.santaconstacia.sta.database.dao.ComunicacaoDAO;
import br.com.santaconstacia.sta.database.dao.DAOFactory;
import br.com.santaconstacia.sta.database.dao.MascaraDAO;
import br.com.santaconstacia.sta.loaders.Comm;
import br.com.santaconstacia.sta.loaders.Downloadable;
import br.com.santaconstacia.sta.utils.AndroidUtils;
import br.com.santaconstacia.sta.utils.JSONUtils;

import com.google.gson.stream.JsonReader;

public class ResultadoAutenticacaoParser {

	private static final String TAG = ResultadoAutenticacaoParser.class.getSimpleName();
	
	private Context mContext = null;
	
	private static final String COMM = "comm";
	private static final String MASKS = "masks";
	private static final String TEMPLATE_EMAIL = "template-email";
	
	
	public ResultadoAutenticacaoParser( Context context ) {
		mContext = context;
	}

	public void deletarArquivo( Arquivo arquivo ) {
		try {
			DAOFactory.instance( mContext ).arquivoDAO().delete( arquivo );
			File file = AndroidUtils.getDirectoryByUrl(mContext, arquivo.getUrl());
			file = new File( file, arquivo.getNome() );
			file.delete();
		} catch (SQLException e) {
			Log.w("FALHA: Nao foi possivel deletar o arquivo do banco, nome => ", arquivo.getNome() );
		}
	}
	public void deletarArquivos( Collection<Arquivo> arquivos ) {
		for (Arquivo arquivo : arquivos) {
			deletarArquivo(arquivo);
		}
	}
	
	public void saveComm( Comm comm ) {
		System.out.println("Salvando comm => " + comm.getName() );
		
		Comunicacao comunicacao = null;

		Collection<Arquivo> arquivos = new ArrayList<Arquivo>();
		
		try {

			ArquivoDAO arquivoDAO = DAOFactory.instance( mContext ).arquivoDAO();
			ComunicacaoDAO daoComunicacao = DAOFactory.instance( mContext ).comunicacaoDAO();

			List<Comunicacao> list = daoComunicacao.queryForEq("nome", comm.getName() );
			if ( list != null && list.size() > 0 ) {
				comunicacao = list.get(0);
				deletarArquivos( comunicacao.getArquivos() );	
			}

			if ( comunicacao == null ) {
				try {
					comunicacao = new Comunicacao();
					comunicacao.setNome( comm.getName() );
					DAOFactory.instance( mContext ).comunicacaoDAO().create( comunicacao );
				} catch (SQLException e) {
					Log.e(TAG, "FALHA: Nao foi possivel salvar comunicacao " + comm.getName() );
					deletarArquivos( arquivos );
				}
			}
			
			for (Downloadable file : comm.files ) {
				Arquivo arquivo = new Arquivo();
				arquivo.setNome( file.fileName );
				arquivo.setFlagPendenteProc( true );
				arquivo.setType( file.type );
				arquivo.setUrl( file.downloadURL );
				arquivo.setTipoTransf( Arquivo.TIPO_TRANSFERENCIA.DOWNLOAD );
				arquivo.setComunicao(comunicacao);
				arquivo.setTipoArquivo( Arquivo.TIPO_ARQUIVO.COMUNICACAO );
				arquivoDAO.create( arquivo );
				
				arquivos.add( arquivo );
			}
			
		} catch (SQLException e1) {
			Log.e(TAG, "FALHA: Nao foi possivel salvar os arquivos de comunicacao " + comm.getName() );
			return;
		}

	}
	
	public void parserComunicacoes( JsonReader jReader ) throws IOException {
		
		jReader.beginArray();
		
		while( jReader.hasNext() ) {
			Comm comm = JSONUtils.newGson().fromJson(jReader, Comm.class);
			if ( comm != null ) {
				saveComm( comm );
			}
		}
		
		jReader.endArray();
	}

	public void saveMask( Downloadable mask ) {
		System.out.println("Salvando a mascara " + mask.fileName );
		
		Mascara mascara = null;
		
		try {
			
			MascaraDAO daoMascara = DAOFactory.instance( mContext ).mascaraDAO();
			ArquivoDAO daoArquivo = DAOFactory.instance( mContext ).arquivoDAO();
			
			List<Mascara> list = daoMascara.queryForEq("nome", mask.fileName );
			if ( list != null && list.size() > 0 ) {
				mascara = list.get(0);
				if ( mascara.getArquivo() != null ) {
					deletarArquivo( mascara.getArquivo() );
				}
			}
			
			Arquivo arquivo = new Arquivo();
			arquivo.setNome( mask.fileName );
			arquivo.setType( mask.type );
			arquivo.setUrl( mask.downloadURL );
			arquivo.setTipoTransf( Arquivo.TIPO_TRANSFERENCIA.DOWNLOAD );
			arquivo.setTipoArquivo( Arquivo.TIPO_ARQUIVO.MASCARA );
			DAOFactory.instance( mContext ).arquivoDAO().create( arquivo );

			if ( mascara == null ) {
				mascara = new Mascara();
			}
			
			mascara.setArquivo( arquivo );
			mascara.setNome( mask.fileName );
			mascara.setArquivo( arquivo );
			daoMascara.createOrUpdate( mascara );
			
			arquivo.setMascara( mascara );
			daoArquivo.update( arquivo );
			
		} catch (SQLException e) {
			Log.e(TAG, "FALHA: Nao foi possivel salvar a mascara " + mask.fileName );
		}
	}
	
	public void parserMascaras( JsonReader jReader ) throws IOException {
		jReader.beginArray();
		
		while( jReader.hasNext() ) {
			Downloadable mask = JSONUtils.newGson().fromJson(jReader, Downloadable.class);
			if ( mask != null ) {
				saveMask( mask );
			}
		}
		
		jReader.endArray();
	}
	
	public void parserTemplateEmail( JsonReader reader ) throws IOException {
		String value = reader.nextString();
		try {
			DAOFactory.instance( mContext ).configuracaoDAO().salvaTemplateEmail( value );
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void parser( String text ) {
		JsonReader jReader = new JsonReader(new StringReader( text) );
		
		try {
			
			jReader.beginObject();
			
			while( jReader.hasNext() ) {
				
				String tagName = jReader.nextName();
				
				if ( COMM.equals(tagName) ) {
					parserComunicacoes(jReader);
				} else if ( MASKS.equals(tagName) ) {
					parserMascaras( jReader );
				} else if ( TEMPLATE_EMAIL.equals(tagName) ) {
					parserTemplateEmail( jReader );
				} else {
					jReader.skipValue();
				}
			}
			
			jReader.endObject();
			
		} catch (IOException e) {
			Log.e(TAG, "FATAL: " + e.getMessage() );
		}
	}
	
}
