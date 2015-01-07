package br.ufc.great.loccam.service;

import android.os.RemoteException;
import br.ufc.great.syssu.base.Pattern;
import br.ufc.great.syssu.base.Tuple;
import br.ufc.great.syssu.base.interfaces.IClientReaction;
import br.ufc.great.syssu.base.interfaces.IFilter;
import br.ufc.great.syssu.base.interfaces.IReaction;

public class ClientReactionWrapper implements IReaction {

	private IClientReaction clientReaction;
	
	private Pattern pattern;
    private IFilter filter;
    private String id;
	
    /**
     * Utilizado para fazer a subscri��o de uma informa��o
     * @param clientReaction inst�ncia de um Reaction que receber� as informa��es 
     * @param pattern pattern contendo as informa��o (context-key)
     * @param filter um filtro para filtragem das informa��es
     */
	public ClientReactionWrapper(IClientReaction clientReaction, Pattern pattern, IFilter filter) {
		this.clientReaction = clientReaction;
		this.pattern = pattern;
        this.filter = filter;
	}
	
	/**
	 * Seta a identifica��o do subscribe
	 * @param id String contendo o retorno de um subscribe (identifica��o)
	 */
	public void setId(String id) {
    	this.id = id;
	}

	/**
	 * Retorna a identifica��o do subscribe
	 */
	public String getId() {
		return id;
	}

	/**
	 * Retorna o pattern contendo a context-key
	 */
	public Pattern getPattern() {
		return pattern;
	}

	/**
	 * Depreciado
	 */
	public String getJavaScriptFilter() {
		return null;
	}

	/**
	 * Retorna uma inst�ncia do filtro utilizado
	 */
	public IFilter getJavaFilter() {
		return filter;
	}

	/**
	 * Utilizado no subscribe para receber as informa��es
	 * @param tuple Tupla contendo as informa��es do Reaction
	 */
	public void react(Tuple tuple) throws RemoteException {
		clientReaction.react(tuple);
	}

}
