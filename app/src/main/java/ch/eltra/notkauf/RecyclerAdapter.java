package ch.eltra.notkauf;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {
    private ArrayList<RecyclerItem> mRecyclerList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onDetailsClick(int position) throws IOException;
    }

    void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    RecyclerAdapter(ArrayList<RecyclerItem> recyclerList) {
        mRecyclerList = recyclerList;
    }
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new RecyclerViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        RecyclerItem currentItem = mRecyclerList.get(position);

        holder.mTextView1.setText(currentItem.getText1());
        holder.mTextView2.setText(currentItem.getText2());
        holder.mTextView3.setText(currentItem.getText3());
        holder.mTextView5.setText(Integer.toString(currentItem.getPeople()));
        holder.mFood.setVisibility(GONE);
        holder.mDrugs.setVisibility(GONE);
        holder.mCar.setVisibility(GONE);
        holder.mOther.setVisibility(GONE);

        if (currentItem.getFood()) {
            holder.mFood.setVisibility(VISIBLE);
        }
        if (currentItem.getDrugs()) {
            holder.mDrugs.setVisibility(VISIBLE);
        }
        if (currentItem.getCar()) {
            holder.mCar.setVisibility(VISIBLE);
        }
        if (currentItem.getOther()) {
            holder.mOther.setVisibility(VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mRecyclerList.size();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView1;
        TextView mTextView2;
        TextView mTextView3;
        TextView mTextView4;
        TextView mTextView5;
        ImageView mFood;
        ImageView mDrugs;
        ImageView mCar;
        ImageView mOther;

        RecyclerViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            mTextView1 = itemView.findViewById(R.id.text1);
            mTextView2 = itemView.findViewById(R.id.text2);
            mTextView3 = itemView.findViewById(R.id.distanceText);
            mTextView4 = itemView.findViewById(R.id.detailsText);
            mTextView5 = itemView.findViewById(R.id.people);
            mFood = itemView.findViewById(R.id.foodImg);
            mDrugs = itemView.findViewById(R.id.drugImg);
            mCar = itemView.findViewById(R.id.transportImg);
            mOther = itemView.findViewById(R.id.otherImg);

            mTextView4.setOnClickListener(v -> {
               if (listener != null) {
                   int position = getAdapterPosition();
                   if (position != RecyclerView.NO_POSITION) {
                       try {
                           listener.onDetailsClick(position);
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   }
               }
            });
        }
    }
}
