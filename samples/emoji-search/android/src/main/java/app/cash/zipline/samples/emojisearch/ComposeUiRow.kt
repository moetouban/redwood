package app.cash.zipline.samples.emojisearch

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import app.cash.redwood.LayoutModifier
import example.schema.widget.Row

class ComposeUiRow : Row<@Composable () -> Unit> {
  override var layoutModifiers: LayoutModifier = LayoutModifier

  override val children = ComposeUiWidgetChildren()

  override val value = @Composable {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .padding(horizontal = 16.dp)
        .fillMaxWidth()
    ) {
      children.render()
    }
  }
}
