import {
  Action,
  Dispatch,
  Middleware,
  MiddlewareAPI,
} from 'redux';
import EventBus, { EventBus as IEventBus } from 'vertx3-eventbus-client';
import { createAction } from '@reduxjs/toolkit';
import { RootState } from '../app/rootReducer';
import { EventBusOptions } from './EventBusOptions';
import { ebConnected, ebDisconnected } from '../features/render/execSlice';

/** Action types that will be handled by the EventBus middleware. */
type EventBusAction = ReturnType<typeof ebConnected | typeof ebDisconnected>
| 'EB_SEND'
| 'EB_CONNECT'
| 'EB_DISCONNECT';

/** Object that wraps arguments of registerHandler method of EventBus. */
interface EventBusHandler {
  address: Parameters<IEventBus['registerHandler']>[0];
  headers: Parameters<IEventBus['registerHandler']>[1];
  callback: Parameters<IEventBus['registerHandler']>[2];
}

/** Object that wraps arguments of send method of EventBus. */
interface EventBusPayload {
  address: Parameters<IEventBus['send']>[0];
  message: Parameters<IEventBus['send']>[1];
  headers: Parameters<IEventBus['send']>[2];
}

interface EventBusSetup {
  host: string;
  options?: EventBusOptions;
}

const ebConnect = createAction<EventBusSetup, 'EB_CONNECT'>('EB_CONNECT');
const ebDisconnect = createAction<void, 'EB_DISCONNECT'>('EB_DISCONNECT');
const ebSend = createAction<EventBusPayload, 'EB_SEND'>('EB_SEND');

type EbAction = ReturnType<typeof ebConnect | typeof ebDisconnect | typeof ebSend>;

function isConnectAction(action: Action<EventBusAction>): action is ReturnType<typeof ebConnect> {
  return action.type === 'EB_CONNECT';
}

function isDisconnectAction(action: Action<EventBusAction>): action is ReturnType<typeof ebDisconnect> {
  return action.type === 'EB_DISCONNECT';
}

function isSendAction(action: Action<EventBusAction>): action is ReturnType<typeof ebSend> {
  return action.type === 'EB_SEND';
}

const eventBusMiddleware: () => Middleware<{}, RootState, Dispatch<Action<EventBusAction>>> = () => {
  let eventBus: IEventBus | null = null;
  const handlers: EventBusHandler[] = [];

  function onOpen(dispatch: Dispatch): IEventBus['onopen'] {
    return () => {
      // eventBus?.registerHandler('in', {}, ((error, message) => {
      //   if (error) {
      //     console.error(`Handle error "${error.name}": ${error.message}\n${error.stack}`);
      //   } else {
      //     console.log(`Handle message: ${JSON.stringify(message)}`);
      //   }
      // }));
      // TODO: handle specific messages:
      // TODO: - protelis.web.exec.setup
      // TODO: - protelis.web.exec.{id}.init
      // TODO: - protelis.web.exec.{id}.step
      // TODO: - protelis.web.exec.{id}.end
      dispatch(ebConnected());
    };
  }

  function onClose(dispatch: Dispatch): IEventBus['onclose'] {
    return () => dispatch(ebDisconnected());
  }

  function onError(dispatch: Dispatch): IEventBus['onerror'] {
    return (error: Error) => {
      console.error(`EventBus error: ${error}`);
      dispatch(ebDisconnect());
    };
  }

  return <A extends EbAction, D extends Dispatch<A>>
  (api: MiddlewareAPI<Dispatch<Action<EventBusAction>>, RootState>) => (next: D) => (action: A) => {
    if (isConnectAction(action)) {
      // TODO: connect
      if (!eventBus) {
        const { host, options } = action.payload;
        eventBus = new EventBus(host, options);
        eventBus.enableReconnect(false);
        // eslint-disable-next-line @typescript-eslint/unbound-method
        eventBus.onopen = onOpen(api.dispatch);
        // eslint-disable-next-line @typescript-eslint/unbound-method
        eventBus.onclose = onClose(api.dispatch);
        // eslint-disable-next-line @typescript-eslint/unbound-method
        eventBus.onerror = onError(api.dispatch);
      } else {
        throw new Error('EventBus is already initialized');
      }
    } else if (isDisconnectAction(action)) {
      if (eventBus) {
        handlers.forEach(
          ({ address, headers, callback }) => {
            eventBus?.unregisterHandler(address, headers, callback);
          },
        );
        eventBus.close();
        eventBus = null;
      } else {
        throw new Error('EventBus is already closed');
      }
    } else if (isSendAction(action)) {
      if (eventBus) {
        const { address, message, headers } = action.payload;
        eventBus.send(address, message, headers); // TODO: add callback
      } else {
        throw new Error('EventBus is closed');
      }
    }
    return next(action);
  };
};

export {
  ebConnect,
  ebDisconnect,
  ebSend,
};

export default eventBusMiddleware;
