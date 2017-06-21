package br.com.santaconstacia.sta.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import br.com.santaconstacia.sta.R;
import br.com.santaconstacia.sta.database.bean.Arquivo;
import br.com.santaconstacia.sta.vudroid.PdfViewerActivity;

public class IOUtils {
	
	public static int DEFAULT_BUFFER_SIZE = 20;
	
	public static void save(byte[] input, File fileOutput){
		save(input, fileOutput, DEFAULT_BUFFER_SIZE);
	}
	
	public static void save(byte[] input, File fileOutput, int bufSize){
		BufferedOutputStream out = null;
		try {
			OutputStream fout = new FileOutputStream(fileOutput);
			out = new BufferedOutputStream(fout, bufSize);
			out.write(input);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally{
			try {
				out.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public static byte[] read(File fileInput) throws FileNotFoundException{
		return read(fileInput, DEFAULT_BUFFER_SIZE);
	}
	
	public static byte[] read(File fileInput, int bufSize) throws FileNotFoundException{
		return read(new FileInputStream(fileInput), bufSize);
	}
	
	public static byte[] read(InputStream in, int bufSize){
		ByteArrayOutputStream ba = null;
		BufferedInputStream bui = null;
		try {
			bui = new BufferedInputStream(in, bufSize);
			byte[] buffer = new byte[bufSize];
			ba = new ByteArrayOutputStream(bufSize);
			int read;
			while((read = bui.read(buffer, 0, bufSize)) > 0)
				ba.write(buffer, 0, read);
			
			return ba.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally{
			try {
				if(ba != null)
					ba.close();
				
				if(bui != null)
					bui.close();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}			
		}
	}
	
	public static File PUBLIC_LOCATION = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
	
	public static File toPublicLocation(File file){
		File of = new File(PUBLIC_LOCATION, 
				file.getName().replace('\\', '_'));
		
		return of;
	}
	
	public static File toPublicLocation(String filename){
		File of = new File(PUBLIC_LOCATION, filename);
		
		return of;
	}
	
	public static File copyToPublicLocation(File file){
		File of = toPublicLocation(file);
		FileInputStream fis = null;
		FileOutputStream fos = null;
		
		try{
			of.delete();
			
			if ( !of.exists() ) {
				of.mkdirs();
			}
			
			fis = new FileInputStream( file );
			fos = new FileOutputStream(of);
			
			org.apache.commons.io.IOUtils.copy(fis, fos);
			return of;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			return null;
		} finally{
			org.apache.commons.io.IOUtils.closeQuietly(fis);
			org.apache.commons.io.IOUtils.closeQuietly(fos);
		}
	}
	
	public static File copyResourceToPublicLocation(Context context, int resource, String fileName){
		File of = toPublicLocation(fileName);
		InputStream fis = null;
		FileOutputStream fos = null;
		
		try{
			of.delete();
			fis = context.getResources().openRawResource(resource);
			fos = new FileOutputStream(of);
			org.apache.commons.io.IOUtils.copy(fis, fos);
			return of;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			return null;
		} finally{
			org.apache.commons.io.IOUtils.closeQuietly(fis);
			org.apache.commons.io.IOUtils.closeQuietly(fos);
		}
	}
	
	public static void abrir(Activity activity, Arquivo arquivo, String mimeType){
		File directory = AndroidUtils.getDirectoryByUrl( activity, arquivo.getUrl() );	
		File imagem = new File( directory, arquivo.getNome() );
		//imagem = IOUtils.copyToPublicLocation(imagem);
		Uri uri = Uri.fromFile(imagem);
		
		if(mimeType.equals("application/pdf")){
			Intent intent = new Intent(activity, PdfViewerActivity.class);
			intent.putExtra("ARQUIVO_ID", arquivo.getId() );
			intent.setData(uri);
			activity.startActivity(intent);
		}else{
			Intent intent = new Intent();
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(uri, mimeType);
			
			if(activity.getPackageManager().queryIntentActivities(intent, 0).isEmpty())
				AndroidUtils.makeToast(activity, R.string.nao_ha_aplicativo);
			else activity.startActivity(intent);
		}
	}

	
}