package sumati.com.videoplayer;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

public class PlayActivity extends AppCompatActivity {

    public static final String EXTRA_VIDEO_URL = "video_url";
    protected static final String EXTRA_IS_PLAYING = "is_playing";
    protected static final String EXTRA_CURRENT_POSITION = "current_position";

    protected MediaController pMediaController;
    protected MediaPlayer pMediaPlayer;
    protected VideoView videoView;
    protected ProgressBar progressBar;
    protected Uri pUri;
    protected int pPosition;
    private String TAG = PlayActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        if (getIntent() == null || getIntent().getExtras() == null || TextUtils.isEmpty(getIntent().getExtras().getString(EXTRA_VIDEO_URL))) {
            return;
        }

        initViews();
        setupMediaController();
    }

    protected void initViews() {
        progressBar = (ProgressBar) findViewById( R.id.progressBar );

        videoView = (VideoView) findViewById( R.id.videoView );
        videoView.setOnCompletionListener( onCompletionListener );
        videoView.setOnErrorListener( onErrorListener );
        videoView.setOnPreparedListener( onPreparedListener );

        if( videoView == null ) {
            throw new IllegalArgumentException( "Layout must contain a video view with ID video_view" );
        }

        pUri = Uri.parse( getIntent().getExtras().getString( EXTRA_VIDEO_URL ) );
        videoView.setVideoURI( pUri );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        releaseVideoListeners();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if( videoView == null || videoView.getCurrentPosition() == 0 )
            return;

        outState.putInt( EXTRA_CURRENT_POSITION, videoView.getCurrentPosition() );
        outState.putBoolean( EXTRA_IS_PLAYING, videoView.isPlaying() );
        videoView.pause();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if( videoView == null || savedInstanceState == null )
            return;

        if( savedInstanceState.getBoolean( EXTRA_IS_PLAYING, false ) ) {
            videoView.seekTo(savedInstanceState.getInt(EXTRA_CURRENT_POSITION, 0));
            videoView.start();
        }
    }

    protected void setupMediaController() {
        pMediaController = new MediaController( this );
        pMediaController.setEnabled(true);
        pMediaController.show();
        pMediaController.setMediaPlayer( videoView );
    }

    protected void releaseVideoListeners() {
        videoView.setOnCompletionListener( null );
        videoView.setOnErrorListener( null );
        videoView.setOnPreparedListener( null );
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.seekTo( pPosition );
        progressBar.setVisibility( View.VISIBLE );
    }

    @Override
    protected void onPause() {
        super.onPause();
        pPosition = videoView.getCurrentPosition();
    }
    protected MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            videoView.seekTo( 0 );
            if( videoView.isPlaying() )
                videoView.pause();

            if( !pMediaController.isShowing() )
                pMediaController.show();
        }
    };

    protected MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError( MediaPlayer mediaPlayer, int what, int extra ) {
            try {
                videoView.stopPlayback();
            } catch( IllegalStateException e ) {
                Log.e( TAG, e.getStackTrace().toString() );
            }

            return true;
        }
    };

    protected MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            if( mediaPlayer == null )
                return;
            pMediaPlayer = mediaPlayer;
            mediaPlayer.start();
            if( progressBar != null )
                progressBar.setVisibility( View.GONE );

            videoView.setMediaController( pMediaController );
        }
    };
}


