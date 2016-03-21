package com.apps.andrew.lifelinker;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class LifeLinkActivityFragment extends Fragment {

    private static final  int REQUEST_P1_NAME = 0;
    private static final  int REQUEST_P2_NAME = 1;
    Button mP1plusButton;
    Button mP1minusButton;
    Button mP2plusButtton;
    Button mP2minusButton;
    String name;
    static Player p1;
    static Player p2;
    static TextView p1Life;
    static TextView p2Life;
    static TextView p1Name;
    static TextView p2Name;

    private void setPlayerLife(Player p, Boolean plus) {
        if(plus)
            p.setLife(p.getLife() +1);
        else
            p.setLife(p.getLife() - 1);
    }



     public static void resetGame(){
         p1.setLife(20);
         p2.setLife(20);
         p1Life.setText(Integer.toString(p1.getLife()));
         p2Life.setText(Integer.toString(p2.getLife()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK) return;
        if(requestCode == REQUEST_P2_NAME){
            name = (String) data.getSerializableExtra("name");
            p2.setName(name);
            p2Name.setText(p2.getName());
        }
        else if(requestCode == REQUEST_P1_NAME){
            name = (String) data.getSerializableExtra("name");
            p1.setName(name);
            p1Name.setText(p1.getName());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        p1 = new Player();
        p2 = new Player();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_life_link, container, false);
        p1Life = (TextView) v.findViewById(R.id.p1_life);
        p2Life = (TextView) v.findViewById(R.id.p2_life);
        p2Name = (TextView) v.findViewById(R.id.p2_name);
        p2Name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    ChangeNameDialogFragment dialog = ChangeNameDialogFragment.newInstance();
                    dialog.setTargetFragment(LifeLinkActivityFragment.this, REQUEST_P2_NAME);
                    dialog.show(getFragmentManager(), "Name");
                }
            });

        p1Name = (TextView) v.findViewById(R.id.p1_name);
        p1Name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeNameDialogFragment dialog = ChangeNameDialogFragment.newInstance();
                dialog.setTargetFragment(LifeLinkActivityFragment.this, REQUEST_P1_NAME);
                dialog.show(getFragmentManager(), "Name");
            }
        });

        mP1plusButton = (Button) v.findViewById(R.id.p1plus_button);
        mP1plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlayerLife(p1, true);
                p1Life.setText(Integer.toString(p1.getLife()));
            }
        });

        mP1minusButton = (Button) v.findViewById(R.id.p1minus_button);
        mP1minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlayerLife(p1, false);
                p1Life.setText(Integer.toString(p1.getLife()));
            }
        });

        mP2plusButtton = (Button) v.findViewById(R.id.p2plus_button);
        mP2plusButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlayerLife(p2, true);
                p2Life.setText(Integer.toString(p2.getLife()));
            }
        });

        mP2minusButton = (Button) v.findViewById(R.id.p2minus_button);
        mP2minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlayerLife(p2, false);
                p2Life.setText(Integer.toString(p2.getLife()));
            }
        });

        return v;
    }
}
