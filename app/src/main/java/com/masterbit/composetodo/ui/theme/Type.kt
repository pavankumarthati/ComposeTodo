package com.masterbit.composetodo.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.masterbit.composetodo.R

val family = FontFamily(
    Font(R.font.merriweather, FontWeight.W400),
    Font(R.font.merriweather_bold, FontWeight.W700),
    Font(R.font.merriweather_bold_italic, FontWeight.W700, style = FontStyle.Italic),
)

// Set of Material typography styles to start with
val Typography = Typography(
    defaultFontFamily = family,
    body1 = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 16.sp
    ),
    body2 = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 12.sp
    ),
    h6 = TextStyle(
        fontWeight = FontWeight.W700,
        fontSize = 20.sp,
        fontStyle = FontStyle.Italic
    ),
    subtitle1 = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 18.sp,
    ),
    subtitle2 = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
    ),
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