package br.com.santaconstacia.sta.database.dao;

import java.sql.SQLException;

import br.com.santaconstacia.sta.database.bean.Imagem;

import com.j256.ormlite.support.ConnectionSource;


public class ImagemDAO extends BaseDAO<Imagem, Integer>  {

	public ImagemDAO(ConnectionSource connectionSource ) throws SQLException {
		super(connectionSource, Imagem.class);
	}

}
