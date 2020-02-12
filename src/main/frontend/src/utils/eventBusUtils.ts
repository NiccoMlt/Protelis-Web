import { Options as SockJsOptions } from 'sockjs-client';
import { EventBus as IEventBus } from 'vertx3-eventbus-client';
import { NodePosition } from '../features/render/execSlice';

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

export interface EventBusMessage<T = unknown> {
  type: string;
  address: string;
  headers: object;
  body: T;
}

/** Object that wraps arguments of registerHandler method of EventBus. */
export interface EventBusHandler {
  address: Parameters<IEventBus['registerHandler']>[0];
  headers: Parameters<IEventBus['registerHandler']>[1];
  callback: Parameters<IEventBus['registerHandler']>[2];
}

/** Generic object that enriches callback type. */
export interface EventBusMsgHandler<T> extends EventBusHandler {
  callback: ((error: Error, message: EventBusMessage<T>) => void);
}

/** A Protelis Node info object encapsulates all the infos about a Node in the Protelis environment. */
interface ProtelisNode {
  id: string;
  coordinates: {
    first: number;
    second: number;
  };
}

/**
 * A Protelis Update Message is the sum-up of an execution step.
 * It propagates infos about the nodes.
 */
export interface ProtelisUpdateMessage {
  nodes: ProtelisNode[];
}

export function eventBusMsgHandleBuilder<T>(handler: (payload: T) => void): EventBusMsgHandler<T>['callback'] {
  return (error: Error, message: EventBusMessage<T>) => {
    if (error) {
      console.error(error);
    } else {
      handler(message.body);
    }
  };
}

/**
 * Transform a JSON-serialized Kotlin ProtelisUpdateMessage data object into an array of NodePositions.
 * @param msg - the object received from EventBus
 * @returns the useful data
 */
export function mapUpdate(msg: ProtelisUpdateMessage): NodePosition[] {
  return msg
    .nodes
    .map(({ id, coordinates: { first, second } }) => ({
      id,
      x: first,
      y: second,
    }));
}
