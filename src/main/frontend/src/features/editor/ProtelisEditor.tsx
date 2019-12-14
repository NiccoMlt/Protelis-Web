import React, { useState } from 'react';
import { Theme, createStyles, makeStyles } from '@material-ui/core/styles';
import Paper from '@material-ui/core/Paper';
import { ControlledEditor } from '@monaco-editor/react';
import CircularProgress from '@material-ui/core/CircularProgress';
import Grid from '@material-ui/core/Grid';
import Button from '@material-ui/core/Button';
import { LibraryAddRounded, CloudUpload } from '@material-ui/icons';
import { useSelector, useDispatch } from 'react-redux';
import { Dispatch } from 'redux';
import {
  ProtelisSourceFile, ProtelisFile, getSourceFileAtPath,
} from '../../model/File';
import { RootState } from '../../app/rootReducer';
import { addFile, editFile, closeFile } from './editorSlice';
import FileTreeView from './FileTreeView';

const useStyles = makeStyles((theme: Theme) => createStyles({
  root: {
    padding: theme.spacing(3, 2),
    flexGrow: 1,
    height: '85vh',
  },
  button: {
    margin: theme.spacing(1),
  },
  input: {
    display: 'none',
  },
}));

const ProtelisEditor: React.FC = () => {
  const classes = useStyles();

  const files: ProtelisFile[] = useSelector<RootState, ProtelisFile[]>(
    (state) => state.editor.files,
  );
  const path: string | null = useSelector<RootState, string | null>((state) => state.editor.open);
  const open: ProtelisSourceFile | null = useSelector<RootState, ProtelisSourceFile | null>(
    (state) => (state.editor.open
      ? getSourceFileAtPath(state.editor.files, state.editor.open)
      : null
    ),
  );
  const dispatch: Dispatch = useDispatch();

  const [value, setValue] = useState(open?.content);

  function handleShowValue(): void {
    if (path && value) dispatch(editFile({ path, content: value }));
  }

  return (
    <Paper className={classes.root}>
      <Grid container spacing={2} alignItems="stretch">
        <Grid item xs>
          <FileTreeView files={files} />
          <div>
            <label htmlFor="contained-button-file">
              <input
                accept="text/*"
                className={classes.input}
                id="contained-button-file"
                type="file"
                disabled
              />
              <Button
                variant="contained"
                component="span"
                color="primary"
                className={classes.button}
                startIcon={<CloudUpload />}
                disabled
              >
                Upload
              </Button>
            </label>
            <Button
              className={classes.button}
              startIcon={<LibraryAddRounded />}
              variant="contained"
              color="primary"
              component="span"
              onClick={() => dispatch(/* TODO: open dialog */ addFile({ name: 'new.pt', content: 'Hello world' }))}
            >
              Add
            </Button>
          </div>
        </Grid>
        <Grid item xs>
          <ControlledEditor
            height="77vh" // By default, it fully fits with its parent
            language="java"
            loading={<CircularProgress color="primary" /> as unknown as React.FC} // TODO: wrong typings by library
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
            <Button
              variant="contained"
              component="span"
              color="primary"
              onClick={() => dispatch(closeFile(path))}
              disabled={!open}
            >
              Close
            </Button>
          </div>
        </Grid>
      </Grid>
    </Paper>
  );
};

export default ProtelisEditor;
