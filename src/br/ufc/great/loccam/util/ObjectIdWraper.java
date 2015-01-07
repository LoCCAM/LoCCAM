package br.ufc.great.loccam.util;

/**
 * Classe para identificação do objeto
 */
public class ObjectIdWraper {
	Object id;

	/**
	 * Seta o identificação objeto
	 * @param identificação do objeto
	 */
	public void setId(Object id){
		this.id = id;
	}
	
	/**
	 * Retorna a identificação do objeto
	 * @return Objeto contendo a identificação
	 */
	public Object getId(){
		return id;
	}
}
