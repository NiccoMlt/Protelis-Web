import { configureStore, getDefaultMiddleware, Middleware } from '@reduxjs/toolkit';

import { createLogger } from 'redux-logger';
import rootReducer from './rootReducer';
import eventBusMiddleware from '../utils/EventBusMiddleware';

const eventBus: Middleware = eventBusMiddleware();
const logger: Middleware = createLogger();

const store = configureStore({
  reducer: rootReducer,
  middleware: [...getDefaultMiddleware(), logger, eventBus],
});

if (process.env.NODE_ENV === 'development' && module.hot) {
  module.hot.accept('./rootReducer', () => {
    /* eslint-disable-next-line global-require */ /* require needed for Hot-Module-Reload */
    const newRootReducer = require('./rootReducer').default;
    store.replaceReducer(newRootReducer);
  });
}

export type AppDispatch = typeof store.dispatch;

export default store;
