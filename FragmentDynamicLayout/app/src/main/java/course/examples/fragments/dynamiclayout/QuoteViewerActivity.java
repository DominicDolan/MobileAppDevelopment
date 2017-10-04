package course.examples.fragments.dynamiclayout;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import course.examples.fragments.dynamiclayout.HouseFragment.ListSelectionListener;

//Several Activity lifecycle methods are instrumented to emit LogCat output
//so you can follow this class' lifecycle
public class QuoteViewerActivity extends Activity implements
		ListSelectionListener {

	public static String[] HouseArray;
	public static String[] WordsArray;


	private final course.examples.fragments.dynamiclayout.WordsFragment WordsFragment = new WordsFragment();
	private FragmentManager fragmentManager;
	private FrameLayout houseFrameLayout, wordsFrameLayout;

	private static final int MATCH_PARENT = LinearLayout.LayoutParams.MATCH_PARENT;
	private static final String TAG = "HouseViewerActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// Get the string arrays with the titles and qutoes
		HouseArray = getResources().getStringArray(R.array.Houses);
		WordsArray = getResources().getStringArray(R.array.Description);

		setContentView(R.layout.main);

		// Get references to the TitleFragment and to the WordsFragment
		houseFrameLayout = (FrameLayout) findViewById(R.id.title_fragment_container);
		wordsFrameLayout = (FrameLayout) findViewById(R.id.quote_fragment_container);


		// Get a reference to the FragmentManager
		fragmentManager = getFragmentManager();

		// Start a new FragmentTransaction
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();

		// Add the TitleFragment to the layout
		fragmentTransaction.add(R.id.title_fragment_container,
				new HouseFragment());
		
		// Commit the FragmentTransaction
		fragmentTransaction.commit();

		// Add a OnBackStackChangedListener to reset the layout when the back stack changes
		fragmentManager
				.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
					public void onBackStackChanged() {
						setLayout();
					}
				});
	}

	private void setLayout() {
		
		// Determine whether the QuoteFragment has been added
		if (!WordsFragment.isAdded()) {
			
			// Make the TitleFragment occupy the entire layout 
			houseFrameLayout.setLayoutParams(new LinearLayout.LayoutParams(
					MATCH_PARENT, MATCH_PARENT));
			wordsFrameLayout.setLayoutParams(new LinearLayout.LayoutParams(0,
					MATCH_PARENT));
		} else {

			// Make the TitleLayout take 1/3 of the layout's width
			houseFrameLayout.setLayoutParams(new LinearLayout.LayoutParams(0,
					MATCH_PARENT, 1f));
			
			// Make the QuoteLayout take 2/3's of the layout's width
			wordsFrameLayout.setLayoutParams(new LinearLayout.LayoutParams(0,
					MATCH_PARENT, 2f));
		}
	}

	// Called when the user selects an item in the HouseFragment
	@Override
	public void onListSelection(int index) {

		// If the QuoteFragment has not been added, add it now
		if (!WordsFragment.isAdded()) {
		
			// Start a new FragmentTransaction
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();

			// Add the QuoteFragment to the layout
			fragmentTransaction.add(R.id.quote_fragment_container,
					WordsFragment);

			// Add this FragmentTransaction to the backstack
			fragmentTransaction.addToBackStack(null);
			
			// Commit the FragmentTransaction
			fragmentTransaction.commit();
			
			// Force Android to execute the committed FragmentTransaction
			fragmentManager.executePendingTransactions();
		}
		
		if (WordsFragment.getShownIndex() != index) {

			// Tell the QuoteFragment to show the quote string at position index
			WordsFragment.showQuoteAtIndex(index);
		
		}
	}


}