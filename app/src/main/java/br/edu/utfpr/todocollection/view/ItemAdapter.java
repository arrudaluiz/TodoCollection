package br.edu.utfpr.todocollection.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;

import br.edu.utfpr.todocollection.R;
import br.edu.utfpr.todocollection.model.Item;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private ArrayList<Item> itemList;
    private ArrayList<EditText> editTextList;

    ItemAdapter(ArrayList<Item> itemList) {
        this.itemList = itemList;
        editTextList = new ArrayList<>();
    }

    public ArrayList<EditText> getEditTextList() {
        return editTextList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_adapter, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        viewHolder.edtItem.setText(itemList.get(viewHolder.getAdapterPosition()).getContent());
        editTextList.add(viewHolder.edtItem);
        if (viewHolder.edtItem.getText().toString().isEmpty())
            viewHolder.edtItem.requestFocus();

        viewHolder.bttItemDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextList.remove(viewHolder.getAdapterPosition());
                itemList.remove(viewHolder.getAdapterPosition());
                notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        EditText edtItem;
        ImageButton bttItemDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            edtItem = itemView.findViewById(R.id.edtTodoContent);
            bttItemDelete = itemView.findViewById(R.id.bttItemDelete);
        }
    }
}