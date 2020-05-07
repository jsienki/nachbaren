package ch.eltra.notkauf;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static android.view.View.GONE;
import static ch.eltra.notkauf.MainActivity.handler;

public class DetailsPopup extends Activity {
    TextView uuidText, messageText, firstNameText, lastNameText, streetText, cityText, noticeText, phoneText, helpersText;
    Button acceptButton, closeButton, cancelButton, leaveButton;
    private ArrayList<DetailsItem> recyclerList;
    private JSONObject myJSON;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_menu);
        DisplayMetrics dm = new DisplayMetrics();

        uuidText = findViewById(R.id.uuidText);
        messageText = findViewById(R.id.messageText);
        firstNameText = findViewById(R.id.firstNameText);
        lastNameText = findViewById(R.id.lastNameText);
        streetText = findViewById(R.id.streetText);
        cityText = findViewById(R.id.cityText);
        noticeText = findViewById(R.id.noticeText);
        phoneText = findViewById(R.id.phoneText);
        helpersText = findViewById(R.id.helpersText);
        acceptButton = findViewById(R.id.acceptButton);
        closeButton = findViewById(R.id.closeButton);
        cancelButton = findViewById(R.id.cancelButton);
        leaveButton = findViewById(R.id.leaveButton);

        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.8));

        helpersText.setVisibility(GONE);

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
                break;
            case 2:
                acceptButton.setVisibility(GONE);
                cancelButton.setVisibility(GONE);
        }

        closeButton.setOnClickListener(v -> finish());


        myJSON = null;

        try {
            myJSON = handler.getJSONParams("/api/Orders/get-order-details", Uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (myJSON != null) {
            Gson gson = new Gson();

            OrderInfoModel infoModel = gson.fromJson(myJSON.toString(), OrderInfoModel.class);
            MessageModel msgModel = gson.fromJson(infoModel.Order.Message, MessageModel.class);

            uuidText.setText(infoModel.Order.Uuid);
            messageText.setText(msgModel.Notice);
            firstNameText.setText(infoModel.CreatedBy.FirstName);
            lastNameText.setText(infoModel.CreatedBy.LastName);
            streetText.setText(infoModel.CreatedBy.Street);
            cityText.setText(infoModel.CreatedBy.PostalCode + " " + infoModel.CreatedBy.City);
            noticeText.setText(infoModel.CreatedBy.Notice);
            phoneText.setText(infoModel.CreatedBy.Phone);
        }

        acceptButton.setOnClickListener(v -> {
            if (myJSON != null) {
                try {
                    Gson gson = new Gson();
                    String contactUuid = null;

                    JSONObject contact = null;
                    try {
                        contact = handler.getJSON("/api/Contacts/get-contact");

                        contactUuid = contact.get("Uuid").toString();

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    OrderInfoModel infoModel = gson.fromJson(myJSON.toString(), OrderInfoModel.class);

                    OrderModel order = infoModel.Order;

                    AssignmentEntryModel assignmentEntryModel = new AssignmentEntryModel();

                    assignmentEntryModel.createdByUuid = contactUuid;
                    assignmentEntryModel.orderUuid = order.Uuid;
                    assignmentEntryModel.status = AssignedStatus.Assigned.ordinal();

                    int responseCode = handler.post("/api/orders/set-order-assignment?api-version=1.1", gson.toJson(assignmentEntryModel));

                    if (responseCode == 200) {
                        Toast.makeText(DetailsPopup.this, getString(R.string.success), Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            finish();
        });

        cancelButton.setOnClickListener(v -> {
            if (myJSON != null) {
                try {
                    Gson gson = new Gson();
                    OrderInfoModel infoModel = gson.fromJson(myJSON.toString(), OrderInfoModel.class);
                    OrderModel order = infoModel.Order;

                    final int Closed = 3;

                    order.Status = Closed;

                    int responseCode = handler.post("/api/Orders/change-order", gson.toJson(order));
                    if (responseCode == 200) {
                        Toast.makeText(DetailsPopup.this, getString(R.string.success), Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            finish();
        });

        leaveButton.setOnClickListener(v -> {
            if (myJSON != null) {
                try {
                    Gson gson = new Gson();
                    String contactUuid = null;

                    JSONObject contact = null;
                    try {
                        contact = handler.getJSON("/api/Contacts/get-contact");

                        contactUuid = contact.get("Uuid").toString();

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    OrderInfoModel infoModel = gson.fromJson(myJSON.toString(), OrderInfoModel.class);

                    OrderModel order = infoModel.Order;

                    AssignmentEntryModel assignmentEntryModel = new AssignmentEntryModel();

                    assignmentEntryModel.createdByUuid = contactUuid;
                    assignmentEntryModel.orderUuid = order.Uuid;
                    assignmentEntryModel.status = AssignedStatus.Unassigned.ordinal();

                    int responseCode = handler.post("/api/orders/set-order-assignment?api-version=1.1", gson.toJson(assignmentEntryModel));

                    if (responseCode == 200) {
                        Toast.makeText(DetailsPopup.this, getString(R.string.success), Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            finish();
        });

        setupRecycler();
        if (recyclerList.size() > 0) {
           helpersText.setVisibility(View.VISIBLE);
        }
    }

    void setupRecycler() {
        RecyclerView mRecyclerView = findViewById(R.id.detailsRecycler);
        recyclerList = new ArrayList<>();
        if (myJSON != null) {
            Gson gson = new Gson();
            OrderInfoModel infoModel = gson.fromJson(myJSON.toString(), OrderInfoModel.class);
            for (int i = 0; i < infoModel.AssignedTo.size(); i++) {

                String firstName = infoModel.AssignedTo.get(i).FirstName;
                String lastName = infoModel.AssignedTo.get(i).LastName;
                String phone = infoModel.AssignedTo.get(i).Phone;

                recyclerList.add(new DetailsItem(firstName + " " + lastName, phone));
            }
        }
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        DetailsAdapter mAdapter = new DetailsAdapter(recyclerList);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(position -> {
            Intent myIntent = new Intent(Intent.ACTION_DIAL);
            myIntent.setData(Uri.parse("tel:" + recyclerList.get(position).getText2()));
            startActivity(myIntent);
        });
    }
}
