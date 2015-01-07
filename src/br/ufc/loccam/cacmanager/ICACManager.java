package br.ufc.loccam.cacmanager;

import java.util.List;
import java.util.Map;

import br.ufc.loccam.adaptation.model.Component;

public interface ICACManager {

		/**
		 * Inicia um CAC.
		 * @param CAC.
		 */
		public void startCAC(Component cac);

		/**
		 * Para um CAC.
		 * @param CAC.
		 */
		public void stopCAC(Component cac);
		
		/**
		 * Instala um CAC.
		 * @param CAC.
		 */
		public void installCAC(Component cac);
		
		/**
		 * Desinstala um CAC.
		 * @param CAC.
		 */
		public void uninstallCAC(Component cac);
		
		/**
		 * Retorna a lista de todos os componentes CAC disponíveis no repositório local.
		 * @return lista de todos os componentes CAC disponíveis no repositório local.
		 */
		public Map<String, List<Component>> getListOfAvailableCACs();
}
