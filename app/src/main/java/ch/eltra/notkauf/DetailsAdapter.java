package ch.eltra.notkauf;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.RecyclerViewHolder> {
    private ArrayList<DetailsItem> mRecyclerList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onPhoneClick(int position);
    }

    void setOnItemClickListener(DetailsAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    DetailsAdapter(ArrayList<DetailsItem> recyclerList) {
        mRecyclerList = recyclerList;
    }
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.details_item, parent, false);
        return new RecyclerViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        DetailsItem currentItem = mRecyclerList.get(position);
        holder.mTextView1.setText(currentItem.getText1());
    }

    @Override
    public int getItemCount() {
        return mRecyclerList.size();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView1;
        ImageView mImageView2;

        RecyclerViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            mTextView1 = itemView.findViewById(R.id.text1);
            mImageView2 = itemView.findViewById(R.id.phone);

            mImageView2.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onPhoneClick(position);
                    }
                }
            });

        }
    }
}
