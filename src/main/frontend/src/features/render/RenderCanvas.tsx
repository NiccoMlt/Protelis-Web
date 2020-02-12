import React from 'react';
import {
  Layer,
  Stage,
  Text,
  Circle,
} from 'react-konva';
import { Provider, ReactReduxContext, useSelector } from 'react-redux';
import { RootState } from '../../app/rootReducer';
import { NodePosition } from './execSlice';

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

/** Konva canvas wrapper. */
const RenderCanvas: React.FC = () => {
  const stageWidth = window.innerWidth * 0.4;
  const stageHeight = window.innerHeight * 0.85;

  return (
    <ReactReduxContext.Consumer>
      {({ store }) => (
        <>
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
