package app.cash.zipline.samples.emojisearch

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.redwood.LayoutModifier
import coil.compose.AsyncImage
import example.schema.widget.Image

class ComposeUiImage : Image<@Composable () -> Unit> {
  private var url by mutableStateOf("")

  override var layoutModifiers: LayoutModifier = LayoutModifier

  override val value = @Composable {
    AsyncImage(
      model = url,
      contentDescription = null,
      modifier = Modifier
        .size(64.dp)
        .padding(8.dp),
    )
  }

  override fun url(url: String) {
    this.url = url
  }
}
