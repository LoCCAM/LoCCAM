package br.ufc.great.loccam;

import br.ufc.great.syssu.base.Pattern;
import br.ufc.great.syssu.base.Tuple;
import br.ufc.great.syssu.base.interfaces.IFilter;
import br.ufc.great.syssu.base.interfaces.ILocalDomain;
import br.ufc.great.syssu.base.interfaces.IReaction;
import br.ufc.loccam.adaptation.model.Component;
import br.ufc.loccam.adaptation.reasorner.AdaptationReasoner;
import br.ufc.loccam.adaptation.reasorner.IAdaptationReasoner;
import br.ufc.loccam.adaptation.reasorner.IAdaptationReasonerObserver;
import br.ufc.loccam.cacmanager.CACManager;
import br.ufc.loccam.cacmanager.ICACManager;

public class LoCCAM implements IAdaptationReasonerObserver {
	
	private ICACManager cacManager;
	private IAdaptationReasoner adaptationReasoner;
	
	private InterestAddedReaction addedReaction;
	private InterestRemovedReaction removedReaction;
	
	/**
	 * Construtor do LoCCAM, que recebe uma instância do Dominio Local do SysSU, CACManager e AdaptationReasoner
	 * @param localDomain instância Dominio local do SysSU
	 * @param cacManager instância do CACManager
	 * @param adaptationReasoner instância do AdaptationReasoner
	 */
	public LoCCAM(ILocalDomain localDomain, ICACManager cacManager, IAdaptationReasoner adaptationReasoner) {
		this.cacManager = cacManager;
		this.adaptationReasoner = adaptationReasoner;
		
		// Configura-se para escutar mudanças nas ZO e ZI desejadas geradas pelo reasoner
		this.adaptationReasoner.setReasonerObservable(this);
		
		addedReaction = new InterestAddedReaction();
		removedReaction = new InterestRemovedReaction();

		try {
			localDomain.subscribe(addedReaction, "put", "");
			localDomain.subscribe(removedReaction, "take", "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Observador para a mudança da zona de observação. Os parametros são os componentes para add e remover da zona
	 */
	@Override
	public void notifyObservableZoneChanged(Component[] addedComponents, Component[] removedComponents) {
		// Inicia os CACs de acordo com a nova zona de interesse desejada
		if(addedComponents != null && addedComponents.length > 0) {
			for (Component component : addedComponents) {
				cacManager.startCAC(component);
			}
		}
		
		// Para os CACs em funcionamento de acordo com a nova zona de interesse desejada
		if(removedComponents != null && removedComponents.length > 0) {
			for (Component component : removedComponents) {
				cacManager.stopCAC(component);
			}
		}		
	}

	private class InterestAddedReaction implements IReaction {

		private String reactionId;
		
		@Override
		public String getId() {
			return reactionId;
		}

		@Override
		public Pattern getPattern() {
			return (Pattern) new Pattern().addField("AppId", "?").addField("InterestElement", "?");
		}

		@Override
		public void react(Tuple tuple) {
			String appId;
			String contextKey;
			
			if(tuple.getField(0).getName().equals("AppId")) {
				appId = tuple.getField(0).getValue().toString();
				contextKey = tuple.getField(1).getValue().toString();
			} else {
				contextKey = tuple.getField(0).getValue().toString();
				appId = tuple.getField(1).getValue().toString();
			}
			
			adaptationReasoner.addApplicationInterestElement(appId, contextKey);
		}

		@Override
		public void setId(String reactionId) {
			this.reactionId = reactionId;
		}

		@Override
		public String getJavaScriptFilter() {
			return null;
		}

		@Override
		public IFilter getJavaFilter() {
			return null;
		}
	}

	private class InterestRemovedReaction implements IReaction {

		private String reactionId;
		
		@Override
		public String getId() {
			return reactionId;
		}

		@Override
		public Pattern getPattern() {
			return (Pattern) new Pattern().addField("AppId", "?").addField("InterestElement", "?");
		}

		@Override
		public void react(Tuple tuple) {
			String appId;
			String contextKey;
			
			if(tuple.getField(0).getName().equals("AppId")) {
				appId = tuple.getField(0).getValue().toString();
				contextKey = tuple.getField(1).getValue().toString();
			} else {
				contextKey = tuple.getField(0).getValue().toString();
				appId = tuple.getField(1).getValue().toString();
			}
			
			adaptationReasoner.removeApplicationInterestElement(appId, contextKey);
		}

		@Override
		public void setId(String reactionId) {
			this.reactionId = reactionId;
		}

		@Override
		public String getJavaScriptFilter() {
			return null;
		}

		@Override
		public IFilter getJavaFilter() {
			return null;
		}
	}
	
	/**
	 * Retorna o estado do LoCCAM, tanto do CACManager como do AdaptationReasoner	
	 * @return Uma string contento as informações do estado.
	 */
	public String printState() {
		String r;
		r = ((CACManager)cacManager).printBundlesState();
		r += ((AdaptationReasoner)adaptationReasoner).printDesiredOZ();
		
		return r; 
	}
}
