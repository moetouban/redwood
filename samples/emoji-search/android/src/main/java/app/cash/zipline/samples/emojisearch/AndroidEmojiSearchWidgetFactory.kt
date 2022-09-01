package app.cash.zipline.samples.emojisearch

import androidx.compose.runtime.Composable
import example.schema.widget.EmojiSearchWidgetFactory

object AndroidEmojiSearchWidgetFactory : EmojiSearchWidgetFactory<@Composable () -> Unit> {
  override fun Row() = ComposeUiRow()
  override fun Column() = ComposeUiColumn()
  override fun ScrollableColumn() = ComposeUiScrollableColumn()
  override fun TextInput() = ComposeUiTextInput()
  override fun Text() = ComposeUiText()
  override fun Image() = ComposeUiImage()
}
