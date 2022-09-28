Redwood
=======

This artifact includes `Column` and `Row` widget implementations for Android Views, Android Compose UI, and iOS UIKit.

To being, add the follow entries to your `schema.kt`:

```kotlin
@Widget(1)
data class Row(
  @Property(1) @Default("app.cash.redwood.compose.Padding.Zero") val padding: Padding,
  @Property(2) @Default("app.cash.redwood.compose.Overflow.Clip") val overflow: Overflow,
  @Property(3) @Default("app.cash.redwood.compose.CrossAxisAlignment.Start") val horizontalAlignment: CrossAxisAlignment,
  @Property(4) @Default("app.cash.redwood.compose.MainAxisAlignment.Start") val verticalAlignment: MainAxisAlignment,
  @Children(5) val children: RowScope.() -> Unit,
)

object RowScope {
  /** https://developer.mozilla.org/en-US/docs/Web/CSS/flex-grow */
  fun LayoutModifier.grow(value: Int): LayoutModifier {
    return then(GrowLayoutModifier(value))
  }

  /** https://developer.mozilla.org/en-US/docs/Web/CSS/flex-shrink */
  fun LayoutModifier.shrink(value: Int): LayoutModifier {
    return then(ShrinkLayoutModifier(value))
  }

  /** https://developer.mozilla.org/en-US/docs/Web/CSS/align-self */
  fun LayoutModifier.verticalAlignment(alignment: CrossAxisAlignment): LayoutModifier {
    return then(VerticalAlignmentLayoutModifier(alignment))
  }
}

@Widget(2)
data class Column(
  @Property(1) @Default("app.cash.redwood.compose.Padding.Zero") val padding: Padding,
  @Property(2) @Default("app.cash.redwood.compose.Overflow.Clip") val overflow: Overflow,
  @Property(3) @Default("app.cash.redwood.compose.CrossAxisAlignment.Start") val horizontalAlignment: CrossAxisAlignment,
  @Property(4) @Default("app.cash.redwood.compose.MainAxisAlignment.Start") val verticalAlignment: MainAxisAlignment,
  @Children(5) val children: ColumnScope.() -> Unit,
)

object ColumnScope {
  /** https://developer.mozilla.org/en-US/docs/Web/CSS/flex-grow */
  fun LayoutModifier.grow(value: Int): LayoutModifier {
    return then(GrowLayoutModifier(value))
  }

  /** https://developer.mozilla.org/en-US/docs/Web/CSS/flex-shrink */
  fun LayoutModifier.shrink(value: Int): LayoutModifier {
    return then(ShrinkLayoutModifier(value))
  }

  /** https://developer.mozilla.org/en-US/docs/Web/CSS/align-self */
  fun LayoutModifier.horizontalAlignment(alignment: CrossAxisAlignment): LayoutModifier {
    return then(HorizontalAlignmentLayoutModifier(alignment))
  }
}
```
