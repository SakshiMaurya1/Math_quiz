package com.example.prototype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity{
    TextView question;
    ImageView mic;
    TextView answer;
    TextToSpeech tts;
    Button say;
    String str="default";
    int c=0,reqcode=100,randnum_ans,i;
    int a[]=new int[10];
    int b[]=new int[10];
    int score=0;
    String ans;
    Random random;
    SpeechRecognizer speechRecognizer;

    AudioManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        question=findViewById(R.id.question);
        answer=findViewById(R.id.answer);
        mic=findViewById(R.id.mic);
        say=findViewById(R.id.say);
//        next=findViewById(R.id.next);
//        prev=findViewById(R.id.prev);
//        prev.setVisibility(View.INVISIBLE);
        random=new Random();

        for(i=0;i<10;i++) {
            a[i] = random.nextInt(10-1)+1;
            b[i] = random.nextInt(10-1)+1;
        }
        i=0;
        quiz(a[i],b[i]);

        tts=new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i==TextToSpeech.SUCCESS){
                    int res=tts.setLanguage(Locale.ENGLISH);
                }
            }
        });

        manager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);

        say.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question(str);



            }

        });

        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.RECORD_AUDIO},reqcode);
                }
                if (c==0) {
                    mic.setImageDrawable(getDrawable(R.drawable.input));

                    c=1;
                    speakNow();
                }

                else
                {
                    mic.setImageDrawable(getDrawable(R.drawable.mic1));
                    c=0;
                }
            }
        });

    }

    private  void question(String str)
    {
//        manager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        Log.e("TAG", "question: "+str);
        int speech=tts.speak(str,TextToSpeech.QUEUE_FLUSH,null,TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                speakNow();
            }
        }, 4000);

    }
    private void speakNow() {
//        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start speaking.");
//
//        startActivityForResult(intent, 111);
//    }

        Log.e("TAG", "speakNow: =====>>>>>>" );
        speechRecognizer=SpeechRecognizer.createSpeechRecognizer(this);

        Intent speechIntent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault());


        speechRecognizer.setRecognitionListener(new RecognitionListener() {

            @Override
            public void onReadyForSpeech(Bundle bundle) {
                Log.e("TAG", "onReadyForSpeech: ");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.e("TAG", "onBeginningOfSpeech: " );

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {
                mic.setImageDrawable(getDrawable(R.drawable.mic1));
            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ans=bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);
                if(ans!=null) {
                    answer.setText(ans);
                    response(ans);
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        speechRecognizer.startListening(speechIntent);
    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 111 && resultCode == RESULT_OK) {
//            ans=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
//            answer.setText(ans);
//            response(ans);
//            mic.setImageDrawable(getDrawable(R.drawable.mic1));
//        }
//
//
//    }

    private void response(String ans) {
        String resp;

        Log.e("TAG", "check answers: "+ans );
        String orig_ans=String.valueOf(randnum_ans);
        Log.e("TAG", "response: "+ans );

        if(ans.equals(orig_ans))
        {
            resp=ans+" is the correct answer";
            Log.e("TAG", "response: "+ans );
            score++;
        }
        else
        {
            resp="your answer is incorrect";
        }
        tts.speak(resp,TextToSpeech.QUEUE_FLUSH,null,TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);

        if(i==9)
        {
            Toast.makeText(this,"Score: "+score,Toast.LENGTH_SHORT).show();
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(i<=9) {
                    i++;
                    quiz(a[i], b[i]);
                    answer.setText("");
                }
            }
        }, 1000);

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==reqcode)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();

            }
            else
            {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void quiz(int a,int b)
    {
        randnum_ans=a*b;
        String questiontv=a+" * "+b+" =";
        str=a+"multiplied by "+b +"equals";
        question.setText(questiontv);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                speakNow();
            }
        }, 3000);

    }

//    @Override
//    public void onClick(View view) {
//        switch (view.getId())
//        {
//            case R.id.next:
//                if(i<10)
//                {
//                    i++;
//                }
//                if(i==9)
//                {
//                    next..setVisibility(View.INVISIBLE);
//                }
//                else
//                {
//                    prev.setVisibility(View.VISIBLE);
//                    quiz(a[i],b[i]);
//                    answer.setText("");
//                }
//                break;
//            case R.id.prev:
//                if(i>=0)
//                {
//                    i=(i-1)%10;
//                    quiz(a[i],b[i]);
//                    answer.setText("");
//                }
//                if(i==0)
//                {
//                    prev.setVisibility(View.INVISIBLE);
//                }
//                if(i<10)
//                {
//                    next.setVisibility(View.VISIBLE);
//                }
//                break;
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }

}