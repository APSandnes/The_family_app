package com.example.mainactivity.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mainactivity.Database;
import com.example.mainactivity.model.MealPlanListModel;
import com.example.mainactivity.R;
import com.example.mainactivity.model.User;
import java.util.List;

public class MealPlanListAdapter extends RecyclerView.Adapter<MealPlanListAdapter.MealPlanListViewHolder>{
    private List<MealPlanListModel> MealPlanList;
    private LayoutInflater inflater;
    private MealPlanListModel MealPlanToDisplay;
    private Context contexten;
    private SharedPreferences sharedPreferences;
    private Database database;

    public MealPlanListAdapter(Context context, List<MealPlanListModel> MessageList) {
        this.inflater = LayoutInflater.from(context);
        this.MealPlanList = MessageList;
        this.contexten = context;
    }

    @NonNull
    @Override
    public MealPlanListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        database = new Database(contexten);
        sharedPreferences = getContexten().getSharedPreferences(User.SESSION, Context.MODE_PRIVATE);
        int meID = Integer.parseInt(sharedPreferences.getString(User.ID, null));

        View itemView = inflater.inflate(R.layout.meal_plan_list_list_item, parent, false);
        return new MealPlanListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MealPlanListAdapter.MealPlanListViewHolder viewHolder, int position) {
        MealPlanToDisplay = MealPlanList.get(position);

        viewHolder.setMealPlan(MealPlanToDisplay);
        if (MealPlanToDisplay.getFood() != null)
            viewHolder.setFood(MealPlanToDisplay);
        viewHolder.setFocusChange(MealPlanToDisplay);
    }

    @Override
    public int getItemCount() {
        return MealPlanList.size();
    }

    public Context getContexten() {
        return contexten;
    }

    public class MealPlanListViewHolder extends RecyclerView.ViewHolder {
        private TextView day;
        private EditText food;
        private CardView cardView;

        public MealPlanListViewHolder(@NonNull final View itemView) {
            super(itemView);
        }

        // Setter hvilken dag i meal_planen det er
        public void setMealPlan(final MealPlanListModel MealPlanToDisplay) {
            day = itemView.findViewById(R.id.dayOfWeek);
            day.setText(MealPlanToDisplay.getDay());
        }

        // Setter hva mat er for dagen, dersom det ikke er null
        public void setFood(MealPlanListModel meal_planToDisplay) {
            food = itemView.findViewById(R.id.foodInMealPlan);
            food.setText(meal_planToDisplay.getFood());
        }

        // Dersom en bruker går bort i fra en editText sjekker den om man har endret noe.
            // Dersom man har endret noe, oppdateres databasen
            // Dersom man ikke har endret noe, skjer det ingen ting
        public void setFocusChange(final MealPlanListModel meal_planToDisplay) {
            food = itemView.findViewById(R.id.foodInMealPlan);

            final String before = food.getText().toString();

            food.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus && !before.equals(food.getText().toString())) {
                        updateFood();
                    }
                }

                private void updateFood() {
                    boolean updateFood = database.updateFoodInMealPlan(meal_planToDisplay.getSubMealPlanID(), food.getText().toString());

                    if (updateFood) {
                        Log.i("MealPlanListAdapter", "Matrett på " + meal_planToDisplay.getDay() + " ble oppdatert til " + food.getText().toString());
                    } else
                        Log.e("MealPlanListAdapter", "Matrett på " + meal_planToDisplay.getDay() + " ble IKKE oppdatert");
                }
            });
        }
    }

}
