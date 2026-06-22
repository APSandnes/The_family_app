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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mainactivity.Database;
import com.example.mainactivity.model.CalendarPageModel;
import com.example.mainactivity.R;
import com.example.mainactivity.model.User;
import java.util.List;

public class CalendarPageAdapter extends RecyclerView.Adapter<CalendarPageAdapter.CalendarViewHolder>{

    private List<CalendarPageModel> AktivitetsList;
    private LayoutInflater inflater;
    private CalendarPageModel calendarPageModel;
    private Context contexten;
    private Database database;
    private SharedPreferences sharedPreferences;
    private int meID;

    public CalendarPageAdapter(Context context, List<CalendarPageModel> AktivitetsList) {
        this.inflater = LayoutInflater.from(context);
        this.AktivitetsList = AktivitetsList;
        this.contexten = context;
    }

    private void removeItem(int position) {
        AktivitetsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, AktivitetsList.size());
    }

    @NonNull
    @Override
    public CalendarPageAdapter.CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        sharedPreferences = getContexten().getSharedPreferences(User.SESSION, Context.MODE_PRIVATE);
        meID = Integer.parseInt(sharedPreferences.getString(User.ID, null));

        View itemView = inflater.inflate(R.layout.calendar_activity_list_item, parent, false);
        return new CalendarPageAdapter.CalendarViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarPageAdapter.CalendarViewHolder viewHolder, int position) {
        calendarPageModel = AktivitetsList.get(position);

        viewHolder.setAktivitet(calendarPageModel);
        viewHolder.setDato(calendarPageModel);
        viewHolder.hideDelete(calendarPageModel, position);
    }

    @Override
    public int getItemCount() {
        return AktivitetsList.size();
    }

    public Context getContexten() {
        return contexten;
    }

    public class CalendarViewHolder extends RecyclerView.ViewHolder {
        private TextView aktivitet, userName, datoOgTid;
        private ImageView delete;
        private CardView kortID;

        public CalendarViewHolder(@NonNull final View itemView) {
            super(itemView);
        }

        // Setter aktivitet og brukernavn på CardView
        public void setAktivitet(final CalendarPageModel ActivityToDisplay) {
            aktivitet = itemView.findViewById(R.id.aktivitet);
            userName = itemView.findViewById(R.id.brukerNavn);

            aktivitet.setText(ActivityToDisplay.getTheActivity());
            if (!calendarPageModel.getIsBirthday())
                userName.setText("Aktivitet for: " + ActivityToDisplay.getUserName());
            else
                userName.setVisibility(View.GONE);
        }

        // Bygger opp en tekst og setter dato på CardView
        public void setDato(CalendarPageModel calendarPageModel) {
            datoOgTid = itemView.findViewById(R.id.datoOgTid);

            String datoOfTidText = calendarPageModel.getDateFrom();

            if (calendarPageModel.getTimeFrom() != null && !calendarPageModel.getIsBirthday())
                datoOfTidText += " (" + calendarPageModel.getTimeFrom() + ")";

            if (calendarPageModel.getDateTo() != null)
                datoOfTidText += " - " + calendarPageModel.getDateTo();

            if (calendarPageModel.getTimeTo() != null) {
                if (calendarPageModel.getDateTo() == null)
                    datoOfTidText += " -";
                datoOfTidText += " (" + calendarPageModel.getTimeTo() + ")";
            }
            datoOgTid.setText(datoOfTidText);
        }

        // Sletter en aktivitet
        public void setDeleteOnActivity(final CalendarPageModel calendarPageModel, final int position) {
                delete = itemView.findViewById(R.id.slettAktivitet);
                View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(contexten, R.style.MyDialogStyle);
                        builder.setTitle("Slett aktivitet")
                                .setMessage("Er du sikker på at du vil slette denne aktiviteten?");
                        builder.setPositiveButton("Ja",
                                new DialogInterface.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        // Sletter aktivitet
                                        database = new Database(contexten);
                                        database.deleteRowFromTableById(Database.TABLE_CALENDAR_ACTIVITY , String.valueOf(calendarPageModel.getActivityID()));
                                        removeItem(position);
                                        Log.i("CalendarPageAdapter", "Aktiviteten ble slettet");
                                    }
                                });
                        builder.setNegativeButton("Nei",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        Log.i("CalendarPageAdapter", "Aktiviteten ble ikke slettet");
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert1 = builder.create();
                        alert1.show();
                    }
                };
                delete.setOnClickListener(onClickListener);
            }

        // Dersom man er "eier" av aktiviteten kan man slette den, hvis ikke vil slett-kanppen bli gjemt
        public void hideDelete(CalendarPageModel calendarPageModel, int position) {
            if (meID == calendarPageModel.getUserID() && !calendarPageModel.getIsBirthday()) {
                setDeleteOnActivity(calendarPageModel, position);
                setClickOnConversation(calendarPageModel);
            } else {
                delete = itemView.findViewById(R.id.slettAktivitet);
                delete.setVisibility(View.INVISIBLE);

                kortID = itemView.findViewById(R.id.cardShoppingList);
                kortID.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Log.i("CalendarPageAdapter", "Oida, denne har ikke du rettigheter til å ordne med");
                        return true;
                    }
                });
            }
        }

        // Ggør at en bruker kan slette en aktivitet
        private void setClickOnConversation(final CalendarPageModel calendarPageModel) {
            kortID = itemView.findViewById(R.id.cardShoppingList);
            kortID.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(contexten, R.style.MyDialogStyle);
                    builder.setTitle("Endre aktivitet")
                            .setMessage("Vil du endre aktiviteten: " + calendarPageModel.getTheActivity() + "?");
                    builder.setPositiveButton("Ja",
                            new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("dateFrom", calendarPageModel.getDateFrom());
                                    bundle.putString("dateTo", calendarPageModel.getDateTo());
                                    bundle.putString("timeFrom", calendarPageModel.getTimeFrom());
                                    bundle.putString("timeTo", calendarPageModel.getTimeTo());
                                    bundle.putString("theActivity", calendarPageModel.getTheActivity());
                                    bundle.putInt("activityID", calendarPageModel.getActivityID());
                                    Log.i("CalendarPageAdapter", "Sender deg videre til redigering av calendar aktivitet");
                                    Navigation.findNavController(kortID).navigate(R.id.calendarEditFragment, bundle);
                                }
                            });
                    builder.setNegativeButton("Nei",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    Log.i("CalendarPageAdapter", "Du blir ikke sendt videre til redigering av calendar aktivitet");
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert1 = builder.create();
                    alert1.show();
                    return true;
                }
            });
        }
    }
}























