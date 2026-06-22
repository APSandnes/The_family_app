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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mainactivity.Database;
import com.example.mainactivity.model.FamilyBubbleModel;
import com.example.mainactivity.R;

import java.util.List;

public class FamilyBubbleAdapter extends RecyclerView.Adapter<FamilyBubbleAdapter.FamilyBubbleViewHolder> {
    // Variabler
    private List<FamilyBubbleModel> ConversationList;
    private LayoutInflater inflater;
    private FamilyBubbleModel ConversationToDisplay;
    private Context contexten;
    private Database database;

    // Konstruktør
    public FamilyBubbleAdapter(Context context, List<FamilyBubbleModel> ConversationList) {
        this.inflater = LayoutInflater.from(context);
        this.ConversationList = ConversationList;
        this.contexten = context;
    }

    // Sletter valgt samtale
    private void removeItem(int position) {
        ConversationList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, ConversationList.size());
    }

    @NonNull
    @Override
    public FamilyBubbleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View itemView = inflater.inflate(R.layout.family_bubble_list_item, parent, false);
        return new FamilyBubbleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FamilyBubbleAdapter.FamilyBubbleViewHolder viewHolder, int position) {
        ConversationToDisplay = ConversationList.get(position);

        viewHolder.setConversation(ConversationToDisplay);
        viewHolder.setDeleteOnConversation(ConversationToDisplay, position);
        viewHolder.setClickOnConversation(ConversationToDisplay);
    }

    @Override
    public int getItemCount() {
        return ConversationList.size();
    }

    public class FamilyBubbleViewHolder extends RecyclerView.ViewHolder {

        // Variabler
        private TextView navn, userName;
        private ConstraintLayout card;
        private ImageView delete;

        public FamilyBubbleViewHolder(@NonNull final View itemView) {
            super(itemView);
        }

        // Setter navn og brukernavn på CardView
        public void setConversation(final FamilyBubbleModel ConversationToDisplay) {
            navn = itemView.findViewById(R.id.FamilyBoblaNameCardview);
            userName = itemView.findViewById(R.id.userNameFamilyBubble);

            navn.setText(ConversationToDisplay.getConversationName());
            userName.setText("Conversation med: " + ConversationToDisplay.getUserToName());
        }

        // Sletter en samtale fra databasen
        public void setDeleteOnConversation(final FamilyBubbleModel ConversationToDisplay, final int position) {
            delete = itemView.findViewById(R.id.imageButton);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(contexten);
                    builder.setTitle("Slett samtale")
                            .setMessage("Er du sikker på at du vil slette denne samtalen med " + ConversationToDisplay.getUserToName() + "?");
                    builder.setPositiveButton("Ja",
                            new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    // Sletter samtalen
                                    database = new Database(contexten);
                                    database.deleteRowFromTableById(Database.TABLE_CONVERSATION ,ConversationToDisplay.getIden());
                                    removeItem(position);
                                    Log.i("FamilyBubbleAdapter", "Conversationn er slettet");
                                }
                            });
                    builder.setNegativeButton("Nei",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    Log.i("FamilyBubbleAdapter", "Conversationn ble IKKE slettet");
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert1 = builder.create();
                    alert1.show();
                    System.out.println(ConversationToDisplay.getIden() + " - " + getNavn());
                }
            };
            delete.setOnClickListener(onClickListener);
        }

        // Dersom man klikker på samtalen, kommer man inn til selve samtalen, og det blir sendt med en bundle med info
        public void setClickOnConversation(final FamilyBubbleModel ConversationToDisplay) {
            card = itemView.findViewById(R.id.cardID);
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("samtaleId", ConversationToDisplay.getIden());
                    bundle.putString("samtaleTo", ConversationToDisplay.getUserToName());
                    bundle.putString("samtaleName", ConversationToDisplay.getConversationName());

                    Navigation.findNavController(card).navigate(R.id.action_family_bubbleFragment_to_family_bubbleConversationFragment, bundle);
                }
            });
        }
        public String getNavn() {
            return navn.getText().toString();
        }
    }
}
