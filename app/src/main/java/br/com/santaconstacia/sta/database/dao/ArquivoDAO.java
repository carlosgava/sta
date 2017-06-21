package br.com.santaconstacia.sta.database.dao;

import java.sql.SQLException;

import com.j256.ormlite.support.ConnectionSource;

import br.com.santaconstacia.sta.database.bean.Arquivo;


public class ArquivoDAO extends BaseDAO<Arquivo, Integer>  {

	public ArquivoDAO(ConnectionSource connectionSource ) throws SQLException {
		super(connectionSource, Arquivo.class);
	}

}
