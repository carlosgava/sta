package br.com.santaconstacia.sta;

public class Constantes {
	

	public static enum Mode{
		PRODUCAO, DESENVOLVIMENTO; 
	}
	
	public static final Mode MODE = Mode.DESENVOLVIMENTO;
	//public static final Mode MODE = Mode.PRODUCAO;
	
	static{
		if (MODE == Mode.DESENVOLVIMENTO) {
			//SERVER = "scweb-4.santaconstancia.com.br";
			SERVER = "estampaswebteste.santaconstancia.com.br";
			PORT = 4862;
		} else {
			SERVER = "scweb-4.santaconstancia.com.br";
		    PORT = 4863;
		}
	}
	
	public static int PORT = 4862;
	public static final String PROTOCOL = "http";
	public static final String SERVER;
	public static final int SERVER_TIMEOUT = 2 * 60 * 1000; // 2 minutos
	public static final int SHORT_TIMEOUT = 6 * 1000; // 6 segundos
	public static final String SERVER_BASE_URL_WITHOUT_PORT = PROTOCOL + "://" + SERVER;
    public static final String SERVER_BASE_URL = PROTOCOL + "://" + SERVER + ":" + PORT;
    public static final int TOAST_DURATION = 4;
    public static final long MAX_USER_INACTIVITY = 300000;
    public static final String REPOSITORIRIO_ARQUIVOS = "sta/arquivos";
    public static final String REPOSITORIRIO_ARQUIVOS_IMAGENS = "/imagens"; 
    public static final String REPOSITORIRIO_ARQUIVOS_COMUNICACAO = "/comunicacao"; 

    public static final String USUARIO_TESTE = "SYSPLANTA";
    public static final String SENHA_TESTE = "XET01DDW001SP";
    public static final String IMEI_TESTE = "11121CCC-F2B9-43FD-9EC0-802827C385EB";
  
    public static final String ERRO = "erro";
    public static final String CONEXAO = "conexao";
    public static final String AUTENTICACAO = "autenticacao";
    public static final String OK = "ok";
    public static final String CANCEL = "cancel";
    public static final String ERRO_ARQUIVO_NAO_ENCONTRADO = "ARQUIVO_NAO_ENCONTRADO";
    public static final String ERRO_ARQUIVO_INVALIDO = "ARQUIVO_INVALIDO";
    public static final String ERRO_SQL = "SQL";
    public static final String ERRO_CONEXAO_TIMEOUT = "TIMEOUT_CONEXAO";
    
    public static final int TIPO_MASCARA = 0;
    public static final int TIPO_APRESENTACAO = 1;
    public static final int TIPO_ARTIGO = 2;
    public static final int TIPO_COMUNICACAO = 3;
    public static final int TIPO_INDEFINIDO = -1;
    
}