package com.metaisle.photosync.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.metaisle.photosync.R;
import com.metaisle.photosync.R.id;
import com.metaisle.photosync.R.layout;
import com.metaisle.photosync.data.Provider;
import com.metaisle.photosync.data.UploadTable;
import com.metaisle.profiler.UploadStatisticFile;
import com.metaisle.util.Util;

@TargetApi(11)
public class ProfilerActivity extends Activity {
	public static ProfilerActivity self;
	private int plot1;
	private static int show1;
	private static int month1;
	private static int day1;
	private static int hour1;
	private TextView type1;
	private static Spinner m_spinner, d_spinner, h_spinner;

	// private Handler handler = new Handler();
	final String TAG = "ProfilerActivity";

	// for plotting line chart
	private LinearLayout linechart1;
	private XYMultipleSeriesRenderer currentrenderer1 = new XYMultipleSeriesRenderer();
	private XYMultipleSeriesDataset dataset1 = new XYMultipleSeriesDataset();
	private XYSeries currentseries1;
	private GraphicalView chart1;
	private int sample_num = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		self = this;
		setContentView(layout.profiler);
		Util.log("onCreate");
		plot_init();
	}// end of oncreate

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// Util.log("onDestroy()");
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if (chart1 == null) {
			linechart1 = (LinearLayout) findViewById(id.linearLayout1);
			chart1 = ChartFactory.getLineChartView(this, dataset1,
					currentrenderer1);
			linechart1.addView(chart1, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		} else {
			chart1.repaint();
		}

		// handler.postDelayed(screen_update, 1 * 1000);
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		MenuItem refresh = menu.add("Update Profile");
		refresh.setIcon(R.drawable.navigation_refresh);
		refresh.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		refresh.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				final ProgressDialog myDialog = ProgressDialog.show(
						ProfilerActivity.this, "", "Loading", false);

				final Handler handler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						myDialog.dismiss();
						chartrefreash(plot1, show1, month1, day1, hour1,
								chart1, currentrenderer1, currentseries1,
								dataset1, type1);
					}
				};

				Thread t = new Thread() {

					@Override
					public void run() {
						UploadStatisticFile.Copy2SD();
						handler.sendEmptyMessage(0);
					}
				};
				t.start();
				return true;

			}
		});
		MenuItem rescale = menu.add("rscale");
		rescale.setIcon(R.drawable.ic_rescale);
		rescale.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		rescale.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				if (chart1 != null) {
					if (sample_num > 0) {
						currentrenderer1.setYAxisMax(currentseries1.getMaxY());
						currentrenderer1.setYAxisMin(currentseries1.getMinY());
						currentrenderer1.setXAxisMax(Math.ceil(currentseries1
								.getMaxX()));
						currentrenderer1.setXAxisMin(0);
						chart1.repaint();
					}
				}
				return true;
			}
		});
		return true;
	};

	private void plot_init() {
		plot1 = 0;
		show1 = 0;
		month1 = 0;
		day1 = 0;
		hour1 = 0;

		currentrenderer1.setAxisTitleTextSize(16);
		currentrenderer1.setChartTitleTextSize(20);
		currentrenderer1.setLabelsTextSize(20);
		currentrenderer1.setLegendTextSize(20);
		currentrenderer1.setAxesColor(Color.YELLOW);

		XYSeries series = new XYSeries("3G Signal Strength");
		dataset1.addSeries(series);
		currentseries1 = series;
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setColor(Color.CYAN);
		renderer.setLineWidth(4.5f);
		currentrenderer1.addSeriesRenderer(renderer);
		currentrenderer1.setGridColor(Color.GREEN);
		currentrenderer1.setShowLegend(false);
		currentrenderer1.setZoomEnabled(true, true);
		currentrenderer1.setShowGridY(true);
		currentrenderer1.setXLabelsAngle(-90);
		Spinner spinner1 = (Spinner) findViewById(id.spinner1);
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, new String[] {
						"3G Signal Strength (dbm)",
						"WiFi Signal Strength (dbm)",
						"Network Throughput (Kbps)", "Battery Level (%)",
						"Wifi Transfer Energy Comsumsion (mAh)",
						"3G Transfer Energy Comsumsion (mAh)" });
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner1.setAdapter(adapter1);
		spinner1.setSelection(plot1);
		spinner1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				plot1 = arg2;
				chartrefreash(plot1, show1, month1, day1, hour1, chart1,
						currentrenderer1, currentseries1, dataset1, type1);
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
		
		type1 = (TextView) findViewById(id.textView1);
		plotshow_set();
	}

	private void plotshow_set() {
		if (show1 == 1) {
			// type1.setText(String.valueOf(month1) + "/" + String.valueOf(day1)
			// + " " + String.valueOf(hour1) + ":00" + " ~ "
			// + String.valueOf(hour1) + ":59");
			chartrefreash(plot1, show1, month1, day1, hour1, chart1,
					currentrenderer1, currentseries1, dataset1, type1);
		} else {
			chartrefreash(plot1, show1, month1, day1, hour1, chart1,
					currentrenderer1, currentseries1, dataset1, type1);
		}
	}

	private void setting_dialog(final int n, final int show, final int month,
			final int day, final int hour) {
		final Dialog dialog = new Dialog(ProfilerActivity.this);

		dialog.setContentView(layout.profiler_settings);
		dialog.setTitle("Settings");
		dialog.setCancelable(true);

		RadioGroup show_type = (RadioGroup) dialog
				.findViewById(com.metaisle.photosync.R.id.radioGroup1);
		RadioButton rb1 = (RadioButton) dialog
				.findViewById(com.metaisle.photosync.R.id.radio0);
		RadioButton rb2 = (RadioButton) dialog
				.findViewById(com.metaisle.photosync.R.id.radio1);
		if (show == 1) {
			rb2.setChecked(true);
			rb1.setChecked(false);
		} else {
			rb1.setChecked(true);
			rb2.setChecked(false);
		}

		show_type
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO Auto-generated method stub

						if (checkedId == id.radio0) {
							if (n == 0) {
								ProfilerActivity.show1 = 0;
							}
							m_spinner.setClickable(false);
							d_spinner.setClickable(false);
							h_spinner.setClickable(false);
						} else if (checkedId == id.radio1) {
							if (n == 0) {
								ProfilerActivity.show1 = 1;
							}
							m_spinner.setClickable(true);
							d_spinner.setClickable(true);
							h_spinner.setClickable(true);
						}
					}

				});

		m_spinner = (Spinner) dialog
				.findViewById(com.metaisle.photosync.R.id.monthspinner);

		ArrayAdapter<String> m_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, new String[] { "-", "1",
						"2", "3", "4", "5", "6", "7", "8", "9", "10", "11",
						"12" });
		m_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		m_spinner.setAdapter(m_adapter);
		m_spinner.setSelection(month);
		m_spinner
				.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						if (n == 0)
							ProfilerActivity.month1 = arg2;
					}

					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
					}
				});

		d_spinner = (Spinner) dialog
				.findViewById(com.metaisle.photosync.R.id.dayspinner);

		ArrayAdapter<String> d_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, new String[] { "-", "1",
						"2", "3", "4", "5", "6", "7", "8", "9", "10", "11",
						"12", "13", "14", "15", "16", "17", "18", "19", "20",
						"21", "22", "23", "24", "25", "26", "27", "28", "29",
						"30", "31" });
		d_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		d_spinner.setAdapter(d_adapter);
		d_spinner.setSelection(day);
		d_spinner
				.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						if (n == 0)
							ProfilerActivity.day1 = arg2;
					}

					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
					}
				});

		h_spinner = (Spinner) dialog
				.findViewById(com.metaisle.photosync.R.id.hourspinner);

		ArrayAdapter<String> h_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, new String[] { "-", "1",
						"2", "3", "4", "5", "6", "7", "8", "9", "10", "11",
						"12", "13", "14", "15", "16", "17", "18", "19", "20",
						"21", "22", "23", "24" });
		h_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		h_spinner.setAdapter(h_adapter);
		h_spinner.setSelection(hour);
		h_spinner
				.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						if (n == 0)
							ProfilerActivity.hour1 = arg2;
					}

					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
					}
				});

		if ((n == 0 && show1 == 0)) {
			m_spinner.setClickable(false);
			d_spinner.setClickable(false);
			h_spinner.setClickable(false);
		}

		Button ok = (Button) dialog
				.findViewById(com.metaisle.photosync.R.id.button1);
		ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				plotshow_set();
				if (time_check(n) == false) {
					show_reset(n, show, month, day, hour);
				}
				plotshow_set();
				dialog.dismiss();
			}
		});

		Button cancel = (Button) dialog
				.findViewById(com.metaisle.photosync.R.id.button2);
		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				show_reset(n, show, month, day, hour);
				plotshow_set();
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private boolean time_check(int n) {
		int month = 0, day = 0, hour = 0;
		if (n == 0) {
			month = ProfilerActivity.month1;
			day = ProfilerActivity.day1;
			hour = ProfilerActivity.hour1;
		}

		if (month == 0 || day == 0 || hour == 0) {
			return false;
		} else if (month == 2 && day >= 29) {
			return false;
		} else if (month == 4 && day == 31) {
			return false;
		} else if (month == 6 && day == 31) {
			return false;
		} else if (month == 9 && day == 31) {
			return false;
		} else if (month == 11 && day == 31) {
			return false;
		}
		return true;
	}

	private void show_reset(int n, int show, int month, int day, int hour) {
		if (n == 0) {
			ProfilerActivity.show1 = show;
			ProfilerActivity.month1 = month;
			ProfilerActivity.day1 = day;
			ProfilerActivity.hour1 = hour;
		}
	}

	public void chartrefreash(int plot, int show, int month, int day, int hour,
			GraphicalView chart, XYMultipleSeriesRenderer currentrenderer,
			XYSeries currentseries, XYMultipleSeriesDataset dataset,
			TextView text) {
		Calendar start_date = Calendar.getInstance();
		currentseries.clear();
		sample_num = 0;
		if (dataset.getSeriesCount() > 1
				&& currentrenderer.getSeriesRendererCount() > 1) {
			for (int i = dataset.getSeriesCount() - 1; i >= 1; i--) {
				dataset.removeSeries(i);
				currentrenderer.removeSeriesRenderer(currentrenderer
						.getSeriesRendererAt(i));
			}
		}
		// Util.log("SeriesRenderer : " +
		// currentrenderer.getSeriesRendererCount()
		// + ", Series : " + dataset.getSeriesCount());
		String profile_name = null;
		switch (plot) {
		case 0:
			currentseries.setTitle("3G Signal Strength");
			// currentrenderer.setXTitle("hour");
			currentrenderer.setYTitle("dbm");
			currentrenderer.setAxisTitleTextSize(20);
			// currentrenderer.setYAxisMin(0);
			profile_name = "Cellular.csv";
			break;
		case 1:
			currentseries.setTitle("WiFi Signal Strength");
			// currentrenderer.setXTitle("hour");
			currentrenderer.setYTitle("dbm");
			currentrenderer.setAxisTitleTextSize(20);
			// currentrenderer.setYAxisMin(0);
			profile_name = "Connectivity.csv";
			break;
		case 2:
			currentseries.setTitle("Throughput");
			// currentrenderer.setXTitle("hour");
			currentrenderer.setYTitle("kbps");
			currentrenderer.setAxisTitleTextSize(20);
			// currentrenderer.setYAxisMin(0);
			profile_name = "NetworkTraffic.csv";
			break;

		case 3:
			currentseries.setTitle("Battery Level");
			// currentrenderer.setXTitle("hour");
			currentrenderer.setYTitle("%");
			currentrenderer.setAxisTitleTextSize(20);
			// currentrenderer.setYAxisMin(0);
			profile_name = "Battery.csv";
			break;

		case 4: // energy
			currentseries.setTitle("Wifi Transfer Energy");
			// currentrenderer.setXTitle("hour");
			currentrenderer.setYTitle("mAh");
			currentrenderer.setAxisTitleTextSize(20);
			// currentrenderer.setYAxisMin(0);
			profile_name = "Connectivity.csv";
			break;

		case 5:
			currentseries.setTitle("3G Transfer Energy");
			// currentrenderer.setXTitle("hour");
			currentrenderer.setYTitle("mAh");
			currentrenderer.setAxisTitleTextSize(20);
			// currentrenderer.setYAxisMin(0);
			profile_name = "Cellular.csv";
			break;

		default:
			currentseries.setTitle("3G Signal Strength");
			// currentrenderer.setXTitle("hour");
			currentrenderer.setYTitle("3G Signal Strength");
			currentrenderer.setAxisTitleTextSize(20);
			break;
		}
		
		if (show == 0) {
			BufferedReader br;
			long time = 0;
			long start = 0;
			File profile = new File("/sdcard/PhotoSync", profile_name);
			try {
				br = new BufferedReader(new FileReader(profile));
				String temp;
				while ((temp = br.readLine()) != null) {
					switch (plot) {
					case 0:
						if (temp.contains("Signal")) {
							time = Long.parseLong(temp.substring(0,
									temp.indexOf(',')));
							if (sample_num == 0) {
								start = time / (1000 * 60 * 60)
										* (1000 * 60 * 60);
							}
							for (int i = 0; i < 2; i++) {
								temp = temp.substring(temp.indexOf(',') + 1);
							}
							currentseries
									.add(((double) (time - start) / (1000 * 60 * 60)),
											Long.parseLong(temp));
							sample_num++;
						}
						break;

					case 1:
						if ((temp.contains(",CONNECTED") && temp
								.contains("WIFI"))) {
							time = Long.parseLong(temp.substring(0,
									temp.indexOf(',')));
							if (sample_num == 0) {
								start = time / (1000 * 60 * 60)
										* (1000 * 60 * 60);
							}
							for (int i = 0; i < 5; i++) {
								temp = temp.substring(temp.indexOf(',') + 1);
							}
							temp = temp.substring(0, temp.indexOf(','));
							currentseries
									.add(((double) (time - start) / (1000 * 60 * 60)),
											Long.parseLong(temp));
							sample_num++;
						}
						break;
					case 2:
						if (temp.contains("thru")) {
							time = Long.parseLong(temp.substring(0,
									temp.indexOf(',')));
							if (sample_num == 0) {
								start = time / (1000 * 60 * 60)
										* (1000 * 60 * 60);
							}
							double mobile_tx = 0;
							double wifi_tx = 0;
							for (int i = 0; i < 3; i++) {
								temp = temp.substring(temp.indexOf(',') + 1);
								if (i == 1) {
									mobile_tx = Double.parseDouble(temp
											.substring(0, temp.indexOf(',')));
								} else if (i == 2) {
									wifi_tx = Double.parseDouble(temp);
								}
							}
							// Util.log("" + wifi_tx + "," + mobile_tx);
							if (wifi_tx > mobile_tx) {
								currentseries
										.add(((double) (time - start) / (1000 * 60 * 60)),
												wifi_tx);
								sample_num++;
							} else {
								currentseries
										.add(((double) (time - start) / (1000 * 60 * 60)),
												mobile_tx);
								sample_num++;
							}
						}
						break;
					case 3:
						time = Long.parseLong(temp.substring(0,
								temp.indexOf(',')));
						if (sample_num == 0) {
							start = time / (1000 * 60 * 60) * (1000 * 60 * 60);
						}
						for (int i = 0; i < 2; i++) {
							temp = temp.substring(temp.indexOf(',') + 1);
						}
						currentseries.add(
								((double) (time - start) / (1000 * 60 * 60)),
								Double.parseDouble(temp));
						sample_num++;
						break;
					case 4:
						if ((temp.contains(",CONNECTED") && temp
								.contains("WIFI"))) {
							time = Long.parseLong(temp.substring(0,
									temp.indexOf(',')));
							if (sample_num == 0) {
								start = time / (1000 * 60 * 60)
										* (1000 * 60 * 60);
							}
							for (int i = 0; i < 5; i++) {
								temp = temp.substring(temp.indexOf(',') + 1);
							}
							temp = temp.substring(0, temp.indexOf(','));
							double ss = Double.parseDouble(temp);
							double curr = (1.9285e-4 * ss * ss + 0.0326 * ss + 2.805) / 3.8;
							double trans_time = (0.1478 * ss * ss + 15.786 * ss + 434.5004) / 50 * 2;
							double energy = (curr * 1000) * trans_time / 3600;
							currentseries
									.add(((double) (time - start) / (1000 * 60 * 60)),
											energy);
							sample_num++;
						}
						break;

					case 5:
						if (temp.contains("Signal")) {
							time = Long.parseLong(temp.substring(0,
									temp.indexOf(',')));
							if (sample_num == 0) {
								start = time / (1000 * 60 * 60)
										* (1000 * 60 * 60);
							}
							for (int i = 0; i < 2; i++) {
								temp = temp.substring(temp.indexOf(',') + 1);
							}
							double ss = Double.parseDouble(temp);
							double curr = (1.9285e-4 * ss * ss + 0.0326 * ss + 2.805) / 3.8;
							double trans_time = (0.1478 * ss * ss + 15.786 * ss + 434.5004) / 50 * 2;
							double energy = (curr * 1000) * trans_time / 3600;
							currentseries
									.add(((double) (time - start) / (1000 * 60 * 60)),
											energy);
							sample_num++;
						}
						break;

					default:
						if ((temp.contains(",CONNECTED") && temp
								.contains("WIFI"))) {
							time = Long.parseLong(temp.substring(0,
									temp.indexOf(',')));
							if (sample_num == 0) {
								start = time / (1000 * 60 * 60)
										* (1000 * 60 * 60);
							}
							for (int i = 0; i < 5; i++) {
								temp = temp.substring(temp.indexOf(',') + 1);
							}
							temp = temp.substring(0, temp.indexOf(','));
							currentseries
									.add(((double) (time - start) / (1000 * 60 * 60)),
											Long.parseLong(temp));
							sample_num++;
						}
						break;
					}
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (sample_num != 0) {
				start_date.setTimeInMillis(start);
				text.setText("profile from "
						+ (start_date.get(Calendar.MONTH) + 1) + "/"
						+ (start_date.get(Calendar.DAY_OF_MONTH)) + " "
						+ (start_date.get(Calendar.HOUR_OF_DAY)) + ":00");
				Calendar end_date = Calendar.getInstance();
				// end_date.setTimeInMillis(start
				// + (long) UploadStatisticFile.profilebackup_length);
				// Util.log("" + start_date.getTimeInMillis() + ","
				// + end_date.getTimeInMillis());
				photo_take_marker(start_date, end_date, currentrenderer,
						dataset);
				photo_upload_marker(start_date, end_date, currentrenderer,
						dataset);
			}

		} else {
			BufferedReader br;
			start_date = null;
			Calendar end_date = null;
			File profile = new File("/sdcard/PhotoSync", profile_name);
			try {
				br = new BufferedReader(new FileReader(profile));
				String temp = br.readLine();
				while (temp != null) {
					Calendar log_date = Calendar.getInstance();
					log_date.setTimeInMillis(Long.parseLong((String) temp
							.subSequence(0, temp.indexOf(','))));

					start_date = (Calendar) log_date.clone();
					start_date.set(Calendar.MONTH, month - 1);
					start_date.set(Calendar.DAY_OF_MONTH, day);
					start_date.set(Calendar.HOUR_OF_DAY, hour);
					start_date.set(Calendar.MINUTE, 0);
					start_date.set(Calendar.SECOND, 0);
					start_date.set(Calendar.MILLISECOND, 0);
					end_date = Calendar.getInstance();
					end_date.setTimeInMillis(start_date.getTimeInMillis()
							+ (long) Math.ceil(currentseries.getMaxX()) * 1000
							* 60 * 60);
					// Util.log("log_date  : " + log_date.getTimeInMillis());
					// Util.log("start_date: " + start_date.getTimeInMillis());
					// Util.log("end_date  : " + end_date.getTimeInMillis());

					if (log_date.compareTo(end_date) >= 0) {
						break;
					} else if (log_date.compareTo(start_date) >= 0) {
						switch (plot) {
						case 0:
							if (temp.contains("Signal")) {
								sample_num++;
								for (int i = 0; i < 2; i++) {
									temp = temp
											.substring(temp.indexOf(',') + 1);
								}
								currentseries
										.add(((log_date.getTimeInMillis() - start_date
												.getTimeInMillis()) / 60 / 1000)
												% (24 * 60), Long
												.parseLong(temp));
							}
							break;

						case 1:
							if ((temp.contains(",CONNECTED") && temp
									.contains("WIFI"))) {
								sample_num++;
								for (int i = 0; i < 5; i++) {
									temp = temp
											.substring(temp.indexOf(',') + 1);

								}
								currentseries
										.add(((log_date.getTimeInMillis() - start_date
												.getTimeInMillis()) / 60 / 1000)
												% (24 * 60), Double
												.parseDouble(temp.substring(0,
														temp.indexOf(','))));
							}
							break;
						case 2:
							sample_num++;
							double mobile_tx = 0;
							double wifi_tx = 0;
							for (int i = 0; i < 2; i++) {
								temp = temp.substring(temp.indexOf(',') + 1);
								if (i == 0) {
									mobile_tx = Double.parseDouble(temp
											.substring(0, temp.indexOf(','))) / 1024;
								} else if (i == 1) {
									wifi_tx = Double.parseDouble(temp) / 1024;
								}
							}
							if (wifi_tx > mobile_tx) {
								currentseries
										.add(((log_date.getTimeInMillis() - start_date
												.getTimeInMillis()) / 60 / 1000)
												% (24 * 60), wifi_tx);
							} else {
								currentseries
										.add(((log_date.getTimeInMillis() - start_date
												.getTimeInMillis()) / 60 / 1000)
												% (24 * 60), mobile_tx);
							}

							break;
						case 3:
							sample_num++;
							for (int i = 0; i < 2; i++) {
								temp = temp.substring(temp.indexOf(',') + 1);
							}
							currentseries.add(
									((log_date.getTimeInMillis() - start_date
											.getTimeInMillis()) / 60 / 1000)
											% (24 * 60), Double
											.parseDouble(temp));
							break;

						case 4:
							if ((temp.contains(",CONNECTED") && temp
									.contains("WIFI"))) {
								sample_num++;
								for (int i = 0; i < 5; i++) {
									temp = temp
											.substring(temp.indexOf(',') + 1);
								}
								double ss = Double.parseDouble(temp);
								double curr = (1.9285e-4 * ss * ss + 0.0326
										* ss + 2.805) / 3.8;
								double trans_time = (0.1478 * ss * ss + 15.786
										* ss + 434.5004) / 50 * 2;
								double energy = (curr * 1000) * trans_time
										/ 3600;
								currentseries
										.add(((log_date.getTimeInMillis() - start_date
												.getTimeInMillis()) / 60 / 1000)
												% (24 * 60), energy);
							}
							break;

						case 5:
							if (temp.contains("Signal")) {
								sample_num++;
								for (int i = 0; i < 2; i++) {
									temp = temp
											.substring(temp.indexOf(',') + 1);
								}
								double ss = Double.parseDouble(temp);
								double curr = (1.9285e-4 * ss * ss + 0.0326
										* ss + 2.805) / 3.8;
								double trans_time = (0.1478 * ss * ss + 15.786
										* ss + 434.5004) / 50 * 2;
								double energy = (curr * 1000) * trans_time
										/ 3600;
								currentseries
										.add(((log_date.getTimeInMillis() - start_date
												.getTimeInMillis()) / 60 / 1000)
												% (24 * 60), energy);
							}
							break;

						default:
							if ((temp.contains(",CONNECTED") && temp
									.contains("WIFI"))) {
								sample_num++;
								for (int i = 0; i < 5; i++) {
									temp = temp
											.substring(temp.indexOf(',') + 1);
								}
								currentseries
										.add(((log_date.getTimeInMillis() - start_date
												.getTimeInMillis()) / 60 / 1000)
												% (24 * 60), Long
												.parseLong(temp.substring(0,
														temp.indexOf(','))));
							}
							break;
						}

					}
					temp = br.readLine();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (sample_num == 0) {
				Toast.makeText(this, "this profile doesn't exist",
						Toast.LENGTH_SHORT).show();
			} else {
				text.setText("profile from "
						+ (start_date.get(Calendar.MONTH) + 1) + "/"
						+ (start_date.get(Calendar.DAY_OF_MONTH)) + " "
						+ (start_date.get(Calendar.HOUR_OF_DAY)) + ":00"
						+ " to " + (start_date.get(Calendar.HOUR_OF_DAY))
						+ ":59");
				photo_take_marker(start_date, end_date, currentrenderer,
						dataset);
				photo_upload_marker(start_date, end_date, currentrenderer,
						dataset);
			}
		}
		currentrenderer.setYAxisMax(currentseries.getMaxY());
		currentrenderer.setYAxisMin(currentseries.getMinY());
		currentrenderer.setXAxisMax(Math.ceil(currentseries.getMaxX()));
		currentrenderer.setXAxisMin(0);
		// currentrenderer.setShowLabels(false);
		currentrenderer.setShowCustomTextGrid(true);
		currentrenderer.setXLabels(0);
		currentrenderer.clearTextLabels();
		// currentrenderer.setXLabels((int) Math.ceil(currentseries.getMaxX()));
		int start_hour = start_date.get(Calendar.HOUR_OF_DAY);
		currentrenderer.addTextLabel(0, "" + start_hour + ":00");
		for (int i = 1; i <= Math.ceil(currentseries.getMaxX()); i++) {
			currentrenderer.addTextLabel(i, "" + ((start_hour + i) % 24)
					+ ":00");
		}
		if (chart != null) {
			chart.repaint();
		}

	}

	private Cursor photo_take_query(Calendar start, Calendar end) {
		Cursor cursor = getBaseContext().getContentResolver().query(
				Provider.UPLOAD_CONTENT_URI,
				new String[] { UploadTable.DATE_TAKEN, UploadTable._ID },
				UploadTable.DATE_TAKEN + " > ? AND " + UploadTable.DATE_TAKEN
						+ "< ?",
				new String[] { String.valueOf(start.getTimeInMillis()),
						String.valueOf(end.getTimeInMillis()) },
				UploadTable.DATE_TAKEN + " ASC");

		if (cursor == null || cursor.getCount() == 0) {
			return null;
		}
		return cursor;
	}

	private Cursor photo_upload_query(Calendar start, Calendar end) {
		Cursor cursor = getApplicationContext().getContentResolver().query(
				Provider.UPLOAD_CONTENT_URI,
				new String[] { UploadTable.FINISH_TIME, UploadTable._ID }, // null,null,null);
				UploadTable.FINISH_TIME + " is NOT null AND "
						+ UploadTable.FINISH_TIME + " > ? AND "
						+ UploadTable.FINISH_TIME + "< ?",
				new String[] { String.valueOf(start.getTimeInMillis()),
						String.valueOf(end.getTimeInMillis()) },
				UploadTable.FINISH_TIME + " ASC");

		if (cursor == null || cursor.getCount() == 0) {
			return null;
		}
		return cursor;
	}

	private void photo_take_marker(Calendar start, Calendar end,
			XYMultipleSeriesRenderer currentrenderer,
			XYMultipleSeriesDataset dataset) {
		Cursor take = photo_take_query(start, end);
		// Util.log("" + start.getTimeInMillis() + " " + end.getTimeInMillis());
		int index = 0;
		XYSeries baseline = dataset.getSeriesAt(0);

		// Util.log("size" + sample_num);
		// // Util.log("scale" + baseline.getScaleNumber());
		// Util.log("minx" + baseline.getMinX());
		// Util.log("maxx" + baseline.getMaxX());
		// Util.log("miny" + baseline.getMinY());
		// Util.log("maxy" + baseline.getMaxY());

		// for (int i = 1; i < sample_num; i++) {
		// Util.log(i + ":" + baseline.getX(i) + "," + baseline.getY(i));
		// }

		if (take != null) {
			while (take.moveToNext()) {
				Long timestamp = take.getLong(take
						.getColumnIndex(UploadTable.DATE_TAKEN));
				double deltime = ((double) (timestamp - start.getTimeInMillis()) / (1000 * 60 * 60));

				double y = 0;
				while (index < sample_num ) {
					if (index == 0) {
						if (deltime < baseline.getMinX()) {
							y = baseline.getY(0);
							break;
						}
					} else {
						// Util.log(""+baseline.getX(index-1)+","+baseline.getX(index));
						if (deltime >= baseline.getX(index - 1)
								&& deltime < baseline.getX(index)) {
							y = inter(baseline.getX(index - 1),
									baseline.getY(index - 1),
									baseline.getX(index), baseline.getY(index),
									deltime);
							break;
						} else if (deltime == baseline.getX(index)) {
							y = baseline.getY(index);
							break;
						} else {
							if (index == sample_num - 1) {
								y = baseline.getY(index);
								break;
							}
						}
					}
					if (index < sample_num - 1) {
						// Util.log("index = " + index);
						index++;
					}
				}

				XYSeries series = new XYSeries("");
				series.add(deltime, y);
				dataset.addSeries(series);
				XYSeriesRenderer renderer = new XYSeriesRenderer();
				renderer.setColor(Color.YELLOW);
				renderer.setPointStyle(PointStyle.CIRCLE);
				renderer.setFillPoints(true);
				currentrenderer1.addSeriesRenderer(renderer);
				currentrenderer1.setPointSize(6);
			}
		}
	}

	private void photo_upload_marker(Calendar start, Calendar end,
			XYMultipleSeriesRenderer currentrenderer,
			XYMultipleSeriesDataset dataset) {
		Cursor upload = photo_upload_query(start, end);
		// Util.log("" + start.getTimeInMillis() + " " + end.getTimeInMillis());
		int index = 0;
		XYSeries baseline = dataset.getSeriesAt(0);

		// Util.log("size" + sample_num);
		// // Util.log("scale" + baseline.getScaleNumber());
		// Util.log("minx" + baseline.getMinX());
		// Util.log("maxx" + baseline.getMaxX());
		// Util.log("miny" + baseline.getMinY());
		// Util.log("maxy" + baseline.getMaxY());

		// for (int i = 1; i < sample_num; i++) {
		// Util.log(i + ":" + baseline.getX(i) + "," + baseline.getY(i));
		// }
		if (upload != null) {
			while (upload.moveToNext()) {
				Long timestamp = upload.getLong(upload
						.getColumnIndex(UploadTable.FINISH_TIME));
				double deltime = ((double) (timestamp - start.getTimeInMillis()) / (1000 * 60 * 60));
				// Util.log("upload " + deltime);
				double y = 0;
				while (index < sample_num) {
					if (index == 0) {
						if (deltime < baseline.getMinX()) {
							y = baseline.getY(0);
							break;
						}
					} else {
						// Util.log(""+baseline.getX(index-1)+","+baseline.getX(index));
						if (deltime >= baseline.getX(index - 1)
								&& deltime < baseline.getX(index)) {
							y = inter(baseline.getX(index - 1),
									baseline.getY(index - 1),
									baseline.getX(index), baseline.getY(index),
									deltime);
							break;
						} else if (deltime == baseline.getX(index)) {
							y = baseline.getY(index);
							break;
						} else {
							if (index == sample_num - 1) {
								y = baseline.getY(index);
								break;
							}
						}
					}
					if (index < sample_num - 1) {
						// Util.log("index = " + index);
						index++;
					}
				}

				XYSeries series = new XYSeries("");
				series.add(deltime, y);
				dataset.addSeries(series);
				XYSeriesRenderer renderer = new XYSeriesRenderer();
				renderer.setColor(Color.RED);
				renderer.setPointStyle(PointStyle.SQUARE);
				renderer.setFillPoints(true);
				currentrenderer1.addSeriesRenderer(renderer);
				currentrenderer1.setPointSize(6);
			}
		}
	}

	private double inter(double start_x, double start_y, double end_x,
			double end_y, double x) {

		double result = start_y;
		result += (end_y - start_y) * ((x - start_x) / (end_x - start_x));
		return result;
	}
	// private Runnable screen_update = new Runnable() {
	// public void run() {
	// if (show1 == 0) {
	// chartrefreash(plot1, show1, month1, day1, hour1, chart1,
	// currentrenderer1, currentseries1, dataset1, type1);
	// }
	// handler.postDelayed(this, 5 * 1000);
	// }
	// };
}
