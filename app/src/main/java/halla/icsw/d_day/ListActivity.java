package halla.icsw.d_day;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ListActivity extends AppCompatActivity {
    TextView tv;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        tv  = findViewById(R.id.textView3);
        tv.setText("훗날기억을 되새겨 봄으로써 순간이나마\n그 시절의 감성을 느낄 수 있는\n추억은, 그 자체로 축복이다.");
    }
public void Onclick(View view){
    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
    startActivity(intent);
    finish();
}
}
