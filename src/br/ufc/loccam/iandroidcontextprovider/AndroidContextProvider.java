package br.ufc.loccam.iandroidcontextprovider;

import android.content.Context;

public class AndroidContextProvider implements IAndroidContextProvider {
	
	private Context context;
	
	/**
	 * Contexto
	 * @param context 
	 */
	public AndroidContextProvider(Context context){
		this.context = context;
	}

	/**
	 * Retorna o Contexto
	 * @return Objeto contedo o Contexto
	 */
	public Object getContext() {
		return context;
	}

}
