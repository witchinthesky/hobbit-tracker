package com.example.hobbittracker.ui.theme.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class WelcomeScreens {

    @Preview(showBackground = true)
    @Composable
    fun CreateWelcomeScreen(){
        Column(
            modifier = Modifier.fillMaxWidth().height(700.dp),

        ){
            Text("Welcome to monumental habits")
        }
    }
}