package com.samad_talukder.androidexoplayerwithpipmode

import android.app.Activity
import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Rational
import android.view.View
import androidx.annotation.RequiresApi
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.samad_talukder.androidexoplayerwithpipmode.databinding.ActivityMainBinding

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var mExoPlayer: ExoPlayer? = null
    private var isPipMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        hideSystemUI()
    }

    private fun hideSystemUI() {
        window.decorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or // Layout of the window is stable, even when the system UI is hidden.
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or // navigation bar is hidden
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or // the layout of the window is adjusted to accommodate the system status bar, even when the status bar is hidden.
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or // hides the system navigation bar
                    View.SYSTEM_UI_FLAG_FULLSCREEN or // hides the system status bar
                    View.SYSTEM_UI_FLAG_IMMERSIVE // allows the app to use the full screen and hide the system UI until the user interacts with the screen.
        )
    }

    override fun onStart() {
        super.onStart()

        initializeExoPlayer()
    }

    private fun initializeExoPlayer() {

        mExoPlayer = ExoPlayer.Builder(applicationContext).build()
        binding.exoPlayer.player = mExoPlayer
        binding.exoPlayer.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL

        val mediaItem: MediaItem = MediaItem.fromUri("http://clips.vorwaerts-gmbh.de/VfE_html5.mp4")
        mExoPlayer!!.setMediaItem(mediaItem)
        mExoPlayer!!.prepare()
        mExoPlayer!!.play()

    }

    override fun onBackPressed() {
        if (!isPipMode) {
            createPIPMode()
            isPipMode = true
        } else {
            super.onBackPressed()
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        isPipMode = !isInPictureInPictureMode
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
    }
    
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        createPIPMode()
    }

    private fun createPIPMode() {
        startPictureInPictureWithRatio(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startPictureInPictureWithRatio(activity: Activity) {
        activity.enterPictureInPictureMode(
            PictureInPictureParams.Builder()
                .setAspectRatio(Rational(16, 9))
                .build()
        )
    }

    override fun onResume() {
        super.onResume()
        binding.exoPlayer.useController = true
    }

    override fun onStop() {
        super.onStop()
        binding.exoPlayer.player = null
        mExoPlayer?.release()
    }
}