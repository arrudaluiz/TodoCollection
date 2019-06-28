package br.edu.utfpr.todocollection.view;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import org.jetbrains.annotations.NotNull;

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
    private Todo todo;
    private Item item;
    private EditText edtTodoName;
    private EditText edtTodoContent;

    public ActHandleTodo() {
        mode = -1;
        id = -1;
        position = -1;
        todo = null;
        item = null;
        edtTodoName = null;
        edtTodoContent = null;
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
        edtTodoContent = findViewById(R.id.edtTodoContent);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            if (bundle.getInt(MODE) == CREATE) {
                todo = new Todo("");
                item = new Item(-1, "");
                mode = CREATE;
                setTitle(getString(R.string.title_activity_act_handle_todo_new) +
                         getString(R.string.title_activity_act_read_todo));

            } else if (bundle.getInt(MODE) == ALTER) {
                mode = ALTER;
                position = bundle.getInt(POSITION);
                id = bundle.getInt(ID);
                setTitle(getString(R.string.title_activity_act_handle_todo_edit) +
                         getString(R.string.title_activity_act_read_todo));

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        TodoDatabase db = TodoDatabase.getDatabase(ActHandleTodo.this);
                        todo = db.todoDAO().queryForId(id);
                        item = db.itemDAO().queryForTodoId(id);

                        ActHandleTodo.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                edtTodoName.setText(todo.getName());
                                edtTodoContent.setText(item.getContent());
                            }
                        });
                    }
                });
            }
        }
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
                if (validateFields())
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    private boolean isEmpty(@NotNull String str) {
        return str.trim().isEmpty();
    }

    private boolean validateFields() {
        String todoName = edtTodoName.getText().toString();
        String todoContent = edtTodoContent.getText().toString();

        if (isEmpty(todoName)) {
            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setTitle(R.string.title_alert);
            dlg.setMessage(R.string.message_invalid_name);
            dlg.setNeutralButton(R.string.lbl_ok, null);
            dlg.show();
            edtTodoName.setText(getString(R.string.lbl_null));
            edtTodoName.requestFocus();
            return false;
        }

        if (isEmpty(todoContent)) {
            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setTitle(R.string.title_alert);
            dlg.setMessage(R.string.message_empty_content);
            dlg.setNeutralButton(R.string.lbl_ok, null);
            dlg.show();
            edtTodoContent.setText(R.string.lbl_null);
            edtTodoContent.requestFocus();
            return false;
        }

        return true;
    }

    public void respondMode() {
        todo.setName(edtTodoName.getText().toString());
        item.setContent(edtTodoContent.getText().toString());

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                switch (mode) {
                    case CREATE:
                        sendNewTodo();
                        break;

                    case ALTER:
                        sendEditedTodo();
                        break;

                    default:
                        setResult(Activity.RESULT_CANCELED);
                }
                finish();
            }
        });
    }

    public void sendNewTodo() {
        TodoDatabase db = TodoDatabase.getDatabase(ActHandleTodo.this);
        item.setTodoId((int) db.todoDAO().insert(todo));
        int todoId = (int) db.itemDAO().insert(item);

        Intent intent = new Intent();
        intent.putExtra(MODE, CREATE);
        intent.putExtra(ID, todoId);
        setResult(Activity.RESULT_OK, intent);
    }

    public void sendEditedTodo() {
        TodoDatabase db = TodoDatabase.getDatabase(ActHandleTodo.this);
        db.todoDAO().update(todo);
        db.itemDAO().update(item);

        Intent intent = new Intent();
        intent.putExtra(MODE, ALTER);
        intent.putExtra(ID, id);
        intent.putExtra(POSITION, position);
        setResult(Activity.RESULT_OK, intent);
    }
}