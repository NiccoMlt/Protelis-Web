import { Layer, Stage, Star } from 'react-konva';
import React from 'react';

/** Konva canvas wrapper. */
const RenderCanvas: React.FC = () => (
  // TODO
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
);

export default RenderCanvas;
