package br.edu.utfpr.todocollection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

public class ActMain extends AppCompatActivity {
    private Toolbar toolbar;
    private Intent intent;
    private ArrayList<Todo> todoList;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MainAdapter adapter;
    private FloatingActionButton fab;

    public ActMain() {
        todoList = new ArrayList<>();
        todoList.add(new Todo("Provas", "Matemática\nPortuguês\nInglês"));
        todoList.add(new Todo("Mercado", "Arroz\nFeijão\nQueijo"));
        todoList.add(new Todo("Lembrete", "Fazer cópia das chaves de casa"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        buildRecycleView();

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
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new MainAdapter(todoList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                ActReadTodo.readTodo(ActMain.this, todoList.get(position));
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

                    } else if (requestCode == ActHandleTodo.ALTER) {
                        int position = bundle.getInt(ActHandleTodo.POSITION);
                        todoList.get(position).setName(todo.getName());
                        todoList.get(position).setContent(todo.getContent());
                    }
                }
            }
        }
    }
}