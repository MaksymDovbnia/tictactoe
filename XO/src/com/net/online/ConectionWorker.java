package com.net.online;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.net.ConnectivityManager;
import android.os.Handler;
import android.util.Log;

public class ConectionWorker extends Thread {
	private Handler handler;
	private static final String TAG = "myLogs";

	public ConectionWorker(Handler handler) {
		this.handler = handler;
	}

	public void run() {
		BufferedReader in = null;
		try {
			Log.d(TAG, "RUN");

			// InetAddress serverAddr = InetAddress.getByName();
			// InetSocketAddress iInetSocketAddress = new InetSocketAddress();
			Socket sc = new Socket("77.47.207.23", 20159);
			// Log.d(TAG, sc.toString());
			Log.d(TAG, ConnectivityManager.EXTRA_NETWORK_INFO);

			// sc.connect(iInetSocketAddress, 5000);

			/*
			 * HttpClient client = new DefaultHttpClient(); HttpGet request =
			 * new HttpGet(); request.setURI(new URI("http://w3mentor.com/"));
			 * HttpResponse response = client.execute(request); in = new
			 * BufferedReader (new
			 * InputStreamReader(response.getEntity().getContent())); Log.d(TAG,
			 * " " + "URAAAAAAAAAAAAAAA!!!!!!!" + in.readLine());
			 */

			// sc.getInputStream();

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// } catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

	}

}
