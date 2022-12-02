package com.example.syncdata.presentation.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Typography
import com.example.syncdata.R


val raleway = FontFamily(
    Font(R.font.raleway_black, FontWeight.Black),
    Font(R.font.raleway_bold, FontWeight.Bold),
    Font(R.font.raleway_extrabold, FontWeight.ExtraBold),
    Font(R.font.raleway_extralight, FontWeight.ExtraLight),
    Font(R.font.raleway_light, FontWeight.Light),
    Font(R.font.raleway_medium, FontWeight.Medium),
    Font(R.font.raleway_regular, FontWeight.Normal),
    Font(R.font.raleway_semibold, FontWeight.SemiBold),
    Font(R.font.raleway_thin, FontWeight.Thin)
)

val dancingscript = FontFamily(
    Font(R.font.dancingscript_bold, FontWeight.Bold),
    Font(R.font.dancingscript_regular, FontWeight.Normal),
    Font(R.font.dancingscript_semibold, FontWeight.SemiBold),
    Font(R.font.dancingscript_medium, FontWeight.Medium)
)

val amaticsc =  FontFamily(
    Font(R.font.amaticsc_bold, FontWeight.Bold),
    Font(R.font.amaticsc_regular, FontWeight.Normal))




// Set of Material typography styles to start with
val Typography = Typography(
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
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