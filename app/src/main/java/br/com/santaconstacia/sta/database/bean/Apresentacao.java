package br.com.santaconstacia.sta.database.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Apresentacao {

	@DatabaseField(generatedId = true)
	private Integer id;
	
	@DatabaseField
	private String name;

	@DatabaseField(canBeNull = false, foreign = true)
	public Arquivo arquivoImagem;
	
	@DatabaseField(canBeNull = false, foreign = true)
	public Colecao colecao;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Arquivo getArquivoImagem() {
		return arquivoImagem;
	}

	public void setArquivoImagem(Arquivo arquivoImagem) {
		this.arquivoImagem = arquivoImagem;
	}

	public Colecao getColecao() {
		return colecao;
	}

	public void setColecao(Colecao colecao) {
		this.colecao = colecao;
	}

}
