package ui

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marcosandre.weatherapp.model.City
import com.marcosandre.weatherapp.viewmodel.MainViewModel


@Preview(showBackground = true)
@Composable
fun ListPage(modifier: Modifier = Modifier,
             viewModel: MainViewModel
) {

    Column(
        modifier = modifier.fillMaxSize()
            .background(Color.Magenta)
            .wrapContentSize(Alignment.Center)

    ) {
        Text(
            text = "Favoritas",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = modifier.align(CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )

    }

    val cityList = viewModel.cities                  // usar diretamente os dados que vêm do ViewModel
    val activity = LocalContext.current as Activity // Para os Toasts
    LazyColumn ( // cria a lista vertical
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(cityList, key = { it.name }) { city ->   // percorre cityList e exibe um CityItem para cada cidade
            CityItem(city = city, onClose = {
                viewModel.remove(city)              // remove a cidade da lista interna (Jetpack Compose reage automaticamente)
                Toast.makeText(activity, "Cidade removida: ${city.name}", Toast.LENGTH_LONG).show()

            }, onClick = {
                Toast.makeText(activity, "Voce clicou em ${city.name}", Toast.LENGTH_LONG).show()
            })
        }
    }


}



@Composable
fun CityItem(
    city: City,                     // A cidade que será exibida
    onClick: () -> Unit,            // O que fazer a clicar no item
    onClose: () -> Unit,            // O que fazer ao clicar no botão X
    modifier: Modifier = Modifier   // permite aplicar modificações (padding, alinhamento, etc)
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(8.dp).clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ícone à esquerda
        Icon(
            Icons.Rounded.FavoriteBorder,
            contentDescription = ""
        )
        Spacer(modifier = Modifier.size(12.dp))

        // Nome e Clima
        Column(modifier = modifier.weight(1f)) {
            Text(modifier = Modifier,
                text = city.name,
                fontSize = 24.sp)
            Text(modifier = Modifier,
                text = city.weather?:"Carregando clima...",
                fontSize = 16.sp)

        }

        // Botão X para remover item
        IconButton(onClick = onClose) {
            Icon(Icons.Filled.Close, contentDescription = "Close")
        }
    }
}
