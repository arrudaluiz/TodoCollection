package br.edu.utfpr.todocollection.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
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

import br.edu.utfpr.todocollection.R;
import br.edu.utfpr.todocollection.dao.TodoDatabase;
import br.edu.utfpr.todocollection.model.Todo;

public class ActMain extends AppCompatActivity {
    private int position;
    private ArrayList<Todo> todoList;

    private FloatingActionButton fab;

    private MainAdapter adapter;
    private View card;

    private ActionMode actionMode;
    private ActionMode.Callback callback;

    // Constructor
    public ActMain() {
        position = -1;
        todoList = new ArrayList<>();
        fab = null;
        adapter = null;
        card = null;
        actionMode = null;
        callback = new ActionMode.Callback() {
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
                        int id = todoList.get(position).getId();
                        ActHandleTodo.alterTodo(ActMain.this, position, id);
                        actionMode.finish();
                        return true;

                    case R.id.action_delete:
                        final int pos = position;
                        AlertDialog.Builder builder = new AlertDialog.Builder(ActMain.this);
                        builder.setTitle(R.string.title_deleting).setMessage(R.string.msg_deleting)
                                .setPositiveButton(R.string.lbl_ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        AsyncTask.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                TodoDatabase db = TodoDatabase.getDatabase(ActMain.this);
                                                db.todoDAO().delete(todoList.remove(pos));
                                                adapter.notifyItemRemoved(pos);
                                            }
                                        });
                                    }
                                })
                                .setNegativeButton(R.string.lbl_cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        buildMainRecycler();

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

    private void buildMainRecycler() {
        final RecyclerView mainRecycler = findViewById(R.id.mainRecycler);
        mainRecycler.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mainRecycler.setLayoutManager(layoutManager);

        /*
         * Setting up an empty Adapter to RecyclerView to avoid
         * "E/RecyclerView: No adapter attached; skipping layout" error.
         */
        adapter = new MainAdapter(new ArrayList<Todo>());
        mainRecycler.setAdapter(adapter);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                TodoDatabase db = TodoDatabase.getDatabase(ActMain.this);
                todoList = (ArrayList<Todo>) db.todoDAO().queryAll();

                ActMain.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new MainAdapter(todoList);
                        mainRecycler.setAdapter(adapter);

                        adapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View viewCard, int position) {
                                if (card == null) {
                                    int id = todoList.get(position).getId();
                                    ActReadTodo.readTodo(ActMain.this, id);

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
                });
            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            final Bundle bundle = data.getExtras();

            if (bundle != null) AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    int id = bundle.getInt(ActHandleTodo.ID);
                    TodoDatabase db = TodoDatabase.getDatabase(ActMain.this);
                    Todo todo = db.todoDAO().queryForId(id);

                    if (requestCode == ActHandleTodo.CREATE) {
                        todoList.add(todo);

                        ActMain.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyItemInserted(todoList.size());
                            }
                        });

                    } else if (requestCode == ActHandleTodo.ALTER) {
                        final int position = bundle.getInt(ActHandleTodo.POSITION);
                        todoList.get(position).setId(todo.getId());
                        todoList.get(position).setName(todo.getName());

                        ActMain.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyItemChanged(position);
                            }
                        });
                    }
                }
            });
        }
    }
}