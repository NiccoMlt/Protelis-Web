import { Options as SockJsOptions } from 'sockjs-client';

/** Possible options for Vert.x EventBus bridge connection. */
interface VertxBusOptions {
  /** Interval between ping messages (in ms); default: 5000 */
  vertxbus_ping_interval?: number;
  /** Max reconnect attempts; default: Infinity */
  vertxbus_reconnect_attempts_max?: number;
  /** Initial delay (in ms) before first reconnect attempt; default: 1000 */
  vertxbus_reconnect_delay_min?: number;
  /** Max delay (in ms) between reconnect attempts; default: 5000 */
  vertxbus_reconnect_delay_max?: number;
  /** Exponential backoff factor; default: 2 */
  vertxbus_reconnect_exponent?: number;
  /** Randomization factor between 0 and 1; default: 0.5 */
  vertxbus_randomization_factor?: number;
}

/** Possible options for Vert.x EventBus SockJS bridge connection. */
export type EventBusOptions = VertxBusOptions & SockJsOptions;
