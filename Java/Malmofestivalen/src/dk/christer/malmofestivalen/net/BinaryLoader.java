package dk.christer.malmofestivalen.net;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class BinaryLoader {

	ProgressListener _progressListener;
	
	public void SetProgressListener(ProgressListener progressListener) 
	{
		_progressListener = progressListener;
	
	}
	public Boolean Download(String url, String filename) {
		BufferedOutputStream fos = null;
		File file = new File(filename);
		try {
			if (file.exists()) {
				file.delete();
			}
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null)
			{
				InputStream stream = entity.getContent();
				byte buf[] = new byte[1024 * 1024];
				int numBytesRead = 0;
				int allBytesRead = 0;
				int fullLength = (int)entity.getContentLength();
				fos = new BufferedOutputStream(new FileOutputStream(filename));
				OnProgress(allBytesRead, fullLength);
				do
				{
					numBytesRead = stream.read(buf);

					if (numBytesRead > 0)
					{
						allBytesRead += numBytesRead;
						OnProgress(allBytesRead, fullLength);
						fos.write(buf, 0, numBytesRead);
					}
				} while (numBytesRead > 0);
				fos.flush();
				fos.close();
				stream.close();
				buf = null;
				httpclient.getConnectionManager().shutdown();
			}
		} catch (Exception e) {
			if (fos != null) {
				try {
					fos.close();
					if (file.exists()) {
						file.delete();
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private void OnProgress(int progress, int fullLength) {
		if (_progressListener != null) {
			_progressListener.onProgress(progress, fullLength);
		}
	}
	
	public interface ProgressListener {
		public void onProgress(int current, int fullLength);
	}
	
}
