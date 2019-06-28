package br.edu.utfpr.todocollection.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import br.edu.utfpr.todocollection.R;
import br.edu.utfpr.todocollection.model.Todo;

class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private ArrayList<Todo> todoList;
    private OnItemClickListener listener;

    // Constructor
    MainAdapter(ArrayList<Todo> todoList) {
        this.todoList = todoList;
    }

    public interface OnItemClickListener {
        void onItemClick(View cardView, int position);
        boolean onItemLongClick(View cardView, int position);
    }

    void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_adapter, viewGroup, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.txtTodoName.setText(todoList.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardTodo;
        TextView txtTodoName;

        ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            cardTodo = itemView.findViewById(R.id.cardTodo);
            txtTodoName = itemView.findViewById(R.id.txtTodoName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(cardTodo, position);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            return listener.onItemLongClick(cardTodo, position);
                        }
                    }
                    return false;
                }
            });
        }
    }
}