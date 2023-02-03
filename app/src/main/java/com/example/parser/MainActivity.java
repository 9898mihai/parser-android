package com.example.parser;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Document doc;
    private Thread SecThread;
    private Runnable runnable;
    private TextView date;
    private ListView listView;
    private CustomArrayAdapter adapter;
    private List<ListItemClass> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        date = findViewById(R.id.cursDate);
        Date currentTime = Calendar.getInstance().getTime();
        String formattedDate = DateFormat.getDateInstance().format(currentTime);
        date.setText(formattedDate);

        listView = findViewById(R.id.listView);
        arrayList = new ArrayList<>();
        adapter = new CustomArrayAdapter(this, R.layout.list_item_1, arrayList,getLayoutInflater());
        listView.setAdapter(adapter);
        runnable = (Runnable) () -> getWeb();
        SecThread = new Thread(runnable);
        SecThread.start();
    }

    private void getWeb() {
        try {
            doc = Jsoup.connect("https://www.curs.md").get();
            Log.d("DebugLog", "Table: " + doc);

            Elements cursBox = doc.getElementsByAttributeValue("id", "cursBox");
            Elements tableBody = cursBox.first().getElementsByTag("tbody");
            Element table = tableBody.get(0);
            for(int i = 0; i < table.childrenSize(); i++) {
                ListItemClass items = new ListItemClass();
                items.setData_1(table.children().get(i).child(1).text());
                items.setData_2(table.children().get(i).child(2).text());
                items.setData_3(table.children().get(i).child(3).getElementsByTag("img").attr("alt").concat(": "));
                items.setData_4(table.children().get(i).child(3).text());
                arrayList.add(items);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });

//            Log.d("DebugLog", "Table: " + table.children().get(0).child(3).getElementsByTag("img").attr("alt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}