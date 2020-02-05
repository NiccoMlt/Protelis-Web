/* eslint-disable no-console */
import React, { useEffect } from 'react';
import EventBus, { EventBus as IEventBus } from 'vertx3-eventbus-client';
import { useDispatch } from 'react-redux';
import { Dispatch } from 'redux';
import { changeStatus } from './execSlice';
import { EventBusOptions } from '../../utils/EventBusOptions';

let eventBus: IEventBus | null = null;

const onOpen: (dispatch: Dispatch) => IEventBus['onopen'] = (dispatch: Dispatch) => () => {
  console.log('EventBus opened');
  dispatch(changeStatus('open'));
  eventBus?.registerHandler('in', {}, ((error, message) => {
    if (error) {
      console.error(`Handle error "${error.name}": ${error.message}\n${error.stack}`);
    } else {
      console.log(`Handle message: ${JSON.stringify(message)}`);
    }
  }));
};

const onClose: (dispatch: Dispatch) => IEventBus['onclose'] = (dispatch: Dispatch) => () => {
  console.log('EventBus closed');
  dispatch(changeStatus('closed'));
};

const onError: () => IEventBus['onerror'] = () => (error: Error) => console.error(`EventBus error: ${error}`);

const onMount: (dispatch: Dispatch) => void = (dispatch: Dispatch) => {
  console.log('Mounting...');
  if (eventBus == null) {
    const options: EventBusOptions = {
      vertxbus_reconnect_attempts_max: Infinity, // Max reconnect attempts
      vertxbus_reconnect_delay_min: 1000, // Initial delay (in ms) before first reconnect attempt
      vertxbus_reconnect_delay_max: 5000, // Max delay (in ms) between reconnect attempts
      vertxbus_reconnect_exponent: 2, // Exponential backoff factor
      vertxbus_randomization_factor: 0.5, // Randomization factor between 0 and 1
    };
    eventBus = new EventBus('http://localhost:8080/eventbus', options);
    eventBus.enableReconnect(true);
    // eslint-disable-next-line @typescript-eslint/unbound-method
    eventBus.onopen = onOpen(dispatch);
    // eslint-disable-next-line @typescript-eslint/unbound-method
    eventBus.onclose = onClose(dispatch);
    // eslint-disable-next-line @typescript-eslint/unbound-method
    eventBus.onerror = onError();
  } else {
    throw new Error('EventBus is already initialized');
  }
};

const onUnmount: () => void | undefined = () => {
  console.log('Unmounting...');
  if (eventBus != null) {
    // TODO: unregister handlers
    eventBus.close();
    eventBus = null;
  } else {
    throw new Error('EventBus is already closed');
  }
};

const EventBusConnector: React.FC = () => {
  const dispatch: Dispatch = useDispatch();

  useEffect(() => {
    onMount(dispatch);
    return onUnmount;
  });

  return <></>;
};

export default EventBusConnector;
