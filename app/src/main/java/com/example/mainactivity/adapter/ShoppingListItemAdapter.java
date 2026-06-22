package com.example.mainactivity.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mainactivity.Database;
import com.example.mainactivity.model.ShoppingListItemsModel;
import com.example.mainactivity.R;
import java.util.List;

public class ShoppingListItemAdapter extends RecyclerView.Adapter<ShoppingListItemAdapter.ShoppingListItemViewHolder> {
    private List<ShoppingListItemsModel> varelistList;
    private LayoutInflater inflater;
    private Context context;
    private Database database;

    public ShoppingListItemAdapter(Context context, List<ShoppingListItemsModel> varelist) {
        this.inflater = LayoutInflater.from(context);
        this.varelistList = varelist;
        this.context = context;
    }

    @NonNull
    @Override
    public ShoppingListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_list_item, parent, false);

        database = new Database(context);

        return new ShoppingListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingListItemViewHolder holder, int position) {
        ShoppingListItemsModel modelToDisplay = varelistList.get(position);

        holder.setItem(modelToDisplay);
        holder.setDelete(modelToDisplay, position);
        holder.setBought(modelToDisplay, position);
    }

    @Override
    public int getItemCount() {
        return varelistList.size();
    }

    public class ShoppingListItemViewHolder extends RecyclerView.ViewHolder {
        public ShoppingListItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        private TextView vare = itemView.findViewById(R.id.vareText);
        private CardView ItemBoks = itemView.findViewById(R.id.ItemBoks);
        private CheckBox checkBox = itemView.findViewById(R.id.shopping_listCheckBox);

        // Sette navn på vare og om den er checked dersom den er det
        public void setItem(ShoppingListItemsModel modelToDisplay) {
            vare.setText(modelToDisplay.getItem());

            if (modelToDisplay.isChecked())
                checkBox.setChecked(true);
        }

        // Gjør at en bruker kan slette en vare dersom han trykker lenge
        public void setDelete(final ShoppingListItemsModel modelToDisplay, final int position) {
            ItemBoks.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialogStyle);
                    builder.setTitle("Slett vare")
                            .setMessage("Er du sikker på at du vil slette denne varen?");
                    builder.setPositiveButton("Ja",
                            new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    // Sletter varen
                                    database = new Database(context);
                                    database.deleteRowFromTableById(Database.TABLE_HANDLELISTE_LISTE, modelToDisplay.getId());
                                    Log.i("ShoppingListItemAdapter", "Itemn " + modelToDisplay.getItem() + " er slettet");
                                    removeItem(position);
                                }
                            });
                    builder.setNegativeButton("Nei",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    // Går ut av popup'en og tilbake til siden uten å gjøre noe
                                    Log.i("ShoppingListItemAdapter", "Itemn " + modelToDisplay.getItem() + " ble ikke slettet");
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert1 = builder.create();
                    alert1.show();

                    return false;
                }
            });

        }

        // Setter varen til kjøpt/ikke kjøpt dersom han trykker på checkbox/card
        public void setBought(final ShoppingListItemsModel modelToDisplay, int position) {
            ItemBoks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox.setChecked(!checkBox.isChecked());
                    setAsBought(modelToDisplay);
                }
            });
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox.setChecked(!checkBox.isChecked());
                    setAsBought(modelToDisplay);
                }
            });
        }

        // Oppdaterer at varen er kjøpt eller ikke i databasen
        public void setAsBought(final ShoppingListItemsModel modelToDisplay) {
            int isChecked;
            if (checkBox.isChecked()) {
                isChecked = 1;
            } else {
                isChecked = 0;
            }
            System.out.println(isChecked);
            database.updateItemIsCheckedShoppingList(Integer.parseInt(modelToDisplay.getId()), isChecked);
        }

        // sletter en vare fra recyclerView
        private void removeItem(int position) {
            varelistList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, varelistList.size());
        }
    }
}