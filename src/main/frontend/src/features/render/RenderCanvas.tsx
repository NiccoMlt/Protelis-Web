import { Layer, Stage, Star } from 'react-konva';
import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Button } from '@material-ui/core';
import { RootState } from '../../app/rootReducer';
import {EventBusStatus, ExecState, ExecutionStatus, NodePosition} from './execSlice';
import { ebConnect, ebSend } from '../../utils/EventBusMiddleware';

/** Konva canvas wrapper. */
const RenderCanvas: React.FC = () => {
  const connectionStatus = useSelector<RootState, EventBusStatus>((state) => state.exec.connection);
  const { drawing, id, status } = useSelector<RootState, ExecState['execution']>((state => state.exec.execution));
  const dispatch = useDispatch();

  return (
    <>
      {/* TODO: remove big testing text */}
      <h1>Connection status: {connectionStatus}</h1>
      <h2>Simulation "{id}" with status {status} draws: {JSON.stringify(drawing)}</h2>
      {/* TODO: remove big testing button */}
      <Button onClick={() => dispatch(ebConnect({ host: 'http://localhost:8080/eventbus' }))}>Connect</Button>
      {/* TODO: remove big testing button */}
      <Button
        onClick={() => dispatch(ebSend({
          address: 'protelis.web.exec.setup',
          message: 'self.nextRandomDouble()',
          headers: {},
        }))}
      >
        Send
      </Button>
      <Stage width={window.innerWidth * 0.4} height={window.innerHeight * 0.85}>
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
    </>
  );
};

export default RenderCanvas;
