/* eslint-disable no-console */
import React, { useEffect } from 'react';
import EventBus, { EventBus as IEventBus } from 'vertx3-eventbus-client';
import { useDispatch } from 'react-redux';
import { Dispatch } from 'redux';
import { changeStatus } from './execSlice';

let eventBus: IEventBus | null = null;

const onOpen: (dispatch: Dispatch) => IEventBus['onopen'] = (dispatch: Dispatch) => () => {
  console.log('EventBus opened');
  dispatch(changeStatus('open'));
};

const onClose: (dispatch: Dispatch) => IEventBus['onclose'] = (dispatch: Dispatch) => () => {
  console.log('EventBus closed');
  dispatch(changeStatus('closed'));
};

const onError: () => IEventBus['onerror'] = () => (error: Error) => console.error(`EventBus error: ${error}`);

const onMount: (dispatch: Dispatch) => void = (dispatch: Dispatch) => {
  console.log('Mounting...');
  if (eventBus == null) {
    eventBus = new EventBus('/sock/');
    // eventBus.enableReconnect(true);
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
