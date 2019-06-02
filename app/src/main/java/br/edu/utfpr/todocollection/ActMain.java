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
    public static final String NOTE = "Note";
    public static final int CREATE_NOTE = 1;
    private Toolbar toolbar;
    private Intent intent;
    private ArrayList<Note> noteList;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MainAdapter adapter;
    private FloatingActionButton fab;

    public ActMain() {
        noteList = new ArrayList<>();
        noteList.add(new Note("Provas", "Matemática\nPortuguês\nInglês"));
        noteList.add(new Note("Mercado", "Arroz\nFeijão\nQueijo"));
        noteList.add(new Note("Lembrete", "Fazer cópia das chaves de casa"));
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
                intent = new Intent(ActMain.this, ActCreateTodo.class);
                startActivityForResult(intent, CREATE_NOTE);
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
                intent = new Intent(this, ActAbout.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void buildRecycleView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new MainAdapter(noteList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                intent = new Intent(ActMain.this, ActReadTodo.class);
                intent.putExtra(NOTE, noteList.get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CREATE_NOTE && resultCode == Activity.RESULT_OK) {
            Note note = data.getParcelableExtra(NOTE);
            if (note != null) {
                noteList.add(note);
            }
        }
    }
}