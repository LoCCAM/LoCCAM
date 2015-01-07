package br.ufc.loccam.adaptation.reasorner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.util.Log;
import br.ufc.loccam.adaptation.model.Component;

/**
 * Utilizado para receber os interesses das aplicações e adaptar, se possivel, o LoCCAM para esses interesses.
 * 
 */
public class AdaptationReasoner implements IAdaptationReasoner {

	private Map<String, List<Component>> availableCACs;

	private List<Component> applications;
	private Set<Component> observableZone;
	
	private List<Component> addedComponents;
	private List<Component> removedComponents;
	
	private IAdaptationReasonerObserver reasonerObserver;
	
	/**
	 * Construtor do AdaptationReasoner.
	 * @param availableCACs Lista contendo os CACs disponiveis.
	 */
	public AdaptationReasoner(Map<String, List<Component>> availableCACs) {
		if(availableCACs == null) {
			availableCACs = new HashMap<String, List<Component>>();
		}
		this.availableCACs = availableCACs;
		
		applications = new ArrayList<Component>();
		observableZone = new HashSet<Component>();
		
		addedComponents = new ArrayList<Component>();
		removedComponents = new ArrayList<Component>();
	}
	
	/**
	 * Observador do Reasoner.
	 * @param Interface contendo o Reasoner.
	 */
	public void setReasonerObservable(IAdaptationReasonerObserver reasonerObserver) {
		this.reasonerObserver = reasonerObserver;
	}
	
	/**
	 * Adiciona um CAC.
	 * @param CAC.
	 */
	public void addApplication(Component application) {
		addApplication(application, true);
	}

	/**
	 * Remove um CAC.
	 * @param Nome simbolico do CAC.
	 */
	public void removeApplication(String symbolicName) {
		Component application = getApp(symbolicName);
		// Retorna caso não exista app com o nome
		if(application == null)
			return;
		
		removeComponentIfPossible(application);

		// Notifica que houve uma alteração na configuração desejada da zona de observação 
		notifyChangesToObservers();
	}

	/**
	 * Adiciona um CAC à lista de CACs disponiveis.
	 * @param CAC.
	 */
	public void addAvailableCAC(Component cac) {
		// Verifica se já existe uma lista com componentes desse tipo e cria uma caso não exista
		List<Component> componentes = availableCACs.get(cac.getContextProvided());
		if(componentes == null) {
			componentes = new ArrayList<Component>();
			availableCACs.put(cac.getContextProvided(), componentes);
		}
		componentes.add(cac);
	}

	/**
	 * Remove um CAC da lista de CACs disponiveis.
	 * @param symbolicName - Nome simbolico do CAC.
	 * @param observableContext - Context-Key do CAC. 
	 */
	public boolean removeAvailableCAC(String symbolicName, String observableContext) {
		Component cac = null;

		List<Component> cacs = availableCACs.get(observableContext);
		
		// Retorna caso não exista app com o nome
		for (Component c : cacs) {
			if(c.getId().equalsIgnoreCase(symbolicName)) {
				cac = c;
				break;
			}
		}
		if(!cac.getUsedBy().isEmpty()) { // Caso não seja usado por ninguem, ele poderá ser removido
			removeComponentIfPossible(cac);
			return true;
		} else { // Caso seja usado, não poderá ser removido
			return false;
		}
	}
	
	/**
	 * Adiciona um CAC.
	 * @param application - CAC que será adicionado.
	 * @param notifyChanges - Variável que determina se uma eventual mudança na zona de obseração desejada deve ser notificada.
	 * se true deseja ser notificado e false se não.
	 */
	private void addApplication(Component application, boolean notifyChanges) {
		boolean succes = putAtGraph(application);
		
		// Caso não tenha obtido sucesso em adicionar as dependências, remove as arestas e componentes adicionados ao grafo.
		if(!succes) {
			removeComponentIfPossible(application);
		} else {
			applications.add(application);
			
			if(notifyChanges) {
				// Notifica que houve uma alteração na configuração desejada da zona de observação 
				notifyChangesToObservers();
			}
		}
	}
	
	/**
	 * Adiciona um CAC ao grafo.
	 * @param CAC.
	 * @return True se foi colocado, false se não foi colocado.
	 */
	private boolean putAtGraph(Component component) {
		return putAtGraph(component, component.getInterestZone());
	}
	
	/**
	 * Adiciona um CAC ao grafo, considerando a zona de interesse passada por parâmetro como sendo do CAC.
	 * @param component - CAC
	 * @param interestZone - Zona de interesse a ser considerada para o CAC
	 * @return True se foi colocado, false se não foi colocado.
	 */
	private boolean putAtGraph(Component component, Set<String> interestZone) {
		if(interestZone != null && !interestZone.isEmpty()) {
			// Adiciona componentes de acordo com a zona de interesse do componente
			for (String contextData : interestZone) {

				// Recupera a lista de componentes que provêem o tipo de contexto requerido
				List<Component> candidateCACs = availableCACs.get(contextData);
				
				// Verifica se existe algum CAC candidato a preencher o interesse do componente a ser adicionado
				if(candidateCACs == null || candidateCACs.isEmpty())
					return false;
				
				boolean success = false;

				// TODO: Lógica que envolva QoS na escolha do melhor componente
				for (Component candidateCAC : candidateCACs) {
					success = putAtGraph(candidateCAC);
					
					// Caso ache uma dependência válida
					if(success) {
						// Configura os relacionamentos no gráfo
						component.addDependencies(candidateCAC);
						candidateCAC.addUsedBy(component);
						break;
					}
				}
				
				// Se não encontrar um componente cujas dependências são satisfeitas, retorna false
				if(!success) {
					removeComponentIfPossible(component);
					return false;
				}
			}
		}

		// Se não for uma aplicação, adiciona o componente ao conjunto de vértices de CACs
		if(!component.isApplication()) {
			observableZone.add(component);
			addedComponents.add(component);
		}
		return true;
	}
	
	/**
	 * Retira o CAC e suas dependencias do grafo, caso seja possível.
	 * @param CAC.
	 */
	private void removeComponentIfPossible(Component component) {
		// Verifica se o componente não é utilizado por alguem para proseguir com a remoção
		if(component.isApplication() || component.getUsedBy().isEmpty()) {
			
			// Remove o componente do conjunto de CACs do grafo
			if(!component.isApplication()) {
				observableZone.remove(component);
				removedComponents.add(component);
			}
			
			//Recupera todas as dependências do componente
			Set<Component> dependencies = component.getDependencies();
			
			if(!dependencies.isEmpty()) {
				for (Component dependence : dependencies) {
					// Retira o componente da lista de componentes que utilizam a dependência corrente
					dependence.getUsedBy().remove(component);
					
					// Tenta remover a dependência do grafo
					removeComponentIfPossible(dependence);
				}
				// Limpa as dependências do componente.
				dependencies.clear();
			}
		}
	}
	
	/**
	 * Notifica as mudanças a serem realizadas.	
	 */
	public void notifyChangesToObservers() {
		if(reasonerObserver != null) {
			Component[] added = new Component[addedComponents.size()];
			addedComponents.toArray(added);
			addedComponents.clear();
			
			Component[] removed = new Component[removedComponents.size()];
			removedComponents.toArray(removed);
			removedComponents.clear();
			
			reasonerObserver.notifyObservableZoneChanged(added, removed);
		}
	}
	
	@Override
	public void addApplicationInterestElement(String appId, String contextKey) {
		Component app = getApp(appId);

		if(app == null) { 
			// Cria a nova aplicação
			app = new Component(appId, true);
			app.addInterestZoneElement(contextKey);
			addApplication(app);		
		} else {
			app.addInterestZoneElement(contextKey);
			
			Set<String> interestZone = new HashSet<String>();
			interestZone.add(contextKey);
			
			boolean success = putAtGraph(app, interestZone);
			
			if(!success) {
				// TODO : Considerar: O que ocorre quando interesse não pode ser satisfeito
				;
			} else {
				// Notifica que houve uma alteração na configuração desejada da zona de observação 
				notifyChangesToObservers();
			}			
		}
	}

	@Override
	public void removeApplicationInterestElement(String appId, String contextKey) {
		Component app = getApp(appId);
		
		if(app == null)
			return;
		
		boolean hadElement = app.removeInterestZoneElement(contextKey);

		if(!hadElement)
			return;
		
		Set<Component> dependencies = app.getDependencies();
		
		// Busca qual dependência corresponde contextData removido
		Component componentToRemove = null;
		for (Component c : dependencies) {
			if(c.getContextProvided().equalsIgnoreCase(contextKey))
				componentToRemove = c;
		}
		
		if(componentToRemove != null) {
			// Remove as arestas entre o componente de origem do evento e sua antiga dependência
			app.getDependencies().remove(componentToRemove);
			componentToRemove.getUsedBy().remove(app);
			
			// Tenta remover a antiga dependência do grafo.
			removeComponentIfPossible(componentToRemove);
		}
		
		// Notifica que houve uma alteração na configuração desejada da zona de observação 
		notifyChangesToObservers();
	}
	
	/**
	 * Encontra um CAC de acordo com o nome simbolico.	
	 * @param appId - O nome simbolico.
	 * @return CAC se for encontrado, null caso contrário.
	 */ 
	private Component getApp(String appId) {
		for (Component app : applications) {
			if(app.getId().equalsIgnoreCase(appId))
				return app;
		}
		
		return null;
	}
	
	/**
	 * Mostra o estado da zona de observação
	 * @return String contendo o estado da zona de observação
	 */
	public String printDesiredOZ() {
		String r;

		Log.i("ADAPT TEST", "-----------------------------------------------------------------------------------------");
		Log.i("ADAPT TEST", "[[DESIRED ZO]]");
		Log.i("ADAPT TEST", "Size: " + observableZone.size());
		
		r = "[[DESIRED ZO]]\nSize: " + observableZone.size();
		
		for (Component c : observableZone) {
			Log.i("ADAPT TEST", c.getId());
			r += "" + c.getId();
		}
		
		return r;
	}
}
