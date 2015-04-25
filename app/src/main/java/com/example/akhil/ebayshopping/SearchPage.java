package com.example.akhil.ebayshopping;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

public class SearchPage extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //on-click method for clear
    public void clearForm(View view) {
        EditText keywords = (EditText)findViewById(R.id.keywordField);
        keywords.setText("");
        keywords.setError(null);
        EditText pricestart = (EditText)findViewById(R.id.pricestartField);
        pricestart.setText("");
        EditText priceto = (EditText)findViewById(R.id.pricetoField);
        priceto.setText("");
        priceto.setError(null);
        Spinner sort = (Spinner)findViewById(R.id.sortBySpinner);
        sort.setSelection(0);
        TextView errorBox = (TextView) findViewById(R.id.erroBox);
        errorBox.setVisibility(View.INVISIBLE);
    }

    //on-click method for search
    public void performSearch(View view) {
        boolean status = validateForm();
        if(!status)
            return;
        TextView errorBox = (TextView) findViewById(R.id.erroBox);
        errorBox.setVisibility(View.INVISIBLE);
        EditText keywords = (EditText)findViewById(R.id.keywordField);
        String keyword = keywords.getText().toString();
        String encodedKeyword = null;
        try {
            encodedKeyword = URLEncoder.encode(keyword, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        EditText pricestart = (EditText)findViewById(R.id.pricestartField);
        EditText priceto = (EditText)findViewById(R.id.pricetoField);
        Spinner sort = (Spinner)findViewById(R.id.sortBySpinner);
        int sortBy = sort.getSelectedItemPosition();
        String sortOption = null;
        switch(sortBy) {
            case 0:
                sortOption = "BestMatch";
                break;

            case 1:
                sortOption = "CurrentPriceHighest";
                break;

            case 2:
                sortOption = "PricePlusShippingHighest";
                break;

            case 3:
                sortOption = "PricePlusShippingLowest";
                break;
        }

        String url = "http://akhilram-cs571-webapp.elasticbeanstalk.com/search-ebay.php?keywords=" +
                encodedKeyword + "&MinPrice="+ pricestart.getText().toString() + "&MaxPrice=" + priceto.getText().toString() +
                "&sortOrder=" + sortOption + "&entriesPerPage=5&pageNum=1";

        AsyncTask<String, String, String> httpResponse = new RequestTask().execute(url);
        String httpOutput = null;
        JSONObject jsonObj = null;

        try {
            httpOutput = (String) httpResponse.get();
            int i = 0;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        try {
            jsonObj = new JSONObject(httpOutput);
            jsonObj.put("keyword", keyword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            String value = jsonObj.get("resultCount").toString();
            if(jsonObj.get("resultCount").toString().equals("0")) {
                noResultsFound();
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent i = new Intent(this, Results.class);
        i.putExtra("jsonResult", jsonObj.toString());
        startActivity(i);
    }

    //validation for search button
    public boolean validateForm() {
        EditText keywords = (EditText)findViewById(R.id.keywordField);
        if( keywords.getText().toString().length() == 0 ) {
            keywords.setError("Keywords is required!");
            return false;
        }

        EditText from = (EditText)findViewById(R.id.pricestartField);
        EditText to = (EditText)findViewById(R.id.pricetoField);

        String temp = to.getText().toString();

        if(to.getText().toString().equals("") || from.getText().toString().equals(""))
            return true;

        if(Float.parseFloat(from.getText().toString()) > Float.parseFloat(to.getText().toString())) {
            to.setError("From price cannot be greater");
            return false;
        }

        return true;
    }

    public void noResultsFound() {
        TextView errorBox = (TextView) findViewById(R.id.erroBox);
        errorBox.setVisibility(View.VISIBLE);
    }

}
