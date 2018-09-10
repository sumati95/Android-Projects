package sumati.com.videoplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button pButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        pButton = findViewById(R.id.playButton);
        pButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchVideoplayer();
            }
        });
    }

    public void launchVideoplayer() {
        Intent i = new Intent(this, PlayActivity.class);
        i.putExtra( PlayActivity.EXTRA_VIDEO_URL, "http://www.youtube.com/watch?v=y62zj9ozPOM" );
        startActivity(i);
    }
}
