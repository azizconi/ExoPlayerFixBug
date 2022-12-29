package com.example.exoplayerfixbug

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView

class MainActivity : AppCompatActivity() {

    private lateinit var button: Button
    private lateinit var playerView: PlayerView
    private lateinit var progressBar: ProgressBar

    private var player: ExoPlayer? = null

    private val videos = MutableLiveData<List<String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        button = findViewById(R.id.button)
        playerView = findViewById(R.id.video_view)
        progressBar = findViewById(R.id.progressBarVideo)

        button.setOnClickListener {
            if (player != null) {
                player!!.release()
                player = null
            }

            videos.postValue(sources.shuffled())
        }

        videos.observe(this) { videoList ->
            if (videoList != null) {

                val listMedia = mutableListOf<MediaItem>()
                videoList.forEach { videoLink ->
                    val mediaItem = MediaItem.fromUri(Uri.parse(videoLink))
                    listMedia.add(mediaItem)
                }


                player = ExoPlayer.Builder(this).build()
                playerView.player = player
                player!!.setMediaItems(listMedia)

                player!!.playWhenReady = true

                player!!.addListener(
                    object : Player.Listener {
                        override fun onIsLoadingChanged(isLoading: Boolean) {
                            super.onIsLoadingChanged(isLoading)
                            Log.e("TAG", "onIsLoadingChanged: $isLoading")
                            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                        }

                        override fun onPlayerErrorChanged(error: PlaybackException?) {
                            super.onPlayerErrorChanged(error)
                            Log.e("TAG", "onPlayerErrorChanged: ${error?.message}")
                        }
                    }
                )

                player!!.prepare()
            }
        }

    }
}