package com.metaisle.profiler;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.metaisle.profiler.collector.BasicCollector;
import com.metaisle.profiler.collector.continuous.Accelerometer;
import com.metaisle.profiler.collector.eventdriven.ActionMiscellaneous;
import com.metaisle.profiler.collector.eventdriven.Cellular;
import com.metaisle.profiler.collector.eventdriven.Connectivity;
import com.metaisle.profiler.collector.periodical.ActivityCollector;
import com.metaisle.profiler.collector.periodical.Battery;
import com.metaisle.profiler.collector.periodical.GPSCollector;
import com.metaisle.profiler.collector.periodical.NeighborCellCollector;
import com.metaisle.profiler.collector.periodical.NetworkTraffic;
import com.metaisle.profiler.collector.periodical.TaskCollector;
import com.metaisle.profiler.collector.periodical.WiFiCollector;
import com.metaisle.util.*;

public class CollectorService extends Service {
	private final String TAG = "Collector Service";
	private final IBinder mBinder = new CollectorBinder();
	private boolean _isRunning = false;
	private Context mContext;

	private ArrayList<BasicCollector> Collectors;

	private PowerManager.WakeLock mWakelock;
	private AlarmManager mAlarmManager;
	private AlarmReceiver mAlarmReceiver;
	private IntentReceiver mUserPresentReceiver;
	private Calendar mCalendar;
	private final int CollectTime = 10; // 10 sec
	private final int DutyTime = 5 * 60; // 5 min
	public static final String ACTION_START_COLLECT = "PhotoSync.Profiler.STARTCOLLECT";
	public static final String ACTION_PAUSE_COLLECT = "PhotoSync.Profiler.PAUSECOLLECT";
	private PendingIntent StartIntent;
	private PendingIntent PauseIntent;
	private int profile_level = 1; // profiling level
	private ConfigFileManager conf;
	private UploadStatisticFile mUploadStatisticFile;

	@Override
	public void onCreate() {
		super.onCreate();
		conf = new ConfigFileManager(this);
		profile_level = conf.getProfiling_level();
		mContext = this.getBaseContext();
		mUploadStatisticFile = new UploadStatisticFile(mContext);
		Collectors = new ArrayList<BasicCollector>();
		mAlarmManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
				"ContextCollector");
		mAlarmReceiver = new AlarmReceiver();
		mCalendar = Calendar.getInstance();

		IntentFilter myFilter = new IntentFilter();
		myFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		myFilter.addAction(Intent.ACTION_SCREEN_ON);
		myFilter.addAction(Intent.ACTION_SCREEN_OFF);
		myFilter.addAction(Intent.ACTION_ANSWER);
		myFilter.addAction(Intent.ACTION_CAMERA_BUTTON);
		myFilter.addAction(Intent.ACTION_DOCK_EVENT);
		myFilter.addAction(Intent.ACTION_HEADSET_PLUG);
		myFilter.addAction(Intent.ACTION_USER_PRESENT);
		myFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		myFilter.addAction(Intent.ACTION_POWER_CONNECTED);
		myFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
		myFilter.addAction(Intent.ACTION_TIME_CHANGED);
		myFilter.addAction(ACTION_START_COLLECT);
		myFilter.addAction(ACTION_PAUSE_COLLECT);
		registerReceiver(mAlarmReceiver, myFilter);

		mUserPresentReceiver = new IntentReceiver();
		myFilter = new IntentFilter();
		myFilter.addAction(Intent.ACTION_USER_PRESENT);
		registerReceiver(mUserPresentReceiver, myFilter);

		Intent intent = new Intent(ACTION_START_COLLECT);
		StartIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
		intent = new Intent(ACTION_PAUSE_COLLECT);
		PauseIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

		// Add context collector into list
		try {
			// Continuous
			try {
				AddCollector(new Accelerometer("Acc", mContext));
			} catch (Exception ex) {
			}
			// try {
			// AddCollector(new Compass("Compass", mContext));
			// } catch (Exception ex) {
			// }
			//
			// // Disable Gyro because its big size
			// try {
			// AddCollector(new Gyroscope("Gyro", mContext));
			// } catch (Exception ex) {
			// }
			//
			// try {
			// AddCollector(new Light("Light", mContext));
			// } catch (Exception ex) {
			// }
			//
			// try {
			// AddCollector(new MagneticField("MagneticField", mContext));
			// } catch (Exception ex) {
			// }
			//
			// try {
			// AddCollector(new Pressure("Pressure", mContext));
			// } catch (Exception ex) {
			// }
			// try {
			// AddCollector(new Proximity("Proxmity", mContext));
			// } catch (Exception ex) {
			// }
			// try {
			// AddCollector(new Temperature("Temperature", mContext));
			// } catch (Exception ex) {
			// }

			// Event Driven
			try {
				AddCollector(new Cellular("Cellular", mContext));
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try {
				AddCollector(new ActionMiscellaneous("ActionIntents", mContext));
			} catch (Exception ex) {
			}

			try {
				AddCollector(new Connectivity("Connectivity", mContext));
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			// try {
			// AddCollector(new Newphoto("Newphoto", mContext));
			// } catch (Exception ex) {
			// Crittercism.logHandledException(ex);
			// }
			/*
			 * try{ AddCollector(new SMS("SMS", mContext)); }catch(Exception
			 * ex){ Crittercism.logHandledException(ex); }
			 */

			// Periodical
			try {
				AddCollector(new ActivityCollector("Activity", mContext));
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			// try {
			// AddCollector(new CPUUsageCollector("CpuUsage", mContext));
			// } catch (Exception ex) {
			// ex.printStackTrace();
			// }

			try {
				AddCollector(new GPSCollector("GPS", mContext));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				AddCollector(new WiFiCollector("WiFi", mContext));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			/*
			 * try{ AddCollector(new BrowserHistory("BrowserHistory",
			 * mContext)); }catch(Exception ex){
			 * Crittercism.logHandledException(ex); }
			 */
			/*
			 * try{ AddCollector(new Bluetooth("Bluetooth", mContext));
			 * }catch(Exception ex){ Crittercism.logHandledException(ex); }
			 */

			try {
				AddCollector(new NetworkTraffic("NetworkTraffic", mContext));
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try {
				AddCollector(new Battery("Battery", mContext));
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try {
				AddCollector(new TaskCollector("TaskInfo", mContext));
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try {
				AddCollector(new NeighborCellCollector("NeighborCell", mContext));
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// Initialize collector
		for (BasicCollector collector : Collectors) {
			try {
				if (collector.isSupported()) {
					collector.SetWritable(true);
					collector.SetEnable(true);
					collector.Initialize();
				}
			} catch (Exception ex) {
			}
		}

		_isRunning = false;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		StartCollect();
		return START_STICKY;

	}

	@Override
	public void onDestroy() {
		Intent i = new Intent();
		i.setAction(RestartReceiver.CUSTOM_INTENT);
		this.sendBroadcast(i);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	public void AddCollector(BasicCollector c) {
		Collectors.add(c);
	}

	// Instance of the Service is now in MyBinder
	public class CollectorBinder extends Binder {

		CollectorService getService() {

			return CollectorService.this;
		}
	}

	public boolean isRunning() {
		return _isRunning;
	}

	public void StartCollect() {

		if (_isRunning)
			return;
		_isRunning = true;
		mCalendar.setTimeInMillis(System.currentTimeMillis());
		mCalendar.add(Calendar.SECOND, 5);
		mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				mCalendar.getTimeInMillis(), DutyTime * 1000, StartIntent);
		// mAlarmManager.set(AlarmManager.RTC_WAKEUP,
		// mCalendar.getTimeInMillis(), StartIntent);
		Util.log("Start Collecting context and setup " + DutyTime
				+ " seconds timer to repeat");
	}

	public void StopCollect() {
		_isRunning = false;
		for (BasicCollector collector : Collectors) {
			if (collector.isSupported()) {
				try {
					collector.Stop();
				} catch (Exception ex) {
				}
			}
		}
		mAlarmManager.cancel(StartIntent);
	}

	public final class IntentReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
				/*
				 * Intent popup = new Intent(context,
				 * BehaviorSelectionActivity.class);
				 * popup.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
				 * Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				 * context.startActivity(popup);
				 */
			} else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				for (BasicCollector collector : Collectors) {
					if (collector.isSupported()
							&& collector.getClass().equals(GPSCollector.class)) {
						try {
							collector.Stop();
							break;
						} catch (Exception ex) {
						}
					}
				}
			}
		}

	}

	public final class AlarmReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			Util.log("Received " + intent.getAction());

			if (intent.getAction().equals(ACTION_PAUSE_COLLECT)) {
				Util.log("Pause Collect by " + intent.getAction());
				for (BasicCollector c : Collectors) {
					if (c.isEnable() && c.isRunning()) {
						Util.log("Pause Collector:"
								+ c.getClass().getSimpleName());
						try {
							c.Pause();
							c.Flush();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				if (mWakelock.isHeld())
					mWakelock.release();
			} else {
				// Acquire the partial wake lock

				if (mWakelock.isHeld())
					return;
				Util.log("===============================================================");
				Util.log("Start Collect by " + intent.getAction());
				mWakelock.acquire();

				for (BasicCollector c : Collectors) {
					if (c.isEnable() && !c.isRunning()) {
						Util.log("Start Collector:"
								+ c.getClass().getSimpleName());
						try {
							c.Start();
						} catch (Exception ex) {
							ex.printStackTrace();
						}

					}
				}

				mCalendar.setTimeInMillis(System.currentTimeMillis());
				mCalendar.add(Calendar.SECOND, CollectTime);
				mAlarmManager.set(AlarmManager.RTC_WAKEUP,
						mCalendar.getTimeInMillis(), PauseIntent);

			}
		}
	}

}
