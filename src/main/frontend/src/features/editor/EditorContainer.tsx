import React from 'react';
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles';
import Paper from '@material-ui/core/Paper';
import Grid from '@material-ui/core/Grid';
import Button from '@material-ui/core/Button';
import { CloudUpload, LibraryAddRounded } from '@material-ui/icons';
import { useSelector } from 'react-redux';
import { ProtelisFile } from '../../model/File';
import { RootState } from '../../app/rootReducer';
import FileTreeView from './FileTreeView';
import { PtMonacoEditor } from '../../components/PtMonacoEditor';
import { AddDialogButton } from '../../components/AddDialogButton';
import { getFileAtPath } from '../../utils/fileUtils';

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

/** Container component that wraps TreeView and Editor. */
const EditorContainer: React.FC = () => {
  const classes = useStyles();

  const files: ProtelisFile[] = useSelector<RootState, ProtelisFile[]>((state) => state.editor.files);

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
                disabled // TODO
              />
              <Button
                variant="contained"
                color="primary"
                className={classes.button}
                startIcon={<CloudUpload />}
                disabled // TODO
              >
                Upload
              </Button>
            </label>
            <AddDialogButton
              startIcon={<LibraryAddRounded />}
              variant="contained"
              color="primary"
              className={classes.button}
              validator={(name: string) => !getFileAtPath(files, name)}
            />
          </div>
        </Grid>
        <Grid item xs>
          <PtMonacoEditor />
        </Grid>
      </Grid>
    </Paper>
  );
};

export default EditorContainer;
