package br.ufc.great.loccam.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import br.ufc.great.loccam.LoCCAM;
import br.ufc.great.syssu.base.Pattern;
import br.ufc.great.syssu.base.Tuple;
import br.ufc.great.syssu.base.TupleSpaceException;
import br.ufc.great.syssu.base.TupleSpaceSecurityException;
import br.ufc.great.syssu.base.interfaces.IClientReaction;
import br.ufc.great.syssu.base.interfaces.IFilter;
import br.ufc.great.syssu.base.interfaces.ILocalDomain;
import br.ufc.great.syssu.ubibroker.LocalUbiBroker;
import br.ufc.loccam.adaptation.reasorner.AdaptationReasoner;
import br.ufc.loccam.cacmanager.CACManager;

public class SysSUStart {

	private LocalUbiBroker localUbiBroker;
	private ILocalDomain domain;
	private LoCCAM loCCAM;
	private String appId;
	private ArrayList<String> interests = new ArrayList<String>();
	private ArrayList<String> reactionIds = new ArrayList<String>();
	private static boolean running = false;
	private String TAG = "SysSUStart";
	static SysSUStart sysSUStart; 
	
	/**
	 * Iniciliza o SysSU
	 * @param context Contexto do LoCCAM
	 * @param appId Id da aplicação
	 */
	public SysSUStart(Context context, String appId) {
		this.appId = appId;
		try {
			localUbiBroker = LocalUbiBroker.createUbibroker();
			domain = (ILocalDomain) localUbiBroker.getDomain("loccam");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try{
			Log.e (TAG, "CAMv2");
			loCCAM = new LoCCAM(domain,CACManager.getInstance(context) ,new AdaptationReasoner(CACManager.getInstance(context).getListOfAvailableCACs()));
		} catch (Exception e){
			Log.e(TAG, e.getLocalizedMessage());
		}
		running = true;
		sysSUStart = this;
	}
	
	/**
	 * Retorna uma instancia do SysSU
	 * @return Retorna uma instancia do SysSUSart
	 */
	public static SysSUStart getInstance(){
		return SysSUStart.sysSUStart;
	}

	/**
	 * Verifica se esta rodando
	 * @return True se estiver rodando e False se nao estiver rodando
	 */
	public static boolean isRunning(){
		return running;
	}
	
	/**
	 * Método para colocar o interesse
	 * @param tuple Tupla contendo as informações
	 */
	public void put(Tuple tuple) {
		try {
			domain.put(tuple, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Método para colocar o interesse a partir da informação contextual
	 * @param key Tipo de informação contextual
	 */
	public void putInterest(String key){
		
		try{	
			Tuple tupla = (Tuple) new Tuple()
			.addField("AppId", appId)
			.addField("InterestElement", key);
			put(tupla);
			interests.add(key);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retorna uma lista de tuplas que é especificada por um filtro
	 * @param pattern Indica que informação contextual deseja ser recebida
	 * @param filter Especifica alguma característica especial da leitura
	 * @return Lista de tuplas
	 */
	public List<Tuple> read(Pattern pattern, IFilter filter) {
		List<Tuple> tuples = null;
		try {
			tuples = domain.read(pattern, filter, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tuples;
	}
	
	/**
	 * Retorna uma tupla de um tipo de informação contextual
	 * @param key Tipo de informação contextual
	 * @return Uma tupla de uma informação contextual
	 */
	public Tuple read(String key){
		
		try{
			Pattern pattern1 = (Pattern) new Pattern().addField("ContextKey", key);
			List<Tuple> lista = read(pattern1, null);
			if(!lista.isEmpty()){
				return lista.get(0);
			}
			else{
				return new Tuple();
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Retorna uma lista de tuplas de todos os interesses contextuais
	 * @return ArrayList de tuplas 
	 */
	public ArrayList<Tuple> readAll(){
		
		ArrayList<Tuple> tuplas = new ArrayList<Tuple>();
		
		for(int count = 0; count < interests.size(); count++){
			try{
				Pattern pattern1 = (Pattern) new Pattern().addField("ContextKey", interests.get(count));
				List<Tuple> lista = read(pattern1, null);
				if(!lista.isEmpty()){
					tuplas.add(lista.get(0));
				}
				else{
					tuplas.add(new Tuple());
				}
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return tuplas;
	}

	/**
	 * Retira um interesse sobre determinada informação contextual 
	 * @param pattern Indica que informação contextual deseja ser retirada
	 * @param filter Especifica alguma característica especial da retirada
	 * @return Lista de tuplas dessa retirada
	 */
	public List<Tuple> take(Pattern pattern, IFilter filter) {
		List<Tuple> tuples = null;

		try {
			tuples = domain.take(pattern, filter, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tuples;
	}
	
	/**
	 * Retira um interesse a partir de uma informação contextual
	 * @param key Tipo de informação contextual
	 */
	public void takeInterest(String key){
		try{
			Pattern pattern1 = (Pattern)new Pattern().addField("AppId", appId)
				.addField("InterestElement", key);
			take(pattern1, null);
			interests.remove(key);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Retira todos os interesses
	 */
	public void takeAllInterests() {
		
		for(int count = 0; count < interests.size(); count++){
			try{
				Pattern pattern1 = (Pattern)new Pattern().addField("AppId", appId)
					.addField("InterestElement", interests.get(count));
				take(pattern1, null);
				interests.remove(interests.get(count));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Retorna uma lista contendo todos os interesses
	 * @return ArrayList de strings contendo todos os interesses
	 */
	public ArrayList<String> getInterests(){
		return interests;
	}

	/**
	 * Método que retorna leituras assíncronas, na forma de subscriptions, 
	 * onde a aplicação informa que deseja ser notificada quando houver novas 
	 * informações contextuais disponíveis que sejam do seu interesse.
	 * @param reaction É chamado quando a aplicação for notificada com novas informações 
	 * contextuais de interesse
	 * @param event Indica que a aplicação deseja ser notificada quando novas informações 
	 * contextuais forem publicadas
	 * @param pattern Indica que informação contextual deseja ser recebida
	 * @param filter Representa uma implementação da interface que faz com que a aplicação 
	 * apenas seja notificada de fato quando as informações passarem por este filtro
	 * @return
	 */
	public String subscribe(IClientReaction reaction, String event,
			Pattern pattern, IFilter filter) {
		ClientReactionWrapper clientReactionWrapper = new ClientReactionWrapper(
				reaction, pattern, filter);

		try {
			clientReactionWrapper.setId(domain.subscribe(clientReactionWrapper,	event, ""));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return clientReactionWrapper.getId();

	}
	
	/**
	 * Método que retorna leituras assíncronas, na forma de subscriptions, 
	 * onde a aplicação informa que deseja ser notificada quando houver novas 
	 * informações contextuais disponíveis que sejam do seu interesse.
	 * @param reaction É chamado quando a aplicação for notificada com novas informações 
	 * contextuais de interesse
	 * @param event Indica que a aplicação deseja ser notificada quando novas informações 
	 * contextuais forem publicadas
	 * @param key Indica qual o tipo de informação contextual de interesse 
	 * @param filter Representa uma implementação da interface que faz com que a aplicação 
	 * apenas seja notificada de fato quando as informações passarem por este filtro
	 * @return String com id do subscribe.
	 */
	public String subscribe(IClientReaction reaction, String event, String key, IFilter filter){	
		
		String reactionId = "";
		
		Pattern pattern = new Pattern();
		pattern.addField("ContextKey", key);
		try {
			reactionId = subscribe(reaction, event, pattern, filter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(!reactionId.equals("")){
			reactionIds.add(reactionId);
		}
		return reactionId;
	}
	
	/**
	 * Método que retorna leituras assíncronas, na forma de subscriptions, 
	 * onde a aplicação informa que deseja ser notificada quando houver novas 
	 * informações contextuais disponíveis que sejam do seu interesse.
	 * @param reaction É chamado quando a aplicação for notificada com novas informações 
	 * contextuais de interesse
	 * @param key Indica qual o tipo de informação contextual de interesse 
	 * @return String com id do subscribe.
	 */
	public String subscribe(IClientReaction reaction, String key){
		
		
		String reactionId = "";
		
		Pattern pattern = new Pattern();
		pattern.addField("ContextKey", key);
		try {
			reactionId = subscribe(reaction, "put", pattern, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(!reactionId.equals("")){
			reactionIds.add(reactionId);
		}
		return reactionId;
	}

	/**
	 * Método que cancela um subscribe
	 * @param reactionId String que foi obtida quando o subscribe() correspondente foi realizado
	 */
	public void unSubscribe(String reactionId) {
		try {
			domain.unsubscribe(reactionId, "");
			reactionIds.remove(reactionId);
		} catch (TupleSpaceException e) {
			e.printStackTrace();
		} catch (TupleSpaceSecurityException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Método que cancela todos os subscribes
	 */
	public void unSubscribeAll(){
		
		for(String reactionId : reactionIds){
			try {
				unSubscribe(reactionId);
				reactionIds.remove(reactionId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Método que retorna o estado do LoCCAM
	 * @return String com o estado do LoCCAM
	 */
	public String printLoCCAMState() {
		return loCCAM.printState();
	}

}
