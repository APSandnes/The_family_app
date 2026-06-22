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
import com.example.mainactivity.model.WishlistModel;
import com.example.mainactivity.R;
import com.example.mainactivity.model.User;
import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {
    private List<WishlistModel> Wishlistr;
    private LayoutInflater inflater;
    private WishlistModel modelToDisplay;
    private Context contexten;
    private Database database;
    private SharedPreferences sharedPreferences;
    private int meID;

    public WishlistAdapter(Context context, List<WishlistModel> Wishlistr, int meID) {
        this.inflater = LayoutInflater.from(context);
        this.Wishlistr = Wishlistr;
        this.contexten = context;
        this.meID = meID;
    }

    // Sletter fra RecyclerView
    private void removeItem(int position) {
        Wishlistr.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, Wishlistr.size());
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        sharedPreferences = contexten.getSharedPreferences(User.SESSION, Context.MODE_PRIVATE);
        View itemView = inflater.inflate(R.layout.shopping_list_list_item, parent, false);

        return new WishlistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistViewHolder viewHolder, int position) {
        modelToDisplay = Wishlistr.get(position);

        viewHolder.setWishlist(modelToDisplay);
        viewHolder.setDeleteOnWishlist(modelToDisplay, position);
        viewHolder.setClickOnWishlist(modelToDisplay);
        viewHolder.hideElements(modelToDisplay);
    }

    @Override
    public int getItemCount() {
        return Wishlistr.size();
    }

    public class WishlistViewHolder extends RecyclerView.ViewHolder {
        private TextView tittel, navn;
        private CardView card;
        private ImageView delete;

        public WishlistViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        // Setter data i card
        public void setWishlist(WishlistModel modelToDisplay) {
            tittel = itemView.findViewById(R.id.shopping_listummer);
            navn = itemView.findViewById(R.id.bruker);
            tittel.setText(modelToDisplay.getWishlistName());
            navn.setText("Createet av: " + modelToDisplay.getUserToName());
        }

        // Gjør at en bruker kan slette et element
        public void setDeleteOnWishlist(final WishlistModel modelToDisplay, final int position) {
            delete = itemView.findViewById(R.id.shopping_listDelete);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(contexten, R.style.MyDialogStyle);
                    builder.setTitle("Slett ønskelist")
                            .setMessage("Er du sikker på at du vil slette denne ønskelist?");
                    builder.setPositiveButton("Ja",
                            new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    // Sletter samtalen
                                    database = new Database(contexten);
                                    database.deleteRowFromTableById(Database.TABLE_WISHLIST , String.valueOf(modelToDisplay.getWishlistID()));
                                    removeItem(position);
                                    Log.i("WishlistAdapter", "Ønskelist er slettet");
                                }
                            });
                    builder.setNegativeButton("Nei",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    Log.i("WishlistAdapter", "Ønskelist ble ikke slettet");
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert1 = builder.create();
                    alert1.show();
                    System.out.println(modelToDisplay.getWishlistID() + " - " + modelToDisplay.getWishlistName() + "(" + modelToDisplay.getUserToName() + ")");
                }
            };
            delete.setOnClickListener(onClickListener);
        }

        // Går videre til neste side med en bundle
        public void setClickOnWishlist(final WishlistModel modelToDisplay) {
            card = itemView.findViewById(R.id.cardShoppingList);
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("wishlistId", modelToDisplay.getWishlistID());
                    bundle.putString("wishlistForBruker", modelToDisplay.getUserToName());
                    bundle.putInt("wishlistForBrukerID", modelToDisplay.getUserToID());
                    bundle.putString("wishlistNavn", modelToDisplay.getWishlistName());

                    Navigation.findNavController(card).navigate(R.id.action_wishlistFragment_to_wishlistListFragment, bundle);
                }
            });
        }

        public void hideElements(WishlistModel modelToDisplay) {
            delete = itemView.findViewById(R.id.shopping_listDelete);

            if (modelToDisplay.getUserToID() != meID)
                delete.setVisibility(View.INVISIBLE);
        }
    }
}
