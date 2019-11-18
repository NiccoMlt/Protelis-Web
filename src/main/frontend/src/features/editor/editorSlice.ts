import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { ProtelisFile, ProtelisSourceFile } from '../../model/File';

/** This partial state is related to the TreeView of the editor block. */
interface FileTreeState {
  files: Set<ProtelisFile>
}

/** This partial state is related to the TextArea of the editor block. */
interface EditorState {
  open: ProtelisSourceFile | null
}

/** State type of the Protelis editor block. */
type EditorBlockState = FileTreeState & EditorState;

const initialState: EditorBlockState = {
  files: new Set(),
  open: null,
};

const editorSlice = createSlice({
  name: 'editor',
  initialState,
  reducers: {
    openFile(state, action: PayloadAction<ProtelisSourceFile>) {
      state.open = action.payload;
    },
    closeFile(state, action: PayloadAction<ProtelisSourceFile>) {
      if (state.open === action.payload) {
        state.open = null;
      }
    },
  },
});

/**
 * @param openFile - action dispatched when a file is selected to be opened
 * @param closeFile - action dispatched when a file is closed
 */
export const { openFile, closeFile } = editorSlice.actions;

/** Reducer from the Redux slice of the editor. */
export default editorSlice.reducer;
