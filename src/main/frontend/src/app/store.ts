import { configureStore, getDefaultMiddleware } from '@reduxjs/toolkit';

import rootReducer from './rootReducer';
import eventBusMiddleware from '../utils/EventBusMiddleware';

const store = configureStore({
  reducer: rootReducer,
  middleware: [...getDefaultMiddleware(), eventBusMiddleware()],
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
