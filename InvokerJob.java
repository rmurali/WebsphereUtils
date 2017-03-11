

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import javax.net.ssl.SSLSession;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ibm.websphere.ssl.JSSEHelper;

/**
 * A job that invokes an XML-RPC service within Websphere
 * Application Server using a dynamic SSL configuration 
 * that head already been setup.
 * @author rmurali
 *
 */
public class InvokerJob implements Job {
	

	
	
	public void execute(JobExecutionContext jec) throws JobExecutionException {
		

		BufferedWriter out = null;
		HttpsURLConnection connection = null;

		try {
			String urlString = System.getProperty("RPCURL");
			String[] components =urlString.split(":");
			String port = components[2].substring(0, 4);
			URL url = new URL(urlString);

			//accepts the certificate presented by the server regardless of whether there is a mismatch between the hostname
			//in the cert and what is requested in the URL
			HostnameVerifier hv = new HostnameVerifier()
			{
				public boolean verify(String urlHostName, SSLSession session)
				{
					return true;
				}
			};

			HttpsURLConnection.setDefaultHostnameVerifier(hv); 
			connection = (HttpsURLConnection) url.openConnection();
			String alias = "<DunamicSSLConfigurationName>";

			final HashMap connectionInfo = new HashMap();
			connectionInfo.put(JSSEHelper.CONNECTION_INFO_DIRECTION, JSSEHelper.DIRECTION_OUTBOUND);
			connectionInfo.put(JSSEHelper.CONNECTION_INFO_REMOTE_HOST, "localhost");
			connectionInfo.put(JSSEHelper.CONNECTION_INFO_REMOTE_PORT, port);

			javax.net.ssl.SSLSocketFactory sslFact = JSSEHelper.getInstance().getSSLSocketFactory(alias, connectionInfo, null);

			connection.setSSLSocketFactory(sslFact);
			connection.setDoOutput(true);


			out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));

			if (connection.getResponseCode() == 200) {
				System.out.println("Invoked RPC successfully on Port-"+port);
				
			} else {
				
				throw new Exception("Error in invoking Pool Servlet Refresh url -"+urlString);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		
		}
		finally {
			//Disconnecting HTTPSConnection
			connection.disconnect();
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
	}

}

