package br.com.santaconstacia.sta.database.dao;

import java.sql.SQLException;

import br.com.santaconstacia.sta.database.bean.Favorito;

import com.j256.ormlite.support.ConnectionSource;

public class FavoritoDAO  extends BaseDAO<Favorito, Integer>  {

	public FavoritoDAO(ConnectionSource connectionSource ) throws SQLException {
		super(connectionSource, Favorito.class);
	}

}
