package br.ufc.loccam.ipublisher;

import java.util.List;

import br.ufc.great.syssu.base.Tuple;
import br.ufc.great.syssu.base.interfaces.IReaction;

public interface IPublisher {
		
	/**
	 * Método para colocar a tupla do tipo Object no espaço de tuplas
	 * @param contextData Context-Key.
	 * @param source Fonte da informação (Fisico, Logico, Virtual).
	 * @param values O valor da informação contextual.
	 * @param accuracy Acuracia.
	 * @param timestamp O instante em que a informação foi publicada.
	 */
	public void put(String contextData, String source, List<Object> values, String accuracy, String timestamp);
	
	/**
	 * Método para colocar a tupla do tipo String no espaço de tuplas
	 * @param contextData Context-Key.
	 * @param source Fonte da informação (Fisico, Logico, Virtual).
	 * @param values O valor da informação contextual.
	 * @param accuracy Acuracia.
	 * @param timestamp O instante em que a informação foi publicada.
	 */
	public void putString(String contextData, String source, List<String> values, String accuracy, String timestamp);
	
	/**
	 * Método síncrono, que retorna uma tupla que contem a informação contextual passada como parametro.
	 * @param contextData Context-Key.
	 * @return Uma lista de tuplas.
	 */
	public List<Tuple> read(String contextData);
	
	/**
	 * Remove o interesse de uma informação contextual.
	 * @param contextData Context-Key.
	 * @return True se conseguir remover o interesse, false caso contrário.
	 */
	public boolean remove(String contextData);
		
	/**
	 * Método assíncrono, que faz a subscrição de uma informação contextual, notificando sempre que uma nova informação contextual
	 * for colocado no espaço de tuplas.
	 * @param reaction Reaction que receberá a informação.
	 * @param event Evento: put, take, ...
	 * @param key Context-Key.
	 */
	public void subscribe(IReaction reaction, String event, String key);
}
