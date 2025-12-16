package com.marcosandre.weatherapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.marcosandre.weatherapp.R
import com.marcosandre.weatherapp.viewmodel.MainViewModel

//@Preview(showBackground = true)
@Composable
fun HomePage(
    //modifier: Modifier = Modifier,  // OBSOLETO na pratica 08
             viewModel: MainViewModel
) {

// OBSOLETO na pratica 08
    /*
    Column(
        modifier = modifier.fillMaxSize()
            .background(Color.Blue)
            .wrapContentSize(Alignment.Center)
    )
     */

    Column {

        if (viewModel.city == null) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Blue)
                    .wrapContentSize(Alignment.Center)
            ) {
                Text(
                    text = "Selecione uma cidade!",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center,
                    fontSize = 28.sp
                )
            }

        } else {

            // Cidade selecionada
            Row {

                // OBSOLETO na prática 09
                /*
                Icon(
                    imageVector = Icons.Filled.AccountBox,
                    contentDescription = null,
                    modifier = Modifier.size(150.dp)
                )
                 */

                // IMAGEM DO CLIMA (AsyncImage)
                AsyncImage(
                    model = viewModel.weather(viewModel.city!!).imgUrl,
                    modifier = Modifier.size(140.dp),
                    error = painterResource(id = R.drawable.loading),
                    contentDescription = "Imagem do clima"
                )

                Column {

                    Spacer(modifier = Modifier.size(12.dp))

                    Text(
                        text = viewModel.city ?: "Selecione uma cidade...",
                        fontSize = 28.sp
                    )

                    viewModel.city?.let { name ->

                        val weather = viewModel.weather(name)

                        Spacer(modifier = Modifier.size(12.dp))

                        Text(
                            text = weather.desc,
                            fontSize = 22.sp
                        )

                        Spacer(modifier = Modifier.size(12.dp))

                        Text(
                            text = "Temp: ${weather.temp}℃",
                            fontSize = 22.sp
                        )
                    }
                }
            }

            // Lista de previsão
            viewModel.forecast(viewModel.city!!)?.let { forecasts ->

                LazyColumn {
                    items(items = forecasts) { forecast ->
                        ForecastItem(
                            forecast = forecast,
                            onClick = { }
                        )
                    }
                }
            }
        }


    }
}