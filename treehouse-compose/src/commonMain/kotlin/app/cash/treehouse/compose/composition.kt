package app.cash.treehouse.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.MonotonicFrameClock
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.runtime.withFrameMillis
import app.cash.treehouse.protocol.ChildrenDiff
import app.cash.treehouse.protocol.ChildrenDiff.Companion.RootId
import app.cash.treehouse.protocol.Diff
import app.cash.treehouse.protocol.Event
import app.cash.treehouse.protocol.PropertyDiff
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart.UNDISPATCHED
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

interface TreehouseComposition {
  fun sendEvent(event: Event)
  fun setContent(content: @Composable TreehouseScope.() -> Unit)
  fun cancel()
}

/**
 * @param scope A [CoroutineScope] whose [coroutineContext][kotlin.coroutines.CoroutineContext]
 * must have a [MonotonicFrameClock] key which is being ticked.
 */
fun TreehouseComposition(
  scope: CoroutineScope,
  diffs: (Diff) -> Unit,
): TreehouseComposition {
  val clock = requireNotNull(scope.coroutineContext[MonotonicFrameClock]) {
    "Composition scope's CoroutineContext must have a MonotonicFrameClock key"
  }
  val server = RealTreehouseComposition(scope, clock, diffs)
  server.launch()
  return server
}

private class RealTreehouseComposition(
  private val scope: CoroutineScope,
  private val clock: MonotonicFrameClock,
  private val diffs: (Diff) -> Unit,
) : TreehouseComposition {
  private var childrenDiffs = mutableListOf<ChildrenDiff>()
  private var propertyDiffs = mutableListOf<PropertyDiff>()

  private val treehouseScope = RealTreehouseScope()
  inner class RealTreehouseScope : TreehouseScope {
    // TODO atomics if compose becomes multithreaded?
    private var nextId = RootId + 1
    override fun nextId() = nextId++

    override fun appendDiff(diff: ChildrenDiff) {
      childrenDiffs.add(diff)
    }

    override fun appendDiff(diff: PropertyDiff) {
      propertyDiffs.add(diff)
    }
  }

  private val applier = ProtocolApplier(treehouseScope)
  private val recomposer = Recomposer(scope.coroutineContext)
  private val composition = Composition(applier, recomposer)

  private lateinit var recomposeJob: Job
  private lateinit var diffJob: Job

  fun launch() {
    // Set up a trigger to apply changes on the next frame if a global write was observed.
    // TODO where should this live?
    var applyScheduled = false
    Snapshot.registerGlobalWriteObserver {
      if (!applyScheduled) {
        applyScheduled = true
        scope.launch {
          applyScheduled = false
          Snapshot.sendApplyNotifications()
        }
      }
    }

    // These launch undispatched so that they reach their first suspension points before returning
    // control to the caller.
    recomposeJob = scope.launch(start = UNDISPATCHED) {
      recomposer.runRecomposeAndApplyChanges()
    }
    diffJob = scope.launch(start = UNDISPATCHED) {
      while (true) {
        clock.withFrameMillis {
          val existingChildrenDiffs = childrenDiffs
          val existingPropertyDiffs = propertyDiffs
          if (existingPropertyDiffs.isNotEmpty() || existingChildrenDiffs.isNotEmpty()) {
            childrenDiffs = mutableListOf()
            propertyDiffs = mutableListOf()

            diffs(
              Diff(
                childrenDiffs = existingChildrenDiffs,
                propertyDiffs = existingPropertyDiffs,
              )
            )
          }
        }
      }
    }
  }

  override fun sendEvent(event: Event) {
    val node = applier.nodes[event.id]
    if (node == null) {
      // TODO how to handle race where an incoming event targets this removed node?
      throw IllegalArgumentException("Unknown node ${event.id} for event with tag ${event.tag}")
    }
    node.sendEvent(event)
  }

  override fun setContent(content: @Composable TreehouseScope.() -> Unit) {
    composition.setContent {
      treehouseScope.content()
    }
  }

  override fun cancel() {
    diffJob.cancel()
    recomposeJob.cancel()
    recomposer.cancel()
  }
}
