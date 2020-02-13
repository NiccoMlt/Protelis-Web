import { AppBar, Toolbar, Typography } from '@material-ui/core';
import React from 'react';
import {
  Layer,
  Stage,
  Text,
  Circle,
} from 'react-konva';
import { Provider, ReactReduxContext, useSelector } from 'react-redux';
import { RootState } from '../../app/rootReducer';
import { NodePosition, ExecState } from './execSlice';

interface NodeCirclesProps {
//   widthScale: (w: number) => number;
//   heightScale: (h: number) => number;
  isVisible: (x: number, y: number) => boolean;
}

/** Component that draws a Konva Circle for each node in Redux Store. */
const NodeCircles: React.FC<NodeCirclesProps> = ({ isVisible }) => {
  const nodes = useSelector<RootState, NodePosition[] | null>((state) => state.exec.execution.drawing);
  return (
    <Layer>
      {
        nodes
          ?.map(({ id, x, y }, i) => {
            if (!isVisible(x, y)) console.warn(`Node ${id} (${i}/${nodes?.length}) is not visible`); // fixme
            return <Circle id={id} key={id} x={x} y={y} radius={5} fill="black" />;
          })
          || <Text fill="white" text="NO NODES FOUND" />
      }
    </Layer>
  );
};

const ExecInfo: React.FC = () => {
  const { id, status } = useSelector<RootState, ExecState['execution']>((state: RootState) => state.exec.execution);

  return (
    <>
      <Typography variant="h6">
        Execution&nbsp;
        {id && 'of '}
        {id && <i>{id}</i>}
        {id && ' has '}
        status:&nbsp;
        <b>{status}</b>
      </Typography>
    </>
  );
};

/** Konva canvas wrapper. */
const RenderCanvas: React.FC = () => {
  const stageWidth = window.innerWidth * 0.4;
  const stageHeight = window.innerHeight * 0.7;

  return (
    <ReactReduxContext.Consumer>
      {({ store }) => (
        <>
          <AppBar position="static">
            <Toolbar>
              <ExecInfo />
            </Toolbar>
          </AppBar>
          <Stage width={stageWidth} height={stageHeight}>
            <Provider store={store}>
              <NodeCircles isVisible={(x, y) => x <= stageWidth && y <= stageHeight} />
            </Provider>
          </Stage>
        </>
      )}
    </ReactReduxContext.Consumer>
  );
};
export default RenderCanvas;
