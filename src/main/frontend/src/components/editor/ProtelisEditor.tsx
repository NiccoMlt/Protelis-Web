import React from 'react';
import { Theme, createStyles, makeStyles } from '@material-ui/core/styles';
import Paper from '@material-ui/core/Paper';
import Editor from '@monaco-editor/react';
import Grid from '@material-ui/core/Grid';
import FileTreeView from './FileTreeView';

const useStyles = makeStyles((theme: Theme) => createStyles({
  root: {
    padding: theme.spacing(3, 2),
  },
}));

const ProtelisEditor: React.FC = () => {
  const classes = useStyles();

  return (
    <>
      <Paper className={classes.root}>
        <Grid container spacing={2}>
          <Grid item xs={6} sm={6} md={6} lg={6}>
            <FileTreeView files={[
              {
                name: 'Folder',
                content: [
                  { name: 'Foo', content: 'Foo' },
                  { name: 'Bar', content: 'Bar' },
                  { name: 'Empty', content: [] },
                ],
              },
              { name: 'Empty', content: [] },
              { name: 'Pippo', content: 'Pippo' },
            ]}
            />
          </Grid>
          <Grid item xs={6} sm={6} md={6} lg={6}>
            <Editor
              height="70vh"
              language="java"
              theme="dark"
              options={{
                automaticLayout: true,
                minimap: { enabled: false },
              }}
            />
          </Grid>
        </Grid>

      </Paper>
    </>
  );
};

export default ProtelisEditor;
