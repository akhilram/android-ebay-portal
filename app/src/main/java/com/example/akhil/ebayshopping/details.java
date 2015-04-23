package com.example.akhil.ebayshopping;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;


public class details extends ActionBarActivity {

    CallbackManager callbackManager;
    ShareDialog shareDialog;
    static JSONObject basic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {

            @Override
            public void onSuccess(Sharer.Result result) {
                if(result.getPostId() == null) {
                    Toast.makeText(getBaseContext(), "Post cancelled", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getBaseContext(), "Posted Story, ID:" + result.getPostId(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onError(FacebookException error) {
                // Log.d(TAG, "error");
                Toast.makeText(getBaseContext(), "Posting failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                //Log.d(TAG, "cancel");
                Toast.makeText(getBaseContext(), "Post Cancelled", Toast.LENGTH_SHORT).show();

            }
        });


        JSONObject itemJSON = null;
        try {

            itemJSON = new JSONObject(getIntent().getStringExtra("item"));
            JSONObject basicInfo = itemJSON.getJSONObject("basicInfo");
            basic = itemJSON.getJSONObject("basicInfo");
            JSONObject sellerInfo = itemJSON.getJSONObject("sellerInfo");
            JSONObject shippingInfo = itemJSON.getJSONObject("shippingInfo");
            ImageView i = (ImageView)findViewById(R.id.headerImage);
            String imageUrl = "";

            if(basicInfo.getString("pictureURLSuperSize").isEmpty() || basicInfo.getString("pictureURLSuperSize") == null) {
                imageUrl = basicInfo.getString("galleryURL");
            }
            else {
                imageUrl = basicInfo.getString("pictureURLSuperSize");
            }
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

            //basic info tab
            TextView category = (TextView) findViewById(R.id.categoryField);
            category.setText(basicInfo.getString("categoryName"));
            TextView condition = (TextView) findViewById(R.id.conditionField);
            condition.setText(basicInfo.getString("conditionDisplayName"));
            TextView buyingFormat = (TextView) findViewById(R.id.buyingFormatField);
            buyingFormat.setText(basicInfo.getString("listingType"));

            //seller info tab
            TextView userName = (TextView) findViewById(R.id.userNameField);
            userName.setText(sellerInfo.getString("sellerUserName"));
            TextView feedbackScore = (TextView) findViewById(R.id.feedbackScoreField);
            feedbackScore.setText(sellerInfo.getString("feedbackScore"));
            TextView positiveFeedback = (TextView) findViewById(R.id.positiveFeedbackField);
            positiveFeedback.setText(sellerInfo.getString("positiveFeedbackPercent"));
            TextView feedbackRating = (TextView) findViewById(R.id.feedbackRatingField);
            feedbackRating.setText(sellerInfo.getString("feedbackRatingStar"));
            ImageView topRated = (ImageView)findViewById(R.id.topRatedField);
            if(sellerInfo.getString("topRatedSeller").equals("true")) {
                topRated.setImageResource(R.drawable.ok);
            }
            else {
                topRated.setImageResource(R.drawable.cancel);
            }
            TextView store = (TextView) findViewById(R.id.storeField);
            if(sellerInfo.getString("sellerStoreName").isEmpty())
                store.setText("N/A");
            else
                store.setText(sellerInfo.getString("sellerStoreName"));

            //shipping info tab
            TextView shippingType = (TextView) findViewById(R.id.shippingTypeField);
            String shippingTypes = shippingInfo.getString("shippingType");
            shippingTypes = splitCamelCase(shippingTypes);
            shippingType.setText(shippingTypes);
            TextView handlingTime = (TextView) findViewById(R.id.handlingTimeField);
            handlingTime.setText(shippingInfo.getString("handlingTime"));

            TextView shippingLocations = (TextView) findViewById(R.id.shippingLocationsField);
            JSONArray shippingLocArray = (JSONArray) shippingInfo.get("shipToLocations");
            StringBuilder shippingLocText = new StringBuilder("");
            for(int j=0; j<shippingLocArray.length(); j++) {
                shippingLocText.append(shippingLocArray.get(j).toString() + ", ");
            }
            shippingLocText.substring(0, shippingLocText.length()-2);
            shippingLocations.setText(shippingLocText.substring(0, shippingLocText.length() - 2));

            ImageView expeditedShipping = (ImageView)findViewById(R.id.expeditedShippingField);
            if(shippingInfo.getString("expeditedShipping").equals("true")) {
                expeditedShipping.setImageResource(R.drawable.ok);
            }
            else {
                expeditedShipping.setImageResource(R.drawable.cancel);
            }
            ImageView oneDayShipping = (ImageView)findViewById(R.id.oneDayShippingField);
            if(shippingInfo.getString("oneDayShippingAvailable").equals("true")) {
                oneDayShipping.setImageResource(R.drawable.ok);
            }
            else {
                oneDayShipping.setImageResource(R.drawable.cancel);
            }
            ImageView returnsAccepted = (ImageView)findViewById(R.id.returnsAcceptedField);
            if(shippingInfo.getString("returnsAccepted").equals("true")) {
                returnsAccepted.setImageResource(R.drawable.ok);
            }
            else {
                returnsAccepted.setImageResource(R.drawable.cancel);
            }



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

    //on-click event for buy now
    public void buyNow(View view) {
        String itemURL = null;
        try {
            itemURL = basic.getString("viewItemURL");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(itemURL));
        startActivity(viewIntent);
    }



    //on-click event for fb share
    public void postToFB(View view) {

//        Toast.makeText(getBaseContext(), "Posted Story on Timeline", Toast.LENGTH_SHORT).show();

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = null;
            try {
                linkContent = new ShareLinkContent.Builder()
                        .setContentTitle(basic.getString("title"))
                        .setContentUrl(Uri.parse(basic.getString("viewItemURL")))
                        .setContentDescription(
                                    ((TextView) findViewById(R.id.price)).getText().toString() + "\n" +
                                    ((TextView) findViewById(R.id.shippingInfo)).getText().toString()
                                )
                        .setImageUrl(Uri.parse(basic.getString("galleryURL")))
                        .build();
            } catch (JSONException e) {
                e.printStackTrace();
            }

//            if(linkContent != null)
                shareDialog.show(linkContent);

//            ShareLinkContent content = new ShareLinkContent.Builder()
//                    .setContentUrl(Uri.parse("https://developers.facebook.com"))
//                    .build();
//
//            shareDialog.show(content);


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public String splitCamelCase(String s) {
        return s.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                ", "
        );
    }

}

