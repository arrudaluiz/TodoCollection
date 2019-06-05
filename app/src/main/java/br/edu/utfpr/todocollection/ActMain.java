package br.edu.utfpr.todocollection;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

public class ActMain extends AppCompatActivity {
    private ArrayList<Todo> todoList;
    private MainAdapter adapter;
    private FloatingActionButton fab;

    private ActionMode actionMode;
    private View card;
    private int position;

    private ActionMode.Callback callback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.context_menu_act_main, menu);

            fab.setEnabled(false);
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.utfpr_grey)));
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            int position = ActMain.this.position;
            switch (menuItem.getItemId()) {
                case R.id.action_edit:
                    ActHandleTodo.alterTodo(ActMain.this, todoList.get(position), position);
                    actionMode.finish();
                    return true;

                case R.id.action_delete:
                    final int pos = position;
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActMain.this);
                    builder.setTitle(R.string.title_deleting).setMessage(R.string.msg_deleting)
                            .setPositiveButton(R.string.lbl_ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    todoList.remove(pos);
                                    adapter.notifyItemRemoved(pos);
                                }
                            })
                            .setNegativeButton(R.string.lbl_cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {}
                            }).create().show();
                    actionMode.finish();
                    return true;

                case android.R.id.home:
                    actionMode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            card.setBackgroundColor(getResources()
                    .getColor(android.R.color.background_light));
            card = null;
            fab.setEnabled(true);
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.utfpr_yellow)));
        }
    };

    // Constructor
    public ActMain() {
        card = null;
        todoList = new ArrayList<>();
        todoList.add(new Todo("Provas", "Matemática\nPortuguês\nInglês"));
        todoList.add(new Todo("Mercado", "Arroz\nFeijão\nQueijo"));
        todoList.add(new Todo("Lembrete", "Fazer cópia das chaves de casa"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        buildRecycleView();
        //registerForContextMenu(recyclerView);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActHandleTodo.createTodo(ActMain.this);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_act_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                ActAbout.showAbout(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void buildRecycleView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new MainAdapter(todoList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View viewCard, int position) {
                if (card == null) {
                    ActReadTodo.readTodo(ActMain.this, todoList.get(position));

                } else if (card != viewCard) {
                    card.setBackgroundColor(getResources()
                            .getColor(android.R.color.background_light));
                    viewCard.setBackgroundColor(getResources()
                            .getColor(R.color.utfpr_grey));
                    card = viewCard;
                    ActMain.this.position = position;

                } else {
                    viewCard.setBackgroundColor(getResources()
                            .getColor(android.R.color.background_light));
                    actionMode.finish();
                }
            }

            @Override
            public boolean onItemLongClick(View viewCard, int position) {
                if (card == null) {
                    viewCard.setBackgroundColor(getResources()
                            .getColor(R.color.utfpr_grey));
                    card = viewCard;
                    ActMain.this.position = position;
                    actionMode = startSupportActionMode(callback);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Bundle bundle = null;
            if (data != null) {
                bundle = data.getExtras();
            }

            if (bundle != null) {
                Todo todo = bundle.getParcelable(ActHandleTodo.TODO);

                if (todo != null) {
                    if (requestCode == ActHandleTodo.CREATE) {
                        todoList.add(todo);
                        adapter.notifyItemInserted(todoList.size()-1);

                    } else if (requestCode == ActHandleTodo.ALTER) {
                        int position = bundle.getInt(ActHandleTodo.POSITION);
                        todoList.get(position).setName(todo.getName());
                        todoList.get(position).setContent(todo.getContent());
                        adapter.notifyItemChanged(position);
                    }
                }
            }
        }
    }
}