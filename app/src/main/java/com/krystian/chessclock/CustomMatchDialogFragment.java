package com.krystian.chessclock;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class CustomMatchDialogFragment extends DialogFragment implements SeekBar.OnSeekBarChangeListener {

    private View dialogView;

    private SeekBar numberOfGamesBar;
    private TextView numberOfGamesText;
    private EditText matchName;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.custom_match_dialog, null);
        setViewComponents();
        builder.setView(dialogView)
                .setTitle(R.string.add_new_match)
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String name = matchName.getText().toString();
                        if(name.isEmpty())
                            Toast.makeText(getActivity(), R.string.no_name_chosen, Toast.LENGTH_SHORT).show();
                        else {
                            Intent intent = new Intent(getContext(), CustomMatchActivityList.class);
                            intent.putExtra("matchName", name);
                            intent.putExtra("numberOfGames", numberOfGamesBar.getProgress()+2);
                            startActivity(intent);
                            Toast.makeText(getActivity(), R.string.match_created, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });

        return builder.create();
    }

    public void setViewComponents() {
        numberOfGamesBar = dialogView.findViewById(R.id.number_of_games_bar);
        numberOfGamesText = dialogView.findViewById(R.id.number_of_games_dialog);
        matchName = dialogView.findViewById(R.id.match_name);
        numberOfGamesBar.setOnSeekBarChangeListener(this);

        numberOfGamesBar.setProgress(3);
        numberOfGamesText.setText(getString(R.string.number_of_games, numberOfGamesBar.getProgress()+2));
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int numberOfGames = progress + 2; //range 2-30
        numberOfGamesText.setText(getString(R.string.number_of_games, numberOfGames));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
