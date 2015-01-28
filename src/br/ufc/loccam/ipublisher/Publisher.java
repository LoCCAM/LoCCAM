package br.ufc.loccam.ipublisher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.ufc.great.syssu.base.Pattern;
import br.ufc.great.syssu.base.Tuple;
import br.ufc.great.syssu.base.TupleSpaceException;
import br.ufc.great.syssu.base.interfaces.IDomain;
import br.ufc.great.syssu.base.interfaces.IReaction;
import br.ufc.great.syssu.ubibroker.LocalUbiBroker;

public class Publisher implements IPublisher{

	private LocalUbiBroker localUbiBroker;
	private IDomain domain;
	
	public Publisher(){
		try {
			localUbiBroker = LocalUbiBroker.createUbibroker();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			domain = localUbiBroker.getDomain("loccam");
		} catch (TupleSpaceException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * M�todo para colocar a tupla do tipo String no espa�o de tuplas
	 * @param contextData Context-Key.
	 * @param source Fonte da informa��o (Fisico, Logico, Virtual).
	 * @param values O valor da informa��o contextual.
	 * @param accuracy Acuracia.
	 * @param timestamp O instante em que a informa��o foi publicada.
	 */
	public void putString(String contextData, String source, List<String> _values, String accuracy, String timestamp) {		
		// Armazena os valores atuais e depois limpa o array para uma futura reutiliza��o.
		List<String> values = new ArrayList<String>(_values);
		_values.clear();
		
		// Retira a tupla com o valor anterior
		Pattern pattern = (Pattern) new Pattern()
		.addField("ContextKey", contextData)
		.addField("Source", "?")
		.addField("Values", "?")
		.addField("Accuracy", "?")
		.addField("Timestamp", "?");
	
		try {
			domain.take(pattern, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Tuple tuple = (Tuple) new Tuple()
			.addField("ContextKey", contextData)
			.addField("Source", source)
			.addField("Values", values)
			.addField("Accuracy", accuracy)
			.addField("Timestamp", timestamp);
		
		try {
			domain.put(tuple, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * M�todo para colocar a tupla do tipo Object no espa�o de tuplas
	 * @param contextData Context-Key.
	 * @param source Fonte da informa��o (Fisico, Logico, Virtual).
	 * @param values O valor da informa��o contextual.
	 * @param accuracy Acuracia.
	 * @param timestamp O instante em que a informa��o foi publicada.
	 */	
	public void put(String contextData, String source, List<Object> _values, String accuracy, String timestamp) {		
		// Armazena os valores atuais e depois limpa o array para uma futura reutiliza��o.
		List<Object> values = new ArrayList<Object>(_values);
		_values.clear();
		
		// Retira a tupla com o valor anterior
		Pattern pattern = (Pattern) new Pattern()
		.addField("ContextKey", contextData)
		.addField("Source", "?")
		.addField("Values", "?")
		.addField("Accuracy", "?")
		.addField("Timestamp", "?");
	
		try {
			domain.take(pattern, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Tuple tuple = (Tuple) new Tuple()
			.addField("ContextKey", contextData)
			.addField("Source", source)
			.addField("Values", values)
			.addField("Accuracy", accuracy)
			.addField("Timestamp", timestamp);
		
		try {
			domain.put(tuple, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Remove o interesse de uma informa��o contextual.
	 * @param contextData Context-Key.
	 * @return True se conseguir remover o interesse, false caso contr�rio.
	 */
	public boolean remove(String contextData){
		Pattern pattern = (Pattern) new Pattern()
		.addField("ContextKey", contextData)
		.addField("Source", "?")
		.addField("Values", "?")
		.addField("Accuracy", "?")
		.addField("Timestamp", "?");
	
		try {
			domain.take(pattern, null, null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * M�todo s�ncrono, que retorna uma tupla que contem a informa��o contextual passada como parametro.
	 * @param contextData Context-Key.
	 * @return Uma lista de tuplas.
	 */
	public List<Tuple> read(String contextData) {
		Pattern pattern = (Pattern) new Pattern()
		.addField("ContextKey", contextData)
		.addField("Source", "?")
		.addField("Values", "?")
		.addField("Accuracy", "?")
		.addField("Timestamp", "?");
	
		List<Tuple> list = null;

		try {
			list = domain.read(pattern, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}

	/**
	 * M�todo ass�ncrono, que faz a subscri��o de uma informa��o contextual, notificando sempre que uma nova informa��o contextual
	 * for colocado no espa�o de tuplas.
	 * @param reaction Reaction que receber� a informa��o.
	 * @param event Evento: put, take, ...
	 * @param key Context-Key.
	 */
	public void subscribe(IReaction reaction, String event, String key) {
		try {
			reaction.setId(domain.subscribe(reaction, event, key));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
