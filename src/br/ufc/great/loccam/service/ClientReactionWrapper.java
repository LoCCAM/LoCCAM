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
     * Utilizado para fazer a subscrição de uma informação
     * @param clientReaction instância de um Reaction que receberá as informações 
     * @param pattern pattern contendo as informação (context-key)
     * @param filter um filtro para filtragem das informações
     */
	public ClientReactionWrapper(IClientReaction clientReaction, Pattern pattern, IFilter filter) {
		this.clientReaction = clientReaction;
		this.pattern = pattern;
        this.filter = filter;
	}
	
	/**
	 * Seta a identificação do subscribe
	 * @param id String contendo o retorno de um subscribe (identificação)
	 */
	public void setId(String id) {
    	this.id = id;
	}

	/**
	 * Retorna a identificação do subscribe
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
	 * Retorna uma instância do filtro utilizado
	 */
	public IFilter getJavaFilter() {
		return filter;
	}

	/**
	 * Utilizado no subscribe para receber as informações
	 * @param tuple Tupla contendo as informações do Reaction
	 */
	public void react(Tuple tuple) throws RemoteException {
		clientReaction.react(tuple);
	}

}
