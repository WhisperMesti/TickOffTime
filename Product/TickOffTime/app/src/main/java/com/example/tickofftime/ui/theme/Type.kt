package com.example.tickofftime.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.tickofftime.R

//all fonts
val Rosarivo = FontFamily(
    Font(R.font.rosarivo)
)
val Acme = FontFamily(
    Font(R.font.acme)
)
val Tajawal = FontFamily(
    Font(R.font.tajawal),
    Font(R.font.tajawal_bold, FontWeight.Bold)
)
val Arvo = FontFamily(
    Font(R.font.arvo)
)
val Typography = Typography(
    h1 = TextStyle( //months and days
        fontFamily = Rosarivo,
        fontWeight = FontWeight.Normal,
        fontSize = 40.sp
    ),
    h2 = TextStyle( //categories
        fontFamily = Acme,
        fontWeight = FontWeight.Normal,
        fontSize = 25.sp
    ),
    body1 = TextStyle( //tasks
        fontFamily = Tajawal,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    ),
    body2 = TextStyle( //information, button, annoucements
        fontFamily = Arvo,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    )
)