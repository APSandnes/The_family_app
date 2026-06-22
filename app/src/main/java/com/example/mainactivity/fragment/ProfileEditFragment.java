package com.example.mainactivity.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.mainactivity.Database;
import com.example.mainactivity.R;
import com.example.mainactivity.model.User;

import java.util.Objects;

public class ProfileEditFragment extends Fragment {
    Database database;
    SharedPreferences sharedPreferences;

    private EditText endreNavn, endreEmail, endreMobilnr;
    private DatePicker endreBirthday;
    private String navn, mobil, email, dato, id;
    private ImageView photo;

    public ProfileEditFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_edit, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment);

        // Instansierer variabler
        Button send = view.findViewById(R.id.EndreProfileEditBtn);
        endreNavn = view.findViewById(R.id.endreProfileNavn);
        endreBirthday = view.findViewById(R.id.endreProfileBirthday);
        endreEmail = view.findViewById(R.id.endreProfileEmail);
        endreMobilnr = view.findViewById(R.id.profileMobilnr);
        database = new Database(getActivity());
        sharedPreferences = requireContext().getSharedPreferences(User.SESSION, Context.MODE_PRIVATE);

        photo = view.findViewById(R.id.profilebildeEdit);
        photo.setImageResource(R.drawable.ic_baseline_account_circle_24);

        assert getArguments() != null;
        endreNavn.setText(getArguments().getString("NAVN"));
        endreEmail.setText(getArguments().getString("EMAIL"));
        endreMobilnr.setText(getArguments().getString("MOBILNUMMER"));

        String[] parts = Objects.requireNonNull(requireArguments().getString("FODSELSDATO")).split("\\.");
        int dag, maaned, aar;
        dag = Integer.parseInt(parts[0]);
        maaned = Integer.parseInt(parts[1]);
        aar = Integer.parseInt(parts[2]);

        endreBirthday.updateDate(aar, maaned-1, dag);
        endreBirthday.setMaxDate(System.currentTimeMillis());

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navn = endreNavn.getText().toString();
                mobil = endreMobilnr.getText().toString();
                email = endreEmail.getText().toString();
                dato = endreBirthday.getDayOfMonth() + "." + (endreBirthday.getMonth()+1) + "." + endreBirthday.getYear();
                assert getArguments() != null;
                id = getArguments().getString("ID");
                database.updateUserInDatabase(id, navn, email, dato, mobil);
                System.out.println(dato);
                database.updateOneColumnFromTable(Database.TABLE_BIRTHDAY, Database.COLUMN_BIRTHDAY_DATE, dato, Database.COLUMN_BIRTHDAY_USERID, sharedPreferences.getString(User.ID, null));
                navController.navigateUp();
            }
        });
    }
}
