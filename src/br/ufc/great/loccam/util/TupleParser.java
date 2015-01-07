package br.ufc.great.loccam.util;

import java.util.ArrayList;
import java.util.List;

import br.ufc.great.syssu.base.Tuple;
import br.ufc.loccam.adaptation.model.Component;

/**
 * Classe para analise das Tuplas
 * 
 */
public class TupleParser {

	/**
	 * Retorna uma lista de componentes que contém os interesses publicados no SysSU
	 * @param tuples - Lista contendo a tuplas de interesse no SysSU
	 * @return A lista de componentes
	 */
	public static List<Component> parseToApplications(List<Tuple> tuples) {
		ArrayList<Component> applications = new ArrayList<Component>();
		
		for (Tuple tuple : tuples) {
			parseNewInterestToApplication(applications, tuple);
		}
		
		return applications;
	}
	
	/**
	 * Adiciona um novo interesse a um componente. Caso o componente não exista, ele será criado com esse interesse.
	 * @param applications - Tista contendo os componentes
	 * @param tuple - Tupla contendo o interesse
	 * @return Lista de componentes
	 */
	public static List<Component> parseNewInterestToApplication(List<Component> applications, Tuple tuple) {
		String applicationId = (String)tuple.getField(0).getValue();
		String contextData = (String)tuple.getField(1).getValue();
		
		for (Component application: applications) {
			
			if(application.getId().equalsIgnoreCase(applicationId)) {
				application.addInterestZoneElement(contextData);
				return applications;
			}
		}
		
		Component app = new Component(applicationId, true);
		app.addInterestZoneElement(contextData);
		applications.add(app);
		
		return applications;
	}
	
	/**
	 * Retira o interesse de um componente. 
	 * @param applications - Lista contendo os componentes
	 * @param tuple - Tupla contendo o interesse
	 * @return Lista de componentes
	 */
	public static List<Component> parseRemovedInterestToApplication(List<Component> applications, Tuple tuple) {
		Component app = null;
		String applicationId = (String)tuple.getField(0).getValue();
		String contextData = (String)tuple.getField(1).getValue();
		
		for (Component application: applications) {
			if(application.getId().equalsIgnoreCase(applicationId)) {
				app = application;
				break;
			}
		}
		
		if(app != null) {
			app.removeInterestZoneElement(contextData);
			if(app.getInterestZone().isEmpty())
				applications.remove(app);
		}
		
		return applications;
	}	
}
