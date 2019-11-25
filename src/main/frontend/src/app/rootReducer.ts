import { combineReducers } from '@reduxjs/toolkit';
import editorReducer from '../features/editor/editorSlice';

const rootReducer = combineReducers({
  editor: editorReducer,
  // TODO
});

export type RootState = ReturnType<typeof rootReducer>;

export default rootReducer;
