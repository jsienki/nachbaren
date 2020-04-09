package ch.eltra.notkauf;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import static ch.eltra.notkauf.MainActivity.handler;

public class PopupAddRequest extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_addreq);
        DisplayMetrics dm = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.5));

        EditText textInput = findViewById(R.id.textInput);
        TextView limit = findViewById(R.id.limit);
        Button addButton = findViewById(R.id.addButton);
        Button cancelButton = findViewById(R.id.cancelButton);
        CheckBox foodCheck = findViewById(R.id.foodCheck);
        CheckBox drugCheck = findViewById(R.id.drugCheck);
        CheckBox transportCheck = findViewById(R.id.transportCheck);
        CheckBox otherCheck = findViewById(R.id.otherCheck);

        TextWatcher inputTextWatcher = new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                limit.setText(textInput.getText().toString().length() + "/255");
            }
        };

        textInput.addTextChangedListener(inputTextWatcher);

        addButton.setOnClickListener(v -> {
           if (!textInput.getText().toString().equals("")) {

               MessageModel msgModel = new MessageModel();

               msgModel.Car = transportCheck.isChecked();
               msgModel.Drugstore = drugCheck.isChecked();
               msgModel.Notice = textInput.getText().toString();
               msgModel.Shop = foodCheck.isChecked();
               msgModel.Other = otherCheck.isChecked();

               if (!msgModel.Car && !msgModel.Drugstore && !msgModel.Shop && !msgModel.Other) {
                   msgModel.Other = true;
               }

               Gson myMsgGson = new GsonBuilder().setPrettyPrinting().create();
               Gson myOrderGson = new GsonBuilder().setPrettyPrinting().create();

               OrderModel myOrderModel = new OrderModel();

               myOrderModel.Message = myMsgGson.toJson(msgModel);

               String orderJson = myOrderGson.toJson(myOrderModel);

               try {
                   int responseCode = handler.post("/api/Orders/add", orderJson);
                   if (responseCode == 200) {
                       Toast.makeText(this, getString(R.string.success), Toast.LENGTH_LONG).show();
                   } else {
                       Toast.makeText(this, getString(R.string.adding_req_failed), Toast.LENGTH_LONG).show();
                   }
                   finish();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
        });

        cancelButton.setOnClickListener(v -> finish());

    }
}
