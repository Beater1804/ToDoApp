package nguyenthanhdung.android.to_doapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddNewTask extends BottomSheetDialogFragment {
    public static final String TAG = "AddNewTask";

    private TextView setDueDate;
    private EditText editTextTask;
    private Button saveTask;

    private FirebaseFirestore firestore;
    private Context context;
    private  String dueDate = "";
    public static AddNewTask newInstance(){
        return new AddNewTask();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_new_task,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setDueDate = view.findViewById(R.id.tvSetDueDate);
        editTextTask = view.findViewById(R.id.etTask);
        saveTask = view.findViewById(R.id.btnSaveTask);

        firestore = FirebaseFirestore.getInstance();
        editTextTask.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().equals("")){
                    saveTask.setEnabled(false);
                    saveTask.setBackgroundColor(Color.GRAY);
                }
                else{
                    saveTask.setEnabled(true);
                    saveTask.setBackgroundColor(getResources().getColor(R.color.pastel_light_green));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setDueDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar calendar = Calendar.getInstance();

                        int MONTH = calendar.get(Calendar.MONTH);
                        int YEAR = calendar.get(Calendar.YEAR);
                        int DAY = calendar.get(Calendar.DATE);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                                month = month +1;
                                setDueDate.setText(dayOfMonth + "/"+month + "/"+ year);

                                dueDate = dayOfMonth + "/" +month + "/"+year ;
                            }
                        }, YEAR, MONTH, DAY);
                        datePickerDialog.show();
                    }
                });
                saveTask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String task = editTextTask.getText().toString();

                        if(task.isEmpty()){
                            Toast.makeText(context,"Empty task not allowed", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Map<String,Object> taskMap = new HashMap<>();

                            taskMap.put("task",task);
                            taskMap.put("due",dueDate);
                            taskMap.put("status", 0);

                            firestore.collection("task").add(taskMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(context,"Task Successful", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(context,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context,e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });

                        }
                        dismiss();
                    }
                });
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity instanceof OnDialogCloseListener){
            ((OnDialogCloseListener)activity).onDialogClose(dialog);
        }
    }
}
