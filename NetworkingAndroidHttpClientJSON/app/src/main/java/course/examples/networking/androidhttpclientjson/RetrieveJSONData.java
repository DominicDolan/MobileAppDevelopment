package course.examples.networking.androidhttpclientjson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

public class RetrieveJSONData extends ListActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new HttpGetTask().execute();
	}

	private class HttpGetTask extends AsyncTask<Void, Void, List<String[]>> {

		private static final String URL = "https://raw.githubusercontent.com/btford/philosobot/master/quotes/cats.json";

		AndroidHttpClient client = AndroidHttpClient.newInstance("");

		@Override
		protected List<String[]> doInBackground(Void... params) {
			HttpGet request = new HttpGet(URL);
			JSONResponseHandler responseHandler = new JSONResponseHandler();
			try {
				return client.execute(request, responseHandler);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<String[]> result) {
			QuotesAdapter adapter = new QuotesAdapter(getApplicationContext(), (ArrayList<String[]>) result);
			setListAdapter(adapter);
		}
	}

	private class JSONResponseHandler implements ResponseHandler<List<String[]>> {

		private static final String AUTHOR_TAG = "author";
		private static final String QUOTE_TAG = "quote";
		private static final String QUOTES_TAG = "quotes";
		private static final String TOPIC_TAG = "topic";

		@Override
		public List<String[]> handleResponse(HttpResponse response) throws IOException {

			List<String[]> result = new ArrayList<String[]>();
			String JSONResponse = new BasicResponseHandler().handleResponse(response);

			try {

				JSONObject responseObject = new JSONObject(JSONResponse);

				JSONArray quotes = responseObject.getJSONArray(QUOTES_TAG);

				// Iterate over quotes list
				for (int i = 0; i < quotes.length(); i++) {

					String quote = ((JSONObject) quotes.get(i)).getString(QUOTE_TAG);
					String author = ((JSONObject) quotes.get(i)).getString(AUTHOR_TAG);
					String[] quoteAndAuthor = {quote, author};

					result.add(quoteAndAuthor);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return result;
		}
	}
}