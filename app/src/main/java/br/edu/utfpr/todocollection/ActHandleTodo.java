package br.edu.utfpr.todocollection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class ActHandleTodo extends AppCompatActivity {
    public static final String TODO = "TODO";
    public static final String MODE = "MODE";
    public static final String POSITION = "POSITION";
    public static final int CREATE = 0;
    public static final int ALTER = 1;

    private EditText edtTodoName;
    private EditText edtTodoContent;
    private int position;
    private int mode;

    public static void createTodo(AppCompatActivity activity) {
        Intent intent = new Intent(activity, ActHandleTodo.class);
        intent.putExtra(MODE, CREATE);
        activity.startActivityForResult(intent, CREATE);
    }

    public static void alterTodo(AppCompatActivity activity, Todo todo, int position) {
        Intent intent = new Intent(activity, ActHandleTodo.class);
        intent.putExtra(MODE, ALTER);
        intent.putExtra(TODO, todo);
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
                mode = CREATE;
                setTitle(getString(R.string.title_activity_act_handle_todo_new) +
                         getString(R.string.title_activity_act_read_todo));
            }

            if (bundle.getInt(MODE) == ALTER) {
                mode = ALTER;
                Todo todo = bundle.getParcelable(TODO);
                position = bundle.getInt(POSITION);
                setTitle(getString(R.string.title_activity_act_handle_todo_edit) +
                         getString(R.string.title_activity_act_read_todo));

                if (todo != null) {
                    edtTodoName.setText(todo.getName());
                    edtTodoContent.setText(todo.getContent());
                }
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

    private boolean isEmpty(String str) {
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

    public void sendNewTodo() {
        Todo todo = new Todo(edtTodoName.getText().toString(), edtTodoContent.getText().toString());
        Intent intent = new Intent();
        intent.putExtra(MODE, CREATE);
        intent.putExtra(TODO, todo);
        setResult(Activity.RESULT_OK, intent);
    }

    public void sendEditedTodo() {
        Todo todo = new Todo(edtTodoName.getText().toString(), edtTodoContent.getText().toString());
        Intent intent = new Intent();
        intent.putExtra(MODE, ALTER);
        intent.putExtra(TODO, todo);
        intent.putExtra(POSITION, position);
        setResult(Activity.RESULT_OK, intent);
    }
}