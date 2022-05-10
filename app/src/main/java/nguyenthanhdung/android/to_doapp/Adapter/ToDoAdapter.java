package nguyenthanhdung.android.to_doapp.Adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import nguyenthanhdung.android.to_doapp.MainActivity;
import nguyenthanhdung.android.to_doapp.Model.ToDoModel;
import nguyenthanhdung.android.to_doapp.R;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.MyViewHolder> {

    private List<ToDoModel> toDoList;
    private MainActivity activity;
    private FirebaseFirestore firestore;

    public ToDoAdapter(MainActivity mainActivity, List<ToDoModel> toDoList){
        this.toDoList=toDoList;
        activity= mainActivity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.task_to_do, parent,false);
        firestore = FirebaseFirestore.getInstance();
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ToDoModel toDoModel = toDoList.get(position);
        holder.checkBox.setText(toDoModel.getTask());
        holder.tvDueDate.setText("Due On "+ toDoModel.getDue());
        holder.checkBox.setChecked(toDoBoolean(toDoModel.getStatus()));
        //change value status on Firebase
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    firestore.collection("task").document(toDoModel.TaskId).update("status",1);
                }
                else{
                    firestore.collection("task").document(toDoModel.TaskId).update("status",0);
                }
            }
        });
    }

    private  boolean toDoBoolean(int status){
        return status !=0;
    }
    @Override
    public int getItemCount() {
        return toDoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvDueDate;
        CheckBox checkBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDueDate = itemView.findViewById(R.id.tvDueDate);
            checkBox = itemView.findViewById(R.id.checkboxTask);

        }
    }

}
