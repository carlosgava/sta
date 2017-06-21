package br.com.santaconstacia.sta.database.bean;

import java.util.Collection;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Comunicacao {

	@DatabaseField(generatedId = true)
	private Integer id;

	@DatabaseField(unique=true)
	private String nome;

	@ForeignCollectionField(foreignFieldName="comunicacao")
	public Collection<Arquivo> arquivos;

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

	public Collection<Arquivo> getArquivos() {
		return arquivos;
	}

	public void setArquivo(Collection<Arquivo> arquivos) {
		this.arquivos = arquivos;
	}
	
}
