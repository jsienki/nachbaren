package ch.eltra.notkauf;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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
    private static final String TAG = "TabFragment1";
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<RecyclerItem> recyclerList;
    private Spinner dropdown;
    private SwipeRefreshLayout mSwipeContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_frag1, container, false);
        dropdown = view.findViewById(R.id.spinner);
        setupSpinner();
        mSwipeContainer = view.findViewById(R.id.swipeContainer);
        mRecyclerView = view.findViewById(R.id.recycler);
        try {
            setupRecycler();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        setupSwiper(view);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    getOrders();
                    mAdapter.notifyDataSetChanged();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    private void setupRecycler() throws IOException, JSONException {
        recyclerList = new ArrayList<>();
        getOrders();
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mAdapter = new RecyclerAdapter(recyclerList);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onDetailsClick(int position) {
                Intent myIntent = new Intent(getActivity(), DetailsPopup.class);
                myIntent.putExtra("Uuid", recyclerList.get(position).getText1());
                myIntent.putExtra("mode", 0);
                startActivity(myIntent);
            }
        });

    }

    private void setupSwiper(View view) {
        mSwipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    getOrders();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                mAdapter.notifyDataSetChanged();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeContainer.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        mSwipeContainer.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
    }

    private void setupSpinner() {
        String[] items = new String[]{"City", "Region", "Country"};
        ArrayAdapter adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
    }

    private void getOrders() throws IOException, JSONException {
        //JSONArray orders = handler.getJSONArray("/api/Orders/get-all");
        JSONArray orders = null;
        JSONObject contact = handler.getJSON("/api/Contacts/get");
        switch (dropdown.getSelectedItem().toString()) {
            case "City":
                orders = handler.getJSONArrayParams("/api/Orders/get-all-region", contact.get("City").toString(), dropdown.getSelectedItem().toString());
                break;
            case "Region":
                orders = handler.getJSONArrayParams("/api/Orders/get-all-Region", contact.get("Region").toString(), dropdown.getSelectedItem().toString());
                break;
            case "Country":
                orders = handler.getJSONArrayParams("/api/Orders/get-all-Region", null, dropdown.getSelectedItem().toString());
                break;
            default:
                break;
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<OrderInfoModel>>() {
        }.getType();
        recyclerList.clear();

        if (orders != null) {
            List<OrderInfoModel> modelList = gson.fromJson(orders.toString(), listType);

            for (int i = 0; i < modelList.size(); i++) {

                MessageModel msgModel = gson.fromJson(modelList.get(i).Order.Message, MessageModel.class);

                recyclerList.add(i, new RecyclerItem(modelList.get(i).Order.Uuid, modelList.get(i).Order.Created.substring(11, 16), "1km",
                        msgModel.Shop, msgModel.Drugstore, msgModel.Car, msgModel.Other, modelList.get(i).AssignedTo.size()));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    getOrders();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                mAdapter.notifyDataSetChanged();
            }
        }, 1000);
    }
}
