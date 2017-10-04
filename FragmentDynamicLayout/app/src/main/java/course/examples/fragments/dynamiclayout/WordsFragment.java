package course.examples.fragments.dynamiclayout;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class WordsFragment extends Fragment {

	private static final String TAG = "WordsFragment";

	private TextView houseWordsView = null;
	private int currentIndex = -1;
	private int wordsArrayLength;

	int getShownIndex() {
		return currentIndex;
	}

	// Show the Quote string at position newIndex
	void showQuoteAtIndex(int newIndex) {
		if (newIndex < 0 || newIndex >= wordsArrayLength)
			return;
		currentIndex = newIndex;
		houseWordsView.setText(QuoteViewerActivity.WordsArray[currentIndex]);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	// Called to create the content view for this Fragment
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.words_fragment,
				container, false);
	}
	
	// Set up some information about the houseWordsView TextView
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		houseWordsView = (TextView) getActivity().findViewById(R.id.quoteView);
		wordsArrayLength = QuoteViewerActivity.WordsArray.length;
	}

}
