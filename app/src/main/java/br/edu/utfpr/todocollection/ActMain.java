package br.edu.utfpr.todocollection;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class ActMain extends AppCompatActivity {
    private Toolbar toolbar;
    private Intent intent;
    private ArrayList<Todo> todoList;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MainAdapter adapter;
    private FloatingActionButton fab;

    private ActionMode actionMode;
    private ArrayList<View> cards;
    private ArrayList<Integer> positions;

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
            Toast.makeText(getApplicationContext(),
                    getString(R.string.lbl_ok),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_edit:
                    int position = positions.get(0);
                    ActHandleTodo.alterTodo(ActMain.this, todoList.get(position), position);

                    actionMode.finish();
                    return true;

                case R.id.action_delete:
                    for (int i : positions)
                        todoList.remove(i);

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
            for (View card : cards) {
                card.setBackgroundColor(getResources().getColor(android.R.color.background_light));
            }
            cards.clear();

            fab.setEnabled(true);
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.utfpr_yellow)));
        }
    };

    // Constructor
    public ActMain() {
        cards = new ArrayList<>();
        positions = new ArrayList<>();
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
        registerForContextMenu(recyclerView);

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
            public void onItemClick(View viewCard, int position) {
                if (cards.isEmpty()) {
                    ActReadTodo.readTodo(ActMain.this, todoList.get(position));

                } else if (cards.remove(viewCard)) {
                    viewCard.setBackgroundColor(getResources()
                            .getColor(android.R.color.background_light));
                    positions.remove(Integer.valueOf(position));

                    switch (cards.size()) {
                        case 0:
                            actionMode.finish();
                            break;

                        case 1:
                            actionMode.getMenu().findItem(R.id.action_edit).setVisible(true);
                    }

                } else {
                    cards.add(viewCard);
                    positions.add(position);
                    viewCard.setBackgroundColor(getResources().getColor(R.color.utfpr_grey));
                    actionMode.getMenu().findItem(R.id.action_edit).setVisible(false);
                    actionMode.setTitle(getString(cards.size()) + getString(R.string.title_items_selected));
                }
            }

            @Override
            public boolean onItemLongClick(View viewCard, int position) {
                if (!cards.isEmpty())
                    actionMode.finish();

                cards.add(viewCard);
                positions.add(position);
                viewCard.setBackgroundColor(getResources().getColor(R.color.utfpr_grey));
                actionMode = startSupportActionMode(callback);
                if (actionMode != null)
                    actionMode.getMenu().findItem(R.id.action_edit).setVisible(true);

                return true;
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