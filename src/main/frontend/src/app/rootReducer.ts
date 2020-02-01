import { combineReducers } from '@reduxjs/toolkit';
import editorReducer from '../features/editor/editorSlice';
import execReducer from '../features/render/execSlice';

const rootReducer = combineReducers({
  editor: editorReducer,
  exec: execReducer,
});

/** Root Redux store types. */
export type RootState = ReturnType<typeof rootReducer>;

export default rootReducer;
