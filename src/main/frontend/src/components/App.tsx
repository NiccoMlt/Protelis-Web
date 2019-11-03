import React from 'react';
import { BrowserRouter as Router } from 'react-router-dom';
import { Grid, Paper } from '@material-ui/core';
import {
  Stage, Layer, Star, Text,
} from 'react-konva';
import ProtelisAppBar from './AppBar';
import ProtelisEditor from './ProtelisEditor';

const App: React.FC = () => (
  <Router>
    <div className="App">
      <ProtelisAppBar />
      <Grid container spacing={2}>
        <Grid item xs={6} sm={6} md={6} lg={6}>
          <ProtelisEditor />
        </Grid>
        <Grid item xs={6} sm={6} md={6} lg={6}>
          <Paper>
            <Stage width={window.innerWidth} height={window.innerHeight}>
              <Layer>
                <Text text="Try to drag a star" />
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
);

export default App;
