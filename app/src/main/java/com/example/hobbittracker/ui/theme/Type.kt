package com.example.hobbittracker.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.hobbittracker.R

// Set of Material typography styles to start with

val headerFont = FontFamily(
    Font(R.font.nunito_bold, FontWeight.Bold),
    Font(R.font.nunito_light, FontWeight.Light),
    Font(R.font.nunito_medium, FontWeight.Medium),
    Font(R.font.nunito_regular, FontWeight.Normal),
    Font(R.font.nunito_semibold, FontWeight.SemiBold),
)

val textFont = FontFamily(
    Font(R.font.opensans_bold, FontWeight.Bold),
    Font(R.font.opensans_light, FontWeight.Light),
    Font(R.font.opensans_medium, FontWeight.Medium),
    Font(R.font.opensans_regular, FontWeight.Normal),
    Font(R.font.opensans_semibold, FontWeight.SemiBold),
)

val Typography = Typography(

    body1 = TextStyle(
        fontFamily = textFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),

    h1 = TextStyle(
        fontFamily = headerFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp
    )

    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)