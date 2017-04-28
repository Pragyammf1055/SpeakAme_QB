package com.speakame.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.rockerhieu.emojicon.EmojiconEditText;
import com.speakame.Database.DatabaseHelper;
import com.speakame.R;
import com.speakame.utils.AppConstants;
import com.speakame.utils.AppPreferences;
import com.speakame.utils.JSONParser;
import com.speakame.utils.VolleyCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EditNameActivity extends AppCompatActivity implements View.OnClickListener {

    EmojiconEditText editname;
    ImageView emojiImageView;
    Button cancel, ok;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit group name");

        initView();

        id = getIntent().getStringExtra("Groupid");

    }

    private void initView() {
        editname = (EmojiconEditText) findViewById(R.id.editname);
        emojiImageView = (ImageView) findViewById(R.id.emojiImageView);
        cancel = (Button) findViewById(R.id.cancel);
        ok = (Button) findViewById(R.id.ok);

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);
        emojiImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ok:
                try {
                    String text = editname.getText().toString();
                    if(TextUtils.isEmpty(text)) {
                        Toast.makeText(EditNameActivity.this, "Please enter text...",Toast.LENGTH_LONG).show();
                    }else{
                        changeGName(text);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.cancel:
                finish();
                break;
            case R.id.emojiImageView:
                break;
        }
    }

    private void changeGName(String text) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("method", AppConstants.GROUP_NAME_UPDATE);
        jsonObject.put("user_id", AppPreferences.getLoginId(EditNameActivity.this));
        jsonObject.put("group_id",id);
        jsonObject.put("group_name", text);
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(jsonObject);

        final ProgressDialog progressDialog  = ProgressDialog.show(EditNameActivity.this, "", "Group Image updating.....", false);

        JSONParser jsonParser = new JSONParser(EditNameActivity.this);
        jsonParser.parseVollyJsonArray(AppConstants.USERGROUPURL, 1, jsonArray, new VolleyCallback() {
            @Override
            public void backResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    if(object.getString("status").equalsIgnoreCase("200")){
                        JSONArray array = object.getJSONArray("result");
                        JSONObject object1 = array.getJSONObject(0);
                        String group_name = object1.getString("update_group_name");

                        Intent intent = new Intent();
                        intent.putExtra("name",group_name);
                        setResult(RESULT_OK, intent);
                        finish();

                    }else if(object.getString("status").equalsIgnoreCase("200")){
                        Toast.makeText(EditNameActivity.this, "Server not respond", Toast.LENGTH_LONG).show();
                    }else{
                        JSONArray array = object.getJSONArray("result");
                        JSONObject object1 = array.getJSONObject(0);
                        Toast.makeText(EditNameActivity.this, object1.getString("msg"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });
    }
}
