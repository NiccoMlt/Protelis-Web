import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import CircularProgress from '@material-ui/core/CircularProgress';
import { ControlledEditor } from '@monaco-editor/react';
import Button from '@material-ui/core/Button';
import { Dispatch } from 'redux';
import { RootState } from '../app/rootReducer';
import { ProtelisSourceFile } from '../model/File';
import { closeFile, editFile } from '../features/editor/editorSlice';
import { getSourceFileAtPath } from '../utils/fileUtils';

export const PtMonacoEditor: React.FC = () => {
  const path: string | null = useSelector<RootState, string | null>((state) => state.editor.open);

  const open: ProtelisSourceFile | null = useSelector<RootState, ProtelisSourceFile | null>(
    (state) => (state.editor.open
      ? getSourceFileAtPath(state.editor.files, state.editor.open)
      : null
    ),
  );

  const dispatch: Dispatch = useDispatch();

  const [value, setValue] = useState<string | undefined>(open?.content);

  // If component updates because of Redux, refresh content if file is open
  useEffect(() => setValue(open?.content), [open]);

  function handleShowValue(): void {
    if (path && value) dispatch(editFile({ path, content: value }));
  }

  return (
    <>
      <ControlledEditor
        height="77vh" // By default, it fully fits with its parent
        language="java"
        loading={<CircularProgress color="primary" /> as unknown as React.FC} // TODO: wrong typings
        onChange={(_, newValue) => setValue(newValue)}
        options={{
          automaticLayout: true,
          lineNumbers: 'on',
          minimap: { enabled: false },
          readOnly: (!open),
        }}
        theme="dark"
        value={open ? value : null}
      />
      <div>
        <Button
          variant="contained"
          component="span"
          color="primary"
          onClick={handleShowValue}
          disabled={!open}
        >
          Save
        </Button>
      </div>
    </>
  );
};

export default PtMonacoEditor;
