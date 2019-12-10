import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { ProtelisFile, ProtelisSourceFile } from '../../model/File';

/** This partial state is related to the TreeView of the editor block. */
interface FileTreeState {
  files: ProtelisFile[];
}

/** This partial state is related to the TextArea of the editor block. */
interface EditorState {
  open: ProtelisSourceFile | null;
}

/** State type of the Protelis editor block. */
type EditorBlockState = FileTreeState & EditorState;

const initialState: EditorBlockState = { files: [], open: null };

const editorSlice = createSlice({
  name: 'editor',
  initialState,
  reducers: {
    addFile(state, action: PayloadAction<ProtelisSourceFile>): void {
      const { files } = state;
      files.push(action.payload);
      state.files = files;
    },
    openFile(state, action: PayloadAction<ProtelisSourceFile>): void {
      state.open = action.payload;
    },
    closeFile(state, action: PayloadAction<ProtelisSourceFile>): void {
      if (state.open?.name === action.payload.name) {
        state.open = null;
      }
      // TODO should save content?
    },
  },
});

/**
 * @param addFile - action dispatched when a file is added
 * @param openFile - action dispatched when a file is selected to be opened
 * @param closeFile - action dispatched when a file is closed
 */
export const { addFile, openFile, closeFile } = editorSlice.actions;

/** Reducer from the Redux slice of the editor. */
export default editorSlice.reducer;
