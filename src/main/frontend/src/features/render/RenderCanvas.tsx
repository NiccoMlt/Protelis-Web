import { Button } from '@material-ui/core';
import React from 'react';
import {
  Layer,
  Stage,
  Star,
  Text,
} from 'react-konva';
import {
  Provider,
  ReactReduxContext,
  useDispatch,
  useSelector,
} from 'react-redux';
import { RootState } from '../../app/rootReducer';
import { NodePosition, drawStep } from './execSlice';

const NodeStars: React.FC = () => {
  const nodes = useSelector<RootState, NodePosition[] | null>((state) => state.exec.execution.drawing);
  return (
    <>
      {
        nodes?.map(({ id, x, y }) => (
          <Star
            id={id}
            key={id}
            x={x * window.innerWidth}
            y={y * window.innerHeight}
            numPoints={5}
            innerRadius={20}
            outerRadius={40}
            fill="#89b717"
            opacity={0.8}
            draggable
            rotation={180}
            shadowColor="black"
            shadowBlur={10}
            shadowOpacity={0.6}
          />
        )) || <Text fill="white" text="NO NODES FOUND" />
      }
    </>
  );
};

/** Konva canvas wrapper. */
const RenderCanvas: React.FC = () => {
  const dispatch = useDispatch();
  const nodes = useSelector<RootState, NodePosition[]>((state) => state.exec.execution.drawing || []);
  function onAddStar(): void {
    dispatch(drawStep([
      ...nodes,
      {
        id: Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15),
        x: Math.random(),
        y: Math.random(),
      },
    ]));
  }
  return (
    <ReactReduxContext.Consumer>
      {({ store }) => (
        <>
          <Stage width={window.innerWidth * 0.4} height={window.innerHeight * 0.85}>
            <Provider store={store}>
              <Layer>
                <NodeStars />
              </Layer>
            </Provider>
          </Stage>
          <Button
            variant="contained"
            color="primary"
            onClick={onAddStar}
          >
            Add a Star
          </Button>
        </>
      )}
    </ReactReduxContext.Consumer>
  );
};
export default RenderCanvas;
