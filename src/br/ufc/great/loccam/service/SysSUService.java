package br.ufc.great.loccam.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import br.ufc.great.download.CACDownload;
import br.ufc.great.loccam.LoCCAM;
import br.ufc.great.syssu.base.Pattern;
import br.ufc.great.syssu.base.Tuple;
import br.ufc.great.syssu.base.TupleSpaceException;
import br.ufc.great.syssu.base.TupleSpaceSecurityException;
import br.ufc.great.syssu.base.interfaces.IClientReaction;
import br.ufc.great.syssu.base.interfaces.IFilter;
import br.ufc.great.syssu.base.interfaces.ILocalDomain;
import br.ufc.great.syssu.base.interfaces.ISysSUService;
import br.ufc.great.syssu.ubibroker.LocalUbiBroker;
import br.ufc.loccam.adaptation.model.Component;
import br.ufc.loccam.adaptation.reasorner.AdaptationReasoner;
import br.ufc.loccam.cacmanager.CACManager;

public class SysSUService extends Service {

	private LocalUbiBroker localUbiBroker;
	private ILocalDomain domain;
	private LoCCAM loCCAM;
	private CACManager cacManager;
	private AdaptationReasoner adaptationReasoner;
	private ArrayList<String> list; 
	
	@Override
	public void onCreate() {
		super.onCreate();		
		list = new ArrayList<String>();
		
		try {
			localUbiBroker = LocalUbiBroker.createUbibroker();
			domain = (ILocalDomain)localUbiBroker.getDomain("loccam");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		cacManager = CACManager.getInstance(this);		
		adaptationReasoner = new AdaptationReasoner(cacManager.getListOfAvailableCACs());
		
		loCCAM = new LoCCAM(domain, cacManager, adaptationReasoner);
		
		// Notificação
		//NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setContentTitle("SysSU Service");
		//startForeground(1, mBuilder.build());
		Toast.makeText(getApplicationContext(), "SysSU Service started.", Toast.LENGTH_SHORT).show();		
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return new ISysSUService.Stub() {
			
			public void put(Tuple tuple) throws RemoteException {
				try {
					domain.put(tuple, null);
				} catch (Exception e) {
					e.printStackTrace(); 
				}
			}

			public void putCAC(List<String> list) throws RemoteException {
				try {
					new CACDownload().instalarCACs(list);					
				} catch (Exception e) {
					e.printStackTrace(); 
				}
			}
			
			public List<Tuple> read(Pattern pattern, IFilter filter) throws RemoteException {
				List<Tuple> tuples = null;
				try {
					tuples = domain.read(pattern, filter, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return tuples; 
			}

			public List<Tuple> take(Pattern pattern, IFilter filter) throws RemoteException {
				List<Tuple> tuples = null;
				
				try {
					tuples = domain.take(pattern, filter, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return tuples; 
			}
			
			public String subscribe(IClientReaction reaction, String event, Pattern pattern, IFilter filter) throws RemoteException {
				 ClientReactionWrapper clientReactionWrapper = new ClientReactionWrapper(reaction, pattern, filter);
				 
				 try {
					clientReactionWrapper.setId(domain.subscribe(clientReactionWrapper, event, ""));
				 } catch (Exception e) { 
					e.printStackTrace();
				 }	 
				 
				 return clientReactionWrapper.getId();
			}
			
			@Override
			public void unSubscribe(String id) throws RemoteException {
				try {
					domain.unsubscribe(id, "");
				} catch (TupleSpaceException e) {
					e.printStackTrace();
				} catch (TupleSpaceSecurityException e) {
					e.printStackTrace();
				}
			}
			
			public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
				boolean r = false;
				try {
					r = super.onTransact(code, data, reply, flags);
				} catch (RuntimeException e) {
					Log.w("MyClass", "Unexpected remote exception", e);
					throw e;
				}

				return r;
			}

			@Override
			public String printLoCCAMState() throws RemoteException {
				return loCCAM.printState();
			}

			@Override
			public void putApp(String arg0) throws RemoteException {
				if(!list.contains(arg0))
					list.add(arg0);
			}

			@Override
			public void removeApp(String arg0) throws RemoteException {
				list.remove(arg0);
				isToStopLoCCAM();
			}
		};
	}
	
	public void isToStopLoCCAM() {
		System.out.println(list);
		if(list.isEmpty())
			stopSelf();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Toast.makeText(getApplicationContext(), "SysSU Service stopped.", Toast.LENGTH_SHORT).show();
	}
}