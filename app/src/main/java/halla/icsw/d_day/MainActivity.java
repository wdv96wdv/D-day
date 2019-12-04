package halla.icsw.d_day;



import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.drm.DrmStore;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import halla.icsw.d_day.customView.CustomActionBar;

public class MainActivity extends AppCompatActivity
    implements TextToSpeech.OnInitListener {
    TextToSpeech tts;
    float speed = 0;
    TextView stv;


    private TextView ddayText;
    private TextView todayText;
    private TextView resultText;
    private ImageButton dateButton;

    private int tYear;           //오늘 연월일 변수
    private int tMonth;
    private int tDay;

    private int dYear = 1;        //디데이 연월일 변수
    private int dMonth = 1;
    private int dDay = 1;
    private final int GET_GALLERY_IMAGE = 200;
    private ImageView imageview;
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

        stv = findViewById(R.id.seekbartext);
        tts = new TextToSpeech(this, this);
        ddayText = findViewById(R.id.dday);
        todayText = findViewById(R.id.today);
        resultText = findViewById(R.id.result);
        dateButton = findViewById(R.id.dateButton);
        SeekBar sb = findViewById(R.id.seekBar);
        RelativeLayout = findViewById(R.id.Layout);


        Drawable draw = getDrawable(R.drawable.dday2);//메인화면 레이아웃 백그라운드 이미지
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

        Calendar dCalendar = Calendar.getInstance();
        dCalendar.set(dYear, dMonth, dDay);

        t = calendar.getTimeInMillis();                 //오늘 날짜를 밀리타임으로 바꿈
        d = dCalendar.getTimeInMillis();              //디데이날짜를 밀리타임으로 바꿈
        r = (d - t) / (24 * 60 * 60 * 1000);                 //디데이 날짜에서 오늘 날짜를 뺀 값을 '일'단위로 바꿈

        resultNumber = (int) r + 1;
        updateDisplay();
        setActionBar();
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
                Bitmap img = BitmapFactory.decodeStream(in);
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
            ArrayList<String> result =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String str = result.get(0);
            ddayText.setText(str);
            String str2 = ddayText.getText().toString();
            Speak(str2 + "는 날짜가아닙니다");
        }

        super.onActivityResult(requestCode, resultCode, data);
    }//갤러리 연동


    private void updateDisplay() {

        todayText.setText(String.format("%d년 %d월 %d일", tYear, tMonth + 1, tDay));
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

            d = dCalendar.getTimeInMillis();
            r = (d - t) / (24 * 60 * 60 * 1000);

            resultNumber = (int) r;
            updateDisplay();
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

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == 0 && resultCode == RESULT_OK) {
//            ArrayList<String> result =
//                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//            String str = result.get(0);
//            ddayText.setText(str);
//            String str2 = ddayText.getText().toString();
//            Speak(str2 + "는 날짜가아닙니다");
//        }
//
//        super.onActivityResult(requestCode, resultCode, data);
//    }


    public void Onclick2(View view) {

        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
        startActivity(intent);
        finish();
    } // Button 내위치는 눌렀을때


    public void Speak(String str) {

        if (str.length() > 0) {
            if (tts.isSpeaking()) tts.stop();
            tts.setSpeechRate(speed);
            tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);
        }
    } // TTS 스피킹


    public void Onclick3(View view) {
        String str = resultText.getText().toString();
        Speak("남은 날짜는"+ str);
    } // Button 남은 날짜는 눌렀을때

//    public static void setSeekberThumb(final SeekBar seekBar, final Resources res) {
//        seekBar.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//
//                if (seekBar.getHeight() > 0) {
//                    Drawable thumb = res.getDrawable(R.drawable.seekbar_thumb);
//                    int h = seekBar.getMeasuredHeight();
//                    int w = h;
//                    Bitmap bmpOrg = ((BitmapDrawable) thumb).getBitmap();
//                    Bitmap bmpScaled = Bitmap.createScaledBitmap(bmpOrg, w, h, true);
//                    Drawable newThumb = new BitmapDrawable(res, bmpScaled);
//                    newThumb.setBounds(0, 0, newThumb.getIntrinsicWidth(), newThumb.getIntrinsicHeight());
//                    seekBar.setThumb(newThumb);
//                    seekBar.getViewTreeObserver().removeOnPreDrawListener(this);
//                }
//                return true;
//            }
//        });
//    }
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
    public void backbtn(View view){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
//        actionBar.setCustomView(mCustomView);
//
//    Toolbar parent = (Toolbar) mCustomView.getParent();
//    parent.setContentInsetsAbsolute(0,0);
//
//    actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.dday2));
//
//    ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT);
//    actionBar.setCustomView(mCustomView,params);
//}//액션바 커스텀

