package com.example.graysonorr.ohsugar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.graysonorr.ohsugar.db.AppDatabase;
import com.example.graysonorr.ohsugar.db.Food;

import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        TextView toolBarTitle = findViewById(R.id.toolbar_title);
        Typeface customFont = Typeface.createFromAsset(getAssets(), getString(R.string.font));
        toolBarTitle.setTypeface(customFont);

        db = AppDatabase.getInMemoryDatabase(getApplicationContext());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, db.foodDao().getAllNames());
        final AutoCompleteTextView textView = (AutoCompleteTextView)
                findViewById(R.id.searchBox);
        textView.setAdapter(adapter);

        Button searchBtn = (Button) findViewById(R.id.searchBtn);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchResultArrayAdapter adapter1 = new SearchResultArrayAdapter
                        (SearchActivity.this, R.layout.custom_search_results_listview, db.foodDao().searchByName("%"+textView.getText().toString()+"%"));
                ListView lv = (ListView) findViewById(R.id.SearchResultsLstVw);
                lv.setAdapter(adapter1);
            }
        });
    }

    public class SearchResultArrayAdapter extends ArrayAdapter<Food>{
        public SearchResultArrayAdapter (Context context, int resource, List<Food> objects){
            super(context, resource, objects);
        }
        public View getView(int position, View convertView, ViewGroup container){
            LayoutInflater inflater = LayoutInflater.from(SearchActivity.this);
            View customView = inflater.inflate(R.layout.custom_search_results_listview , container, false);

            TextView resultTxtVw = (TextView) customView.findViewById(R.id.ResultTxtVw);
            Button addBtn = (Button) customView.findViewById(R.id.AddBtn);
            Button moreInfoBtn = (Button) customView.findViewById(R.id.MoreInfoBtn);

            final Food currentItem = getItem(position);

            resultTxtVw.setText(currentItem.name);
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SearchActivity.this, ShoppingListActivity.class);
                    startActivity(intent);
                }
            });
            moreInfoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SearchActivity.this, MoreInfoActivity.class);
                    intent.putExtra("ID", currentItem.foodID);
                    startActivity(intent);
                }
            });

            return customView;
        }
    }
}
