package br.com.santaconstacia.sta.database.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Compartilhar {

	@DatabaseField(generatedId = true)
	private Integer id;

	@DatabaseField(canBeNull = false)
	private String usuario;
	
	@DatabaseField(canBeNull = false, foreign = true)
	private Arquivo arquivo;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String nome) {
		this.usuario = nome;
	}

	public Arquivo getArquivo() {
		return arquivo;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}
}
