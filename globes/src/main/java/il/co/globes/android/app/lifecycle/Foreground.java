package il.co.globes.android.app.lifecycle;

import il.co.globes.android.Definitions;
import il.co.globes.android.SplashScreen;
import il.co.globes.android.objects.GlobesURL;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//import com.google.android.gms.internal.fa;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

// work from 4.0
@SuppressLint("NewApi")
public class Foreground implements Application.ActivityLifecycleCallbacks 
{
	public static final long CHECK_DELAY = 1000;
	public static final int TIME_TO_BACK_MAIN_SCREEN_SEC = 300;

	public static final String TAG = "eli";

	public interface Listener 
	{
		public void onBecameForeground();
		public void onBecameBackground();
	}

	private static Foreground instance;

	private boolean foreground = false, paused = true;
	private Handler handler = new Handler();
	private List<Listener> listeners = new CopyOnWriteArrayList<Listener>();
	private Runnable check;
	ScheduledExecutorService scheduledExecutorService =  Executors.newScheduledThreadPool(1);
	private static Application application;
	private volatile int countTime;
	private int activityCount = 0;


	@SuppressLint("NewApi")
	public static Foreground init(Application application)
	{
		if (instance == null)
		{
			Foreground.application = application;
			instance = new Foreground();
			application.registerActivityLifecycleCallbacks(instance);
		}
		return instance;
	}

	public static Foreground get(Application application)
	{
		if (instance == null)
		{
			init(application);
		}
		return instance;
	}

	public static Foreground get(Context ctx)
	{
		if (instance == null)
		{
			Context appCtx = ctx.getApplicationContext();
			if (appCtx instanceof Application)
			{
				init((Application)appCtx);
			}
			//			throw new IllegalStateException(
			//					"Foreground is not initialised and " +
			//							"cannot obtain the Application object");
		}
		return instance;
	}

	//	public static Foreground get()
	//	{
	//		if (instance == null)
	//		{
	//			throw new IllegalStateException(
	//					"Foreground is not initialised - invoke " +
	//					"at least once with parameterised init/get");
	//		}
	//		return instance;
	//	}

	public boolean isForeground()
	{
		return foreground;
	}

	public boolean isBackground()
	{
		return !foreground;
	}

	public void addListener(Listener listener)
	{
		listeners.add(listener);
	}

	public void removeListener(Listener listener)
	{
		listeners.remove(listener);
	}

	@Override
	public void onActivityResumed(Activity activity)
	{
		paused = false;
		boolean wasBackground = !foreground;
		foreground = true;

		if (check != null)
			handler.removeCallbacks(check);

		if (wasBackground)
		{
			if (countTime > TIME_TO_BACK_MAIN_SCREEN_SEC)
				lunchLuncher(activity);
			stopTimeMesure();
			Log.i(TAG, "went foreground");
//			Definitions.appActive = true;

			for (Listener l : listeners)
			{
				try
				{
					l.onBecameForeground();
				} 
				catch (Exception exc)
				{
					Log.e(TAG, "Listener threw exception!", exc);
				}
			}
		}
		else
		{	
			if (countTime > TIME_TO_BACK_MAIN_SCREEN_SEC)
				lunchLuncher(activity);
			stopTimeMesure();
			Log.i(TAG, "still foreground");
//			Definitions.appActive = true;
		}
	}

	@Override
	public void onActivityPaused(final Activity activity) 
	{
		paused = true;

		if (check != null)
			handler.removeCallbacks(check);

		handler.postDelayed(check = new Runnable()
		{
			@Override
			public void run()
			{
				if (foreground && paused)
				{
					foreground = false;
					Log.i(TAG, "went background");
//					Definitions.appActive = false;
					//					Log.e("eli", activity.getClass().getSimpleName());
					//					if (activity.getClass().getSimpleName().equals("MainSlidingActivity"))
					//					{
					//						GlobesURL.clearURLs();
					//					}
					startTimeMesure();
					for (Listener l : listeners)
					{
						try 
						{
							l.onBecameBackground();
						} 
						catch (Exception exc)
						{
							Log.e(TAG, "Listener threw exception!", exc);
						}
					}
				} 
				else
				{
					if (countTime > TIME_TO_BACK_MAIN_SCREEN_SEC)
						lunchLuncher(activity);
					stopTimeMesure();
					Log.i(TAG, "still foreground");
//					Definitions.appActive = true;

				}
			}
		}, CHECK_DELAY);
	}

	private void startTimeMesure()
	{
		countTime = 0 ;
		scheduledExecutorService =  Executors.newScheduledThreadPool(1);
		scheduledExecutorService.scheduleAtFixedRate(new Runnable()
		{
			@Override
			public void run()
			{
				countTime++; 
				if (countTime > TIME_TO_BACK_MAIN_SCREEN_SEC)
				{
					Definitions.appActive = false;
				}
				Log.e(TAG, countTime+"");
			}
		}, 1, 1, TimeUnit.SECONDS);
	}

	private void stopTimeMesure()
	{
		countTime = 0;
		if (scheduledExecutorService != null)
		{
			scheduledExecutorService.shutdown();
		}
	}

	private void lunchLuncher(final Activity activity)
	{
//		Definitions.appActive = false;
		
		Log.e("alex", "SplashScreenShowInterstatial: lunchLuncher");
		
		GlobesURL.clearURLs();
		Intent intent = new Intent(activity, SplashScreen.class);
		if (!Definitions.PUSH_WAS_HANDLE)
		{
			intent.putExtra("pushNotificationDocID", Definitions.pushDid);
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); 
		application.startActivity(intent);
	}

	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState)
	{
//		Definitions.appActive = true;

//		Log.e("eli", "onActivityCreated " + Definitions.appActive);
	}

	@Override
	public void onActivityStarted(Activity activity) 
	{
		//		if (activity.getClass().getSimpleName().equals("MainSlidingActivity"))
		//		{
//		Definitions.MainSlidingActivityActive = true;
		//		}
	}

	@Override
	public void onActivityStopped(Activity activity) 
	{

	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) 
	{

	}

	@Override
	public void onActivityDestroyed(Activity activity) 
	{
		//		Definitions.appActive = false;
		//		if (activity.getClass().getSimpleName().equals("MainSlidingActivity"))
		//		{
//		Definitions.MainSlidingActivityActive = false;
		//		}
//		Log.e("eli", "onActivityDestroyed " + Definitions.appActive);
	}


}