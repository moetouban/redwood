package example.android

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test

class MainActivityTest {
  @get:Rule val activity = ActivityTestRule(MainActivity::class.java)

  @Test fun smokeTest() {
    onView(withText("0")).check(matches(isDisplayed()))
    onView(withText("+1")).perform(click())
    onView(withText("1")).check(matches(isDisplayed()))
    onView(withText("+1")).perform(click())
    onView(withText("2")).check(matches(isDisplayed()))
    onView(withText("-1")).perform(click())
    onView(withText("1")).check(matches(isDisplayed()))
    onView(withText("-1")).perform(click())
    onView(withText("0")).check(matches(isDisplayed()))
  }
}
