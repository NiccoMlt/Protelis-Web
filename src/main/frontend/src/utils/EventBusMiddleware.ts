/* eslint-disable @typescript-eslint/unbound-method */ // needed to use on*() EventBus methods
import {
  Dispatch,
  Middleware,
  MiddlewareAPI,
} from 'redux';
import EventBus, { EventBus as IEventBus } from 'vertx3-eventbus-client';
import { createAction } from '@reduxjs/toolkit';
import { RootState } from '../app/rootReducer';
import { isSourceFile } from '../model/File';
import {
  EventBusHandler,
  EventBusMessage,
  eventBusMsgHandleBuilder,
  EventBusMsgHandler,
  EventBusOptions,
  mapUpdate,
  ProtelisUpdateMessage,
} from './eventBusUtils';
import {
  ebConnected,
  ebDisconnected,
  drawInit,
  drawStep,
  drawEnd,
  setId,
} from '../features/render/execSlice';

/** Object that wraps arguments of send method of EventBus. */
interface EventBusPayload {
  address: Parameters<IEventBus['send']>[0];
  message: Parameters<IEventBus['send']>[1];
  headers: Parameters<IEventBus['send']>[2];
}

/** Object that wraps arguments of EventBus object constructor. */
interface EventBusSetup {
  host: string;
  options?: EventBusOptions;
}

const ebConnect = createAction<EventBusSetup, 'EB_CONNECT'>('EB_CONNECT');
const ebDisconnect = createAction<void, 'EB_DISCONNECT'>('EB_DISCONNECT');
const ebUpload = createAction<void, 'EB_UPLOAD'>('EB_UPLOAD');
const ebSend = createAction<EventBusPayload, 'EB_SEND'>('EB_SEND');

/** Action types that will be handled by the EventBus middleware. */
type EbAction = ReturnType<typeof ebConnect | typeof ebDisconnect | typeof ebSend | typeof ebUpload>;

/** Middleware creator function that builds a middleware to let Redux handle a Vert.x EventBus connection. */
function eventBusMiddleware(): Middleware<{}, RootState> {
  let eventBus: IEventBus | null = null;
  const handlers: EventBusHandler[] = [];

  function onOpen(dispatch: Dispatch): IEventBus['onopen'] {
    return () => dispatch(ebConnected());
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

  return (api: MiddlewareAPI<Dispatch, RootState>) => (next: Dispatch<EbAction>) => (action: EbAction) => {
    switch (action.type) {
      case ebConnect.type:
        if (!eventBus) {
          const { host, options } = action.payload;
          eventBus = new EventBus(host, options);
          eventBus.enableReconnect(false);
          eventBus.onopen = onOpen(api.dispatch);
          eventBus.onclose = onClose(api.dispatch);
          eventBus.onerror = onError(api.dispatch);
        }
        break;
      case ebDisconnect.type:
        if (eventBus) {
          handlers.forEach(
            ({ address, headers, callback }) => {
                eventBus?.unregisterHandler(address, headers, callback);
            },
          );
          handlers.length = 0; // clean the array
          eventBus.close();
          eventBus = null;
        } else {
          throw new Error('EventBus is already closed');
        }
        break;
      case ebUpload.type: {
        if (!eventBus) {
          api.dispatch(ebConnect({ host: '/eventbus/' }));
        }
        const { files } = api.getState().editor;
        const source: string = files.filter(isSourceFile)[0].content; // fixme: merge all source files
        api.dispatch(ebSend({ address: 'protelis.web.exec.setup', message: source, headers: {} }));
        break;
      }
      case ebSend.type:
        if (eventBus) {
          const { address, message, headers } = action.payload;
          eventBus.send(address, message, headers, ((error: Error, answer: EventBusMessage<string | unknown>) => {
            if (error) {
              console.error('Unexpected error for answer of message %o to address %s: %o', message, address, error);
            } else if (typeof answer.body !== 'string') {
              console.error('Unexpected answer %o of type %s', answer, typeof answer.body);
            } else {
              const simId = answer.body;
              api.dispatch(setId(simId));
              const simInitHandler: EventBusMsgHandler<ProtelisUpdateMessage> = {
                address: `protelis.web.exec.${simId}.init`,
                headers: {},
                callback: eventBusMsgHandleBuilder<ProtelisUpdateMessage>(
                  (msg) => api.dispatch(drawInit(mapUpdate(msg))),
                ),
              };
              const simStepHandler: EventBusMsgHandler<ProtelisUpdateMessage> = {
                address: `protelis.web.exec.${simId}.step`,
                headers: {},
                callback: eventBusMsgHandleBuilder<ProtelisUpdateMessage>(
                  (msg) => api.dispatch(drawStep(mapUpdate(msg))),
                ),
              };
              const simEndHandler: EventBusMsgHandler<ProtelisUpdateMessage> = {
                address: `protelis.web.exec.${simId}.end`,
                headers: {},
                callback: eventBusMsgHandleBuilder<ProtelisUpdateMessage>(
                  (msg) => api.dispatch(drawEnd(mapUpdate(msg))),
                ),
              };
              eventBus?.registerHandler(simInitHandler.address, simInitHandler.headers, simInitHandler.callback);
              handlers.push(simInitHandler);
              eventBus?.registerHandler(simStepHandler.address, simStepHandler.headers, simStepHandler.callback);
              handlers.push(simStepHandler);
              eventBus?.registerHandler(simEndHandler.address, simEndHandler.headers, simEndHandler.callback);
              handlers.push(simEndHandler);
            }
          }));
        } else {
          throw new Error('EventBus is closed');
        }
        break;
      default:
        // do nothing
    }
    return next(action);
  };
}

export {
  ebConnect,
  ebDisconnect,
  ebSend,
  ebUpload,
};

export default eventBusMiddleware;
