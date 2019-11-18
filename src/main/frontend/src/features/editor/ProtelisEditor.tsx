import React, { useState } from 'react';
import { Theme, createStyles, makeStyles } from '@material-ui/core/styles';
import Paper from '@material-ui/core/Paper';
import Editor from '@monaco-editor/react';
import Grid from '@material-ui/core/Grid';
import Button from '@material-ui/core/Button';
import { LibraryAddRounded, CloudUpload } from '@material-ui/icons';
import FileTreeView from './FileTreeView';
import { ProtelisSourceFile } from '../../model/File';
import { CreateFileDialog } from './CreateFileDialog';

const useStyles = makeStyles((theme: Theme) => createStyles({
  root: {
    padding: theme.spacing(3, 2),
    flexGrow: 1,
    height: '100%',
  },
  button: {
    margin: theme.spacing(1),
  },
  input: {
    display: 'none',
  },
}));

type ProtelisEditorState = {
  files: Set<ProtelisSourceFile>
  open: ProtelisSourceFile | null
};

const ProtelisEditor: React.FC = () => {
  const classes = useStyles();

  const [{ files, open }, setState] = useState<ProtelisEditorState>(
    {
      files: new Set<ProtelisSourceFile>(),
      open: null,
    },
  );

  const [dialogOpen, setDialogOpen] = React.useState(false);

  const onDialogClose = (value: string | null) => {
    setDialogOpen(false);
    if (value) {
      setState({
        files: files.add({ name: value, content: '' }),
        open,
      });
    }
  };

  return (
    <Paper className={classes.root}>
      <Grid container spacing={2} alignItems="stretch">
        <Grid item xs={6} sm={6} md={6} lg={6}>
          <FileTreeView files={files} />
          <div>
            <label htmlFor="contained-button-file">
              <input
                accept="text/*"
                className={classes.input}
                id="contained-button-file"
                type="file"
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
              onClick={() => { setDialogOpen(true); }}
            >
              Add
            </Button>
            <CreateFileDialog name="new.pt" open={dialogOpen} onDialogClose={onDialogClose} />
          </div>
        </Grid>
        <Grid item xs={6} sm={6} md={6} lg={6}>
          <Editor
            language="java"
            theme="dark"
            value={open ? open.content : null}
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
