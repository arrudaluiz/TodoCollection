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

    private Toolbar toolbar;
    private ActionBar ab;
    private EditText edtTodoName;
    private EditText edtTodoContent;

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
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        edtTodoName = findViewById(R.id.edtTodoName);
        edtTodoContent = findViewById(R.id.edtTodoContent);
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
                if (validateFields()) {
                    sendTodo();
                    finish();
                }
                return true;

            case android.R.id.home:
            case R.id.action_cancel:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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

    public void sendTodo() {
        Todo todo = new Todo(edtTodoName.getText().toString(), edtTodoContent.getText().toString());
        Intent intent = new Intent();
        intent.putExtra(TODO, todo);
        setResult(Activity.RESULT_OK, intent);
    }
}