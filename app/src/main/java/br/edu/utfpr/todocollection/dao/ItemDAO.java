package br.edu.utfpr.todocollection.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import br.edu.utfpr.todocollection.model.Item;

@Dao
public interface ItemDAO {
    @Insert
    long insert(Item item);

    @Delete
    void delete(Item item);

    @Update
    void update(Item item);

    @Query("SELECT * FROM item WHERE todoId = :todoId ORDER BY id ASC")
    List<Item> queryForTodoId(long todoId);
}