package com.example.basicexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {

    Interpreter interpreter;
    Button processBtn;
    EditText inputField1, inputField2;
    TextView resultTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputField1 = findViewById(R.id.editTextNumber); // Primer campo de entrada
        inputField2 = findViewById(R.id.editTextNumber2); // Segundo campo de entrada
        resultTV = findViewById(R.id.textView);
        processBtn = findViewById(R.id.button);

        try {
            interpreter = new Interpreter(loadModelFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        processBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float result = doInference(inputField1.getText().toString(), inputField2.getText().toString());
                resultTV.setText("Result: " + result);
            }
        });
    }

    public float doInference(String val1, String val2) {
        float[][] inputVal = new float[1][2]; // Tamaño [1][2] para dos entradas

        inputVal[0][0] = Float.parseFloat(val1); // Altura
        inputVal[0][1] = Float.parseFloat(val2); // Agudeza visual

        float[][] outputVal = new float[1][1];   // Resultado de la predicción
        interpreter.run(inputVal, outputVal);
        return outputVal[0][0];
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor assetFileDescriptor = this.getAssets().openFd("model.tflite");
        FileInputStream fileInputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset = assetFileDescriptor.getStartOffset();
        long length = assetFileDescriptor.getLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, length);
    }
}
