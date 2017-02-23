package com.metaisle.profiler.collector.periodical;

import java.io.IOException;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.metaisle.profiler.collector.PeriodicalCollector;
import com.metaisle.util.Util;

public class GPSCollector extends PeriodicalCollector {

	private LocationManager mLocationManager;
	protected static final String TAG = "GPS Collector";
	private Criteria fineCriteria = new Criteria();
	private Criteria coarseCriteria = new Criteria();
	private FineLocationListener mFineLocationListener;
	private CoarseLocationListener mCoarseLocationListener;
	private PassiveLocationListener mPassiveLocationReceiver;
	private PendingIntent PauseIntent;
	private AlarmManager mAlarmManager;
	private static final String ACTION_STOP_GPS_COLLECT = "philip.padslab.STARTGPSCOLLECT";
	private GPSAlarmReceiver mAlarmReceiver;

	private Location lastKnownLocation;
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	private static final double THIRTY_MINUTES = 1000 * 60 * 30;
	private Calendar LastScan = null;

	public GPSCollector(String filename, Context mContext) throws IOException {
		super(filename, mContext);
		// TODO Auto-generated constructor stub
		lastKnownLocation = null;

		// Setup alarm service
		mAlarmManager = (AlarmManager) mContext
				.getSystemService(Service.ALARM_SERVICE);
		Intent intent = new Intent(ACTION_STOP_GPS_COLLECT);
		PauseIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
		mAlarmReceiver = new GPSAlarmReceiver();
		IntentFilter myFilter = new IntentFilter();
		myFilter.addAction(ACTION_STOP_GPS_COLLECT);
		mContext.registerReceiver(mAlarmReceiver, myFilter);

		// Create location manager
		mLocationManager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);

		/*
		 * if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER
		 * )) { Intent myIntent = new Intent( Settings.ACTION_SECURITY_SETTINGS
		 * ); mContext.startActivity(myIntent); }
		 */
		// Then, print the example
		WriteLine("Time,Latitude,Longitude");
		fineCriteria.setAccuracy(Criteria.ACCURACY_FINE);
		coarseCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
		mFineLocationListener = new FineLocationListener();
		mCoarseLocationListener = new CoarseLocationListener();
		mPassiveLocationReceiver = new PassiveLocationListener();

		// this.dutyCycle = 30*60*1000;
	}

	protected boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (location == null) {
			return false;
		}
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	@Override
	protected void _Start() {
		if (LastScan == null) {
			Util.log( "LastScan:"+LastScan);
			Util.log( "start scan GPS");
			LastScan = Calendar.getInstance();
			LastScan.setTimeInMillis(System.currentTimeMillis());
			AskRequest();
		}
		double delta = ((double) (System.currentTimeMillis() - LastScan
				.getTimeInMillis()) / THIRTY_MINUTES);
		if (delta >= 1) {
			Util.log( "LastScan:"+LastScan);
			Util.log( "start scan GPS");
			Util.log( "delta:"+delta);
			LastScan.setTimeInMillis(System.currentTimeMillis());
		AskRequest();
	}
	}

	@Override
	protected void _Stop() {
		mLocationManager.removeUpdates(mFineLocationListener);
		mLocationManager.removeUpdates(mCoarseLocationListener);
	}

	@Override
	public boolean isSupported() {
		return true;
	}

	public class FineLocationListener implements LocationListener {

		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			Util.log( location.getTime() + "," + location.getLatitude() + ","
					+ location.getLongitude());
			try {
				if (isBetterLocation(location, lastKnownLocation)) {
					lastKnownLocation = location;
					WriteLine(location.getTime() + "," + location.getLatitude()
							+ "," + location.getLongitude() + ","
							+ location.getProvider());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			try {
				WriteLineWithTime(String.valueOf(status));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public class CoarseLocationListener implements LocationListener {

		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			Util.log( location.getTime() + "," + location.getLatitude() + ","
					+ location.getLongitude());
			try {
				if (isBetterLocation(location, lastKnownLocation)) {
					lastKnownLocation = location;
					WriteLine(location.getTime() + "," + location.getLatitude()
							+ "," + location.getLongitude() + ","
							+ location.getProvider());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

			try {
				WriteLineWithTime(String.valueOf(status));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public class PassiveLocationListener implements LocationListener {

		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			Util.log( location.getTime() + "," + location.getLatitude() + ","
					+ location.getLongitude() + "," + location.getProvider());
			try {
				if (isBetterLocation(location, lastKnownLocation)) {
					lastKnownLocation = location;
					WriteLine(location.getTime() + "," + location.getLatitude()
							+ "," + location.getLongitude());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

			try {
				WriteLineWithTime(String.valueOf(status));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public final class GPSAlarmReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Util.log( "GPS Timeout, stop collection");
			_Stop();
		}
	}

	public void AskRequest() {
		if (running) {
			Calendar calendar = Calendar.getInstance();
			String provider;
			try {
				Util.log( "Send request of location updates");
				// provider = mLocationManager.getBestProvider(fineCriteria,
				// true);
//				if (provider != null)
//					mLocationManager.requestLocationUpdates(provider,
//							dutyCycle, 10f, mFineLocationListener);

				provider = mLocationManager.getBestProvider(coarseCriteria,
						true);
				if (provider != null)
					mLocationManager.requestLocationUpdates(provider,
							dutyCycle, 10f, mCoarseLocationListener);

				mLocationManager.requestLocationUpdates(
						LocationManager.PASSIVE_PROVIDER, dutyCycle, 10f,
						mPassiveLocationReceiver);
			} catch (Exception ex) {
				Util.log( ex.getMessage());
			}

			// setup timer
			Util.log( "Setup timer");
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.add(Calendar.MINUTE, 2);
			mAlarmManager.set(AlarmManager.RTC_WAKEUP,
					calendar.getTimeInMillis(), PauseIntent);
		} else {
			Util.log( "Stop GPS");
		}
	}

	@Override
	public Object GetReading() {

		return null;
	}

	@Override
	protected String GetTAG() {
		// TODO Auto-generated method stub
		return TAG;
	}

	@Override
	protected String GetExtension() {
		// TODO Auto-generated method stub
		return "csv";
	}

	@Override
	public boolean isEnableThread() {
		// TODO Auto-generated method stub
		return false;
	}
}
