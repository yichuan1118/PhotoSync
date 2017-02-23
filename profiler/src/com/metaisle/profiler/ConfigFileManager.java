package com.metaisle.profiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;

public class ConfigFileManager {
	public static final int conf_version = 2;

	private int profiling_level = 2;
	private boolean profiler_UI_enable = true;
	private static String SDStorage = null;

	public ConfigFileManager(Context context) {
		SDStorage = context.getFileStreamPath("").getAbsolutePath();
		LoadConfig();
	}

	private void LoadConfig() {
		File config = new File(SDStorage, "config");
		if (config.exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(config));
				String version = br.readLine();
				String level = br.readLine();
				String profilerUI = br.readLine();
				if (Integer
						.parseInt(version.substring(version.indexOf(',') + 1)) != conf_version) {
					config.delete();
					config = new File(SDStorage, "config");
					FileWriter fr = new FileWriter(config);
					fr.append("version," + String.valueOf(conf_version) + "\n");
					fr.append("profiling_level,"
							+ String.valueOf(profiling_level) + "\n");
					fr.append("profiler_UI_enable,"
							+ String.valueOf(profiler_UI_enable) + "\n");
					fr.flush();
					fr.close();
				} else {
					profiling_level = Integer.parseInt(level.substring(level
							.indexOf(',') + 1));
					profiler_UI_enable = Boolean.parseBoolean(profilerUI
							.substring(profilerUI.indexOf(',') + 1));
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				FileWriter fr = new FileWriter(config);
				fr.append("version," + String.valueOf(conf_version) + "\n");
				fr.append("profiling_level," + String.valueOf(profiling_level)
						+ "\n");
				fr.append("profiler_UI_enable,"
						+ String.valueOf(profiler_UI_enable) + "\n");
				fr.flush();
				fr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public boolean getProfilerUI_enable() {
		return profiler_UI_enable;
	}

	public int getProfiling_level() {

		return profiling_level;
	}
}
