package br.com.santaconstacia.sta.utils;

import java.io.InputStream;

public class StringUtils {
	public final static int BUFFER_SIZE = 1024;

	public static boolean isEmpty(String string) {
		return string == null || string.length() == 0
				|| string.trim().length() == 0;
	}

	public static String toString(InputStream is) {
		return new String(IOUtils.read(is, BUFFER_SIZE));
	}

	public static String preecherEsquerda(String string, int tamanho,
			String caracter) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < tamanho - string.length(); i++) {
			sb.append(caracter);
		}

		sb.append(string);
		return sb.toString();
	}

	public static String toString(String value) {
		String result = null;
		if (value != null && !value.trim().equals("")) {
			result = value;
		}
		return result;
	}
}
