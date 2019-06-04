package br.edu.utfpr.todocollection;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

public class ActReadTodo extends AppCompatActivity {
    public static final int READ = 2;

    private Toolbar toolbar;
    private ActionBar ab;
    private TextView txtReadNameCatch;
    private TextView txtReadContentCatch;
    private Intent intent;

    public static void readTodo(AppCompatActivity activity, Todo todo) {
        Intent intent = new Intent(activity, ActReadTodo.class);
        intent.putExtra(ActHandleTodo.TODO, todo);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_read_todo);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        intent = getIntent();
        Todo todo = intent.getParcelableExtra(ActHandleTodo.TODO);

        txtReadNameCatch = findViewById(R.id.txtReadNameCatch);
        txtReadNameCatch.setText(todo.getName());

        txtReadContentCatch = findViewById(R.id.txtReadContentCatch);
        txtReadContentCatch.setText(todo.getContent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.context_menu_act_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
//                ActHandleTodo.alterTodo();
                finish();
                return true;

            case android.R.id.home:
                finish();
                return true;

            case R.id.action_delete:
                // Call delete function
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}