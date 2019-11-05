import React from 'react';
import { BrowserRouter as Router } from 'react-router-dom';
import {
  Grid, Paper, CssBaseline, createMuiTheme, useMediaQuery,
} from '@material-ui/core';
import { ThemeProvider, Theme } from '@material-ui/core/styles';

import ProtelisAppBar from './ProtelisAppBar';
import ProtelisEditor from './ProtelisEditor';
import { protelisTheme } from '../styles/theme';
import RenderCanvas from './RenderCanvas';

const App: React.FC = () => {
  const prefersDarkMode: boolean = useMediaQuery('(prefers-color-scheme: dark)');

  const theme: Theme = React.useMemo(
    () => createMuiTheme({
      palette: {
        type: prefersDarkMode ? 'dark' : 'light',
        primary: protelisTheme.palette.primary,
      },
    }),
    [prefersDarkMode],
  );

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Router>
        <div className="App">
          <ProtelisAppBar />
          <Grid container spacing={2}>
            <Grid item xs={6} sm={6} md={6} lg={6}>
              <ProtelisEditor />
            </Grid>
            <Grid item xs={6} sm={6} md={6} lg={6}>
              <Paper>
                <RenderCanvas />
              </Paper>
            </Grid>
          </Grid>
        </div>
      </Router>
    </ThemeProvider>
  );
};
export default App;
