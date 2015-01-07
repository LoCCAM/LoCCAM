package br.ufc.loccam.adaptation.model;

import java.util.HashSet;
import java.util.Set;

public class Component {

	private String id;
	private String fileName;
	
	private Set<String> interestZone;
	private String contextProvided;

	private Set<Component> dependencies;
	private Set<Component> usedBy;
	
	private boolean application;
	
	/**
	 * Construtor de Componente 
	 * @param id Nome simbolico
	 * @param application True se estiver em uso e False se não estiver em uso 
	 */
	public Component(String id, boolean application) {
		this(id, application, null);
	}
	
	/**
	 * Construtor de Componente
	 * @param id Nome simbolico
	 * @param application True se estiver em uso e False se não estiver em uso 
	 * @param observableContext Tipo de informação contextual
	 */
	public Component(String id, boolean application, String observableContext) {
		this.id = id;
		this.contextProvided = observableContext;
		this.application = application;
		
		dependencies = new HashSet<Component>();
		usedBy = new HashSet<Component>();
		
		interestZone = new HashSet<String>();
	}
	
	/**
	 * Retorna o nome simbolico
	 * @return String contendo o nome simbolico
	 */
	public String getId() {
		return id;
	}

	/**
	 * Seta o nome simbolico
	 * @param id Nome simbolico
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Retorna o nome do arquivo
	 * @return String contendo o nome do arquivo
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Seta o nome do arquivo
	 * @param fileName Nome do arquivo
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * Retorna a zona de interesse
	 * @return Estrutura de dados de string contendo a zona de interesse
	 */
	public Set<String> getInterestZone() {
		return interestZone;
	}

	/**
	 * Adiciona um interesse a zona de interesses a partir de uma informação contextual
	 * @param contextKey Tipo de informação contextual
	 */
	public void addInterestZoneElement(String contextKey) {
		interestZone.add(contextKey);
	}
	
	/**
	 * Remove um interesse da zona de interesses a partir de uma informação contextual
	 * @param contextKey Tipo de informação contextual
	 * @return Retorna True se for removido e false se nao for removido
	 */
	public boolean removeInterestZoneElement(String contextKey) {
		return interestZone.remove(contextKey);
	}
	
	/**
	 * Retorna a informação contextual
	 * @return Uma string contendo a informação contextual
	 */
	public String getContextProvided() {
		return contextProvided;
	}

	/**
	 * Seta a informação contextual 
	 * @param contextProvided String contendo o tipo de informação contextual
	 */
	public void setContextProvided(String contextProvided) {
		this.contextProvided = contextProvided;
	}
	
	/**
	 * Configura os relacionamentos no gráfo. Adiciona dependencias de um CAC candidato 
	 * @param c o componente CAC
	 */
	public void addDependencies(Component c) {
		dependencies.add(c);
	}
	
	/**
	 * Configura os relacionamentos no gráfo. Retorna as dependencias de um CAC candidato 
	 * @return Retorna uma estrutura de dados contendo as dependencias de um CAC candidato
	 */
	public Set<Component> getDependencies() {
		return dependencies;
	}

	/**
	 * Configura os relacionamentos no gráfo. O CAC candidato é adicionado ao uso 
	 * @param c CAC que foi adicionado as dependencias
	 */
	public void addUsedBy(Component c) {
		usedBy.add(c);
	}

	/**
	 * Retorna uma estrutura de dados que está em uso.
	 * @return Retorna uma estrutura de dados contendo os CAC's em uso. 
	 */
	public Set<Component> getUsedBy() {
		return usedBy;
	}
	
	/**
	 * Seta o estado do componente
	 * @param application True se estiver em uso e False se não estiver em uso
	 */
	public void setApplication(boolean application) {
		this.application = application;
	}
	
	/**
	 * Verifica o estado do componente
	 * @return True se estiver em uso e False se não estiver em uso
	 */
	public boolean isApplication() {
		return application;
	}
}
