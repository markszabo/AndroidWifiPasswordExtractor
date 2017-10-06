package hu.szabosimon.mark.wifipasswordextractor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv=(TextView)findViewById(R.id.outputTV);
        tv.setText("Output :"+"\n"+runAsRoot());
    }

    public String runAsRoot() {

        try {
            // Executes the command.
            Process process = Runtime.getRuntime().exec("su -c cat /data/misc/wifi/wpa_supplicant.conf");
            // Reads stdout.
            // NOTE: You can write to stdin of the command using
            //       process.getOutputStream().
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();

            // Waits for the command to finish.
            process.waitFor();

            return output.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
