package it.unibo.alchemist.core.interfaces

import it.unibo.alchemist.model.interfaces.Position
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.util.concurrent.TimeUnit

/**
 * Wait for the simulation to reach the selected [Status] and check if it actually reached it.
 *
 * @param s The [Status] the simulation must reach before returning from this method
 * @param timeout The maximum lapse of time the caller wants to wait before being resumed
 * @param timeUnit The [TimeUnit] used to define "timeout"
 *
 * @return the [Status] of the Simulation at the end of the wait
 *
 * @throws InterruptedException if the wait somehow crashes
 * @throws IllegalStateException if the actual status at the end of the wait does not match with the expected status
 */
fun <T, P : Position<out P>> Simulation<T, P>.waitForAndCheck(
  s: Status,
  timeout: Long = 10,
  timeUnit: TimeUnit = TimeUnit.DAYS
): Status {
  val actual = waitFor(s, timeout, timeUnit)
  check(actual == s) { "Status is $actual instead of expected $s" }
  return actual
}

/**
 * Asynchronous wait for the simulation to reach the selected [Status].
 *
 * @param s The [Status] the simulation must reach before returning from this method
 * @param timeout The maximum lapse of time the caller wants to wait before being resumed
 * @param timeUnit The [TimeUnit] used to define "timeout"
 *
 * @return the [Deferred] [Status] of the Simulation at the end of the wait
 *
 * @throws InterruptedException if the wait somehow crashes
 * @throws IllegalStateException if the actual status at the end of the wait does not match with the expected status
 */
fun <T, P : Position<out P>> Simulation<T, P>.waitForAsync(
  s: Status,
  timeout: Long = 10,
  timeUnit: TimeUnit = TimeUnit.DAYS
): Deferred<Status> = GlobalScope.async { waitForAndCheck(s, timeout, timeUnit) }

/**
 * Suspended function that waits for the simulation to reach the selected [Status].
 *
 * @param s The [Status] the simulation must reach before returning from this method
 * @param timeout The maximum lapse of time the caller wants to wait before being resumed
 * @param timeUnit The [TimeUnit] used to define "timeout"
 *
 * @return the [Status] of the Simulation at the end of the wait
 *
 * @throws InterruptedException if the wait somehow crashes
 * @throws IllegalStateException if the actual status at the end of the wait does not match with the expected status
 */
suspend fun <T, P : Position<out P>> Simulation<T, P>.awaitFor(
  s: Status,
  timeout: Long = 10,
  timeUnit: TimeUnit = TimeUnit.DAYS
): Status = waitForAsync(s, timeout, timeUnit).await()
