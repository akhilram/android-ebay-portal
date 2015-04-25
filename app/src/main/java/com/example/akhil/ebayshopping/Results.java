package com.example.akhil.ebayshopping;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.net.URI;

public class Results extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        JSONObject resultJSON = null;
        try {
            resultJSON = new JSONObject(getIntent().getStringExtra("jsonResult"));
            int resultCount = Integer.parseInt(resultJSON.getString("resultCount"));
            if(resultCount < 5){
                while(resultCount<5) {
                    int rowId = getResources().getIdentifier("item" + Integer.toString(resultCount), "id", "com.example.akhil.ebayshopping");
                    ((TableRow) findViewById(rowId)).setVisibility(View.GONE);
                    resultCount++;
                }
            }

            TextView header = (TextView)findViewById(R.id.resultHeader);
            header.setText("Results for '" + resultJSON.get("keyword") + "'");

            Iterator<?> keys = resultJSON.keys();

            while( keys.hasNext() ) {
                String key = (String)keys.next();
                if ( resultJSON.get(key) instanceof JSONObject ) {
                    if(key.toString().startsWith("item") && !key.toString().equals("itemCount")) {

                        JSONObject item = resultJSON.getJSONObject(key);
                        JSONObject basicInfo = item.getJSONObject("basicInfo");

                        int imageResId = getResources().getIdentifier("image" + key.charAt(4), "id", "com.example.akhil.ebayshopping");
                        ImageView i = (ImageView)findViewById(imageResId);
                        String imageUrl = basicInfo.getString("galleryURL");
                        new ImageLoadTask(imageUrl, i).execute();

                        int titleResId = getResources().getIdentifier("title" + key.charAt(4), "id", "com.example.akhil.ebayshopping");
                        TextView title = (TextView) findViewById(titleResId);
                        title.setText(basicInfo.getString("title"));

                        String priceTag = "Price: $" + Double.toString(basicInfo.getDouble("convertedCurrentPrice")) + " ";
                        if(basicInfo.getDouble("shippingServiceCost") == 0) {
                            priceTag += "(FREE SHIPPING)";
                        }
                        else {
                            priceTag += "(+$" + Double.toString(basicInfo.getDouble("shippingServiceCost")) + " for shipping)";
                        }

                        int priceResId = getResources().getIdentifier("price" + key.charAt(4), "id", "com.example.akhil.ebayshopping");
                        TextView price = (TextView) findViewById(priceResId);
                        price.setText(priceTag);

                        //set hidden text view
                        int hiddenResId = getResources().getIdentifier("hidden" + key.charAt(4), "id", "com.example.akhil.ebayshopping");
                        TextView hidden = (TextView) findViewById(hiddenResId);
                        hidden.setText(item.toString());
                    }
                }
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_results, menu);
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

    //on-click event for title
    public void gotDetails(View view) {
        int viewId = view.getId();
        String hiddenIdString = "hidden" + view.getResources().getResourceName(viewId).charAt(39);

        TextView hiddenView = (TextView)findViewById(getResources().getIdentifier(hiddenIdString, "id", "com.example.akhil.ebayshopping"));

        Intent i = new Intent(this, details.class);
        i.putExtra("item", hiddenView.getText());
        startActivity(i);
    }

    //on-click event for image
    public void openWebPage(View view) {
        int viewId = view.getId();

        String hiddenIdString = "hidden" + view.getResources().getResourceName(viewId).charAt(39);
        TextView hiddenView = (TextView)findViewById(getResources().getIdentifier(hiddenIdString, "id", "com.example.akhil.ebayshopping"));

        JSONObject hiddenObj = null;

        try {
            hiddenObj = new JSONObject(hiddenView.getText().toString());
            String itemURL = ((JSONObject)hiddenObj.get("basicInfo")).getString("viewItemURL");
            Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(itemURL));
            startActivity(viewIntent);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}