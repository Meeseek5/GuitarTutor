package com.example.guitartutor;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoricalRecFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoricalRecFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayAdapter<String> listViewAdapter;
    private ListView listView;

    Button buttonReflash;
    Button buttonDelete;

    // SQLite使用的變數
    private final String DB_NAME = "record.db";
    private String TABLE_NAME = "record";
    private final int DB_VERSION = 1;
    private SQLiteDataBaseHelper sqlDataBaseHelper;
    private SQLiteDatabase db;

    public HistoricalRecFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoricalRecFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoricalRecFragment newInstance(String param1, String param2) {
        HistoricalRecFragment fragment = new HistoricalRecFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historical_rec, container, false);

        listView = (ListView) view.findViewById(R.id.mainMenu);

        // 測試ListView使用的陣列
        // String[] menuItem = {"紀錄1", "紀錄2", "紀錄3"};

        /* 使用Shared Preferences
            SharedPreferences recordShortestSecond = getContext().getSharedPreferences("record_shortest_second",MODE_PRIVATE);
            SharedPreferences recordDate = getContext().getSharedPreferences("record_date",MODE_PRIVATE);

            recordDate.edit()
            .putString("DATE", dateFormat.format(date))
            .commit();

            String str = String.format("%20ss",Integer.toString(recordShortestSecond.getInt("SHORTEST_SECOND", 0)));
            menuItem2.add(recordDate.getString("DATE","NULL")+str);
         */

        // 建立SQLiteOpenHelper物件
        sqlDataBaseHelper = new SQLiteDataBaseHelper(this.getContext(), DB_NAME,null, DB_VERSION, TABLE_NAME);
        // 開啟資料庫
        db = sqlDataBaseHelper.getWritableDatabase();
        ArrayList<String> menuItem2 = traverseDataBase();

        // 資料放進ListView
        // ListView listView = (ListView) view.findViewById(R.id.mainMenu);
        listViewAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                menuItem2
        );

        listView.setAdapter(listViewAdapter);

        // Inflate the layout for this fragment
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonReflash = (Button) getView().findViewById(R.id.button_reflash);
        // buttonDelete = (Button) getView().findViewById(R.id.button_delete);

        // 更新ListView畫面
        buttonReflash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqlDataBaseHelper = new SQLiteDataBaseHelper(getContext(), DB_NAME,null, DB_VERSION, TABLE_NAME);
                db = sqlDataBaseHelper.getWritableDatabase();
                ArrayList<String> menuItem3 = traverseDataBase();

                listViewAdapter = new ArrayAdapter<String>(
                        getActivity(),
                        android.R.layout.simple_list_item_1,
                        menuItem3
                );

                listView.setAdapter(listViewAdapter);
                Toast toast = Toast.makeText(getContext(), "資料已更新", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 400);
                toast.show();
            }
        });

        /* 刪除按鈕 點擊事件監聽
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqlDataBaseHelper = new SQLiteDataBaseHelper(getContext(), DB_NAME,null, DB_VERSION, TABLE_NAME);
                sqlDataBaseHelper.deleteAll();
                Toast toast = Toast.makeText(getContext(), "全部資料已經刪除", Toast.LENGTH_SHORT );
                toast.setGravity(Gravity.CENTER, 0, 400);
                toast.show();
            }
        });
         */

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

    /**
     * 讀出SQLite資料並放進ListView
     */
    public ArrayList<String> traverseDataBase() {
        ArrayList<String> list = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ TABLE_NAME, null);
        if(cursor == null) {
            return list;
        }

        if(cursor.getCount() == 0) {
            return  list;
        } else {
            cursor.moveToFirst();
            String str = String.format("%s%20ss", cursor.getString(1),
                    cursor.getString(2));
            list.add(str);
            while (cursor.moveToNext()) {
                String str2 = String.format("%s%20ss", cursor.getString(1),
                        cursor.getString(2));
                list.add(str2);
            }
        }
        cursor.close();

        return  list;
    }



}