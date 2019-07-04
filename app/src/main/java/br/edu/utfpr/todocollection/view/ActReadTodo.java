package br.edu.utfpr.todocollection.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

import br.edu.utfpr.todocollection.R;
import br.edu.utfpr.todocollection.dao.TodoDatabase;
import br.edu.utfpr.todocollection.model.Item;
import br.edu.utfpr.todocollection.model.Todo;

public class ActReadTodo extends AppCompatActivity {
    private int id;
    private ArrayList<Item> itemList;
    private Todo todo;
    private TextView txtName;
    private RecyclerView readRecycler;
    private ReadAdapter adapter;


    public static void readTodo(AppCompatActivity activity, int id) {
        Intent intent = new Intent(activity, ActReadTodo.class);
        intent.putExtra(ActHandleTodo.ID, id);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_read_todo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        txtName = findViewById(R.id.txtName);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            id = bundle.getInt(ActHandleTodo.ID);
            buildMainRecycler();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                ActAbout.showAbout(this);
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void buildMainRecycler() {
        readRecycler = findViewById(R.id.readRecycler);
        readRecycler.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        readRecycler.setLayoutManager(layoutManager);

        /*
         * Setting up an empty Adapter to RecyclerView to avoid
         * "E/RecyclerView: No adapter attached; skipping layout" error.
         */
        adapter = new ReadAdapter(new ArrayList<Item>());
        readRecycler.setAdapter(adapter);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                TodoDatabase db = TodoDatabase.getDatabase(ActReadTodo.this);
                todo = db.todoDAO().queryForId(id);
                itemList = (ArrayList<Item>) db.itemDAO().queryForTodoId(id);

                ActReadTodo.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtName.setText(todo.getName());
                        adapter = new ReadAdapter(itemList);
                        readRecycler.setAdapter(adapter);
                    }
                });
            }
        });
    }
}