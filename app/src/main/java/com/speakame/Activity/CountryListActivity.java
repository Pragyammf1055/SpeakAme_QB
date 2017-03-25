package com.speakame.Activity;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.speakame.Adapter.CountryAdapter;
import com.speakame.Beans.AllBeans;
import com.speakame.Classes.AnimRootActivity;
import com.speakame.R;
import com.speakame.utils.Function;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class CountryListActivity extends AnimRootActivity {
    TextView toolbartext;
    ArrayList<AllBeans> countrylist;
    AlertDialog mProgressDialog;
    CountryAdapter countryAdapter;
    RecyclerView recyclerView;
    AllBeans allBeans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        toolbartext = (TextView) findViewById(R.id.toolbar_title);
        toolbartext.setText("Choose country");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());


        countrylist = new ArrayList<AllBeans>();

        JSONObject obj = null;

        try {
            obj = new JSONObject(Function.loadJSONFromAsset(CountryListActivity.this, "countries.json"));

            JSONArray m_jArry = obj.getJSONArray("countries");

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jsonObject = m_jArry.getJSONObject(i);
                allBeans = new AllBeans();

                allBeans.setCountryName(jsonObject.getString("name"));
                allBeans.setCountrycode(jsonObject.getString("code"));
                countrylist.add(allBeans);

                //////Sorting name////////
                Collections.sort(countrylist, new Comparator<AllBeans>() {
                    @Override
                    public int compare(AllBeans lhs, AllBeans rhs) {
                        return lhs.getCountryName().compareTo(rhs.getCountryName());
                    }
                });
                //////Sorting name////////

            }


            if (countrylist != null) {
                countryAdapter = new CountryAdapter(CountryListActivity.this, countrylist);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CountryListActivity.this);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.addItemDecoration(new CountryListActivity.VerticalSpaceItemDecoration(5));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(countryAdapter);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.country_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) CountryListActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();

        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(CountryListActivity.this.getComponentName()));
            searchView.setIconifiedByDefault(true);
        }

        final SearchView finalSearchView = searchView;
        finalSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String value = finalSearchView.getQuery().toString().toLowerCase(Locale.getDefault());
                countryAdapter.filter(value.toLowerCase());
                return true;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int mVerticalSpaceHeight;

        public VerticalSpaceItemDecoration(int mVerticalSpaceHeight) {
            this.mVerticalSpaceHeight = mVerticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = mVerticalSpaceHeight;
        }
    }
}
