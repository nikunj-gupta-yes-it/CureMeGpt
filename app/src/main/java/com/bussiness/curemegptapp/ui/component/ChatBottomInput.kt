package com.bussiness.curemegptapp.ui.component

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.viewModel.main.ChatInputState
import com.bussiness.curemegptapp.util.SpeechRecognizerManager
import com.bussiness.curemegptapp.ui.viewModel.main.ChatViewModel
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun BottomMessageBar(
    modifier: Modifier = Modifier,
    state: ChatInputState= ChatInputState(),
    viewModel: ChatViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // Pickers
    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { viewModel.addImage(it) }
    }
    val pdfLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { viewModel.addPdf(it) }
    }

    // Permission launcher for RECORD_AUDIO
    val recordPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            // nothing here; clicking mic will handle starting
        } else {
            // show explanation or snackbar as needed
        }
    }

    // Speech recognizer manager (keeps simple lifecycle inside compose)
    var speechMgr by remember { mutableStateOf<SpeechRecognizerManager?>(null) }

    DisposableEffect(key1 = context) {
        onDispose {
            speechMgr?.stopListening()
            speechMgr = null
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rounded text box
        Row(
            modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .background(Color(0xFFF5F0FF), RoundedCornerShape(28.dp))
                .padding(horizontal = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.attach_ic), // your clip icon
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .wrapContentSize()
                    .clickable ( interactionSource = remember { MutableInteractionSource() },
                        indication = null){ /*onAttachClick()*/ }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                if (state.images.isNotEmpty() || state.pdfs.isNotEmpty()) {
                    InlineAttachmentPreview(
                        images = state.images,
                        pdfs = state.pdfs,
                        onRemoveImage = viewModel::removeImage,
                        onRemovePdf = viewModel::removePdf
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                TextField(
                    value = state.message,
                    onValueChange = { viewModel.onMessageChange(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Ask anything…") },
                    maxLines = 4,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

            }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Voice Button
        val canSend = state.message.isNotBlank() || state.images.isNotEmpty() || state.pdfs.isNotEmpty()

        AnimatedContent(targetState = canSend) { sending ->
            if (sending) {
                IconButton(onClick = { viewModel.sendMessageFromInput() }) {
                    Icon(painterResource(R.drawable.send_ic), contentDescription = "Send", tint = Color.Unspecified, modifier = Modifier.wrapContentSize())
                }
            } else {
                IconButton(onClick = { viewModel.sendMessageFromInput() }) {
                    Icon(painterResource(R.drawable.voiceinc_ic), contentDescription = "Send", tint = Color.Unspecified, modifier = Modifier.wrapContentSize())
                }
//                IconButton(onClick = {
//                    // request permission if needed then start
//                    val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
//                    if (!hasPermission) {
//                        recordPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
//                        return@IconButton
//                    }
//
//                    if (!state.isRecording) {
//                        // create manager and start
//                        speechMgr = SpeechRecognizerManager(
//                            context = context,
//                            onPartialResult = { partial ->
//                                // show partial transcription in input
//                                viewModel.onMessageChange(partial)
//                            },
//                            onFinalResult = { final ->
//                                viewModel.onMessageChange(final)
//                                viewModel.stopRecording()
//                                // optionally auto-send
//                                // viewModel.sendMessageFromInput()
//                            },
//                            onError = { error ->
//                                // log or show snackbar
//                            }
//                        ).also { it.startListening() }
//                        viewModel.toggleRecording()
//                    } else {
//                        // stop
//                        speechMgr?.stopListening()
//                        viewModel.stopRecording()
//                    }
//                }) {
//                    if (state.isRecording) {
////                                Image(painter = painterResource(R.drawable.ic_mic_off), contentDescription = "Mic", modifier = Modifier.size(24.dp))
//                    } else {
////                                Icon(Icons.Default.Mic, contentDescription = "Mic")
//                    }
//                }

            }
        }
    }
}