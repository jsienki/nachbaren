package ch.eltra.notkauf;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ch.eltra.notkauf.MainActivity.handler;

public class TabFragment2 extends Fragment {
    private RecyclerView mRecyclerView, mAcceptedView;
    private RecyclerAdapter mAdapter, mAcceptedAdapter;
    private ArrayList<RecyclerItem> recyclerList, acceptedList;
    private Button addRequest;
    private SwipeRefreshLayout mSwipeContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_frag2, container, false);
        mRecyclerView = view.findViewById(R.id.recycler);
        mAcceptedView = view.findViewById(R.id.recyclerAccepted);
        addRequest = view.findViewById(R.id.addRequest);

        addRequest.setOnClickListener(v -> {
            JSONObject contact = null;
            try {
                contact = handler.getJSON("/api/Contacts/get-contact");
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (contact != null) {
                Intent myIntent = new Intent(getActivity(), PopupAddRequest.class);
                Objects.requireNonNull(getActivity()).startActivity(myIntent);
            } else {
                Toast.makeText(getActivity(), getString(R.string.contact_warning), Toast.LENGTH_LONG).show();
            }
        });
        try {
            setupRecycler();
            setupAcceptedRecycler();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setupSwiper(view);

        return view;
    }

    private void setupRecycler() throws IOException {
        recyclerList = new ArrayList<>();
        getOrders();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new RecyclerAdapter(recyclerList);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(position -> {
            Intent myIntent = new Intent(getActivity(), DetailsPopup.class);
            myIntent.putExtra("Uuid", recyclerList.get(position).getText1());
            myIntent.putExtra("mode", 1);
            startActivity(myIntent);
        });
    }

    private void setupAcceptedRecycler() throws IOException {
        acceptedList = new ArrayList<>();
        getAcceptedOrders();

        RecyclerView.LayoutManager mAcceptedLayoutManager = new LinearLayoutManager(getActivity());
        mAcceptedAdapter = new RecyclerAdapter(acceptedList);

        mAcceptedView.setHasFixedSize(true);
        mAcceptedView.setLayoutManager(mAcceptedLayoutManager);
        mAcceptedView.setAdapter(mAcceptedAdapter);

        mAcceptedAdapter.setOnItemClickListener(position -> {
            Intent myIntent = new Intent(getActivity(), DetailsPopup.class);
            myIntent.putExtra("Uuid", acceptedList.get(position).getText1());
            myIntent.putExtra("mode", 2);
            startActivity(myIntent);
        });
    }

    private void getOrders() throws IOException {
        JSONArray orders = handler.getJSONArray("/api/Orders/get-my-orders");
        Gson gson = new Gson();
        Type listType = new TypeToken<List<OrderModel>>() {
        }.getType();
        recyclerList.clear();
        if (orders != null) {
            List<OrderModel> modelList = gson.fromJson(orders.toString(), listType);
            for (int i = 0; i < modelList.size(); i++) {
                JSONObject myJSON = handler.getJSONParams("/api/Orders/get-order-details", modelList.get(i).Uuid);
                if (myJSON != null) {
                    OrderInfoModel infoModel = gson.fromJson(myJSON.toString(), OrderInfoModel.class);
                    MessageModel msgModel = gson.fromJson(infoModel.Order.Message, MessageModel.class);

                    recyclerList.add(i, new RecyclerItem(infoModel.Order.Uuid, infoModel.Order.Created.substring(11, 16), "",
                            msgModel.Shop, msgModel.Drugstore, msgModel.Car, msgModel.Other, infoModel.AssignedTo.size()));
                }
            }
        }
        if (recyclerList.size() > 0) {
            addRequest.setEnabled(false);
        } else {
            addRequest.setEnabled(true);
        }
    }

    private void getAcceptedOrders() throws IOException {
        JSONArray orders = handler.getJSONArray("/api/Orders/get-assigned");
        Gson gson = new Gson();
        Type listType = new TypeToken<List<OrderInfoModel>>() {
        }.getType();
        acceptedList.clear();

        if (orders != null) {
            List<OrderInfoModel> modelList = gson.fromJson(orders.toString(), listType);

            for (int i = 0; i < modelList.size(); i++) {

                MessageModel msgModel = gson.fromJson(modelList.get(i).Order.Message, MessageModel.class);

                acceptedList.add(i, new RecyclerItem(modelList.get(i).Order.Uuid, modelList.get(i).Order.Created.substring(11, 16), "",
                        msgModel.Shop, msgModel.Drugstore, msgModel.Car, msgModel.Other, modelList.get(i).AssignedTo.size()));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            try {
                getOrders();
                getAcceptedOrders();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mAdapter.notifyDataSetChanged();
            mAcceptedAdapter.notifyDataSetChanged();
        }, 1000);
    }

    private void setupSwiper(View view) {
        mSwipeContainer = view.findViewById(R.id.swipeContainer);
        mSwipeContainer.setOnRefreshListener(() -> {
            try {
                getAcceptedOrders();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mAcceptedAdapter.notifyDataSetChanged();

            new Handler().postDelayed(() -> mSwipeContainer.setRefreshing(false), 2000);
        });
        mSwipeContainer.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
    }
}
