package com.example.mainactivity.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mainactivity.Database;
import com.example.mainactivity.model.MealPlanModel;
import com.example.mainactivity.R;
import java.util.List;

public class MealPlanAdapter extends RecyclerView.Adapter<MealPlanAdapter.MealPlanViewHolder> {
    private List<MealPlanModel> MealPlanlist;
    private LayoutInflater inflater;
    private Context context;
    private Database database;

    public MealPlanAdapter(Context context, List<MealPlanModel> MealPlanList) {
        this.inflater = LayoutInflater.from(context);
        this.MealPlanlist = MealPlanList;
        this.context = context;
    }

    @NonNull
    @Override
    public MealPlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View itemView = inflater.inflate(R.layout.meal_plan_list_item, parent, false);
        return new MealPlanViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MealPlanViewHolder viewHolder, int position) {
        MealPlanModel meal_planToDisplay = MealPlanlist.get(position);

        viewHolder.setMealPlan(meal_planToDisplay, position);
        viewHolder.setDelete(meal_planToDisplay, position);
        viewHolder.setMealPlanen(meal_planToDisplay, position);
        viewHolder.setDate(meal_planToDisplay);
    }

    @Override
    public int getItemCount() {
            return MealPlanlist.size();
    }

    public class MealPlanViewHolder extends RecyclerView.ViewHolder {
        // Cardviewet
        private CardView card;

        // Elementer i cardviewet
        private TextView uke, dato;
        private ImageView delete;

        public MealPlanViewHolder(@NonNull View itemView) {
            super(itemView);
            uke = itemView.findViewById(R.id.CardviewMealPlan);
        }

        // Setter overskrift
        public void setMealPlan(MealPlanModel meal_planToDisplay, int position) {
            uke = itemView.findViewById(R.id.CardviewMealPlan);
            uke.setText("MealPlan uke " + meal_planToDisplay.getWeek());
        }

        // Gjør at en bruker kan slette et element
        public void setDelete(final MealPlanModel meal_planToDisplay, final int position) {
            delete = itemView.findViewById(R.id.deletemeal_plan);
            // pop up som spør om brukeren vil slette oppføringer
            View.OnClickListener deleteMealPlan = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialogStyle);
                    builder.setTitle("Slett meal_plan")
                            .setMessage("Er du sikker på at du vil slette denne meal_planen?");
                    builder.setPositiveButton("Ja",
                            new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    // Sletter meal_planen
                                    database = new Database(context);
                                    database.deleteRowFromTableById(Database.TABLE_MATPLAN, String.valueOf(meal_planToDisplay.getMealPlanID()));
                                    removeItem(position);
                                    Log.i("MealPlanAdapter", "MealPlanen ble slettet");
                                }
                            });
                    builder.setNegativeButton("Nei",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    // Går ut av popup'en og tilbake til siden uten å gjøre noe
                                    Log.i("MealPlanAdapter", "MealPlanen ble ikke slettet");
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert1 = builder.create();
                    alert1.show();
                }
            };
            delete.setOnClickListener(deleteMealPlan);
        }

        //Fjerner og oppdaterer element fra recyclerviewet
        private void removeItem(int position) {
            MealPlanlist.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, MealPlanlist.size());
        }

        // Dersom bruker trykker på meal_plan, vil han bli sendt videre med en bundle
        public void setMealPlanen(final MealPlanModel meal_planToDisplay, int position) {
            card = itemView.findViewById(R.id.MealPlancardview);
            View.OnClickListener edit = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("ID", meal_planToDisplay.getMealPlanID());
                    bundle.putInt("UKE", meal_planToDisplay.getWeek());

                    Navigation.findNavController(card).navigate(R.id.meal_planListFragment, bundle);
                }
            };
            card.setOnClickListener(edit);
        }

        public void setDate(MealPlanModel meal_planToDisplay) {
            dato = itemView.findViewById(R.id.datoMealPlan);
            String datoer = meal_planToDisplay.getFromDate() + " - " + meal_planToDisplay.getToDate();
            dato.setText(datoer);
        }
    }
}
