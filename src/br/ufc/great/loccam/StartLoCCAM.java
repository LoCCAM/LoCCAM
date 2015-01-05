package br.ufc.great.loccam;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import br.ufc.great.loccam.service.SysSUService;
import br.ufc.great.loccam.service.SysSUStart;
import br.ufc.great.syssu.base.Pattern;
import br.ufc.great.syssu.base.Tuple;
import br.ufc.great.syssu.base.interfaces.IClientReaction;
import br.ufc.great.syssu.base.interfaces.IFilter;
import br.ufc.loccam.adaptation.model.Component;
import br.ufc.loccam.adaptation.reasorner.AdaptationReasoner;
import br.ufc.loccam.cacmanager.CACManager;

public class StartLoCCAM {

	// Service State
	public boolean isLoCCAMRunning = false;

	// Tag of Service
	private static final String TAG = "StartLoccam";

	//Objeto para startar o LoCCAM
	private SysSUStart sysSUStart;

	//Strings de argumentos para startar o LoCCAM
	private final static String INTERESTELEMENT = "InterestElement";
	private final static String APPID = "AppId";
	private final static String RLOCCAM = "Rloccan";

	/**
	 * Método usado para iniciar o LoCCAM quando utilizado como biblioteca
	 * @param activity Da aplicação
	 */
	public void startLoCCAM(Activity activity) {

		if (!isLoCCAMRunning) {
			Intent startServiceIntent = new Intent(activity, SysSUService.class);
			activity.startService(startServiceIntent);

		} else {
			Intent startServiceIntent = new Intent(activity, SysSUService.class);
			activity.stopService(startServiceIntent);
		}

		isLoCCAMRunning = checkServiceState(activity);
	}

	/**
	 * Método usado para iniciar o LoCCAM quando utilizado como biblioteca
	 * @param context Contexto da aplicação 
	 * @param stringContext Indica o tipo de informação contextual
	 * @param appId Id da aplicação
	 */
	public void startLoCCAM(Context context, String stringContext, String appId) {
		try{
			if (!SysSUStart.isRunning()){
				sysSUStart = new SysSUStart(context, appId);
				Log.w(TAG, "syssu start not running");
			} else {
				Log.w(TAG, "syssu start running");
				if (sysSUStart == null){
					Log.w(TAG, "syssu start null");
					sysSUStart = SysSUStart.getInstance();
				}
			}
		} catch (Exception e){
			Log.e(TAG, e.getLocalizedMessage());
		}
		if(!stringContext.equals(""))
			putLoCCAM(stringContext);
	}

	/**
	 * Método usado para iniciar o LoCCAM quando utilizado como biblioteca	
	 * @param context Contexto da aplicação 
	 * @param appId Id da aplicação
	 */
	public void startLoCAMM(Context context, String appId){
		try{
			if (!SysSUStart.isRunning()){
				sysSUStart = new SysSUStart(context, appId);
				Log.w(TAG, "syssu start not running");
			} else {
				Log.w(TAG, "syssu start running");
				if (sysSUStart == null){
					Log.w(TAG, "syssu start null");
					sysSUStart = SysSUStart.getInstance();
				}
			}
		} catch (Exception e){
			Log.e(TAG, e.getLocalizedMessage());
		}
	}

	/**
	 * Método usado para colocar um interesse
	 * @param stringContext Indica o tipo de informação contextual
	 */
	public void putLoCCAM(String stringContext) {
		Tuple tupla = (Tuple) new Tuple().addField(APPID, RLOCCAM).addField(
				INTERESTELEMENT, "context." + stringContext);
		sysSUStart.put(tupla);
	}

	/**
	 * Retorna uma lista de tuplas de um contexto
	 * @param stringContext Indica o tipo de informação contextual
	 * @return Uma lista de tuplas
	 */
	public List<Tuple> readingLoCCAM(String stringContext) {
		Pattern pattern = (Pattern) new Pattern().addField("ContextKey",
				"context." + stringContext);
		return sysSUStart.read(pattern, null);
	}

	/**
	 * Serve para retirar um interesse  
	 * @param stringContext Indica o tipo de informação contextual
	 */
	public void takeLoCCAM(String stringContext) {
		Pattern pattern = (Pattern) new Pattern().addField(APPID, RLOCCAM)
				.addField(INTERESTELEMENT, "context." + stringContext);
		sysSUStart.take(pattern, null);
	}

	/**
	 * Método que retorna leituras assíncronas, na forma de subscriptions, 
	 * onde a aplicação informa que deseja ser notificada quando houver novas 
	 * informações contextuais disponíveis que sejam do seu interesse.
	 * @param reaction É chamado quando a aplicação for notificada com novas informações 
	 * contextuais de interesse
	 * @param stringContext Indica qual o tipo de informação contextual de interesse 
	 * @param event Indica que a aplicação deseja ser notificada quando novas informações 
	 * contextuais forem publicadas
	 * @param ifilter Representa uma implementação da interface que faz com que a aplicação 
	 * apenas seja notificada de fato quando as informações passarem por este filtro
	 */
	public String subscribeLoCCAM(IClientReaction reaction, String stringContext,
			String event, IFilter ifilter) {
		Pattern pattern = (Pattern) new Pattern().addField("ContextKey",
				"context." + stringContext);

		return sysSUStart.subscribe(reaction, event, pattern, ifilter);
	}

	/**
	 * Método que cancela um subscribe
	 * @param idSubscribe String que foi obtida quando o subscribe() correspondente foi realizado
	 */
	public void unsbscribeLoCCAM(String idSubscribe){
		sysSUStart.unSubscribe(idSubscribe);
	}

	/**
	 * Implementar o removeCACList()
	 */
	public void removeCACList() {
		//TODO implementar
	}

	/**
	 * implementar o fillCACList()
	 */
	public void fillCACList() {
		//TODO implementar
	}

	/**
	 * Retira um interesse
	 * @param stringContext Retirar o tipo de informação contextual 
	 */
	public void stopLoCCAM(String stringContext){
		//TODO implementar
		Pattern pattern = (Pattern) new Pattern().addField(APPID, RLOCCAM)
				.addField(INTERESTELEMENT, "context." + stringContext);
		sysSUStart.take(pattern, null);
	}

	/**
	 * Retira todos os interesses
	 */
	public void stopAll(){
		Log.d(TAG, "stopAll");
		List<String> ints =	sysSUStart.getInterests();
		
		Log.d(TAG, "stopAll: "+ints.size());
		for (String in: ints){
			Log.d(TAG, "StopAll: "+in);
			sysSUStart.takeInterest(in);
		}
		sysSUStart.takeAllInterests();
		sysSUStart.unSubscribeAll();
	}

	/**
	 * Ler estado de uma informação contextual
	 * @param context Indica o tipo de informação contextual
	 * @return Uma string contendo o estado
	 */
	public String readState(Context context) {
		CACManager cacManager = CACManager.getInstance(context);
		AdaptationReasoner adptReasoner = new AdaptationReasoner(CACManager
				.getInstance(context).getListOfAvailableCACs());
		String r;
		r = ((CACManager) cacManager).printBundlesState();
		r += ((AdaptationReasoner) adptReasoner).printDesiredOZ();

		return r;
	}
	
	/**
	 * Método que retorna uma lista de CAC's de um contexto
	 * @param context Indica o tipo de informação contextual
	 * @return Lista com os CAC's
	 */
	public Map<String, List<Component>> getCACList(Context context){
		return CACManager.getInstance(context).getListOfAvailableCACs();
	}
	
	/**
	 * Método que checa o estado do serviço do LoCCAM
	 * @param activity A activity da aplicação
	 * @return True se estiver operando e False se nao estiver operando.
	 */
	public boolean checkServiceState(final Activity activity) {
		ActivityManager manager = (ActivityManager) activity
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			Log.d("Services", service.service.getClassName().toString());
			if (SysSUService.class.getName().equals(
					service.service.getClassName())) {
				Log.d(TAG, "true");
				return true;
			}
		}
		Log.d(TAG, "false");
		return false;
	}
}
