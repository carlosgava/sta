package br.com.santaconstacia.sta.database.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Arquivo {

	public static final class TIPO_TRANSFERENCIA {
		public static final int DOWNLOAD = 1;
		public static final int UPLOAD = 2;
	}
	
	public static final class TIPO_ARQUIVO {
		public static final int APRESENTACAO = 0;
		public static final int COMPARTILHAR = 1;
		public static final int COMUNICACAO = 2;
		public static final int DISTRIBUICAO = 3;
		public static final int FAVORITO = 4;
		public static final int INFORMACAO_TECNICA = 5;
		public static final int MASCARA = 6;
		public static final int IMAGEM = 7;
	}
	
	@DatabaseField(generatedId = true)
	private Integer id;
	
	@DatabaseField 
	private String type;
	
	@DatabaseField( canBeNull = false )
	private String nome;
	
	@DatabaseField
	private String url;
	
	@DatabaseField
	private Boolean flagDeletar;
	
	@DatabaseField
	private Integer tipoTransf;
	
	@DatabaseField
	private Boolean flagPendenteProc;
	
	@DatabaseField 
	private Integer tipoArquivo;

	@DatabaseField(foreign = true, canBeNull=true)
	private Comunicacao comunicacao;
	
	@DatabaseField(foreign = true, canBeNull=true)
	private Mascara mascara;
	
	public Arquivo() {
		flagDeletar = Boolean.valueOf(false);
		flagPendenteProc = Boolean.valueOf(true);
	}
	
	public Integer getId() {
		return id;
	}

	public Arquivo setId(Integer id) {
		this.id = id;
		return this;
	}

	public String getType() {
		return type;
	}

	public Arquivo setType(String type) {
		this.type = type;
		return this;
	}

	public String getNome() {
		return nome;
	}

	public Arquivo setNome(String nome) {
		this.nome = nome;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public Arquivo setUrl(String url) {
		this.url = url;
		return this;
	}

	public Boolean getFlagDeletar() {
		return flagDeletar;
	}

	public Arquivo setFlagDeletar(Boolean valor) {
		this.flagDeletar = valor;
		return this;
	}

	public Integer getTipoTransf() {
		return tipoTransf;
	}

	public Arquivo setTipoTransf(Integer tipoTransf) {
		this.tipoTransf = tipoTransf;
		return this;
	} 

	public Boolean getFlagPendenteProc() {
		return this.flagPendenteProc;
	}
	
	public Arquivo setFlagPendenteProc( Boolean valor ) {
		this.flagPendenteProc = valor;
		return this;
	}
	
	@Override
	public boolean equals(Object o) {
		return ( this.id == ((Arquivo)o).id );
	}
	
	public void setComunicao( Comunicacao comunicacao ) {
		this.comunicacao = comunicacao;
	}
	
	public Comunicacao getComunicacao() {
		return this.comunicacao;
	}
	
	public void setMascara( Mascara mascara ) {
		this.mascara = mascara;
	}
	
	public Mascara getMascara() {
		return this.mascara;
	}
	
	public void setTipoArquivo( int tipo ) {
		this.tipoArquivo = tipo;
	}
	
	public Integer getTipoArquivo() {
		return this.tipoArquivo;
	}
}
