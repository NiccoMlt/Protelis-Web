import { Options as SockJsOptions } from "sockjs-client";

interface VertxBusOptions {
  vertxbus_ping_interval?: number; // = 5000
  vertxbus_reconnect_attempts_max?: number; // = Infinity;
  vertxbus_reconnect_delay_min?: number; // = 1000;
  vertxbus_reconnect_delay_max?: number; // = 5000;
  vertxbus_reconnect_exponent?: number; // = 2;
  vertxbus_randomization_factor?: number; // = 0.5
}

export type EventBusOptions = VertxBusOptions & SockJsOptions;
