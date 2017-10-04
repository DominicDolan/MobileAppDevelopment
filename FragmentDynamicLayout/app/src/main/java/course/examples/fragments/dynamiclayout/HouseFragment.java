package course.examples.fragments.dynamiclayout;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class HouseFragment extends ListFragment {
	private static final String TAG = "HouseFragment";
	private ListSelectionListener listener = null;

	interface ListSelectionListener {
		void onListSelection(int index);
	}

	// Called when the user selects an item from the List
	@Override
	public void onListItemClick(ListView l, View v, int pos, long id) {

		// Indicates the selected item has been checked
		getListView().setItemChecked(pos, true);
		
		// Inform the QuoteViewerActivity that the item in position pos has been selected
		listener.onListSelection(pos);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {

			// Set the ListSelectionListener for communicating with the QuoteViewerActivity
			listener = (ListSelectionListener) activity;
		
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnArticleSelectedListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}


	@Override
	public void onActivityCreated(Bundle savedState) {
		super.onActivityCreated(savedState);

		setListAdapter(new ArrayAdapter<String>(getActivity(),
				R.layout.house_item, QuoteViewerActivity.HouseArray));

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

}