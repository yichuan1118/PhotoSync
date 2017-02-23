package com.metaisle.profiler.collector.periodical;

import java.io.IOException;
import java.util.Set;

import com.metaisle.profiler.collector.PeriodicalCollector;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class Bluetooth extends PeriodicalCollector {

	// Member fields
	private BluetoothReceiver mReceiver;
	private BluetoothAdapter mBtAdapter;
	private static final String TAG = "Bluetooth Context";
	private int REQUEST_ENABLE_BT = 1;

	public Bluetooth(String filename, Context mContext) throws IOException {
		super(filename, mContext);
		// TODO Auto-generated constructor stub

		mReceiver = new BluetoothReceiver();

		// Register for broadcasts when a device is discovered
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		mContext.registerReceiver(mReceiver, filter);

		// Register for broadcasts when discovery has finished
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		mContext.registerReceiver(mReceiver, filter);

		// Get the local Bluetooth adapter
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if (!mBtAdapter.isEnabled()) {
	        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	        mContext.startActivity(enableBtIntent);
		}
	}

	@Override
	public boolean isEnableThread() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void _Start() {
		// TODO Auto-generated method stub
		GetReading();
	}

	@Override
	protected void _Stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isSupported() {
		// TODO Auto-generated method stub
		return mBtAdapter != null;
	}

	@Override
	public Object GetReading() {
		// TODO Auto-generated method stub

		// Get a set of currently paired devices
		Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
		try {
			WriteLineWithTime("");
			// If there are paired devices, record each one
			if (pairedDevices != null && pairedDevices.size() > 0) {
				for (BluetoothDevice device : pairedDevices) {
					WriteLine(device.getName() + "," + device.getAddress()
							+ "," + device.getBondState() + ",paired");
					// mPairedDevicesArrayAdapter.add(device.getName() + "\n" +
					// device.getAddress());
				}
			}
			// If we're already discovering, stop it
			if (mBtAdapter.isDiscovering()) {
				mBtAdapter.cancelDiscovery();
			}

			// Request discover from BluetoothAdapter
			mBtAdapter.startDiscovery();

		} catch (Exception ex) {
		}
		return null;
	}

	@Override
	protected String GetTAG() {
		// TODO Auto-generated method stub
		return TAG;
	}

	@Override
	public void Pause() {
		running = false;
		
		if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
	}
	
	@Override
	protected String GetExtension() {
		// TODO Auto-generated method stub
		return "csv";
	}

	private class BluetoothReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			try {
				WriteLineWithTime("");
				// When discovery finds a device
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					// Get the BluetoothDevice object from the Intent
					BluetoothDevice device = intent
							.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					// If it's already paired, skip it, because it's been listed
					// already
					if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
						WriteLine(device.getName() + "," + device.getAddress());
					}
					// When discovery is finished, change the Activity title
				}
			} catch (Exception ex) {
			}
		}

	}

}
