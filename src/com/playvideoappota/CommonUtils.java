package com.playvideoappota;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.CookieManager;

public class CommonUtils {
	private static final String TAG = CommonUtils.class.getName();
	
	public static String secondsToString(int seconds) {
		String s = String.format("%02d", seconds / 60) + ":";
		int t = seconds % 60;
		s += t < 10 ? "0" + t : t;
		return s;
	}

	public static String md5(String paramString) {
		try {
			MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
			localMessageDigest.update(paramString.getBytes());
			byte[] arrayOfByte = localMessageDigest.digest();
			StringBuffer localStringBuffer = new StringBuffer();
			
			for (int i = 0;; i++) {
				if (i >= arrayOfByte.length)
					return localStringBuffer.toString();

				String str = Integer.toHexString(0xFF & arrayOfByte[i]);
				if (str.length() == 1)
					localStringBuffer.append('0');
				localStringBuffer.append(str);
			}
		} catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
			localNoSuchAlgorithmException.printStackTrace();
		}
		return "";
	}

	public static String getCookie(CookieManager cookieManager, String url) {
		return cookieManager.getCookie(url);
	}

	public static String convertUnixTime(int time) {
		if (time == 0) {
			return "";
		}
		Date date = new Date(Long.valueOf(time) * 1000);
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String returnDate = format.format(date);
		return returnDate;
	}
	
	public static String deleteBlankString(String str)
	{
		String res=str;
		return res.replaceAll("\\s", "%20");
	}
	
	public static String replaceString(String str,String expression,String replacment)
	{
		String res=str;
		return res.replaceAll(expression, replacment);
	}

	public static String inputStreamToString(InputStream is) {
		String line = "";
		StringBuilder total = new StringBuilder();
		// Wrap a BufferedReader around the InputStream
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		// Read response until the end
		try {
			while ((line = rd.readLine()) != null) {
				total.append(line);
			}
		} catch (IOException e) {
			Log.e(TAG, "Error read data");
			return null;
		}
		// Return full string
		return total.toString();
	}

	public static String getDeviceId(Context context) {
		TelephonyManager tManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String device_id = tManager.getDeviceId();
		Log.d("Device Id", device_id);
		return device_id;
	}

	public static String getDeviceVersion() {
		Log.d(TAG, android.os.Build.VERSION.RELEASE);
		return android.os.Build.VERSION.RELEASE;
	}

	public static String getDeviceOs() {
		return "android";
	}

	public static String getVendor() {
		Log.d(TAG, android.os.Build.MANUFACTURER);
		return android.os.Build.MANUFACTURER;
	}

	public static String getPhoneNumber(Context context) {
		TelephonyManager tMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tMgr.getLine1Number();
	}

	public static String getClientVersion(Context context) {
		try {
			Log.d(TAG,
					context.getPackageManager().getPackageInfo(
							context.getPackageName(), 0).versionName
							+ "");
			return context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return "1.0";
	}
	public static int getClientVersionCode(Context context) {
		try {
			Log.d(TAG,
					context.getPackageManager().getPackageInfo(
							context.getPackageName(), 0).versionCode
							+ "");
			return context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return 1;
	}
	public static float convertDpToPixel(float dp, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}

	/**
	 * This method converts device specific pixels to device independent pixels.
	 * 
	 * @param px
	 *            A value in px (pixels) unit. Which we need to convert into db
	 * @param context
	 *            Context to get resources and device specific display metrics
	 * @return A float value to represent db equivalent to px value
	 */
	public static float convertPixelsToDp(float px, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;

	}

	// Check whether the device's network is available.
	public static boolean checkNetworkAvaliable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		return cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isConnectedOrConnecting();
	}

	public static void writetoFile(String str, Context context) {
		String path = "/mnt/sdcard/" + "errormedia.txt";
		try {

			BufferedWriter out = new BufferedWriter(new FileWriter(path));
			out.write(str);
			out.close();
		} catch (IOException e) {
			System.out.println("Exception " + e.toString());
		}

	}

	public static void clearApplicationData(Context context) {
		File cache = context.getCacheDir();
		File appDir = new File(cache.getParent());
		if (appDir.exists()) {
			String[] children = appDir.list();
			for (String s : children) {
				File f = new File(appDir, s);
				if (deleteDir(f))
					Log.e(TAG,
							String.format(
									"**************** DELETED -> (%s) *******************",
									f.getAbsolutePath()));
			}
		}
	}

	private static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		if (dir.getName().contains("prefs")) {
			return false;
		} else {
			return dir.delete();
		}

	}

	public static String bytesToHex(byte[] bytes) {
		StringBuilder sbuf = new StringBuilder();
		for (int idx = 0; idx < bytes.length; idx++) {
			int intVal = bytes[idx] & 0xff;
			if (intVal < 0x10)
				sbuf.append("0");
			sbuf.append(Integer.toHexString(intVal).toUpperCase());
		}
		return sbuf.toString();
	}

	/**
	 * Get utf8 byte array.
	 * 
	 * @param str
	 * @return array of NULL if error was found
	 */
	public static byte[] getUTF8Bytes(String str) {
		try {
			return str.getBytes("UTF-8");
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Load UTF8withBOM or any ansi text file.
	 * 
	 * @param filename
	 * @return
	 * @throws java.io.IOException
	 */
	public static String loadFileAsString(String filename)
			throws java.io.IOException {
		final int BUFLEN = 1024;
		BufferedInputStream is = new BufferedInputStream(new FileInputStream(
				filename), BUFLEN);
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFLEN);
			byte[] bytes = new byte[BUFLEN];
			boolean isUTF8 = false;
			int read, count = 0;
			while ((read = is.read(bytes)) != -1) {
				if (count == 0 && bytes[0] == (byte) 0xEF
						&& bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
					isUTF8 = true;
					baos.write(bytes, 3, read - 3); // drop UTF8 bom marker
				} else {
					baos.write(bytes, 0, read);
				}
				count += read;
			}
			return isUTF8 ? new String(baos.toByteArray(), "UTF-8")
					: new String(baos.toByteArray());
		} finally {
			try {
				is.close();
			} catch (Exception ex) {
			}
		}
	}


	/**
	 * Get IP address from first non-localhost interface
	 * 
	 * @param ipv4
	 *            true=return ipv4, false=return ipv6
	 * @return address or empty string
	 */
	public static String getIPAddress(boolean useIPv4) {
		try {
			List<NetworkInterface> interfaces = Collections
					.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				List<InetAddress> addrs = Collections.list(intf
						.getInetAddresses());
				for (InetAddress addr : addrs) {
					if (!addr.isLoopbackAddress()) {
						String sAddr = addr.getHostAddress().toUpperCase();
						boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
						if (useIPv4) {
							if (isIPv4)
								return sAddr;
						} else {
							if (!isIPv4) {
								int delim = sAddr.indexOf('%'); // drop ip6 port
																// suffix
								return delim < 0 ? sAddr : sAddr.substring(0,
										delim);
							}
						}
					}
				}
			}
		} catch (Exception ex) {
		} // for now eat exceptions
		return "";
	}

	public static String getCountry() {
		return Locale.getDefault().getCountry();
	}
	
	public static int getWidthScreen(Context context) {
		DisplayMetrics metric = context.getResources().getDisplayMetrics();
		int width = metric.widthPixels;
		return width;
	}

	public static int getHeightScreen(Context context) {
		DisplayMetrics metric = context.getResources().getDisplayMetrics();
		int height = metric.heightPixels;
		return height;
	}
	
	
}
