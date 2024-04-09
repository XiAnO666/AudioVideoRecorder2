package com.example.audiovideorecordeer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.view.Surface
import android.view.SurfaceHolder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.audiovideorecordeer.databinding.RecordingAudioVideoBinding
import java.io.IOException

class RecordActivity : AppCompatActivity(), SurfaceHolder.Callback {
    private lateinit var binding: RecordingAudioVideoBinding
    private lateinit var mediaRecorder: MediaRecorder
    private var isRecordingVideo: Boolean = false
    private var outputFilePath: String = ""

    private val REQUEST_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RecordingAudioVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Solicitar permisos necesarios
        requestPermissionsIfNeeded()

        // Determinar si se está grabando audio o video basándose en el Intent
        isRecordingVideo = intent.getBooleanExtra("isRecordingVideo", false)

        if (isRecordingVideo) {
            // Si se está grabando video, ocultar la imagen de la nota musical y mostrar la vista previa de la cámara
            binding.musicNote.visibility = View.GONE
            binding.cameraPreview.visibility = View.VISIBLE
        } else {
            // Si se está grabando audio, ocultar la vista previa de la cámara y mostrar la imagen de la nota musical
            binding.musicNote.visibility = View.VISIBLE
            binding.cameraPreview.visibility = View.GONE
        }

        binding.cameraPreview.holder.addCallback(this)

        binding.startButton.setOnClickListener {
            startRecording()
        }

        binding.stopButton.setOnClickListener {
            stopRecording()
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // Lógica a ejecutar cuando se crea la superficie
        if (isRecordingVideo) {
            try {
                // Configurar y preparar MediaRecorder para la grabación de video
                mediaRecorder = MediaRecorder().apply {
                    setAudioSource(MediaRecorder.AudioSource.DEFAULT)
                    setVideoSource(MediaRecorder.VideoSource.DEFAULT)
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    setVideoEncoder(MediaRecorder.VideoEncoder.H264)
                    val fileName = "${System.currentTimeMillis()}.mp4"
                    val externalDir = getExternalFilesDir("video")
                    outputFilePath = "${externalDir?.absolutePath}/$fileName"
                    setOutputFile(outputFilePath)

                    // Configurar la orientación del vídeo basándonos en la rotación del dispositivo
                    val rotation = windowManager.defaultDisplay.rotation
                    when (rotation) {
                        Surface.ROTATION_0 -> setOrientationHint(90)
                        Surface.ROTATION_90 -> setOrientationHint(0)
                        Surface.ROTATION_180 -> setOrientationHint(270)
                        Surface.ROTATION_270 -> setOrientationHint(180)
                    }

                    setPreviewDisplay(holder.surface)
                    prepare()
                    start()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Si cambia el tamaño o el formato de la superficie, puedes ajustar la configuración de la cámara aquí
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // Aquí puedes liberar los recursos de la cámara
        if (isRecordingVideo) {
            // Detener y liberar MediaRecorder
            mediaRecorder.stop()
            mediaRecorder.release()
        }
    }

    private fun requestPermissionsIfNeeded() {
        val permissionsToRequest = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        val permissionsNotGranted = permissionsToRequest.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsNotGranted.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNotGranted, REQUEST_PERMISSION_CODE)
        }
    }

    private fun startRecording() {
        // Configurar MediaRecorder para la grabación de audio
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.DEFAULT)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            val fileName = "${System.currentTimeMillis()}.mp4"
            val externalDir = getExternalFilesDir("audio")
            outputFilePath = "${externalDir?.absolutePath}/$fileName"
            setOutputFile(outputFilePath)
            prepare()
            start()
        }
    }

    private fun stopRecording() {
        // Detener y liberar MediaRecorder
        mediaRecorder.stop()
        mediaRecorder.release()

        // Devolver la ruta del archivo grabado a MainActivity
        val intent = Intent().apply {
            putExtra("filePath", outputFilePath)
        }
        setResult(RESULT_OK, intent)

        // Cerrar esta actividad y volver a MainActivity
        finish()
    }
}

/*
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.view.Surface
import android.view.SurfaceHolder
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.audiovideorecordeer.databinding.RecordingAudioVideoBinding
import java.io.IOException

class RecordActivity : AppCompatActivity(), SurfaceHolder.Callback {
    private lateinit var binding: RecordingAudioVideoBinding
    private lateinit var mediaRecorder: MediaRecorder
    private var isRecordingVideo: Boolean = false
    private var outputFilePath: String = ""

    private val REQUEST_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RecordingAudioVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Solicitar permisos necesarios
        requestPermissionsIfNeeded()

        // Determinar si se está grabando audio o video basándose en el Intent
        isRecordingVideo = intent.getBooleanExtra("isRecordingVideo", false)

        if (isRecordingVideo) {
            // Si se está grabando video, ocultar la imagen de la nota musical y mostrar la vista previa de la cámara
            binding.musicNote.visibility = View.GONE
            binding.cameraPreview.visibility = View.VISIBLE
        } else {
            // Si se está grabando audio, ocultar la vista previa de la cámara y mostrar la imagen de la nota musical
            binding.musicNote.visibility = View.VISIBLE
            binding.cameraPreview.visibility = View.GONE
        }

        binding.cameraPreview.holder.addCallback(this)

        binding.startButton.setOnClickListener {
            startRecording()
        }

        binding.stopButton.setOnClickListener {
            stopRecording()
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // Lógica a ejecutar cuando se crea la superficie
        if (isRecordingVideo) {
            try {
                // Configurar y preparar MediaRecorder para la grabación de video
                mediaRecorder = MediaRecorder().apply {
                    setAudioSource(MediaRecorder.AudioSource.DEFAULT)
                    setVideoSource(MediaRecorder.VideoSource.DEFAULT)
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    setVideoEncoder(MediaRecorder.VideoEncoder.H264)
                    val fileName = "${System.currentTimeMillis()}.mp4"
                    val externalDir = getExternalFilesDir("video")
                    outputFilePath = "${externalDir?.absolutePath}/$fileName"
                    setOutputFile(outputFilePath)

                    // Configurar la orientación del vídeo basándonos en la rotación del dispositivo
                    val rotation = windowManager.defaultDisplay.rotation
                    when (rotation) {
                        Surface.ROTATION_0 -> setOrientationHint(90)
                        Surface.ROTATION_90 -> setOrientationHint(0)
                        Surface.ROTATION_180 -> setOrientationHint(270)
                        Surface.ROTATION_270 -> setOrientationHint(180)
                    }

                    setPreviewDisplay(holder.surface)
                    prepare()
                    start()
                    showToast("Recording started")
                }
            } catch (e: IOException) {
                e.printStackTrace()
                showToast("Failed to start recording")
            }
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Si cambia el tamaño o el formato de la superficie, puedes ajustar la configuración de la cámara aquí
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // Aquí puedes liberar los recursos de la cámara
        if (isRecordingVideo) {
            // Detener y liberar MediaRecorder
            mediaRecorder.stop()
            mediaRecorder.release()
        }
    }

    private fun requestPermissionsIfNeeded() {
        val permissionsToRequest = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        val permissionsNotGranted = permissionsToRequest.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsNotGranted.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNotGranted, REQUEST_PERMISSION_CODE)
        }
    }

    private fun startRecording() {
        // Configurar MediaRecorder para la grabación de audio
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.DEFAULT)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            val fileName = "${System.currentTimeMillis()}.mp4"
            val externalDir = getExternalFilesDir("audio")
            outputFilePath = "${externalDir?.absolutePath}/$fileName"
            setOutputFile(outputFilePath)
            try {
                prepare()
                start()
                showToast("Recording started")
            } catch (e: IOException) {
                e.printStackTrace()
                showToast("Failed to start recording")
            }
        }
    }

    private fun stopRecording() {
        // Detener y liberar MediaRecorder
        try {
            mediaRecorder.stop()
            mediaRecorder.release()
            showToast("Recording stopped")
        } catch (e: RuntimeException) {
            e.printStackTrace()
            showToast("Failed to stop recording")
        }
        // Devolver la ruta del archivo grabado a MainActivity
        val intent = Intent().apply {
            putExtra("filePath", outputFilePath)
        }
        setResult(RESULT_OK, intent)
        // Cerrar esta actividad y volver a MainActivity
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}*/
