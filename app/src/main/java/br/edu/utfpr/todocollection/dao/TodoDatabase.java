package br.edu.utfpr.todocollection.dao;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import br.edu.utfpr.todocollection.model.Item;
import br.edu.utfpr.todocollection.model.Todo;

@Database(entities = {Item.class, Todo.class}, version = 1)
public abstract class TodoDatabase extends RoomDatabase {
    public abstract ItemDAO itemDAO();
    public abstract TodoDAO todoDAO();
    private static TodoDatabase instance;

    public static TodoDatabase getDatabase(final Context context) {
        if (instance == null) {
            synchronized (TodoDatabase.class) {
                if (instance == null) {
                    RoomDatabase.Builder builder = Room.databaseBuilder(
                            context,
                            TodoDatabase.class,
                            "todo.db");

                    builder.fallbackToDestructiveMigration();
                    instance = (TodoDatabase) builder.build();
                }
            }
        }
        return instance;
    }

    public final void destroy() {
        if(instance != null)
            instance.close();
    }
}