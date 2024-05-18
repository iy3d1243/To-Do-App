package com.example.todoapp;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Task> taskList = new ArrayList<>();
    private TaskAdapter taskAdapter;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("ToDoApp", Context.MODE_PRIVATE);
        gson = new Gson();
        loadTasks();

        EditText editTextTask = findViewById(R.id.editTextTask);
        Button buttonAdd = findViewById(R.id.buttonAdd);
        RecyclerView recyclerViewTasks = findViewById(R.id.recyclerViewTasks);

        // Pass the reference to MainActivity when creating TaskAdapter
        taskAdapter = new TaskAdapter(taskList, this); // 'this' refers to the current MainActivity instance
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTasks.setAdapter(taskAdapter);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskName = editTextTask.getText().toString();
                if (!taskName.isEmpty()) {
                    Task newTask = new Task(taskName, false); // Initialize task with completion status
                    taskList.add(newTask);
                    taskAdapter.notifyDataSetChanged();
                    saveTasks();
                    editTextTask.setText("");
                }
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                taskList.remove(position);
                taskAdapter.notifyItemRemoved(position);
                saveTasks();
            }
        }).attachToRecyclerView(recyclerViewTasks);
    }

    public void saveTasks() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(taskList);
        editor.putString("tasks", json);
        editor.apply();
    }

    private void loadTasks() {
        String json = sharedPreferences.getString("tasks", null);
        if (json != null) {
            Type type = new TypeToken<List<Task>>() {}.getType();
            taskList = gson.fromJson(json, type);
        } else {
            taskList = new ArrayList<>();
        }
        if (taskAdapter != null) {
            taskAdapter.notifyDataSetChanged();
        }
    }
}

