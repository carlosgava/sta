package br.com.santaconstacia.sta.database.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Favorito {
	
	public static final String ID_FIELD_NAME = "id";
	public static final String NOME_FIELD_NAME = "nome";
	
	public Favorito() {
	}
	
	public Favorito( String nome ) {
		this.nome = nome;
	}
	
	@DatabaseField(generatedId = true, columnName = ID_FIELD_NAME)
	private Integer id;

	@DatabaseField(canBeNull = false, unique = true, columnName = NOME_FIELD_NAME)
	private String nome;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
}
