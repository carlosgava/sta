package br.com.santaconstacia.sta.database.dao;

import java.sql.SQLException;

import br.com.santaconstacia.sta.database.bean.InformacaoTecnica;

import com.j256.ormlite.support.ConnectionSource;


public class InformacaoTecnicaDAO extends BaseDAO<InformacaoTecnica, Integer>  {

	public InformacaoTecnicaDAO(ConnectionSource connectionSource ) throws SQLException {
		super(connectionSource, InformacaoTecnica.class);
	}

}
