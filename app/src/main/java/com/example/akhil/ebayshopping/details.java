package com.example.akhil.ebayshopping;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


public class details extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        JSONObject itemJSON = null;
        try {

            itemJSON = new JSONObject(getIntent().getStringExtra("item"));
            JSONObject basicInfo = itemJSON.getJSONObject("basicInfo");

            ImageView i = (ImageView)findViewById(R.id.headerImage);
            String imageUrl = basicInfo.getString("galleryURL");
            new ImageLoadTask(imageUrl, i).execute();

            TextView title = (TextView) findViewById(R.id.title);
            title.setText(basicInfo.getString("title"));

            String priceTag = "Price: $" + Double.toString(basicInfo.getDouble("convertedCurrentPrice")) + " ";
            if(basicInfo.getDouble("shippingServiceCost") == 0) {
                priceTag += "(FREE SHIPPING)";
            }
            else {
                priceTag += "(+$" + Double.toString(basicInfo.getDouble("shippingServiceCost")) + " for shipping)";
            }

            TextView price = (TextView) findViewById(R.id.price);
            price.setText(priceTag);

            TextView shipping = (TextView) findViewById(R.id.shippingInfo);
            shipping.setText(basicInfo.getString("location"));

            ImageView top = (ImageView)findViewById(R.id.topRated);
            if(!basicInfo.get("topRatedListing").equals("true")) {
                top.setVisibility(View.INVISIBLE);
            }

            TabHost tabHost = (TabHost)findViewById(R.id.tabHost);
            tabHost.setup();

            TabHost.TabSpec tab1 = tabHost.newTabSpec("First Tab");
            TabHost.TabSpec tab2 = tabHost.newTabSpec("Second Tab");
            TabHost.TabSpec tab3 = tabHost.newTabSpec("Third Tab");

            tab1.setIndicator("basic info");
            tab1.setContent(R.id.basic);

            tab2.setIndicator("seller");
            tab2.setContent(R.id.seller);

            tab3.setIndicator("shipping");
            tab3.setContent(R.id.shipping);

            tabHost.addTab(tab1);
            tabHost.addTab(tab2);
            tabHost.addTab(tab3);

            TextView category = (TextView) findViewById(R.id.categoryField);
            category.setText(basicInfo.getString("categoryName"));
            TextView condition = (TextView) findViewById(R.id.conditionField);
            condition.setText(basicInfo.getString("conditionDisplayName"));
            TextView buyingFormat = (TextView) findViewById(R.id.buyingFormatField);
            buyingFormat.setText(basicInfo.getString("listingType"));

            ImageView topRated = (ImageView)findViewById(R.id.topRatedField);
//            int topRatedImage = getResources().getIdentifier("com.example.akhil.ebayshopping:drawable/ok.png", null, null);
            topRated.setImageResource(R.drawable.ok);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
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
}
