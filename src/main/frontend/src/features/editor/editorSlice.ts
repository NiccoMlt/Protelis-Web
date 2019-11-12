import { createSlice, PayloadAction } from 'redux-starter-kit';
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
    // TODO: add actions after the model is refactored
  },
});

export const { openFile } = editorSlice.actions;

export default editorSlice.reducer;
