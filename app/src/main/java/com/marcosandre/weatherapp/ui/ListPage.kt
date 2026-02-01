package com.marcosandre.weatherapp.ui

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.marcosandre.weatherapp.R
import com.marcosandre.weatherapp.model.City
import com.marcosandre.weatherapp.model.Weather
import com.marcosandre.weatherapp.ui.nav.Route
import com.marcosandre.weatherapp.viewmodel.MainViewModel


@Preview(showBackground = true)
@Composable
fun ListPage(modifier: Modifier = Modifier,
             viewModel: MainViewModel
) {

    val cityList = viewModel.cities                  // usar diretamente os dados que vêm do ViewModel
    val activity = LocalContext.current as Activity // Para os Toasts
    LazyColumn ( // cria a lista vertical
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(cityList, key = { it.name }) { city ->   // percorre cityList e exibe um CityItem para cada cidade
            CityItem(city = city,
                weather = viewModel.weather(city.name),
                onClose = {
                viewModel.remove(city)              // remove a cidade da lista interna (Jetpack Compose reage automaticamente)
                Toast.makeText(activity, "Cidade removida: ${city.name}", Toast.LENGTH_LONG).show()

            }, onClick = {
                Toast.makeText(activity, "Voce clicou em ${city.name}", Toast.LENGTH_LONG).show()
                    viewModel.city = city.name

                    // Muda a página atual para "Home"
                    viewModel.page = Route.Home
            })
        }
    }


}



@Composable
fun CityItem(
    city: City,                     // A cidade que será exibida
    weather: Weather,
    onClick: () -> Unit,            // O que fazer a clicar no item
    onClose: () -> Unit,            // O que fazer ao clicar no botão X
    modifier: Modifier = Modifier   // permite aplicar modificações (padding, alinhamento, etc)
) {
    val desc =
        if (weather == Weather.LOADING) "Carregando clima..."
        else weather.desc

    val monitorIcon =
        if (city.isMonitored)
            Icons.Filled.Notifications
        else
            Icons.Outlined.Notifications


    Row(
        modifier = modifier.fillMaxWidth().padding(8.dp).clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ícone à esquerda
        // OBSOLETO NA PRÁTICA 09
        /*
        Icon(
            Icons.Rounded.FavoriteBorder,
            contentDescription = ""
        )
         */

        // 🔥 IMAGEM DO CLIMA (AsyncImage)
        AsyncImage(
            model = weather.imgUrl,
            modifier = Modifier.size(75.dp),
            error = painterResource(id = R.drawable.loading),
            contentDescription = "Imagem do clima"
        )

        Spacer(modifier = Modifier.size(12.dp))

        // Nome e Clima
        Column(modifier = modifier.weight(1f)) {
            Text(modifier = Modifier,
                text = city.name,
                fontSize = 24.sp)
            Text(modifier = Modifier,
                //text = city.weather?:"Carregando clima...",
                text = desc,
                fontSize = 16.sp)

        }

        // Ícone de monitoramento (APENAS VISUAL)
        Icon(
            imageVector = monitorIcon,
            contentDescription = "Monitorada?",
            modifier = Modifier
                .size(22.dp)
                .padding(end = 8.dp)
        )

        // Botão X para remover item
        IconButton(onClick = onClose) {
            Icon(Icons.Filled.Close, contentDescription = "Close")
        }
    }
}
