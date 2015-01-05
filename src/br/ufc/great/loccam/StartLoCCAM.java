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
	 * M�todo usado para iniciar o LoCCAM quando utilizado como biblioteca
	 * @param activity Da aplica��o
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
	 * M�todo usado para iniciar o LoCCAM quando utilizado como biblioteca
	 * @param context Contexto da aplica��o 
	 * @param stringContext Indica o tipo de informa��o contextual
	 * @param appId Id da aplica��o
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
	 * M�todo usado para iniciar o LoCCAM quando utilizado como biblioteca	
	 * @param context Contexto da aplica��o 
	 * @param appId Id da aplica��o
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
	 * M�todo usado para colocar um interesse
	 * @param stringContext Indica o tipo de informa��o contextual
	 */
	public void putLoCCAM(String stringContext) {
		Tuple tupla = (Tuple) new Tuple().addField(APPID, RLOCCAM).addField(
				INTERESTELEMENT, "context." + stringContext);
		sysSUStart.put(tupla);
	}

	/**
	 * Retorna uma lista de tuplas de um contexto
	 * @param stringContext Indica o tipo de informa��o contextual
	 * @return Uma lista de tuplas
	 */
	public List<Tuple> readingLoCCAM(String stringContext) {
		Pattern pattern = (Pattern) new Pattern().addField("ContextKey",
				"context." + stringContext);
		return sysSUStart.read(pattern, null);
	}

	/**
	 * Serve para retirar um interesse  
	 * @param stringContext Indica o tipo de informa��o contextual
	 */
	public void takeLoCCAM(String stringContext) {
		Pattern pattern = (Pattern) new Pattern().addField(APPID, RLOCCAM)
				.addField(INTERESTELEMENT, "context." + stringContext);
		sysSUStart.take(pattern, null);
	}

	/**
	 * M�todo que retorna leituras ass�ncronas, na forma de subscriptions, 
	 * onde a aplica��o informa que deseja ser notificada quando houver novas 
	 * informa��es contextuais dispon�veis que sejam do seu interesse.
	 * @param reaction � chamado quando a aplica��o for notificada com novas informa��es 
	 * contextuais de interesse
	 * @param stringContext Indica qual o tipo de informa��o contextual de interesse 
	 * @param event Indica que a aplica��o deseja ser notificada quando novas informa��es 
	 * contextuais forem publicadas
	 * @param ifilter Representa uma implementa��o da interface que faz com que a aplica��o 
	 * apenas seja notificada de fato quando as informa��es passarem por este filtro
	 */
	public String subscribeLoCCAM(IClientReaction reaction, String stringContext,
			String event, IFilter ifilter) {
		Pattern pattern = (Pattern) new Pattern().addField("ContextKey",
				"context." + stringContext);

		return sysSUStart.subscribe(reaction, event, pattern, ifilter);
	}

	/**
	 * M�todo que cancela um subscribe
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
	 * @param stringContext Retirar o tipo de informa��o contextual 
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
	 * Ler estado de uma informa��o contextual
	 * @param context Indica o tipo de informa��o contextual
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
	 * M�todo que retorna uma lista de CAC's de um contexto
	 * @param context Indica o tipo de informa��o contextual
	 * @return Lista com os CAC's
	 */
	public Map<String, List<Component>> getCACList(Context context){
		return CACManager.getInstance(context).getListOfAvailableCACs();
	}
	
	/**
	 * M�todo que checa o estado do servi�o do LoCCAM
	 * @param activity A activity da aplica��o
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
