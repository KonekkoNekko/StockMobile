package com.bielcode.stockmobile.ui.screens.utility.camera

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bielcode.stockmobile.ViewModelFactory
import com.bielcode.stockmobile.data.injection.Injection
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun CameraScreen(navController: NavHostController, folder: String, filename: String) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraViewModel: CameraViewModel = viewModel(
        factory = ViewModelFactory(Injection.provideRepository(context))
    )

    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
        }
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                // Upload and navigate back with the URI
                uploadPhotoAndNavigateBack(navController, context, cameraViewModel, uri, folder, filename)
            }
        }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(controller = controller, modifier = Modifier.fillMaxSize())

        IconButton(
            onClick = {
                controller.cameraSelector =
                    if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    } else CameraSelector.DEFAULT_BACK_CAMERA
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Cameraswitch,
                contentDescription = "Switch camera",
                tint = Color.White
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconButton(
                onClick = {
                    singlePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Photo,
                    contentDescription = "Open gallery",
                    tint = Color.White
                )
            }
            IconButton(
                onClick = {
                    takePhoto(
                        context = context,
                        lifecycleOwner = lifecycleOwner,
                        controller = controller,
                        folder = folder,
                        filename = filename,
                        onImageCaptured = { file ->
                            // Upload and navigate back with the URI
                            uploadPhotoAndNavigateBack(
                                navController,
                                context,
                                cameraViewModel,
                                file.toUri(),
                                folder,
                                filename
                            )
                        }
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = "Take photo",
                    tint = Color.White
                )
            }
        }
    }
}

private fun takePhoto(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    controller: LifecycleCameraController,
    folder: String,
    filename: String,
    onImageCaptured: (File) -> Unit
) {
    val photoFile = createFile(context, folder, filename)
    controller.takePicture(
        ImageCapture.OutputFileOptions.Builder(photoFile).build(),
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                Log.d("CameraScreen", "Photo captured: ${photoFile.absolutePath}")
                onImageCaptured(photoFile)
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraScreen", "Couldn't take photo: ", exception)
            }
        }
    )
}


private fun createFile(context: Context, folder: String, filename: String): File {
    val dir = File(context.cacheDir, folder)
    if (!dir.exists()) {
        dir.mkdirs()
    }
    return File(dir, filename)
}


private fun uploadPhotoAndNavigateBack(
    navController: NavHostController,
    context: Context,
    viewModel: CameraViewModel,
    uri: Uri,
    folder: String,
    filename: String
) {
    viewModel.uploadPhoto(context, uri, "$folder/$filename", onSuccess = { downloadUrl ->
        navController.previousBackStackEntry?.savedStateHandle?.set("imageUri", downloadUrl)
        navController.navigateUp()
    }, onFailure = { exception ->
        Log.e("CameraScreen", "Upload failed", exception)
    })
}
