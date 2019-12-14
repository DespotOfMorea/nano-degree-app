package org.vnuk.nanodegreeapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.vnuk.nanodegreeapp.model.FakePerson;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonAdapterViewHolder> {

    private final Context mContext;
    private final ItemClickListener itemClickListener;
    private Cursor mCursor;

    public interface ItemClickListener {
        void onItemClick(int personData);
    }

    public PersonAdapter(@NonNull Context context, ItemClickListener itemClickListener) {
        this.mContext = context;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public PersonAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        int itemsLayoutID = R.layout.person_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean attachToParent = false;

        View view = inflater.inflate(itemsLayoutID, viewGroup, attachToParent);
        view.setFocusable(true);

        return new PersonAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        FakePerson person = cursor2Person(mCursor);
        holder.tvPersonItemView.setText(person.simpleDataDisplay());
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    void swapCursor (Cursor newCursor) {
        mCursor = newCursor;
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
            int adapterPosition = getAdapterPosition();

            mCursor.moveToPosition(adapterPosition);
            FakePerson person = cursor2Person(mCursor);

            itemClickListener.onItemClick(adapterPosition+1);
        }
    }

    private FakePerson cursor2Person(Cursor cursor) {
        String title = cursor.getString(MainActivity.INDEX_PERSON_TITLE);
        String firstName = cursor.getString(MainActivity.INDEX_PERSON_FIRST_NAME);
        String lastName = cursor.getString(MainActivity.INDEX_PERSON_LAST_NAME);

        return new FakePerson(title,firstName,lastName);
    }
}
