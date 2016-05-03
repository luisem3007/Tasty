package com.example.jorge.tasty;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;



public class fragmentlistmap extends Fragment {
    private ArrayAdapter<String> adapter;
    ArrayList<String> arrayList;
    private ListView list;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragmentlistmap, container, false);
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
        list = (ListView) getView().findViewById(R.id.listView);
        arrayList = new ArrayList<String>();
        ArrayList<String> arrayList1 = new ArrayList<String>();

        adapter = new ArrayAdapter<String>(getActivity(), R.layout.custom_layout, arrayList1);


        for (int i = 0 ; i < information.arrayListRestaurantInformation.size() ; i ++)
        {
            String[] macaco =(information.arrayListRestaurantInformation.get(i).toString().split("_"));
            arrayList1.add(macaco[0]);
        }
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // *********************************************************************Touch arrayList Functionality

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] Information = information.arrayListRestaurantInformation.get(position).toString().split("_");
                information.ServerIP = Information[1];
                String item = ((TextView) view).getText().toString();
                information.restauranteNombre = item;
                startConversation();
            }
        });


        // *********************************************************************End Touch arrayList Functionality

    }

     public void startConversation()
    {
        Intent i;
        i = new Intent(getActivity(), chatrestaruante.class);
        startActivity(i);
    }

}
