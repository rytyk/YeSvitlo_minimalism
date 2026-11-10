package com.example.yesvitlo;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    Button btnCheck;
    TextView tvResult, tvDate;
    String channelURL = "https://t.me/s/cek_info";
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        spinner = findViewById(R.id.spinner);
        btnCheck = findViewById(R.id.btnCheck);
        tvResult = findViewById(R.id.tvResult);
        tvDate = findViewById(R.id.tvDate);

        String[] group = {"Черга 1.1", "Черга 1.2","Черга 2.1", "Черга 2.2","Черга 3.1", "Черга 3.2","Черга 4.1", "Черга 4.2","Черга 5.1", "Черга 5.2","Черга 6.1", "Черга 6.2",};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, group);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        String selectedGroup = spinner.getSelectedItem().toString();


        btnCheck.setOnClickListener(v -> checkTg(selectedGroup));
    }

    private void checkTg(String group){
        tvResult.setText(R.string.status_loading);
        new Thread(() -> {
           try{
               Document doc = Jsoup.connect(channelURL).userAgent("Mozilla/5.0").timeout(15000).get();
               Elements posts = doc.select(".tgme_widget_message");
               String dateTime = "дату не знайдено";
               String result = getString(R.string.status_not_found);
               if (!posts.isEmpty()){
                   for (int i = posts.size()-1; i>=0; i--){
                       Element lastPost = posts.last();
                       Element textElement = lastPost.selectFirst(".tgme_widget_message_text");
                        if (textElement == null){
                            continue;
                        }
                        String postText = textElement.text();
                        if (postText.contains(group)){
                            Element timeElement = lastPost.selectFirst(".tgme_widget_message_date time");
                            if (textElement != null){
                                result = textElement.text();
                            }else{
                                result = getString(R.string.status_no_text);
                            }if(timeElement != null){
                                dateTime = timeElement.attr("datetime");
                                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US);
                                Date date = inputFormat.parse(dateTime);
                                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());
                                dateTime = outputFormat.format(date);
                            }else{
                                dateTime = "Не вдалось отримати дату";
                            }
                        }
                   }



               }
               String finalResult = result;
               String finalDate = dateTime;
               runOnUiThread(() -> tvResult.setText(finalResult));
               runOnUiThread(() -> tvDate.setText(finalDate));
           } catch (Exception e){
               runOnUiThread(()-> tvResult.setText(R.string.status_error));
               runOnUiThread(()-> tvResult.setText(R.string.status_error));
           }
        }).start();
    }
}