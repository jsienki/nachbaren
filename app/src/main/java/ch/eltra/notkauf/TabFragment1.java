package ch.eltra.notkauf;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ch.eltra.notkauf.MainActivity.handler;

public class TabFragment1 extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;
    private ArrayList<RecyclerItem> recyclerList;
    private Spinner dropdown, countrySpinner, regionSpinner;
    private SwipeRefreshLayout mSwipeContainer;
    private RadioButton radioPeri, radioSearch;
    private EditText cityEdit, postalEdit;
    JSONObject _contact = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_frag1, container, false);
        dropdown = view.findViewById(R.id.spinner);
        countrySpinner = view.findViewById(R.id.countrySpinner);
        regionSpinner = view.findViewById(R.id.regionSpinner);
        radioPeri = view.findViewById(R.id.radioPeri);
        radioSearch = view.findViewById(R.id.radioSearch);
        mSwipeContainer = view.findViewById(R.id.swipeContainer);
        mRecyclerView = view.findViewById(R.id.recycler);
        cityEdit = view.findViewById(R.id.cityEdit);
        postalEdit = view.findViewById(R.id.postalEdit);

        try {
            _contact = handler.getJSON("/api/Contacts/get-contact");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (_contact == null) {
            radioSearch.setChecked(true);
            radioPeri.setChecked(false);
            dropdown.setEnabled(false);
            mSwipeContainer.setEnabled(false);
        } else {
            radioPeri.setChecked(true);
            radioSearch.setChecked(false);
            dropdown.setEnabled(true);
            mSwipeContainer.setEnabled(true);
            countrySpinner.setEnabled(false);
            regionSpinner.setEnabled(false);
            cityEdit.setEnabled(false);
            postalEdit.setEnabled(false);
        }

        try {
            setupSpinner();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            setupRecycler();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        setupSwipe(view);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (radioPeri.isChecked()) {
                        getOrders();
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
        radioPeri.setOnClickListener(v -> {
            radioPeri.setChecked(true);
            radioSearch.setChecked(false);
            dropdown.setEnabled(true);
            mSwipeContainer.setEnabled(true);
            try {
                getOrders();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            mAdapter.notifyDataSetChanged();
            countrySpinner.setEnabled(false);
            regionSpinner.setEnabled(false);
            cityEdit.setEnabled(false);
            postalEdit.setEnabled(false);
        });

        radioSearch.setOnClickListener(v -> {
            radioSearch.setChecked(true);
            radioPeri.setChecked(false);
            dropdown.setEnabled(false);
            mSwipeContainer.setEnabled(false);
            recyclerList.clear();
            mAdapter.notifyDataSetChanged();
            countrySpinner.setEnabled(true);
            regionSpinner.setEnabled(true);
            cityEdit.setEnabled(true);
            postalEdit.setEnabled(true);
        });

        return view;
    }

    private void setupRecycler() throws IOException, JSONException {
        recyclerList = new ArrayList<>();
        if (radioPeri.isChecked()) {
            getOrders();
        }
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mAdapter = new RecyclerAdapter(recyclerList);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(position -> {
            Intent myIntent = new Intent(getActivity(), DetailsPopup.class);
            String uuid = recyclerList.get(position).getText1();
            myIntent.putExtra("Uuid", uuid);
            if (checkIfAccepted(uuid)) {
                myIntent.putExtra("mode", 2);
            } else {
                myIntent.putExtra("mode", 0);
            }
            startActivity(myIntent);
        });

    }

    private boolean checkIfAccepted(String uuid)
    {
        boolean hasAccepted = false;

        try {
            JSONArray myJSON = handler.getJSONArray("/api/Orders/get-assigned");
            Gson gson = new Gson();
            Type listType = new TypeToken<List<OrderInfoModel>>() {
            }.getType();
            if (myJSON != null) {
                List<OrderInfoModel> modelList = gson.fromJson(myJSON.toString(), listType);

                for (int i = 0; i < modelList.size() && !hasAccepted; i++) {
                    List<ContactModel> assignedList = modelList.get(i).AssignedTo;

                    if (assignedList != null) {
                        for (int j = 0; j < assignedList.size(); j++) {
                            ContactModel contact = assignedList.get(j);

                            if (contact.Uuid.equals(_contact.get("Uuid").toString())) {
                                hasAccepted = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        catch (IOException ioEx)
        {
        }
        catch(JSONException jsonEx)
        {
        }

        return hasAccepted;
    }

    private void setupSwipe(View view) {
        mSwipeContainer = view.findViewById(R.id.swipeContainer);
        mSwipeContainer.setOnRefreshListener(() -> {
                try {
                    getOrders();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                mAdapter.notifyDataSetChanged();

                new Handler().postDelayed(() -> mSwipeContainer.setRefreshing(false), 2000);
        });
        mSwipeContainer.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
    }

    private void setupSpinner() throws IOException {
        String[] items = new String[]{getString(R.string.m_postal), getString(R.string.city), getString(R.string.region), getString(R.string.country)};
        ArrayAdapter adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        String[] items2 = new String[]{getString(R.string.switzerland)};
        ArrayAdapter adapter2 = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_dropdown_item, items2);
        countrySpinner.setAdapter(adapter2);

        JSONArray regions = handler.getJSONArrayParams3("/api/Regional/get-regions", "CH", "de");
        Gson gson = new Gson();
        Type listType = new TypeToken<List<RegionModel>>() {
        }.getType();

        List<RegionModel> reg = gson.fromJson(regions.toString(), listType);
        ArrayList<String> regNames = new ArrayList<>();
        for (int i = 0; i < reg.size(); i++) {
            regNames.add(reg.get(i).Name);
        }
        String[] items3 = regNames.toArray(items2);

        ArrayAdapter adapter3 = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_dropdown_item, items3);
        regionSpinner.setAdapter(adapter3);

        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
    }

    private void getOrders() throws IOException, JSONException {
        JSONArray orders = null;
        JSONObject contact = handler.getJSON("/api/Contacts/get-contact");
        if (contact != null) {
            switch (dropdown.getSelectedItemPosition()) {
                case 0:
                    orders = handler.getJSONArrayParams2("/api/Orders/get-orders-by-postal-code", "CH", contact.get("PostalCode").toString());
                    break;
                case 1:
                    orders = handler.getJSONArrayParams("/api/Orders/get-orders-by-region", "CH", contact.get("Region").toString(), contact.get("City").toString());
                    break;
                case 2:
                    orders = handler.getJSONArrayParams("/api/Orders/get-orders-by-region", "CH", contact.get("Region").toString(), null);
                    break;
                case 3:
                    orders = handler.getJSONArrayParams("/api/Orders/get-orders-by-region", "CH", null, null);
                    break;
                default:
                    break;
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.contact_warning), Toast.LENGTH_LONG).show();
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<OrderInfoModel>>() {
        }.getType();
        recyclerList.clear();

        if (orders != null) {
            List<OrderInfoModel> modelList = gson.fromJson(orders.toString(), listType);

            for (int i = 0; i < modelList.size(); i++) {

                MessageModel msgModel = gson.fromJson(modelList.get(i).Order.Message, MessageModel.class);

                recyclerList.add(i, new RecyclerItem(modelList.get(i).Order.Uuid, modelList.get(i).Order.Created.substring(11, 16), "",
                        msgModel.Shop, msgModel.Drugstore, msgModel.Car, msgModel.Other, modelList.get(i).AssignedTo.size()));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (radioPeri.isChecked()) {
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                try {
                    getOrders();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                mAdapter.notifyDataSetChanged();
            }, 1000);
        }
    }
}
