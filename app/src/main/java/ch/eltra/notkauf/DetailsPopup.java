package ch.eltra.notkauf;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;

import static android.view.View.GONE;
import static ch.eltra.notkauf.MainActivity.handler;

public class DetailsPopup extends Activity {
    TextView uuidText, messageText, nameText, streetText, cityText, noticeText, phoneText;
    Button acceptButton, closeButton, cancelButton, leaveButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_menu);
        DisplayMetrics dm = new DisplayMetrics();

        uuidText = findViewById(R.id.uuidText);
        messageText = findViewById(R.id.messageText);
        nameText = findViewById(R.id.nameText);
        streetText = findViewById(R.id.streetText);
        cityText = findViewById(R.id.cityText);
        noticeText = findViewById(R.id.noticeText);
        phoneText = findViewById(R.id.phoneText);
        acceptButton = findViewById(R.id.acceptButton);
        closeButton = findViewById(R.id.closeButton);
        cancelButton = findViewById(R.id.cancelButton);
        leaveButton = findViewById(R.id.leaveButton);

        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.6));

        Intent myIntent = getIntent();
        String Uuid = myIntent.getStringExtra("Uuid");
        switch (myIntent.getIntExtra("mode", 0)) {
            case 0:
                cancelButton.setVisibility(GONE);
                leaveButton.setVisibility(GONE);
                break;
            case 1:
                acceptButton.setVisibility(GONE);
                leaveButton.setVisibility(GONE);
        }

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        JSONObject myJSON = null;

        try {
            myJSON = handler.getJSONParams("/api/Orders/info", Uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (myJSON != null) {
            Gson gson = new Gson();
            OrderInfoModel infoModel = gson.fromJson(myJSON.toString(), OrderInfoModel.class);
            MessageModel msgModel = gson.fromJson(infoModel.Order.Message, MessageModel.class);

            uuidText.setText(infoModel.Order.Uuid);
            messageText.setText(msgModel.Notice);
            nameText.setText(infoModel.CreatedBy.Name);
            streetText.setText(infoModel.CreatedBy.Street);
            cityText.setText(infoModel.CreatedBy.PostalCode + " " + infoModel.CreatedBy.City);
            noticeText.setText(infoModel.CreatedBy.Notice);
            phoneText.setText(infoModel.CreatedBy.Phone);
        }

        JSONObject finalMyJSON = myJSON;
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalMyJSON != null) {
                    try {
                        Gson gson = new Gson();
                        OrderInfoModel infoModel = gson.fromJson(finalMyJSON.toString(), OrderInfoModel.class);
                        OrderModel order = infoModel.Order;
                        order.Status = 2;
                        int responseCode = handler.post("/api/Orders/change", gson.toJson(order));
                        if (responseCode == 200) {
                            Toast.makeText(DetailsPopup.this, "Successful", Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                finish();
            }
        });
    }
}
