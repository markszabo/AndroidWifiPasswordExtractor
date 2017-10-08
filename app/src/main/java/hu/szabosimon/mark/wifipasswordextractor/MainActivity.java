package hu.szabosimon.mark.wifipasswordextractor;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    TextView tv;
    Button getWifiPwdbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv=(TextView)findViewById(R.id.outputTV);
        getWifiPwdbutton=(Button) findViewById(R.id.getWifiPwdbutton);
        getWifiPwdbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Display a loading indicator
                findViewById(R.id.loading).setVisibility(View.VISIBLE);

                //Query data asynchronously, as it might take some seconds
                new AsyncTask<Void, Void, String>() {
                    protected String doInBackground(Void... params) {
                        return getWifiConfig();
                    }
                    protected void onPostExecute(String wifiConfig) {
                        tv.setText(wifiConfig);
                        //Dismiss a loading indicator
                        findViewById(R.id.loading).setVisibility(View.GONE);
                    }
                }.execute();
            }
        });
    }

    public String getWifiConfig() {
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
            if(output.toString().length() == 0) {
                return getString(R.string.failedMsg) + "The command executed successfully, however no data was returned.";
            } else {
                if(((Switch)findViewById(R.id.detailsSwitch)).isChecked()) {
                    return output.toString();
                } else {
                    String pattern = "ssid=\"([^\\n]*)\"\\s*psk=\"([^\\n]*)\"";
                    Pattern media = Pattern.compile(pattern);
                    java.util.regex.Matcher m = media.matcher(output.toString());
                    String shortOutput = getString(R.string.ssidPwdHeader);
                    while(m.find()) {
                        shortOutput += m.group(1) + " - " + m.group(2) + "\n";
                    }
                    return shortOutput;
                }

            }
        } catch (IOException e) {
            return getString(R.string.failedMsg) + e.getStackTrace().toString();
        } catch (InterruptedException e) {
            return getString(R.string.failedMsg) + e.getStackTrace().toString();
        }
    }
}
