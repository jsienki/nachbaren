package ch.eltra.notkauf;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static ch.eltra.notkauf.MainActivity.handler;

public class TabFragment2 extends Fragment {
    private static final String TAG = "TabFragment2";
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<RecyclerItem> recyclerList;
    private Button addRequest;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_frag2, container, false);
        mRecyclerView = view.findViewById(R.id.recycler);
        addRequest = view.findViewById(R.id.addRequest);

        addRequest.setOnClickListener(v -> {
            Intent myIntent = new Intent(getActivity(), PopupAddRequest.class);
            getActivity().startActivity(myIntent);
        });
        try {
            setupRecycler();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    private void setupRecycler() throws IOException, JSONException {
        recyclerList = new ArrayList<>();
        getOrders();
        if (recyclerList.size() > 0) {
            addRequest.setEnabled(false);
        }
        mLayoutManager = new GridLayoutManager(getActivity(), 1);
        mAdapter = new RecyclerAdapter(recyclerList);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onDetailsClick(int position) {
                Intent myIntent = new Intent(getActivity(), DetailsPopup.class);
                myIntent.putExtra("Uuid", recyclerList.get(position).getText1());
                myIntent.putExtra("mode", 1);
                startActivity(myIntent);
            }
        });
    }

    private void getOrders() throws IOException {
        JSONArray orders = handler.getJSONArray("/api/Orders/get");
        Gson gson = new Gson();
        Type listType = new TypeToken<List<OrderModel>>() {
        }.getType();
        recyclerList.clear();
        if (orders != null) {
            List<OrderModel> modelList = gson.fromJson(orders.toString(), listType);
            for (int i = 0; i < modelList.size(); i++) {
                JSONObject myJSON = handler.getJSONParams("/api/Orders/info", modelList.get(i).Uuid);
                OrderInfoModel infoModel = gson.fromJson(myJSON.toString(), OrderInfoModel.class);
                MessageModel msgModel = gson.fromJson(infoModel.Order.Message, MessageModel.class);

                recyclerList.add(i, new RecyclerItem(infoModel.Order.Uuid, infoModel.Order.Created.substring(11, 16), "1km",
                        msgModel.Shop, msgModel.Drugstore, msgModel.Car, msgModel.Other, infoModel.AssignedTo.size()));
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mAdapter.notifyDataSetChanged();
            }
        }, 1000);
    }

}
