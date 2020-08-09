package manoj.skrypto.com.heatconverter;

import android.annotation.SuppressLint;
import android.content.res.AssetFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {


    private EditText inputBox;
    TextView outputText;
    Button goButton;
    private Interpreter tflite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputBox = findViewById(R.id.inputArea);
        goButton = findViewById(R.id.go);
        outputText = findViewById(R.id.outputArea);

        try {
            tflite = new Interpreter(loadModelFile());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Tflite File Not Loaded", Toast.LENGTH_SHORT).show();
        }

        goButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (!inputBox.getText().toString().equals("")) {
                    float prediction = doInference(inputBox.getText().toString());
                    outputText.setText(String.valueOf(prediction) + " F");
                } else {
                    Toast.makeText(MainActivity.this, "Don't Leave the input empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Float doInference(String value) {
        float[] input = new float[1];
        input[0] = Float.valueOf(value);
        float[][] output = new float[1][1];
        tflite.run(input, output);
        return output[0][0];
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = getApplicationContext().getAssets().openFd("HeatConverter.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
}
