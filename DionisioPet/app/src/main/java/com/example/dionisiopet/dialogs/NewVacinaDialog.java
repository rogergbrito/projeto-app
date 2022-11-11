package com.example.dionisiopet.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.dionisiopet.R;

public class NewVacinaDialog extends AppCompatDialogFragment {
    private TextView editNomeVacina;
    private CalendarView editDataVacina;
    private NewVacinaDialogListener listener;
    private String _dataVacina;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.new_vacina_dialog, null);

        builder.setView(view)
                .setTitle("Registrar nova vacina")
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String nomeVacina = editNomeVacina.getText().toString();
                        String dataVacina = _dataVacina;

                        listener.applyText(nomeVacina, dataVacina);
                    }
                });

        editNomeVacina = view.findViewById(R.id.editTextVacinaName);
        editDataVacina = view.findViewById(R.id.calendarView);

        editDataVacina.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                //aqui month é 0-11, por isso do +1. OBRIGADO GOOGLE
                String diaHelper = String.valueOf(dayOfMonth).length() > 1 ? String.valueOf(dayOfMonth) : "0" + String.valueOf(dayOfMonth);
                String mesHelper = String.valueOf(month).length() > 1 ? String.valueOf(month + 1) : "0" + String.valueOf(month + 1);
                String dateText = diaHelper + "/" + mesHelper + "/" + year;

                _dataVacina = dateText;
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (NewVacinaDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "Deve implementar NewVacinaDialogListener");
        }
    }

    public interface NewVacinaDialogListener{
        void applyText(String nomeVacina, String dataVacina);
    }
}
