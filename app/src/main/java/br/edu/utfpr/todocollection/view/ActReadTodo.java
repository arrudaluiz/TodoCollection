package br.edu.utfpr.todocollection.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import br.edu.utfpr.todocollection.R;
import br.edu.utfpr.todocollection.dao.TodoDatabase;
import br.edu.utfpr.todocollection.model.Item;
import br.edu.utfpr.todocollection.model.Todo;

public class ActReadTodo extends AppCompatActivity {
    private int id;

    public static void readTodo(AppCompatActivity activity, int id) {
        Intent intent = new Intent(activity, ActReadTodo.class);
        intent.putExtra(ActHandleTodo.ID, id);
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

        final TextView txtReadNameCatch = findViewById(R.id.txtReadNameCatch);
        final TextView txtReadContentCatch = findViewById(R.id.txtReadContentCatch);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            id = bundle.getInt(ActHandleTodo.ID);

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    TodoDatabase db = TodoDatabase.getDatabase(ActReadTodo.this);
                    final Todo todo = db.todoDAO().queryForId(id);
                    final Item item = db.itemDAO().queryForTodoId(id);

                    ActReadTodo.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtReadNameCatch.setText(todo.getName());
                            txtReadContentCatch.setText(item.getContent());
                        }
                    });
                }
            });
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