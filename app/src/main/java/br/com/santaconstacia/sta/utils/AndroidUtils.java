package br.com.santaconstacia.sta.utils;


import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import br.com.santaconstacia.sta.Constantes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class AndroidUtils {

	private static final String TAG = AndroidUtils.class.getSimpleName();
	
	private static final int TOAST_DURATION = 3000;
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
	public static final ThreadPoolExecutor 
	pool = new ThreadPoolExecutor(2, // core size
		    20, // max size
		    60, // idle timeout
		    TimeUnit.SECONDS,
		    new ArrayBlockingQueue<Runnable>(200)); // queue with a size

	public static void makeToast(Context context, int messageId) {
		Toast.makeText(context, messageId, TOAST_DURATION).show();
	}

	public static String getImei(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId().trim();
	}
	
	public static File getRepositorioArquivosImagens(Context context) {
		File directory = null;
		
		// LCL => Ira usar apenas o sdcard quando ele estiver montado
		if ( Environment.MEDIA_MOUNTED.equals( Environment.getExternalStorageState() ) ) {
			File sdcard = Environment.getExternalStorageDirectory();
			directory = new File( sdcard.getAbsolutePath() + "/" + Constantes.REPOSITORIRIO_ARQUIVOS_IMAGENS );
		} else {
			directory = context.getDir(Constantes.REPOSITORIRIO_ARQUIVOS_IMAGENS, Context.MODE_PRIVATE);
		}
		
		if ( directory != null && !directory.exists() ) {
			directory.mkdirs();
		}
		
		return directory;
	}
	
	public static File getRepositorioArquivosImagens( File rootDirectory ) {
		File directory = new File( rootDirectory, Constantes.REPOSITORIRIO_ARQUIVOS_IMAGENS);
		return directory;
	}
	
	public static File getRepositorioArquivosByType( Context context, int type ) {
		File directory = null;
		
		if ( type == Constantes.TIPO_COMUNICACAO ) {
			directory = new File( AndroidUtils.getRepositorioArquivos( context ), Constantes.REPOSITORIRIO_ARQUIVOS_COMUNICACAO );
		}
		else {
			directory = new File( AndroidUtils.getRepositorioArquivos( context ), Constantes.REPOSITORIRIO_ARQUIVOS_IMAGENS );
		}
		
		if ( directory != null && !directory.exists() ) {
			directory.mkdirs();
		}
		
		return directory;
	}
	
	public static File getDirectoryByUrl(Context context, String url) {
		return getDirectoryByUrl( getRepositorioArquivos(context), url );
		
	}
	
	public static File getDirectoryByUrl(File repositorioArquivos, String url) {
		String path = repositorioArquivos.getAbsolutePath() + "/" + url.replace('\\', '/') ;
		path = path.substring(0, path.lastIndexOf('/')+1);
		File directory = new File(path);
		if ( directory != null && !directory.exists() )
			directory.mkdirs();
		return directory;
	}
	
	public static File getRepositorioArquivos(Context context) {
		File directory = null;
		
		// LCL => Ira usar apenas o sdcard quando ele estiver montado
		if ( Environment.MEDIA_MOUNTED.equals( Environment.getExternalStorageState() ) ) {
			File sdcard = Environment.getExternalStorageDirectory();
			directory = new File( sdcard.getAbsolutePath() + "/" + Constantes.REPOSITORIRIO_ARQUIVOS );
		} else {
			directory = context.getDir(Constantes.REPOSITORIRIO_ARQUIVOS, Context.MODE_PRIVATE);
		}
		
		if ( directory != null && !directory.exists() ) {
			directory.mkdirs();
		}
		
		return directory;
	}
	
	public static boolean removeArquivoLocal( Context context, String url ) {
		File file = AndroidUtils.getDirectoryByUrl( context, url );
		file = new File( file, url );
		file.delete();
		return true;
	}
	
	public static String getDatabasePathFile( String databaseName ) {
		String path = "";
		
		if ( Environment.MEDIA_MOUNTED.equals( Environment.getExternalStorageState() ) ) {
			File sdcard = Environment.getExternalStorageDirectory();
			path = sdcard.getAbsolutePath() + "/" + Constantes.REPOSITORIRIO_ARQUIVOS + "/database/";
			File dir = new File(path);
			if ( !dir.exists() ) {
				dir.mkdirs();
			}
			path = path.concat( databaseName );
		}
		else {
			path = databaseName;
		}
		
		return path;
	}
	
	
	public static void mudaEstado(View view, boolean enabled, int ... ids){
		for (int id : ids) {
			View componente = view.findViewById(id);
			componente.setEnabled(enabled);
			componente.setFocusable(enabled);
		}
	}
	
	public static void mudaEstado(boolean enabled, View ... views){
		for (View view: views) {
			view.setEnabled(enabled);
			view.setFocusable(enabled);
		}
	}
	

	public static Bitmap toBitmap(Context context, String path, int height){
		final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;//n??o carregar a imagem, s?? carregar os dados da imagem
	    BitmapFactory.decodeFile(path, options);
        int width = (height * options.outWidth)/options.outHeight;//largura proporcinal a altura
	    options.inSampleSize = calculateInSampleSize(options, width, height); //verifica a redu????o necess??ria para o tamanho requisitado
	    options.inJustDecodeBounds = false;//carregar a imagem
		Bitmap image = BitmapFactory.decodeFile(path, options);
		
		return image;
	}

	public static PointF scaleToFit(float wTarget, float hTarget, float w, float h){
		PointF result = new PointF();
		
		if(w/h > wTarget/hTarget){
			result.x = wTarget;
			result.y = h * wTarget/w;
		}else{
			result.y = hTarget;
			result.x = w * hTarget/h;
		}
		
		return result;
	}
	
	public static BitmapDrawable toDrawable(Context context, String path, int height){
		Bitmap image = toBitmap(context, path, height);
        return new BitmapDrawable(context.getResources(), image);
	}
	
	private static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}
	
	public static boolean isConnected(Context context){
		ConnectivityManager cm =
		        (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		 
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null &&
		                      activeNetwork.isConnectedOrConnecting();
		
		return isConnected;
	}
	
	public static Date toDate(String texto) {
		Date date = null;
		if ( texto != null ) {
			SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy");
			try {
				sdf.parse( texto );
			} catch (ParseException e) {
				Log.d(TAG, "FALHA: nao foi possivel converter o texto para Date => " + texto);
			}
		}
		return date;
	}
	
	public static boolean validateEmail( String email ) {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}
	
	public static boolean validateEmails( String texto ) {
		if ( texto.trim().isEmpty() )
			return false;
		
		String[] emails = texto.split(",");
		for (String email : emails) {
			if ( !validateEmail(email) )
				return false;
		}
		
		return true;
	}
}
