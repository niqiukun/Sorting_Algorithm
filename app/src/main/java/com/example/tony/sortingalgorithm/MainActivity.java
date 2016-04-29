package com.example.tony.sortingalgorithm;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Random;

class DrawView extends View {
    Paint paint = new Paint();
    public boolean invalidated = false;
    public int n = 50;
    public int[] list = new int[n];
    public int purple_pointer1 = -1;
    public int purple_pointer2 = -1;
    public int red_pointer1 = -1;
    public int red_pointer2 = -1;
    public int green_pointer = -1;
    public DrawView(Context context) {
        super(context);
        for(int i = 0; i < n; i++){
            list[i] = i + 1;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(0);
        canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), paint);
        paint.setColor(Color.WHITE);
        for(int i = 0; i < n; i++){
            if(i == purple_pointer1 | i == purple_pointer2){
                paint.setColor(Color.MAGENTA);
            }
            if(i == red_pointer1 | i == red_pointer2){
                paint.setColor(Color.RED);
            }
            if(i <= green_pointer){
                paint.setColor(Color.GREEN);
            }
            canvas.drawRect(i * this.getWidth() / (float)n,
                    this.getHeight() - list[i] * (this.getHeight() / (float)n),
                    (i + 1) * this.getWidth() / (float)n - 1,
                    this.getHeight(),
                    paint);
            paint.setColor(Color.WHITE);
        }
    }
}

public class MainActivity extends AppCompatActivity {

    DrawView drawView;
    boolean stopSorting = true;
    Thread sortThread;
    int interval = 10;
    int totalCount = 0;
    boolean showColor = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        drawView = new DrawView(this);
        frameLayout.addView(drawView);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sorting_methods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void shuffle(View view){
        final Button shuffleButton = (Button) findViewById(R.id.shuffleButton);
        if(stopSorting){
            shuffleArray(drawView.list);
            drawView.invalidate();
        }else{
            Button sortButton = (Button) findViewById(R.id.button2);
            sortButton.setEnabled(true);
            CheckBox cb = (CheckBox) findViewById(R.id.checkBox);
            cb.setEnabled(true);
            Spinner spinner = (Spinner) findViewById(R.id.spinner);
            spinner.setEnabled(true);
            stopSorting = true;
            shuffleButton.setText("Shuffle");
        }

    }

    private void shuffleArray(int[] ar)
    {
        Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

    public void colour(View view){
        CheckBox cb = (CheckBox) findViewById(R.id.checkBox);
        if(cb.isChecked()){
            showColor = true;
            drawView.n = 20;
            interval = 100;
            drawView.list = new int[drawView.n];
            for(int i = 0; i < drawView.n; i++){
                drawView.list[i] = i + 1;
            }
            drawView.invalidate();
        }else{
            showColor = false;
            drawView.n = 50;
            interval = 10;
            drawView.list = new int[drawView.n];
            for(int i = 0; i < drawView.n; i++){
                drawView.list[i] = i + 1;
            }
            drawView.invalidate();
        }
    }

    private boolean compare(int i, int j){
        totalCount++;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.textView)).setText("Total operations count: " + totalCount);
            }
        });
        if(showColor){
            drawView.red_pointer1 = i;
            drawView.red_pointer2 = j;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    drawView.invalidate();
                }
            });
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            drawView.red_pointer1 = -1;
            drawView.red_pointer2 = -1;
        }
        if(drawView.list[i] > drawView.list[j]){
            return true;
        }else{
            return false;
        }
    }

    private void displace(int i, int j){
        int temp = drawView.list[i];
        drawView.list[i] = drawView.list[j];
        drawView.list[j] = temp;
        totalCount++;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.textView)).setText("Total operations count: " + totalCount);
            }
        });
        if(showColor) {
            drawView.purple_pointer1 = i;
            drawView.purple_pointer2 = j;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    drawView.invalidate();
                }
            });
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            drawView.purple_pointer1 = -1;
            drawView.purple_pointer2 = -1;
        }else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    drawView.invalidate();
                }
            });
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void insert(int a, int b){
        for(int i = a; i > b; i--){
            displace(i, i - 1);
        }
    }

    public void sort(View view){
        totalCount = 0;
        Button shuffleButton = (Button) findViewById(R.id.shuffleButton);
        shuffleButton.setText("Stop");
        CheckBox cb = (CheckBox) findViewById(R.id.checkBox);
        cb.setEnabled(false);
        view.setEnabled(false);
        stopSorting = false;
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setEnabled(false);
        if(spinner.getSelectedItemPosition()==0)
        {
            sortThread = new Thread(){
                @Override
                public void run(){
                    while (!stopSorting) {
                        boolean flag = true;
                        for (int i = 0; i < drawView.list.length - 1; i++) {
                            if(stopSorting){
                                break;
                            }
                            if (compare(i, i + 1)) {
                                flag = false;
                                displace(i, i + 1);
                            }
                        }
                        if (flag) {
                            stopSorting = true;
                            finishSorting();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    drawView.invalidate();
                                    Button shuffleButton = (Button) findViewById(R.id.shuffleButton);
                                    shuffleButton.setText("Shuffle");
                                    Button sortButton = (Button) findViewById(R.id.button2);
                                    sortButton.setEnabled(true);
                                    CheckBox cb = (CheckBox) findViewById(R.id.checkBox);
                                    cb.setEnabled(true);
                                    Spinner spinner = (Spinner) findViewById(R.id.spinner);
                                    spinner.setEnabled(true);
                                }
                            });
                        }
                    }
                }
            };
            sortThread.start();
        }else if(spinner.getSelectedItemPosition() == 1){
            sortThread = new Thread(){
                @Override
                public void run() {
                    int i = 1; int j = 1;
                    for(i = 1; i < drawView.n && !stopSorting; i++){
                        boolean flag = true;
                        for(j = i - 1; j >= 0 && !stopSorting; j--){
                            if(compare(i, j)){
                                insert(i, j + 1);
                                flag = false;
                                break;
                            }
                        }
                        if(flag){
                            insert(i, 0);
                        }
                    }
                    if(i == drawView.n){
                        finishSorting();
                    }
                    stopSorting = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            drawView.invalidate();
                            Button shuffleButton = (Button) findViewById(R.id.shuffleButton);
                            shuffleButton.setText("Shuffle");
                            Button sortButton = (Button) findViewById(R.id.button2);
                            sortButton.setEnabled(true);
                            CheckBox cb = (CheckBox) findViewById(R.id.checkBox);
                            cb.setEnabled(true);
                            Spinner spinner = (Spinner) findViewById(R.id.spinner);
                            spinner.setEnabled(true);
                        }
                    });
                }
            };
            sortThread.start();
        }
    }

    private void finishSorting(){
        for (int i = 0; i < drawView.n; i++) {
            drawView.green_pointer = i;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    drawView.invalidate();
                }
            });
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        drawView.green_pointer = -1;
    }
}
