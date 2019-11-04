import React from 'react';
import { BrowserRouter as Router } from 'react-router-dom';
import {
  Grid, Paper, CssBaseline, createMuiTheme, useMediaQuery,
} from '@material-ui/core';
import {
  Stage, Layer, Star,
} from 'react-konva';
import { ThemeProvider, Theme } from '@material-ui/core/styles';

import ProtelisAppBar from './ProtelisAppBar';
import ProtelisEditor from './ProtelisEditor';
import { protelisTheme } from '../styles/theme';

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
                <Stage width={window.innerWidth * 0.5} height={window.innerHeight * 0.5}>
                  <Layer>
                    <Star
                      key={0}
                      x={Math.random() * window.innerWidth}
                      y={Math.random() * window.innerHeight}
                      numPoints={5}
                      innerRadius={20}
                      outerRadius={40}
                      fill="#89b717"
                      opacity={0.8}
                      draggable
                      rotation={Math.random() * 180}
                      shadowColor="black"
                      shadowBlur={10}
                      shadowOpacity={0.6}
                    />
                  </Layer>
                </Stage>
              </Paper>
            </Grid>
          </Grid>
        </div>
      </Router>
    </ThemeProvider>
  );
};
export default App;
