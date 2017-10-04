package course.examples.networking.androidhttpclientjson;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by domin on 4 Oct 2017.
 */

public class QuotesAdapter extends BaseAdapter {
    private ArrayList<String[]> quotesAndAuthors;
    private Context context;

    public QuotesAdapter(Context context, ArrayList<String[]> quotesAndAuthors) {
        this.quotesAndAuthors = quotesAndAuthors;
        this.context = context;
    }

    @Override
    public int getCount() {
        return quotesAndAuthors.size();
    }

    @Override
    public Object getItem(int i) {
        return quotesAndAuthors.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final String[] quoteAndAuthor = (String[]) getItem(i);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout itemLayout = (LinearLayout) inflater.inflate(R.layout.quote_item, null);

        final TextView quoteTxt = (TextView) itemLayout.findViewById(R.id.quoteTxt);
        quoteTxt.setText(quoteAndAuthor[0]);

        final TextView authorTxt = (TextView) itemLayout.findViewById(R.id.authorTxt);
        authorTxt.setText("-" + quoteAndAuthor[1]);

        return itemLayout;
    }
}
