package com.saaranga.wikikannada;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.saaranga.wikitrack.utilities.Constants;

import android.util.Log;

/**
 * This class handles the login action to wikipedia user account
 * 
 * @author supreeth
 * 
 */
public class LoginHandler {

	private String tag = "Login Handler Class";

	private String feed;
	private String currentSessionToken;

	public static boolean isError = false;

	private String DEBUG_TAG = "Login Handler Class";

	private String errorMessage;

	private HashMap<String, String> loginErrorMessagesMap;
	private String[] loginErrorMessages = { "NoName", "Illegal", "NotExists",
			"EmptyPass", "WrongPass", "WrongPluginPass", "CreateBlocked",
			"Throttled", "Blocked", "mustbeposted", "NeedToken" };

	public LoginHandler() {
		loginErrorMessagesMap = new HashMap<String, String>();
		loginErrorMessagesMap.put("NoName",
				"You didn't set the lgname parameter");
		loginErrorMessagesMap
				.put("Illegal", "You provided an illegal username");
		loginErrorMessagesMap.put("NotExists",
				"The username you provided doesn't exist");
		loginErrorMessagesMap.put("EmptyPass",
				"You didn't set the lgpassword parameter or you left it empty");
		loginErrorMessagesMap.put("WrongPass",
				"The password you provided is incorrect");
		loginErrorMessagesMap.put("WrongPluginPass",
				"he password you provided is incorrect");
		loginErrorMessagesMap
				.put("CreateBlocked",
						"The wiki tried to automatically create a new account for you, but your IP address has been blocked from account creation");
		loginErrorMessagesMap.put("Throttled",
				"You've logged in too many times in a short time.");
		loginErrorMessagesMap.put("Blocked", "User is blocked");
		loginErrorMessagesMap.put("mustbeposted",
				"The login module requires a POST request");
		loginErrorMessagesMap
				.put("NeedToken",
						"Either you did not provide the login token or the sessionid cookie. Request again with the token and cookie given in this response");
	}

	/**
	 * 
	 * @param unm
	 * @param pwd
	 * @return feed in the form of a string - can be null if there is error in
	 *         login
	 */
	public String loginToWikiAccount(String unm, String pwd) {

		HttpClient httpClient = new DefaultHttpClient();
		try {

			// create a local instance of cookie store
			CookieStore cookieStore = new BasicCookieStore();

			// create local http context
			HttpContext localContext = new BasicHttpContext();

			// bind custome cookiestore the local context
			localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

			postFirstRequest(unm, pwd, httpClient, localContext);

			String responseText = postSecondRequest(unm, pwd, httpClient,
					localContext);

			// check if the response has errors
			String error = checkForError(responseText);
			mlog("Error message: " + error);

			// get the rss feed if no error
			if (error.equals("NO_ERROR")) {
				postThirdRequest(httpClient, localContext);
			} else {
				isError = true;
				setErrorMessage(error);
			}

		} finally {
			httpClient.getConnectionManager().shutdown();

		}

		Log.i(tag, feed);
		return feed;

	}

	private void postThirdRequest(HttpClient httpClient,
			HttpContext localContext) {
		URI uriwl = null;
		try {
			uriwl = new URI(Constants.MYWATCHLIST_URL);
		} catch (URISyntaxException e) {
			Log.e(tag + 4, "" + e.getMessage());
			e.printStackTrace();
		}

		HttpGet httpGet = new HttpGet(uriwl);
		HttpResponse response3 = null;

		try {
			response3 = httpClient.execute(httpGet, localContext);
		} catch (ClientProtocolException e) {
			Log.e(tag + 5, "" + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(tag + 5, "" + e.getMessage());
			e.printStackTrace();
		}

		try {
			feed = responseHandler(response3);
			// Log.i(tag+6, "parsed rss: "+responseHandler(response3));

		} catch (UnsupportedEncodingException e) {
			Log.e(tag + 6, "" + e.getMessage());
			e.printStackTrace();
		} catch (IllegalStateException e) {
			Log.e(tag + 6, "" + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(tag + 6, "" + e.getMessage());
			e.printStackTrace();
		}
	}

	private String postSecondRequest(String unm, String pwd,
			HttpClient httpClient, HttpContext localContext) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder
				.append("http://kn.wikipedia.org/w/api.php?action=login&lgname=");
		stringBuilder.append(unm);
		stringBuilder.append("&lgpassword=");
		stringBuilder.append(pwd);
		stringBuilder.append("&lgtoken=");
		stringBuilder.append(getCurrentSessionToken());
		stringBuilder.append("&format=xml");
		// send a second post reques returning thr token - if the result is
		// success, return login true
		// api.php ? action=login & lgname=Bob & lgpassword=secret & lgtoken=b5780b6e2f27e20b450921d9461010b4
		String url2 = stringBuilder.toString();
		URI uri2 = null;
		try {
			uri2 = new URI(url2);
		} catch (URISyntaxException e) {
			Log.e(tag + 2, "" + e.getMessage());
			e.printStackTrace();
		}
		HttpPost httpPostConfirm = new HttpPost(uri2);

		HttpResponse response2 = null;
		try {
			response2 = httpClient.execute(httpPostConfirm, localContext);
		} catch (ClientProtocolException e) {
			Log.e(tag + 3, "" + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(tag + 3, "" + e.getMessage());
			e.printStackTrace();
		}

		String responseText = null;
		try {
			responseText = responseHandler(response2);
		} catch (UnsupportedEncodingException e) {
			Log.e(tag + 3, "" + e.getMessage());
			e.printStackTrace();
		} catch (IllegalStateException e) {
			Log.e(tag + 3, "" + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(tag + 3, "" + e.getMessage());
			e.printStackTrace();
		}
		return responseText;
	}

	private void postFirstRequest(String unm, String pwd,
			HttpClient httpClient, HttpContext localContext) {
		// the post message should be sent to the wikipedia:
		// ref:http://www.mediawiki.org/wiki/API:Login
		URI uri = null;
		try {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder
					.append("http://kn.wikipedia.org/w/api.php?action=login&lgname=");
			stringBuilder.append(unm);
			stringBuilder.append("&lgpassword=");
			stringBuilder.append(pwd);
			stringBuilder.append("&format=xml");
			uri = new URI(stringBuilder.toString());
		} catch (URISyntaxException e) {
			e.printStackTrace();

		}
		// the received message needs for token so from the first response
		// extract the token
		String token = null;
		HttpPost httpPost = new HttpPost(uri);
		try {
			HttpResponse response = httpClient.execute(httpPost, localContext);
			String responseXml = responseHandler(response);
			int start = responseXml.indexOf("token");
			int end = responseXml.indexOf("cookieprefix");
			if (start != -1 && end != -1) {
				token = (responseXml.substring(start + 6, end - 1)).replaceAll(
						"\"", "");
				setCurrentSessionToken(token);
			}

		} catch (ClientProtocolException e) {
			Log.e(tag + 1, "" + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(tag + 1, "" + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Read the response to astring
	 * 
	 * @param response
	 * @return response string
	 * @throws UnsupportedEncodingException
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public String responseHandler(HttpResponse response)
			throws UnsupportedEncodingException, IllegalStateException,
			IOException {
		HttpEntity resEntity = response.getEntity();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				resEntity.getContent(), "UTF-8"));
		String line;
		String result = "";
		while (((line = br.readLine()) != null)) {
			result = result + line + "\n";
		}
		mlog("http response: " + result);

		return result;
	}

	/**
	 * Check if the response message has errors
	 * 
	 * @param response
	 * @return error message, "NO ERROR" if no error msg in the response
	 */
	private String checkForError(String response) {

		int errorCheckCount = loginErrorMessagesMap.size();

		for (int i = 0; i < errorCheckCount; i++) {
			// there can be only one error message, so return if error
			// encountered
			if (response.indexOf(loginErrorMessages[i]) != -1) {
				return loginErrorMessagesMap.get(loginErrorMessages[i]);
			}
		}

		// if none of the error messages are present return NO_ERROR
		return "NO_ERROR";
	}

	/**
	 * Returns the current token
	 * 
	 * @return
	 */
	public String getCurrentSessionToken() {
		return currentSessionToken;
	}

	/**
	 * Set the current session token
	 * 
	 * @param t
	 */
	private void setCurrentSessionToken(String t) {
		currentSessionToken = t;
	}

	/**
	 * Set the error message if login fails
	 * 
	 * @param t
	 */
	public void setErrorMessage(String t) {
		errorMessage = t;
	}

	/**
	 * returns error message
	 * 
	 * @return error message
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	private void mlog(String msg) {
		Log.d(DEBUG_TAG, msg);
	}
}
