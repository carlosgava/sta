package br.com.santaconstacia.sta.database.bean;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Jo??o Paulo Ferreira
 *
 */
@DatabaseTable
public class Configuracao {

	@DatabaseField(id=true)
	public Long id;
	
	@DatabaseField
	public String templateEmail;
	
	@DatabaseField
	public Date ultimaAtualizacao;
	
	@DatabaseField(foreign = true, canBeNull = true, foreignAutoRefresh = true)
	public Favorito favorito;
}
