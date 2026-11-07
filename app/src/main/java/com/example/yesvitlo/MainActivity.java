package com.example.yesvitlo;

import android.os.Bundle;
import android.widget.Button;
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
        btnCheck = findViewById(R.id.btnCheck);
        tvResult = findViewById(R.id.tvResult);
        tvDate = findViewById(R.id.tvDate);

        btnCheck.setOnClickListener(v -> checkTg());
    }

    private void checkTg(){
        tvResult.setText(R.string.status_loading);
        new Thread(() -> {
           try{
               Document doc = Jsoup.connect(channelURL).userAgent("Mozilla/5.0").timeout(15000).get();
               Elements posts = doc.select(".tgme_widget_message");

               String result = getString(R.string.status_not_found);
               String dateTime = "дату не знайдено";
               if (!posts.isEmpty()){
                   Element lastPost = posts.last();
                   Element textElement = lastPost.selectFirst(".tgme_widget_message_text");
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