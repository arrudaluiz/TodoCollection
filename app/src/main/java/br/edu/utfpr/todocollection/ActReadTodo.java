package br.edu.utfpr.todocollection;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

public class ActReadTodo extends AppCompatActivity {

    public static void readTodo(AppCompatActivity activity, Todo todo) {
        Intent intent = new Intent(activity, ActReadTodo.class);
        intent.putExtra(ActHandleTodo.TODO, todo);
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

        TextView txtReadNameCatch = findViewById(R.id.txtReadNameCatch);
        TextView txtReadContentCatch = findViewById(R.id.txtReadContentCatch);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            Todo todo = bundle.getParcelable(ActHandleTodo.TODO);
            if (todo != null) {
                txtReadNameCatch.setText(todo.getName());
                txtReadContentCatch.setText(todo.getContent());
            }
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
}