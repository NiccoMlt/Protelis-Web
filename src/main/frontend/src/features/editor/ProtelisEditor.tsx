import React from 'react';
import { Theme, createStyles, makeStyles } from '@material-ui/core/styles';
import Paper from '@material-ui/core/Paper';
import Editor from '@monaco-editor/react';
import Grid from '@material-ui/core/Grid';
import Button from '@material-ui/core/Button';
import { LibraryAddRounded, CloudUpload } from '@material-ui/icons';
import { useSelector, useDispatch } from 'react-redux';
import { Dispatch } from 'redux';
import { PayloadAction } from '@reduxjs/toolkit';
import { ProtelisSourceFile, ProtelisFile } from '../../model/File';
import { RootState } from '../../app/rootReducer';
import { addFile } from './editorSlice';
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
  const open: ProtelisSourceFile | null = useSelector<RootState, ProtelisSourceFile | null>(
    (state) => state.editor.open,
  );
  const dispatch: Dispatch = useDispatch();

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
              onClick={(): PayloadAction<ProtelisSourceFile> => dispatch(/* TODO: open dialog */ addFile({ name: 'new.pt', content: 'Hello world' }))}
            >
              Add
            </Button>
          </div>
        </Grid>
        <Grid item xs>
          <Editor
            language="java"
            theme="dark"
            value={open ? open.content : null}
            height="80vh"
            options={{
              automaticLayout: true,
              minimap: { enabled: false },
            }}
          />
        </Grid>
      </Grid>
    </Paper>
  );
};

export default ProtelisEditor;
