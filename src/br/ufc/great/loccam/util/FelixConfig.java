package br.ufc.great.loccam.util;

import java.util.Collection;
import java.util.Properties;

import org.twdata.pkgscanner.ExportPackage;
import org.twdata.pkgscanner.PackageScanner;

import br.ufc.loccam.cacmanager.CACManager;

/**
 * Because it is somehow hard to use conf/config.properties file in android, 
 * I build up properties object hard coded for now
 * @author romulogmlima
 */
public class FelixConfig {

	public static final String AVAILABLE_BUNDLES_PATH = "/felix/availablebundles"; 
	public static final String NEWE_BUNDLES_PATH = "/felix/newbundle"; 
    public static final String CACHE_PATH = "/felix/cache";
	
	private Properties configProps;
	
	/**
	 * Configurações do Felix
	 * @param absFilePath Caminho absoluto
	 */
	public FelixConfig(String absFilePath){
		
		// Analyze the classpath / classloader 
		analyzeClassPath();

		configProps = new Properties();
		
		configProps.put("felix.cache.locking", "false");
		
		configProps.put("org.osgi.framework.storage.clean", "onFirstInit");
		configProps.put("felix.service.urlhandlers", "false");
		
		//org.osgi.framework.storage=${felix.cache.rootdir}/felixcache
//		configProps.put("org.osgi.framework.storage", absFilePath + CACHE_PATH);
		configProps.put("org.osgi.framework.storage", CACManager.getCacheDir().getAbsolutePath());
		
		// felix.cache.rootdir=${user.dir} 
		configProps.put("felix.cache.rootdir", absFilePath + "/felix");
		
		// fileinstall watch dir
		configProps.put("felix.fileinstall.dir", absFilePath + NEWE_BUNDLES_PATH); 
		
		//"felix.fileinstall.dir";
		configProps.put("felix.fileinstall.debug", "1"); //"felix.fileinstall.debug";
		
		// Exports packages to provide them for the bundles
		configProps.put("org.osgi.framework.system.packages.extra", ANDROID_FRAMEWORK_PACKAGES_ext);
		
		// felix.log.level=4
		configProps.put("felix.log.level", "4");
		
		// felix.startlevel.bundle=1
		configProps.put("felix.startlevel.bundle", "1");
		
		// org.osgi.service.http.port=-1
		// A negative port number has the same effect as setting org.apache.felix.http.enable to false
		configProps.put("org.osgi.service.http.port", "-1");
		
		configProps.put("felix.bootdelegation.implicit", "true");
		
	}

	/**
	 * Retorna as configurações das propriedades do Felix 
	 * @return
	 */
	public Properties getConfigProps() {
		return configProps;
	}
	
	// package scanner
	private void analyzeClassPath(){
		
		PackageScanner pkgScanner = new PackageScanner();
		
		// set usage of classloader to avoid NPE in internal scanner of PackageScanner
		pkgScanner.useClassLoader(PackageScanner.class.getClassLoader().getParent());
		//FelixConfig.class.getClassLoader()   ClassLoader.getSystemClassLoader()
		//Collection<ExportPackage> exports = pkgScanner.scan();
		
        Collection<ExportPackage> exports = pkgScanner
        .select
        (
        	PackageScanner.jars(
            		PackageScanner.include
            		(
                            "*.jar"),
                            PackageScanner.exclude(
                            "felix.jar",
                            "package*.jar")
                    ),
                            
            PackageScanner.packages(
            		PackageScanner.include
            		(
                            "org.*", "com.*",
                            "javax.*", "android",
                            "android.*", "com.android.*",
                            "dalvik.*", "java.*",
                            "junit.*", "org.apache.*",
                            "org.json", "org.xml.*",
                            "org.xmlpull.*", "org.w3c.*")
                    )
        ).scan();
		
        System.out.println("HIER: "+exports.size());
        // now fill analyzedExportString
        while (exports.iterator().hasNext()){
        	System.out.println("exports: "+ exports.iterator().next().getPackageName());
        }
		
	}
	
	private static final String ANDROID_FRAMEWORK_PACKAGES_ext = (
			"org.osgi.framework; version=1.4.0," +
            "org.osgi.service.packageadmin; version=1.2.0," +
            "org.osgi.service.startlevel; version=1.0.0," +
            "org.osgi.service.url; version=1.0.0," +
            "org.osgi.util.tracker," +
            
            // ANDROID (here starts semicolon as separator -> Why?
            "android; " + 
            "android.app;" + 
            "android.content;" + 
            "android.database;" + 
            "android.database.sqlite;" + 
            "android.graphics; " + 
            "android.graphics.drawable; " + 
            "android.graphics.glutils; " + 
            "android.hardware; " + 
            "android.location; " + 
            "android.media; " + 
            "android.net; " + 
            "android.net.wifi; " +
            "android.opengl; " + 
            "android.os; " + 
            "android.provider; " + 
            "android.sax; " + 
            "android.speech.recognition; " + 
            "android.telephony; " + 
            "android.telephony.gsm; " + 
            "android.text; " + 
            "android.text.method; " + 
            "android.text.style; " + 
            "android.text.util; " + 
            "android.util; " +  
            "android.view; " + 
            "android.view.animation; " + 
            "android.webkit; " + 
            "android.widget;" +
            "android.media; " + 
            "android.util; " +
            "android.bluetooth; " +            
            "com.google.android.gms.common; " +
            "com.google.android.gms.location; " +
            "com.google.android.gms.common.api; " +
            
			"org.apache.http; " + 
			"org.apache.http.client; " + 
			"org.apache.http.impl.client; " +
			"org.apache.http.client.methods; " + 
			
			//Facebook			
			  "br.ufc.great.somc.aggregation.frienddata;"
			+ "br.ufc.great.somc.login.facebook;"
			+ "br.ufc.great.somc.places;" 
			+ "br.ufc.great.somc.people;"
			+ "br.ufc.great.somc.people;"
			+ "br.ufc.loccam.iandroidcontextprovider;"
			+ "br.ufc.loccam.ipublisher;"
			+ "br.ufc.loccam.isensor.ISensor;"
			+ "br.ufc.great.somc.aggregation.model;"
			+ "com.facebook;" 
			+ "com.facebook.android," +
              "com.facebook.internal," +
              "com.facebook.model," +
              "com.facebook.widget," +            
			  "org.json;"+
			  "com.androidquery,"+
              "com.androidquery.auth,"+
              "com.androidquery.callback,"+
              "com.androidquery.service,"+
              "com.androidquery.util,"+
			  "lib.drag.great.ufc.model;" +
			  "com.example.testecac;"+
			  "br.ufc.great.somc.graph;" +
			  "br.ufc.great.somc.util;" +
			  "drag.great.ufc.activityinterface;" +
			  "drag.great.ufc.activityinterface.normal;" +
			  "drag.great.ufc.service;" +
			  "drag.great.ufc.receiver;" +
			  "drag.great.ufc.model;" +

            // Android OS Version?? ->her ends semicolon as seperator -> Why?
            "version=1.5.0.r3," +
            
            // MY OWN
			"br.ufc.loccam.iandroidcontextprovider," + 
            "br.ufc.loccam.isensor," +
            "br.ufc.great.syssu.base," +
            "br.ufc.great.syssu.base.interfaces," + 
			"br.ufc.loccam.ipublisher"
            
		
            
			).intern();
}
