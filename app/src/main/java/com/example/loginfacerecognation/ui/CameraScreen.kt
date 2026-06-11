package com.example.loginfacerecognation.ui

import android.provider.Settings
import android.util.Log
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@OptIn(ExperimentalGetImage::class)
@Composable
fun CameraScreen(
    isRegistering: Boolean,
    onBackClick: () -> Unit = {},
    onSuccess: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val deviceId = remember {
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown-device"
    }
    
    var statusMessage by remember { mutableStateOf("No face detected") }
    var isFaceReady by remember { mutableStateOf(false) }
    var statusColor by remember { mutableStateOf(Color.Gray) }
    var userId by remember { mutableStateOf("") }
    
    val mockEmbedding = remember { FloatArray(128) { 0.5f } }
    
    val viewModel: FaceViewModel = viewModel()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F111A))
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isRegistering) "Register Face" else "Face Login",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Please look into the camera and hold still",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = userId,
                onValueChange = { userId = it },
                label = { Text("AJ username, email, or client ID") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF4A89FF),
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color(0xFF4A89FF),
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = Color(0xFF4A89FF)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(width = 240.dp, height = 320.dp)
                    .clip(RoundedCornerShape(120.dp))
                    .border(4.dp, if (isFaceReady) Color(0xFF4CAF50) else Color(0xFF4A89FF), RoundedCornerShape(120.dp))
            ) {
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx).apply {
                            scaleType = PreviewView.ScaleType.FILL_CENTER
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }

                        val executor = ContextCompat.getMainExecutor(ctx)
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val faceDetector = FaceDetection.getClient(
                                FaceDetectorOptions.Builder()
                                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                                    .build()
                            )

                            val analysisExecutor = Executors.newSingleThreadExecutor()
                            val imageAnalysis = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                                .also {
                                    it.setAnalyzer(analysisExecutor) { imageProxy ->
                                        val mediaImage = imageProxy.image
                                        if (mediaImage != null) {
                                            val image = InputImage.fromMediaImage(
                                                mediaImage,
                                                imageProxy.imageInfo.rotationDegrees
                                            )
                                            faceDetector.process(image)
                                                .addOnSuccessListener { faces ->
                                                    if (faces.isEmpty()) {
                                                        statusMessage = "No face detected"
                                                        isFaceReady = false
                                                        statusColor = Color.Gray
                                                    } else {
                                                        val face = faces[0]
                                                        val faceWidth = face.boundingBox.width().toFloat()
                                                        val imageWidth = if (imageProxy.imageInfo.rotationDegrees == 90 || imageProxy.imageInfo.rotationDegrees == 270) {
                                                            imageProxy.height.toFloat()
                                                        } else {
                                                            imageProxy.width.toFloat()
                                                        }
                                                        
                                                        val faceRatio = faceWidth / imageWidth
                                                        
                                                        if (faceRatio < 0.4) {
                                                            statusMessage = "Please move closer"
                                                            isFaceReady = false
                                                            statusColor = Color.Yellow
                                                        } else if (face.leftEyeOpenProbability != null && face.leftEyeOpenProbability!! < 0.2 &&
                                                                   face.rightEyeOpenProbability != null && face.rightEyeOpenProbability!! < 0.2) {
                                                            statusMessage = "Please open your eyes"
                                                            isFaceReady = false
                                                            statusColor = Color.Red
                                                        } else {
                                                            statusMessage = "Perfect! Click to proceed"
                                                            isFaceReady = true
                                                            statusColor = Color(0xFF4A89FF)
                                                        }
                                                    }
                                                }
                                                .addOnCompleteListener {
                                                    imageProxy.close()
                                                }
                                        } else {
                                            imageProxy.close()
                                        }
                                    }
                                }

                            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview,
                                    imageAnalysis
                                )
                            } catch (e: Exception) {
                                Log.e("CameraScreen", "Use case binding failed", e)
                            }
                        }, executor)

                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Surface(
                color = Color(0xFF1A1D2E),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = statusMessage,
                    color = statusColor,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    fontSize = 14.sp
                )
            }

            FilledIconButton(
                onClick = {
                    scope.launch {
                        if (userId.isBlank()) {
                            statusMessage = "Enter AJ username, email, or client ID"
                            statusColor = Color.Red
                            return@launch
                        }

                        if (!isFaceReady) {
                            statusMessage = "No face detected. Look into the camera first"
                            statusColor = Color.Red
                            return@launch
                        }

                        if (isRegistering) {
                            val success = viewModel.registerFace(userId.trim(), mockEmbedding)
                            if (success) {
                                statusMessage = "Registration Successful!"
                                statusColor = Color.Green
                                // Small delay to show success message before switching
                                kotlinx.coroutines.delay(1000)
                                onSuccess("Registration Successful")
                            } else {
                                statusMessage = "Database Error!"
                                statusColor = Color.Red
                            }
                        } else {
                            val response = viewModel.loginWithBackend(userId.trim(), mockEmbedding, deviceId)
                            if (response.success) {
                                statusMessage = "Welcome ${response.userName ?: "Client"}!"
                                statusColor = Color.Green
                                kotlinx.coroutines.delay(1000)
                                onSuccess(response.token ?: "Login Successful")
                            } else {
                                statusMessage = response.message ?: "Backend login failed"
                                statusColor = Color.Red
                            }
                        }
                    }
                },
                modifier = Modifier
                    .padding(bottom = 48.dp)
                    .size(56.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (isFaceReady && userId.isNotBlank()) Color(0xFF4A89FF) else Color.DarkGray
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Confirm",
                    tint = Color.White
                )
            }
        }
    }
}
