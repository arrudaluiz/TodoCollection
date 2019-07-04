package br.edu.utfpr.todocollection.view;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import br.edu.utfpr.todocollection.R;
import br.edu.utfpr.todocollection.dao.TodoDatabase;
import br.edu.utfpr.todocollection.model.Item;
import br.edu.utfpr.todocollection.model.Todo;

public class ActHandleTodo extends AppCompatActivity {
    public static final String MODE = "MODE";
    public static final String ID = "ID";
    public static final String POSITION = "POSITION";
    public static final int CREATE = 0;
    public static final int ALTER = 1;

    private int mode;
    private int id;
    private int position;
    private ArrayList<Item> itemList;
    private ArrayList<EditText> editTextList;

    private Todo todo;

    private EditText edtTodoName;
    private RecyclerView itemRecycler;
    private ItemAdapter adapter;

    public ActHandleTodo() {
        mode = -1;
        id = -1;
        position = -1;
        itemList = new ArrayList<>();
        editTextList = new ArrayList<>();
        todo = new Todo("");
        edtTodoName = null;
        adapter = null;
    }

    public static void createTodo(AppCompatActivity activity) {
        Intent intent = new Intent(activity, ActHandleTodo.class);
        intent.putExtra(MODE, CREATE);
        activity.startActivityForResult(intent, CREATE);
    }

    public static void alterTodo(AppCompatActivity activity, int position, int id) {
        Intent intent = new Intent(activity, ActHandleTodo.class);
        intent.putExtra(MODE, ALTER);
        intent.putExtra(ID, id);
        intent.putExtra(POSITION, position);
        activity.startActivityForResult(intent, ALTER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_handle_todo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null)
            ab.setDisplayHomeAsUpEnabled(true);

        edtTodoName = findViewById(R.id.edtTodoName);
        itemRecycler = findViewById(R.id.itemRecycler);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            if (bundle.getInt(MODE) == CREATE) {
                mode = CREATE;
                setTitle(getString(R.string.title_activity_act_handle_todo_new) +
                        getString(R.string.title_activity_act_read_todo));
                buildItemRecycler();
                itemList.add(new Item(id, ""));
                adapter.notifyItemInserted(itemList.size());

            } else if (bundle.getInt(MODE) == ALTER) {
                mode = ALTER;
                position = bundle.getInt(POSITION);
                id = bundle.getInt(ID);
                setTitle(getString(R.string.title_activity_act_handle_todo_edit) +
                        getString(R.string.title_activity_act_read_todo));
                fillItemRecycler();
            } else {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        } else {
            finish();
        }

        ImageButton bttItemAdd = findViewById(R.id.bttItemAdd);
        bttItemAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemList.add(new Item(id, ""));
                adapter.notifyItemInserted(itemList.size());
            }
        });
    }

    private void buildItemRecycler() {
        itemRecycler.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        itemRecycler.setLayoutManager(layoutManager);
        adapter = new ItemAdapter(itemList);
        itemRecycler.setAdapter(adapter);
    }

    private void fillItemRecycler() {
        itemRecycler.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        itemRecycler.setLayoutManager(layoutManager);

        /*
         * Setting up an empty Adapter to RecyclerView to avoid
         * "E/RecyclerView: No adapter attached; skipping layout" error.
         */
        adapter = new ItemAdapter(itemList);
        itemRecycler.setAdapter(adapter);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                TodoDatabase db = TodoDatabase.getDatabase(ActHandleTodo.this);
                todo = db.todoDAO().queryForId(id);
                itemList = (ArrayList<Item>) db.itemDAO().queryForTodoId(id);

                ActHandleTodo.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        edtTodoName.setText(todo.getName());
                        adapter = new ItemAdapter(itemList);
                        itemRecycler.setAdapter(adapter);
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_act_handle_todo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (isValidTodo())
                    respondMode();
                return true;

            case android.R.id.home:
            case R.id.action_cancel:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean isValidTodo() {
        if (isEmpty(edtTodoName.getText().toString())) {
            AlertDialog.Builder dlg = new AlertDialog.Builder(this)
                    .setTitle(R.string.title_impossible_to_save)
                    .setMessage(R.string.message_invalid_name)
                    .setNeutralButton(R.string.lbl_ok, null);
            dlg.show();
            edtTodoName.setText(getString(R.string.lbl_null));
            edtTodoName.requestFocus();

            return false;
        }

        boolean hasValidItems = false;
        boolean hasInvalidItems = false;
        editTextList = adapter.getEditTextList();
        for (int i = 0; editTextList.size() > i; i++)
            if (!isEmpty(editTextList.get(i).getText().toString()))
                hasValidItems = true;
            else
                hasInvalidItems = true;

        if (!hasValidItems) {
            AlertDialog.Builder dlg = new AlertDialog.Builder(this)
                    .setTitle(R.string.title_impossible_to_save)
                    .setMessage(R.string.message_empty_list)
                    .setNeutralButton(R.string.lbl_ok, null);
            dlg.show();

            if (itemList.size() == 0) {
                itemList.add(new Item(-1, ""));
                adapter.notifyItemInserted(itemList.size());

            } else if (itemList.size() > 1) {
                int count = itemList.size()-1;
                itemList.subList(1, itemList.size()).clear();
                adapter.notifyItemRangeRemoved(1, count);
            }

            return false;
        }

        if (hasInvalidItems) {
            Toast toast = Toast.makeText(this, R.string.message_empty_fields, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 60);
            toast.show();
        }

        return true;
    }

    private boolean isEmpty(@NotNull String str) {
        return str.trim().isEmpty();
    }

    public void respondMode() {
        todo.setName(edtTodoName.getText().toString());

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                TodoDatabase db = TodoDatabase.getDatabase(ActHandleTodo.this);
                Intent intent = new Intent();

                if (mode == CREATE)
                    id = (int) db.todoDAO().insert(todo);

                else {
                    //mode == ALTER
                    intent.putExtra(POSITION, position);
                    db.todoDAO().update(todo);
                    ArrayList<Item> dbItems = (ArrayList<Item>) db.itemDAO().queryForTodoId(id);
                    for (int i = 0; dbItems.size() > i; i++)
                        db.itemDAO().delete(dbItems.get(i));
                }

                for (int i = 0; editTextList.size() > i; i++)
                    if (!isEmpty(editTextList.get(i).getText().toString()))
                        db.itemDAO().insert(new Item(id, editTextList.get(i).getText().toString()));

                intent.putExtra(MODE, mode);
                intent.putExtra(ID, id);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}