package br.com.santaconstacia.sta.database.dao;

import java.sql.SQLException;
import java.util.Date;

import br.com.santaconstacia.sta.database.bean.Configuracao;
import br.com.santaconstacia.sta.database.bean.Favorito;

import com.j256.ormlite.support.ConnectionSource;

/**
 * @author Luiz Carlos do Lago
 *
 */
public class ConfiguracaoDAO extends BaseDAO<Configuracao, Long>{

	public static Long CONFIG_ID = 1l;
	
	public ConfiguracaoDAO(ConnectionSource connectionSource) throws SQLException {
		super(connectionSource, Configuracao.class);
	}
	
	public void salvaTemplateEmail(String template){
		Configuracao config = findOrCreate();		
		config.templateEmail = template;
		createOrUpdate(config);
	}

	public void salvaTimestampAtualizacao(Date date){
		Configuracao config = findOrCreate();
		config.ultimaAtualizacao = date;
		createOrUpdate(config);
	}
	
	public void salvaFavorito( Favorito favorito ) {
		Configuracao config = findOrCreate();
		config.favorito = favorito;
		createOrUpdate(config);
	}
	
	private Configuracao findOrCreate(){
		Configuracao config = find();
		
		if(config == null){
			config = new Configuracao();
			config.id=CONFIG_ID;
		}
		
		return config;
	}

	public Favorito favorito() {
		Configuracao config = findOrCreate();
		return config.favorito;
	}
	
	public Configuracao find(){
		Configuracao config = queryForId(CONFIG_ID);
		return config;
	}
	
}
