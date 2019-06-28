package br.edu.utfpr.todocollection.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import br.edu.utfpr.todocollection.model.Todo;

@Dao
public interface TodoDAO {
    @Insert
    long insert(Todo todo);

    @Delete
    void delete(Todo todo);

    @Update
    void update(Todo todo);

    @Query("SELECT * FROM todo WHERE id = :id")
    Todo queryForId(long id);

    @Query("SELECT * FROM todo ORDER BY id ASC")
    List<Todo> queryAll();
}