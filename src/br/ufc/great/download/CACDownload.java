package br.ufc.great.download;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

@SuppressLint("DefaultLocale")
public class CACDownload {
	
	//-------------------------------------------------------------
	//Automatizaçao dos CACs
	//raw_names = new ArrayList<String>();
	//raw_ids = new ArrayList<Integer>();
	
	//Insira os nomes - DOWNLOAD
	//raw_names.add("ambientlightcac.jar"); // Light
	//raw_names.add("randomcac.jar");       // Random
	
	//Insira os nomes e ids dos CAC's - MANUALMENTE
	//raw_names.add("ambientlightcac.jar"); // Light
	//raw_names.add("randomcac.jar");       // Random
	//raw_ids.add(R.raw.ambientlightcac);
	//raw_ids.add(R.raw.randomcac);
	//-------------------------------------------------------------
	
	String urlDirCACs = "http://loccam.great.ufc.br/downloadFiles/cacs/jar/";
	String urlFelix = "/Android/data/br.ufc.great.loccam/files/felix/availablebundles/"; 
	String deviceVersion = Build.VERSION.RELEASE;
	String aux = deviceVersion.replaceAll("\\.",",");
	double version = 0.0;
	
	// Automatizaçao da instalação dos CAC's
	/**
	 * Faz o download no repositorio dos CACs que estiverem na lista 
	 * e em seguida instala cada um na pasta padrão. É necessário que 
	 * o dispositivo aceite pelo menos umas das versões dos CACs que tiverem no repositorio.  
	 * @param names Lista contendo as context-key dos CAC's
	 */
	@SuppressLint("DefaultLocale")
	public void instalarCACs(List<String> names) {
		String[] myList = aux.split(",");
		if(myList.length > 2)
			version = Double.parseDouble(myList[0] + "." + myList[1] + myList[2]);
		else
			version = Double.parseDouble(myList[0] + "." + myList[1]);
		
		System.out.println(version);
		File dir = new File(Environment.getExternalStorageDirectory() + urlFelix);
		
		// Se existir
		if(dir.exists() && dir.isDirectory()) {
			File[] listOfFiles = dir.listFiles(); // Lista os arquivos
			
			System.out.println("Size available CACs in device: " + listOfFiles.length);
            
            Manifest m = null;
            Attributes mainAttribs;
            String context;
			
			for(int i = 0; i < listOfFiles.length; i++) {
				if(listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".jar")) {
					try {
						m = new JarFile(listOfFiles[i]).getManifest();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mainAttribs = m.getMainAttributes();
		            context = mainAttribs.getValue("Context-Provided");
		            
		            // Nome e context-key do arquivo
					System.out.println("Name: " + listOfFiles[i].getName() + " - Context-Key: " + context);

					// Verifica se ja existe essa context-key
					if(names.contains(context)) {
						System.out.println("CAC ja instalado: " + listOfFiles[i].getName() + " (" + context + ")");
						names.remove(context);
					}	
				}					
			}
			
			if(names.size() > 0)
				System.out.println("Baixando CACs ...");			
			// Download dos cacs nao instalados
			for(int i = 0; i < names.size(); i++) 
				new DownloadFileFromURL().execute(names.get(i));
		}
	}
	/*
	// Automatizaçao da instalação dos CAC's
	public void instalarCACsManual(List<String> names) {
		File dir = new File(Environment.getExternalStorageDirectory() + urlFelix);
		
		// Testa se existe, se nao cria a pasta url
		if(!dir.exists()) {
			dir.mkdir();
		}
		// Se existir
		if(dir.exists() && dir.isDirectory()) {
			for(int i = 0; i < names.size(); i++) {
				File src = new File(names.get(i));
				String aux2 = names.get(i).substring(names.get(i).lastIndexOf("/") + 1);
				File aux = new File(dir, aux2);
				try {
					System.out.println("Instalando cac: " + aux2);
					System.out.println(aux.getAbsolutePath());
					copyFile(src, aux);
				}
				catch(IOException e) {
					e.printStackTrace();
				}		
			}		
		}
	}
	*/
	/**
	 * Background Async Task to download file
	 * */
	class DownloadFileFromURL extends AsyncTask<String, String, String> {
	    /**
	     * Downloading file in background thread
	     * */
	    @Override
	    protected String doInBackground(String... f_url) {
	        int count;
	        String cac = "";
	        
	        try {
			   	System.out.println("Tentando conectar ...");
			   	URL url2 = new URL(urlDirCACs);	            
	    		URLConnection conection = url2.openConnection();
	    		BufferedReader br = new BufferedReader(new InputStreamReader(conection.getInputStream()));
	    		System.out.println("Conectado!");
			   	
	    		String inputLine = "";
	    		int ai, af;	    		
	    		double aux = 0.0;
	    		
	    		while ((inputLine = br.readLine()) != null) {    
	    			if((ai = inputLine.indexOf("href=")) != -1 && (af = inputLine.indexOf(".jar")) != -1) {
	    				URL url = new URL("jar:" + urlDirCACs + inputLine.substring(ai + 6, af + 4) + "!/");	
	    		    	JarURLConnection jarConnection = (JarURLConnection)url.openConnection();
	    		    	Manifest manifest = jarConnection.getManifest();		            	 
	    		    	Attributes mainAttribs = manifest.getMainAttributes();
	    		    	String context = mainAttribs.getValue("Context-Provided");
	    		    	double version2 = Double.parseDouble(mainAttribs.getValue("Minimal-Version"));
	    		    	
	    		    	if(context.equals(f_url[0])) {
	    		    		if(version >= version2 && aux < version2) {
	    		    			aux = version2;
	    		    			cac = inputLine.substring(ai + 6, af + 4);
	    		    		}
	    				}
	    			}
				}	
	    			
	    		if(!cac.equals("")) {
	    			System.out.println("# CAC encontrado no repositório #\nNome: " + cac + "\nContext-Key: " + f_url[0] + "\nVersion: " + aux);
	    			URL url = new URL(urlDirCACs + cac);	            
	    			URLConnection conection2 = url.openConnection();
	    			conection2.connect();
	    			// input stream to read file - with 8k buffer
	    			InputStream input = new BufferedInputStream(url.openStream(), 8192);
	 
	    			// Output stream to write file
	    			OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory() + urlFelix + cac);
	 
	    			byte data[] = new byte[1024];
	 
	    			while ((count = input.read(data)) != -1) {
	    				// writing data to file
	    				output.write(data, 0, count);
	    			}
	 
	    			// flushing output
	    			output.flush();
	 
	    			// closing streams
	    			output.close();
	    			input.close();
	    			System.out.println("CAC instalado.");
	    		} else 
	    			Log.i("CACDownload", "CAC não encontrado");	    			
	    		} catch (Exception e) {
	    			Log.e("Error: ", e.getMessage());
	    		}
	        
	        return null;
	    }
	}
}
