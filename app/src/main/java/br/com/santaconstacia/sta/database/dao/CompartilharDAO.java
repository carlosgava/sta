package br.com.santaconstacia.sta.database.dao;

import java.sql.SQLException;

import br.com.santaconstacia.sta.database.bean.Compartilhar;

import com.j256.ormlite.support.ConnectionSource;

public class CompartilharDAO extends BaseDAO<Compartilhar, Integer> {

	public CompartilharDAO(ConnectionSource connectionSource ) throws SQLException {
		super(connectionSource, Compartilhar.class);
	}

}
