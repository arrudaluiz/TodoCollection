package br.edu.utfpr.todocollection;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

public class ActReadTodo extends AppCompatActivity {
    private Toolbar toolbar;
    private ActionBar ab;
    private TextView txtReadNameCatch;
    private TextView txtReadContentCatch;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_read_todo);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        Note note = intent.getParcelableExtra(ActMain.NOTE);

        txtReadNameCatch = findViewById(R.id.txtReadNameCatch);
        txtReadNameCatch.setText(note.getName());

        txtReadContentCatch = findViewById(R.id.txtReadContentCatch);
        txtReadContentCatch.setText(note.getContent());
    }
}