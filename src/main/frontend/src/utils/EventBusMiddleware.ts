import { Action, Dispatch, Middleware, MiddlewareAPI } from "redux";
import { RootState } from "../app/rootReducer";
import { EventBus as IEventBus } from "vertx3-eventbus-client";
import { EventBusOptions } from "./EventBusOptions";
import { createAction } from "@reduxjs/toolkit";

type EventBusAction = 'EB_SEND'
  | 'EB_CONNECT'
  | 'EB_CONNECTED'
  | 'EB_DISCONNECT'
  | 'EB_DISCONNECTED';

let eventBus: IEventBus | null = null;

interface EventBusSetup {
  host: string;
  options?: EventBusOptions;
}

const ebConnect = createAction<EventBusSetup, EventBusAction>('EB_CONNECT');
const ebConnected = createAction<void, EventBusAction>('EB_CONNECTED');
const ebDisconnect = createAction<void, EventBusAction>('EB_DISCONNECT');
const ebDisconnected = createAction<void, EventBusAction>('EB_DISCONNECTED');
const ebSend = createAction<string, EventBusAction>('EB_SEND');

export const EventBusMiddleware: Middleware<{}, RootState, Dispatch<Action<EventBusAction>>> =
  <A extends Action<EventBusAction>, D extends Dispatch<A>>
    // TODO: maybe these checks are too much (not correct because can be any action)
    (api: MiddlewareAPI<Dispatch<Action<EventBusAction>>, RootState>) =>
      (next: D) =>
        (action: A) => {
          // console.log('dispatching', action);
          // let result = next(action);
          // console.log('next state', store.getState());
          // return result
          // TODO
          console.log("Before");
          const result = next(action);
          console.log("After"); // Can use: api.getState()
          return result;
        };
