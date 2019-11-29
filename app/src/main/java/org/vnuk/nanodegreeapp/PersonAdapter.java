package org.vnuk.nanodegreeapp;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonAdapterViewHolder> {
    private String[] personsData;
    private final ItemClickListener itemClickListener;

    public interface ItemClickListener {
        void onItemClick(String personData);
    }

    public PersonAdapter(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public PersonAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int itemsLayoutID = R.layout.person_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachToParent = false;

        View view = inflater.inflate(itemsLayoutID, viewGroup, attachToParent);

        return new PersonAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonAdapterViewHolder holder, int position) {
        String singlePersonData = personsData[position];
        holder.tvPersonItemView.setText(singlePersonData);
    }

    @Override
    public int getItemCount() {
        if (null == personsData) return 0;
        return personsData.length;
    }

    public void setPersonsData(String[] personsData) {
        this.personsData = personsData;
        notifyDataSetChanged();
    }

    public class PersonAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView tvPersonItemView;

        public PersonAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPersonItemView = itemView.findViewById(R.id.tv_item_person);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String message = String.valueOf(tvPersonItemView.getText());
            itemClickListener.onItemClick(message);
        }
    }
}
