package dk.christer.malmofestivalen.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

public class JSONLoader {

	public static String Get(String url, HashMap<String, String> data) throws Exception {
		if (data.size() > 0 ) 
		{
			url = url + "?" + ParametersToString(data);
		}
		return Get(url);
	}
	
	public static String Get(String urlString) throws Exception {
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpGet httpGet = new HttpGet(urlString);

		String result = null;
		try{
			HttpResponse response = httpClient.execute(httpGet, localContext);
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder str = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null){
                str.append(line + "\n");
            }
            in.close();
            result = str.toString();
        }catch(Exception ex){
        	ex.printStackTrace();
        }
        return result;
	}

	public static String ParametersToString(HashMap<String, String> data) throws UnsupportedEncodingException
	{
		String urlParameters = "";
		for (String key : data.keySet()) 
		{
			if (urlParameters.length() > 0) {
				urlParameters += "&";
			}
			urlParameters += key + "=" +  URLEncoder.encode(data.get(key), "UTF-8");
		}
		return urlParameters;
	}
	
}
