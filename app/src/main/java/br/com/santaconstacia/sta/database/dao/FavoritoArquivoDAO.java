package br.com.santaconstacia.sta.database.dao;

import java.sql.SQLException;

import br.com.santaconstacia.sta.database.bean.FavoritoArquivo;

import com.j256.ormlite.support.ConnectionSource;

public class FavoritoArquivoDAO  extends BaseDAO<FavoritoArquivo, Integer>  {

	public FavoritoArquivoDAO(ConnectionSource connectionSource ) throws SQLException {
		super(connectionSource, FavoritoArquivo.class);
	}

}
