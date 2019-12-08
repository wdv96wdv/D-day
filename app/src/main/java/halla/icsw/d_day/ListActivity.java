package halla.icsw.d_day;

import android.content.Intent;
import android.media.tv.TvContentRating;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    public static TextView tv;
    public static TextView tv2;
    public static List<String> list;
    private ArrayAdapter simpleAdapter;
    private ListView listView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        //test
        list = new ArrayList<>();

        listView = findViewById(R.id.list);
        simpleAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(simpleAdapter);
        tv = findViewById(R.id.listtv);
        tv2 = findViewById(R.id.listtv2);


      /*  listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent intent1 = new Intent(ListActivity.this, MainActivity.class);
                        intent1.putExtra("request", 0);
                        startActivityForResult(intent1, 0);
                        break;
                    case 1:
                        Intent intent2 = new Intent(ListActivity.this, MainActivity.class);
                        intent2.putExtra("request", 1);
                        startActivityForResult(intent2, 1);
                        break;
                    default:
                        break;
                }

            }
        });*/

        tv.setText("처음으로 디데이를 입력해주세요\n");
        tv2.setText("훗날기억을 되새겨 봄으로써 순간이나마\n그 시절의 감성을 느낄 수 있는\n추억은, 그 자체로 축복이다.");
    }

    public void onclickbtn(View view) {
        Intent intent1 = new Intent(ListActivity.this, MainActivity.class);
        intent1.putExtra("request", 0);
        startActivityForResult(intent1, 0);
        finish();
    }



    @Override
    protected void onResume() {
        super.onResume();
        simpleAdapter.notifyDataSetChanged();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        String response = data.getStringExtra("data");
//        list.add(response);
//        simpleAdapter.notifyDataSetChanged();
//    }

    public void Onclick(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

    }

    public void backbtn(View view) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
