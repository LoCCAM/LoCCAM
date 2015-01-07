package br.ufc.loccam.ipublisher;

import java.util.List;

import br.ufc.great.syssu.base.Tuple;
import br.ufc.great.syssu.base.interfaces.IReaction;

public interface IPublisher {
		
	/**
	 * M�todo para colocar a tupla do tipo Object no espa�o de tuplas
	 * @param contextData Context-Key.
	 * @param source Fonte da informa��o (Fisico, Logico, Virtual).
	 * @param values O valor da informa��o contextual.
	 * @param accuracy Acuracia.
	 * @param timestamp O instante em que a informa��o foi publicada.
	 */
	public void put(String contextData, String source, List<Object> values, String accuracy, String timestamp);
	
	/**
	 * M�todo para colocar a tupla do tipo String no espa�o de tuplas
	 * @param contextData Context-Key.
	 * @param source Fonte da informa��o (Fisico, Logico, Virtual).
	 * @param values O valor da informa��o contextual.
	 * @param accuracy Acuracia.
	 * @param timestamp O instante em que a informa��o foi publicada.
	 */
	public void putString(String contextData, String source, List<String> values, String accuracy, String timestamp);
	
	/**
	 * M�todo s�ncrono, que retorna uma tupla que contem a informa��o contextual passada como parametro.
	 * @param contextData Context-Key.
	 * @return Uma lista de tuplas.
	 */
	public List<Tuple> read(String contextData);
	
	/**
	 * Remove o interesse de uma informa��o contextual.
	 * @param contextData Context-Key.
	 * @return True se conseguir remover o interesse, false caso contr�rio.
	 */
	public boolean remove(String contextData);
		
	/**
	 * M�todo ass�ncrono, que faz a subscri��o de uma informa��o contextual, notificando sempre que uma nova informa��o contextual
	 * for colocado no espa�o de tuplas.
	 * @param reaction Reaction que receber� a informa��o.
	 * @param event Evento: put, take, ...
	 * @param key Context-Key.
	 */
	public void subscribe(IReaction reaction, String event, String key);
}
