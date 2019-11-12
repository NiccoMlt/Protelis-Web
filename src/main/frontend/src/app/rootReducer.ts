import { combineReducers } from 'redux-starter-kit';

const rootReducer = combineReducers({}); // TODO: we don't have any slices yet

export type RootState = ReturnType<typeof rootReducer>;

export default rootReducer;
