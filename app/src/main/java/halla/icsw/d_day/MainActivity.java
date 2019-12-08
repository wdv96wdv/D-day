package halla.icsw.d_day;



import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.drm.DrmStore;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import halla.icsw.d_day.customView.CustomActionBar;

public class MainActivity extends AppCompatActivity
    implements TextToSpeech.OnInitListener {
    TextToSpeech tts;
    int version = 1;
    float speed = 0;
    TextView stv;
    DatabaseOpenHelper helper;
    SQLiteDatabase database;
    Cursor cursor;
    EditText etv;

    private TextView ddayText;
    private TextView todayText;
    private TextView resultText;
    private ImageButton dateButton;


    private int tYear;           //오늘 연월일 변수
    private int tMonth;
    private int tDay;

    private int dYear = 0;        //디데이 연월일 변수
    private int dMonth = 0;
    private int dDay = 0;
    private final int GET_GALLERY_IMAGE = 200;
    private RelativeLayout RelativeLayout;

    private long d;
    private long t;
    private long r;

    private int resultNumber = 0;

    static final int DATE_DIALOG_ID = 0;
    private View view;

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            Locale locale = Locale.getDefault();
            if (tts.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE)
                tts.setLanguage(locale);
            else
                Toast.makeText(this, "지원하지 않는언어 오류", Toast.LENGTH_LONG).show();
        } else if (status == TextToSpeech.ERROR) {
            Toast.makeText(this, "음성 합성 초기화 오류", Toast.LENGTH_LONG).show();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) tts.shutdown();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new DatabaseOpenHelper(MainActivity.this, DatabaseOpenHelper.tableName, null, version);
        database = helper.getWritableDatabase();
        stv = findViewById(R.id.seekbartext);
        tts = new TextToSpeech(this, this);
        ddayText = findViewById(R.id.dday);
        todayText = findViewById(R.id.today);
        resultText = findViewById(R.id.result);
        dateButton = findViewById(R.id.dateButton);
        SeekBar sb = findViewById(R.id.seekBar);
        RelativeLayout = findViewById(R.id.Layout);
        etv = findViewById(R.id.title);

        if(savedInstanceState ==null){
            MainFragment mainFragment = new MainFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFragment,mainFragment,"main").commit();
        }

        int request = getIntent().getIntExtra("request", -1);

        switch (request) {
            case 0:

                dateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("data", "한라대학교");
                        setResult(0, intent);
                        finish(); // 액테비티가 종료되면 ListActivity의 onResume 메서드가 실행되면서 리스트를 dataSetChange메서드를 이용해서 list를 갱신합니다.
                    }
                });

                break;
        }

        String sql = "SELECT img FROM " + helper.tableName;
        String img = null;
        cursor = database.rawQuery(sql, null);
        cursor.moveToLast();

        int x = cursor.getCount() - 1;
        Drawable draw;
        if (cursor.getCount() == 1) {
            draw = getDrawable(R.drawable.dday2);//메인화면 레이아웃 백그라운드 이미지
        } else {
            img = cursor.getString(cursor.getPosition() - x);
            byte[] encodeByte = Base64.decode(img, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            draw = new BitmapDrawable(bitmap);
        }
        draw.setAlpha(70);//투명도
        RelativeLayout.setBackgroundDrawable(draw);


        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            public void onStopTrackingTouch(SeekBar seekBar) {
                speed = seekBar.getProgress();
                stv.setText("말하기 속도는" + (speed / 2));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
            }
        });

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showDialog(0);//----------------
            }
        });


        Calendar calendar = Calendar.getInstance();              //현재 날짜 불러옴
        tYear = calendar.get(Calendar.YEAR);
        tMonth = calendar.get(Calendar.MONTH);
        tDay = calendar.get(Calendar.DAY_OF_MONTH);

        dYear = tYear;
        dMonth = tMonth;
        dDay = tDay;

        Calendar dCalendar = Calendar.getInstance();
        dCalendar.set(dYear, dMonth, dDay);

        t = calendar.getTimeInMillis();                 //오늘 날짜를 밀리타임으로 바꿈
        d = dCalendar.getTimeInMillis();              //디데이날짜를 밀리타임으로 바꿈
        r = (d - t) / (24 * 60 * 60 * 1000);                 //디데이 날짜에서 오늘 날짜를 뺀 값을 '일'단위로 바꿈

        resultNumber = (int) r;
        updateDisplay();
        setActionBar();
    }


    public void timeget(){
        Calendar dCalendar = Calendar.getInstance();
        dCalendar.set(dYear, dMonth, dDay);
        Calendar calendar = Calendar.getInstance();
        t = calendar.getTimeInMillis();                 //오늘 날짜를 밀리타임으로 바꿈
        d = dCalendar.getTimeInMillis();              //디데이날짜를 밀리타임으로 바꿈
        r = (d - t) / (24 * 60 * 60 * 1000);                 //디데이 날짜에서 오늘 날짜를 뺀 값을 '일'단위로 바꿈
        resultNumber = (int) r;
    }
    private  void setActionBar() {
        CustomActionBar ca = new CustomActionBar(this, getSupportActionBar());
        ca.setActionBar();
    }
    public void Onclick5(View view){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, GET_GALLERY_IMAGE);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                InputStream in = getContentResolver().openInputStream(data.getData());
                BitmapFactory.Options options = new BitmapFactory.Options();

                options.inSampleSize = 2;
                Bitmap img = BitmapFactory.decodeStream(in,null,options);
                in.close();
                Drawable drawable =new BitmapDrawable(img);
                drawable.setAlpha(70);
                RelativeLayout.setBackgroundDrawable(drawable);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }//갤러리 연동


        }
        if (requestCode == 0 && resultCode == RESULT_OK) {
            boolean voiceCheck[] =new boolean[4];
            ArrayList<String> result =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String str = result.get(0);
            ddayText.setText(str);
            String str2 = ddayText.getText().toString();
            String sss[] = str2.split("");
            for(int i=1; i<sss.length; i++){
                if(sss[i].equals("년")) {
                    dYear= Integer.parseInt(sss[i-4]+sss[i-3]+sss[i-2]+sss[i-1]);
                    voiceCheck[0]=true;
                }

                if (sss[i].equals("월")){
                    if(sss[i-2].equals(" ")) {
                        dMonth= Integer.parseInt(sss[i-1]);
                        voiceCheck[1]=true;
                    }
                    else {
                        dMonth= Integer.parseInt(sss[i-2]+sss[i-1])-1;
                        voiceCheck[1]=true;
                    }
                }
                if(sss[i].equals("일")){
                    if(sss[i-2].equals(" ")) {
                        dDay = Integer.parseInt(sss[i-1]);
                        voiceCheck[2]=true;
                    }
                    else {
                        voiceCheck[2]=true;
                        dDay = Integer.parseInt(sss[i-2]+sss[i-1]);
                    }
                }
            }
            voiceCheck[3]=true;
            for(int i=0; i<3; i++){
                if(!voiceCheck[i]){
                    voiceCheck[3]=false;
                }
            }
            if(voiceCheck[3]) {
                timeget();
                updateDisplay();
                for(int i=0; i<3; i++) voiceCheck[i]=false;
                Speak(resultText.getText().toString());
            }
            else Speak(str2+"은 날짜가 아닙니다.");
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void updateDisplay() {
        ddayText.setText(String.format("%d년 %d월 %d일", dYear, dMonth + 1, dDay));


        if (resultNumber > 0) {
            resultText.setText(String.format("D-%d", resultNumber));
        } else if (resultNumber == 0) {
            resultText.setText(String.format("D-DAY", resultNumber));
        } else {
            int absR = Math.abs(resultNumber);
            resultText.setText(String.format("D+%d", absR));
        }
    }//디데이 날짜가 오늘날짜보다 뒤에오면 '-', 앞에오면 '+'를 붙인다

    private DatePickerDialog.OnDateSetListener dDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            dYear = year;
            dMonth = monthOfYear;
            dDay = dayOfMonth;
            final Calendar dCalendar = Calendar.getInstance();
            dCalendar.set(dYear, dMonth, dDay);
            Calendar calendar = Calendar.getInstance();
            t = calendar.getTimeInMillis();
            d = dCalendar.getTimeInMillis();
            r = (d - t) / (24 * 60 * 60 * 1000);
            if(r<85000000 && r>70000000){
                r=-1;
            }

            resultNumber = (int) r;
            updateDisplay();
            String asdasd = ddayText.getText().toString()+ "\n" + resultText.getText().toString();
            ListActivity.list.add(asdasd); //날짜 입력의 static 메모리 변수에 list에 데이터 추가
            Intent intent = new Intent();
            intent.putExtra("data",ddayText.getText().toString()+ "                               " + resultText.getText().toString());
            setResult(0, intent);
            ListActivity.tv.setText("");//처음으로 디데이를 입력해주세요 숨기기
            ListActivity.tv2.setText("");//그 밑 문단 숨기기
            finish();

        }
    };


    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DATE_DIALOG_ID) {
            return new DatePickerDialog(this, dDateSetListener, tYear, tMonth, tDay);

        }
        return null;
    }
    public void Onclick1(View view) {
        try {
            Intent intent = new Intent(
                    RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            startActivityForResult(intent, 0);
        } catch (Exception e) {
            Toast.makeText(this, "구글 앱이 설치되지 않았습니다", Toast.LENGTH_LONG).show();
        }
    }


    public void Onclick2(View view) {

        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
        startActivity(intent);
        finish();
    } // 메뉴 내위치를 눌렀을때


    public void Speak(String str) {

        if (str.length() > 0) {
            if (tts.isSpeaking()) tts.stop();
            tts.setSpeechRate(speed);
            tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);
        }
    } // TTS 스피킹

@Override
public boolean onCreateOptionsMenu(Menu menu)
{
    MenuInflater inflater = getMenuInflater();

    inflater.inflate(R.menu.activity_menu, menu);

    return true;
}
@Override
public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId())
    {
        case R.id.menu1:
            Onclick1(view);
            break;
        case R.id.menu2:
            Onclick2(view);
            break;
        case R.id.menu3:
            Onclick5(view);
            break;
    }
    return super.onOptionsItemSelected(item);
    }
    public void onClickbackbtn(View view){
        Intent intent = new Intent(getApplicationContext(), ListActivity.class);
        startActivity(intent);
        finish();
    }//왼쪽 상단에 백버튼 눌렀을때

    public void onClicksave(View view){
        String string = ddayText.getText().toString()+ "\n" + resultText.getText().toString()+"                              "+etv.getText().toString()+"\n";
        ListActivity.list.add(string); //날짜 입력의 static 메모리 변수에 list에 데이터 추가
        Intent intent = new Intent();
        intent.putExtra("data",string);
        setResult(0, intent);
        ListActivity.tv.setText("");//처음으로 디데이를 입력해주세요 숨기기
        ListActivity.tv2.setText("");//그 밑 문단 숨기기
        finish();

    }//오른쪽 상단에 세이브버튼 눌렀을때
}
