package br.com.santaconstacia.sta.database.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Distribuicao {

	@DatabaseField(generatedId = true)
	private Integer id;
	
	@DatabaseField
	private String name;

	@DatabaseField(canBeNull = false, foreign = true)
	private Arquivo arquivoImagem;

	@DatabaseField(canBeNull = false, foreign = true)
	private Artigo artigo;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Arquivo getArquivoImagem() {
		return arquivoImagem;
	}

	public void setArquivoImagem(Arquivo arquivoImagem) {
		this.arquivoImagem = arquivoImagem;
	}

	public Artigo getArtigo() {
		return artigo;
	}

	public void setArtigo(Artigo artigo) {
		this.artigo = artigo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
