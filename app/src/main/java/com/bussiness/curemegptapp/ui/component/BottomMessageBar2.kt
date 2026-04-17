package com.bussiness.curemegptapp.ui.component

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.apimodel.chatModel.FamilyDetails
import com.bussiness.curemegptapp.ui.viewModel.main.ChatDataViewModel
import com.bussiness.curemegptapp.ui.viewModel.main.ChatInputState1
import com.bussiness.curemegptapp.ui.viewModel.main.FakeChatDataViewModel
import com.bussiness.curemegptapp.util.SpeechRecognizerManager
import timber.log.Timber


@Composable
fun BottomMessageBar2(
    modifier: Modifier = Modifier,
    state: ChatInputState1 = ChatInputState1(),
    viewModel: ChatDataViewModel = hiltViewModel(),
    familyList: List<FamilyDetails> = emptyList<FamilyDetails>(),
    familyMemberId: Int,
    onSendClicked: () -> Unit = { }
) {

    val context = LocalContext.current
    val profiles by viewModel.profiles.collectAsState()
    val chatArgs by viewModel.chatArgs.collectAsState()
    val familyMemberId = chatArgs.familyMemberId
    val familyList = chatArgs.familyList
    val selectedMember = familyList.find { it.id == familyMemberId } ?: familyList.find {
        it.relationship.equals("myself", ignoreCase = true)
    }

    val fileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                val mimeType = context.contentResolver.getType(it)
                if (mimeType?.startsWith("image/") == true) {
                    viewModel.addImage(it)
                }
                else if (mimeType == "application/pdf") {
                    viewModel.addPdf(it)
                }
            }
    }



    var isRecording by remember { mutableStateOf(false) }
    var showText by remember { mutableStateOf(true) }
    var recognizedText by remember { mutableStateOf("") }
    var rmsValue by remember { mutableStateOf(0f) }
    var voiceText by remember { mutableStateOf("") }     // speech result
    var isMessageEmpty = state.message.isBlank() && recognizedText.isBlank()

    val speechRecognizer = remember {
        SpeechRecognizer.createSpeechRecognizer(context)
    }

    val intent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
    }

    DisposableEffect(Unit) {

        speechRecognizer.setRecognitionListener(object : RecognitionListener {

            override fun onRmsChanged(rmsdB: Float) {
                rmsValue = rmsdB
            }

            override fun onPartialResults(partialResults: Bundle?) {
                recognizedText =
                    partialResults?.getStringArrayList(
                        SpeechRecognizer.RESULTS_RECOGNITION
                    )?.firstOrNull() ?: ""
            }

            override fun onResults(results: Bundle?) {
                val voiceResult =
                    results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                        ?: ""

                viewModel.onMessageChange(
                    viewModel.uiState.value.message + " " + voiceResult
                )

                isRecording = false
                rmsValue = 0f
                showText = false
            }

            override fun onReadyForSpeech(params: Bundle?) {
                isRecording = true
            }

            override fun onBeginningOfSpeech() {
                isRecording = true
            }

            override fun onEndOfSpeech() {
                isRecording = false
                rmsValue = 0f
            }

            override fun onError(error: Int) {
                isRecording = false
            }

            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        onDispose { speechRecognizer.destroy() }
    }
    var showUserDropdown by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()

        // .padding(horizontal = 5.dp).padding(bottom = 8.dp)
    ) {
        ConstraintLayout(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        )
        {
            val (chatSection, messageBar, image) = createRefs()

            if (showUserDropdown) {
                Image(
                    painter = painterResource(id = R.drawable.white_curved_image2),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .constrainAs(image) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                            height = Dimension.fillToConstraints
                        },
                    contentScale = ContentScale.FillBounds
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .constrainAs(chatSection) {
                        // top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(messageBar.top)
                        height = Dimension.fillToConstraints
                    }
            ) {


                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)

                    //   .padding(horizontal = 5.dp).padding(bottom = 8.dp)
                ) {
                    // Spacer(Modifier.height(25.dp))
                    Surface(
                        modifier = Modifier
                            .wrapContentWidth()
                            .align(Alignment.CenterHorizontally)
                            .padding(horizontal = 18.dp),
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                        color = Color.Transparent,
                        //shadowElevation = if (showUserDropdown) 2.dp else 0.dp
                    ) {

                        Surface(
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(horizontal = 10.dp)
                                .padding(top = 30.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { showUserDropdown = !showUserDropdown },
                            shape = RoundedCornerShape(30.dp),
                            color = Color(0xFFF0EDFF),
                        ) {
                            Row(
                                modifier = Modifier.padding(6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                Image(
                                    painter = painterResource(R.drawable.ic_chat_circle_person_icon),
                                    contentDescription = null,
                                    modifier = Modifier.size(13.dp)
                                )

                                Spacer(Modifier.width(2.dp))

                                Text(
                                    selectedMember?.name ?: "Select User",
                                    color = Color(0xFF5B47DB),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(Modifier.width(2.dp))
                                Image(

                                    painter = painterResource(
                                        if (showUserDropdown) R.drawable.ic_dropdown_show
                                        else R.drawable.ic_dropdown_icon
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(10.dp)
                                        .padding(end = 4.dp)
                                )
                            }
                        }
                    }



                    if (showUserDropdown) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {

                                familyList.forEach { member ->

                                    val isSelected = member.id == familyMemberId

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(44.dp)
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null
                                            ) {

                                                // update ViewModel (IMPORTANT)
                                                viewModel.setChatArgs(
                                                    chatId = viewModel.chatArgs.value.chatId,
                                                    familyMemberId = member.id,
                                                    chatMessage = viewModel.chatArgs.value.chatMessage,
                                                    type = viewModel.chatArgs.value.type,
                                                    familyList = familyList
                                                )

                                                showUserDropdown = false
                                            }
                                            .padding(horizontal = 16.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {

                                            Text(
                                                text = member.name,
                                                fontSize = 16.sp,
                                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                                                color = if (isSelected) Color(0xFF4338CA)
                                                else Color(0xFF374151)
                                            )

                                            if (isSelected) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.ic_tick_icon),
                                                    contentDescription = "Selected",
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Column(modifier = Modifier
                .fillMaxWidth()
                .constrainAs(messageBar) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .background(color = Color.White)) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()/*.padding(horizontal = 5.dp)*/
                        .padding(start = 10.dp, end = 5.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(5.dp))
                    // Rounded text box
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentHeight()
                            .background(Color(0xFFF5F0FF), RoundedCornerShape(28.dp))
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.attach_ic),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .wrapContentSize()
                                .align(alignment = Alignment.Bottom)
                                .padding(start = 13.dp, bottom = 13.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {


                                    fileLauncher.launch(
                                        arrayOf(
                                            "image/*",
                                            "application/pdf"
                                        )
                                    )
                                }
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            if (state.images.isNotEmpty() || state.pdfs.isNotEmpty()) {
                                isMessageEmpty = false
                                Spacer(modifier = Modifier.height(10.dp))
                                InlineAttachmentPreview(
                                    images = state.images,
                                    pdfs = state.pdfs,
                                    onRemoveImage = viewModel::removeImage,
                                    onRemovePdf = viewModel::removePdf
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                            if (!showText) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF4EFFF), RoundedCornerShape(20.dp))
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = "See text",
                                        color = Color(0xFF374151),
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null
                                            ) {
                                                showText = true
                                            }
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {

                                        Icon(
                                            painterResource(R.drawable.ic_close),
                                            contentDescription = null,
                                            tint = Color.Unspecified,
                                            modifier = Modifier
                                                .size(23.dp)
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = null
                                                ) {
                                                    speechRecognizer.cancel()
                                                    recognizedText = ""
                                                    viewModel.onMessageChange("")
                                                    rmsValue = 0f
                                                    isRecording = false
                                                    showText = true

                                                }
                                        )

                                        Spacer(Modifier.width(12.dp))


                                        if (isRecording) {
                                            val composition by rememberLottieComposition(
                                                LottieCompositionSpec.RawRes(R.raw.ic_voice_wave_json)
                                            )

                                            LottieAnimation(
                                                composition,
                                                iterations = LottieConstants.IterateForever,
                                                modifier = Modifier.weight(1f)
                                            )

                                        } else {
                                            Image(
                                                painter = painterResource(R.drawable.voice_waveform),
                                                contentDescription = null,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }

                                        Spacer(Modifier.width(12.dp))


                                    }
                                }
                            } else {
                                if (showText) {
                                    voiceText = recognizedText
                                }


                                TextField(
                                    // value = textInput + voiceText,
                                    value = state.message,
                                    onValueChange = {
                                        viewModel.onMessageChange(it)
                                        // textInput = it
                                        recognizedText = ""
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = TextStyle(
                                        fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                        fontSize = 13.sp,
                                        color = Color.Black
                                    ),
                                    placeholder = {
                                        Text(
                                            "Ask anything…",
                                            fontSize = 12.sp,
                                            color = Color(0xFF949494),
                                            fontFamily = FontFamily(Font(R.font.urbanist_regular))
                                        )
                                    },
                                    maxLines = 4,
                                    colors = TextFieldDefaults.colors(
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedTextColor = Color.Black,
                                        unfocusedTextColor = Color.Black,
                                        cursorColor = Color.Black
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))




                    if (isMessageEmpty) {
                        IconButton(
                            onClick = {
                                if (!isRecording) {
                                    recognizedText = ""
                                    showText = false
                                    speechRecognizer.startListening(intent)
                                } else {
                                    speechRecognizer.stopListening()
                                }
                            },
                            modifier = Modifier.padding(end = 10.dp)

                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.voiceinc_ic),
                                // painter = painterResource(id = R.drawable.ic_mic_icon),
                                contentDescription = "Voice Input",
                                tint = Color.Unspecified,
                                modifier = Modifier.wrapContentSize()
                            )
                        }
                    } else {
                        IconButton(
                            onClick = {
                                viewModel.sendMessageFromInput()
                                onSendClicked()
                            },
                            modifier = Modifier.padding(end = 10.dp)
                        ) {
                            Icon(
                                painterResource(R.drawable.send_ic),
                                contentDescription = "Send",
                                tint = Color.Unspecified,
                                modifier = Modifier.wrapContentSize()
                            )
                        }

                    }
                }
            }
        }

    }

}

