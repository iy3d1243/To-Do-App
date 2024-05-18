package com.example.todoapp;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks;
    private MainActivity mainActivity; // Add a reference to MainActivity

    public TaskAdapter(List<Task> tasks, MainActivity mainActivity) {
        this.tasks = tasks;
        this.mainActivity = mainActivity; // Initialize MainActivity reference
    }
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.textViewTask.setText(task.getName());
        holder.checkBoxCompleted.setChecked(task.isCompleted());
        holder.checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            mainActivity.saveTasks(); // Save tasks using MainActivity reference
        });

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Edit Task");

            final EditText input = new EditText(context);
            input.setText(task.getName());
            builder.setView(input);

            builder.setPositiveButton("Save", (dialog, which) -> {
                String newName = input.getText().toString();
                if (!newName.isEmpty()) {
                    task.setName(newName);
                    notifyItemChanged(position);
                    mainActivity.saveTasks(); // Save tasks after editing using MainActivity reference
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.show();
        });
    }


    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTask;
        CheckBox checkBoxCompleted;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTask = itemView.findViewById(R.id.textViewTask);
            checkBoxCompleted = itemView.findViewById(R.id.checkBoxCompleted);
        }
    }
}
