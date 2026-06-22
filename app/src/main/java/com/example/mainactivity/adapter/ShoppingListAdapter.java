package com.example.mainactivity.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import com.example.mainactivity.model.ShoppingListModel;
import com.example.mainactivity.R;
import com.example.mainactivity.model.User;
import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder> {

    private List<ShoppingListModel> ShoppingList;
    private LayoutInflater inflater;
    private Context context;
    private Database database;
    private SharedPreferences sharedPreferences;
    private ShoppingListModel modelToDisplay;
    private int familieID;

    public ShoppingListAdapter(Context context, List<ShoppingListModel> ShoppingListList, int iD) {
        this.inflater = LayoutInflater.from(context);
        this.ShoppingList = ShoppingListList;
        this.context = context;
        this.familieID = iD;
    }

    @NonNull
    @Override
    public ShoppingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        sharedPreferences = context.getSharedPreferences(User.SESSION, Context.MODE_PRIVATE);
        View itemView = inflater.inflate(R.layout.shopping_list_list_item, parent, false);
        return new ShoppingListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingListViewHolder viewHolder, int position) {
        modelToDisplay = ShoppingList.get(position);

        viewHolder.setShoppingList(modelToDisplay, position);
        viewHolder.setDelete(modelToDisplay, position);
        viewHolder.setEdit(modelToDisplay, position);
    }

    @Override
    public int getItemCount() {
        return ShoppingList.size();
    }

    // Indre klasse
    public class ShoppingListViewHolder extends RecyclerView.ViewHolder {

        //Cardviewet
        private CardView card;
        private TextView nr, bruker;
        private ImageView delete;

        public ShoppingListViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setShoppingList(ShoppingListModel modelToDisplay, int position) {
            // Kobler variablene med sine respektive elementer i cardviewet
            nr = itemView.findViewById(R.id.shopping_listummer);
            bruker = itemView.findViewById(R.id.bruker);
            // setter teksten i cardviewet
            nr.setText(modelToDisplay.getTittel());
            bruker.setText("Createet av: " + modelToDisplay.getNavn());
        }

        // Gjør at en bruker kan slette shopping_list
        public void setDelete(final ShoppingListModel modelToDisplay, final int position) {
            delete = itemView.findViewById(R.id.shopping_listDelete);
            // pop up som spør om brukeren vil slette oppføringer
            View.OnClickListener deleteShoppingList = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialogStyle);
                    builder.setTitle("Slett shopping_list")
                            .setMessage("Er du sikker på at du vil slette denne shopping_list?");
                    builder.setPositiveButton("Ja",
                            new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    // Sletter birthdayen
                                    database = new Database(context);
                                    database.deleteRowFromTableById(Database.TABLE_HANDLELISTE, modelToDisplay.getId());
                                    Log.i("ShoppingListAdapter", "Birthdayen ble slettet");
                                    removeItem(position);
                                }
                            });
                    builder.setNegativeButton("Nei",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    // Går ut av popup'en og tilbake til siden uten å gjøre noe
                                    Log.i("ShoppingListAdapter", "Birthdayen ble ikke slettet");
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert1 = builder.create();
                    alert1.show();
                }
            };
            delete.setOnClickListener(deleteShoppingList);
        }

        //Fjerner og oppdaterer element fra recyclerviewet
        private void removeItem(int position) {
            ShoppingList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, ShoppingList.size());
        }

        // Dersom trykker på CardView, bli han sendt videre shopping_list med en bundle
        public void setEdit(final ShoppingListModel modelToDisplay, int position) {
            card = itemView.findViewById(R.id.cardShoppingList);
            View.OnClickListener edit = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();

                    bundle.putString("TITTEL", modelToDisplay.getTittel());
                    bundle.putString("ID", modelToDisplay.getId());
                    bundle.putInt("FAMILIEID", modelToDisplay.getFamilyID());

                    Navigation.findNavController(card).navigate(R.id.shopping_listListFragment, bundle);
                }
            };
            card.setOnClickListener(edit);
        }
    }
}