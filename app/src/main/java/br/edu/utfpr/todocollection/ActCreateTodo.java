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

public class ActCreateTodo extends AppCompatActivity {
    private Toolbar toolbar;
    private ActionBar ab;
    private EditText edtTodoName;
    private EditText edtTodoContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_create_todo);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        edtTodoName = findViewById(R.id.edtTodoName);
        edtTodoContent = findViewById(R.id.edtTodoContent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_act_create_todo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (validateFields()) {
                    createNote();
                    finish();
                }
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

    public void createNote() {
        Note note = new Note(edtTodoName.getText().toString(), edtTodoContent.getText().toString());
        Intent intent = new Intent();
        intent.putExtra(ActMain.NOTE, note);
        setResult(Activity.RESULT_OK, intent);
    }
}