import Button from '@material-ui/core/Button';
import CircularProgress from '@material-ui/core/CircularProgress';
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles';
import { Save, Send } from '@material-ui/icons';
import { ControlledEditor } from '@monaco-editor/react';
import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Dispatch } from 'redux';
import { RootState } from '../app/rootReducer';
import { editFile } from '../features/editor/editorSlice';
import { ProtelisSourceFile } from '../model/File';
import { getSourceFileAtPath } from '../utils/fileUtils';

const useStyles = makeStyles((theme: Theme) => createStyles({
  root: {
    padding: theme.spacing(3, 2),
    flexGrow: 1,
    height: '85vh',
  },
  button: {
    margin: theme.spacing(1),
  },
}));

/** VS Code / Monaco Editor wrapper component. */
export const PtMonacoEditor: React.FC = () => {
  const path: string | null = useSelector<RootState, string | null>((state) => state.editor.open);

  const open: ProtelisSourceFile | null = useSelector<RootState, ProtelisSourceFile | null>(
    (state) => (state.editor.open
      ? getSourceFileAtPath(state.editor.files, state.editor.open)
      : null
    ),
  );
  const canSend: boolean = useSelector<RootState, boolean>((state) => state.editor.files.length > 0);

  const dispatch: Dispatch = useDispatch();

  const [value, setValue] = useState<string | undefined>(open?.content);

  // If component updates because of Redux, refresh content if file is open
  useEffect(() => setValue(open?.content), [open]);

  function handleShowValue(): void {
    if (path && value) dispatch(editFile({ path, content: value }));
  }

  const classes = useStyles();

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
          className={classes.button}
          onClick={handleShowValue}
          startIcon={<Save />}
          disabled={!open}
        >
          Save
        </Button>
        <Button
          variant="contained"
          component="span"
          color="primary"
          className={classes.button}
          startIcon={<Send />}
          disabled={!canSend}
        >
          Run code
        </Button>
      </div>
    </>
  );
};

export default PtMonacoEditor;
