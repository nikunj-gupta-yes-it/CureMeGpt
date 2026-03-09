package com.bussiness.curemegptapp.ui.screen.settingsScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.component.TopBarHeader2

//PrivacyPolicyScreen

@Composable
fun PrivacyPolicyScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
            .statusBarsPadding()
    ) {
        TopBarHeader2(title = stringResource(R.string.settings_privacy_policy)/*"Privacy Policy"*/, onBackClick = { navController.popBackStack() })
        val fullText =
            "By using CureMeGPT, you agree to follow our app guidelines. CureMeGPT provides AI-powered health support for informational purposes only and does not replace professional medical advice, diagnosis, or treatment. Always consult a licensed doctor for medical emergencies or specific conditions. You agree to use the app responsibly, provide accurate information, and respect other users’ privacy. We reserve the right to update features, policies, or terms at any time. Continued use of the app means you accept the latest version of these Terms & Conditions."

        val keyword = "CureMeGPT"
        val keywordColor = Color(0xFF4338CA)   // Purple

        val annotatedText = buildAnnotatedString {

            var startIndex = 0
            while (true) {
                val index = fullText.indexOf(keyword, startIndex, ignoreCase = false)

                if (index == -1) {
                    append(fullText.substring(startIndex))
                    break
                }

                // Add text before the keyword
                append(fullText.substring(startIndex, index))

                // Add colored keyword
                withStyle(style = SpanStyle(color = keywordColor, fontWeight = FontWeight.Medium)) {
                    append(fullText.substring(index, index + keyword.length))
                }

                startIndex = index + keyword.length
            }
        }

        Text(
            text = annotatedText,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(20.dp)
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PrivacyPolicyScreenPreview() {
    val navController = rememberNavController()
    PrivacyPolicyScreen(navController)
}