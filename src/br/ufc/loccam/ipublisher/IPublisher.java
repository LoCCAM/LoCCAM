package br.ufc.loccam.ipublisher;

import java.util.List;

import br.ufc.great.syssu.base.Tuple;
import br.ufc.great.syssu.base.interfaces.IReaction;

public interface IPublisher {
		
	public void put(String contextData, String source, List<Object> values, String accuracy, String timestamp);
	
	public void putString(String contextData, String source, List<String> values, String accuracy, String timestamp);
	
	public List<Tuple> read(String contextData);
	
	public boolean remove(String contextData);
		
	public void subscribe(IReaction reaction, String event, String key);
}
