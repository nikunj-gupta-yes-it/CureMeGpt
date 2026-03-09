package com.bussiness.curemegptapp.ui.dialog

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.theme.AppGradientColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun SummaryDialog(
    description: String,
    onDismiss: () -> Unit
) {  val context = LocalContext.current
    Dialog(onDismissRequest = onDismiss,   properties = DialogProperties(
        dismissOnClickOutside = false,
        dismissOnBackPress = false
    )) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .padding(20.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(38.dp)
                    )

                    Spacer(modifier = Modifier.width(11.dp))

                    Text(
                        text = "Summary Heading",
                        fontSize = 17.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "Close",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(38.dp)
                            .clickable(interactionSource = remember { MutableInteractionSource() },
                                indication = null) { onDismiss() }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .height(300.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = description,
                        fontSize = 15.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF050505),
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

//                Button(
//                    onClick = {
//                        saveDescriptionToFile(
//                            context = context,
//                            description = description
//                        )
//                    },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(52.dp),
//                    shape = RoundedCornerShape(26.dp),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color.Transparent   // 👈 important
//                    ),
//                    contentPadding = PaddingValues(0.dp)
//                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp).clip(RoundedCornerShape(26.dp))
                            .background(
                                brush = Brush.linearGradient(AppGradientColors)
                            ).clickable(interactionSource = remember { MutableInteractionSource() },
                                indication = null){
                                saveDescriptionToFile(
                                    context = context,
                                    description = description
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Download",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily(Font(R.font.urbanist_semibold))
                        )
                   // }
                }

//                GradientButton(
//                    text = "Download",
//                    onClick = {
//                        saveDescriptionToFile(
//                            context = context,
//                            description = description
//                        )
//                    },
//                    modifier = Modifier.height(52.dp)
//                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
fun saveDescriptionToFile(
    context: Context,
    description: String
) {
    val timeStamp = SimpleDateFormat(
        "yyyy-MM-dd_HH-mm-ss",
        Locale.getDefault()
    ).format(Date())
    val fileName = "description_$timeStamp.txt"
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
    }
    val uri = context.contentResolver.insert(
        MediaStore.Downloads.EXTERNAL_CONTENT_URI,
        contentValues
    )
    uri?.let {
        context.contentResolver.openOutputStream(it)?.use { outputStream ->
            outputStream.write(description.toByteArray())
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Preview(showBackground = true)
@Composable
fun SummaryDialogPreview() {
    SummaryDialog(description= "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
        onDismiss= { })
}

