package br.ufc.great.loccam.util;

/**
 * Classe para identifica��o do objeto
 */
public class ObjectIdWraper {
	Object id;

	/**
	 * Seta o identifica��o objeto
	 * @param identifica��o do objeto
	 */
	public void setId(Object id){
		this.id = id;
	}
	
	/**
	 * Retorna a identifica��o do objeto
	 * @return Objeto contendo a identifica��o
	 */
	public Object getId(){
		return id;
	}
}
