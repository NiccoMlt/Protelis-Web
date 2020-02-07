import { createSlice, PayloadAction } from '@reduxjs/toolkit';

export interface NodePosition {
  id: string;
  x: number;
  y: number;
}

export type EventBusStatus = 'open' | 'closed';
type AlchemistStatus = 'INIT' | 'READY' | 'PAUSED' | 'RUNNING' | 'TERMINATED';
export type ExecutionStatus = 'disconnected' | 'connecting' | AlchemistStatus;

export interface ExecState {
  connection: EventBusStatus;
  execution: {
    drawing: NodePosition[] | null;
    id: string | null;
    status: ExecutionStatus;
  };
}

const initialState: ExecState = {
  connection: 'closed',
  execution: {
    drawing: null,
    id: null,
    status: 'disconnected',
  },
};

const execSlice = createSlice({
  name: 'exec',
  initialState,
  reducers: {
    ebConnected(state): void {
      state.connection = 'open';
    },
    ebDisconnected(state): void {
      state.connection = 'closed';
    },
    setId(state, action: PayloadAction<string>): void {
      state.execution.id = action.payload
    },
    drawInit(state, action: PayloadAction<NodePosition[]>): void {
      console.log("INIT");
      state.execution = {
        status: 'RUNNING',
        drawing: action.payload,
        id: state.execution.id,
      };
    },
    drawStep(state, action: PayloadAction<NodePosition[]>): void {
      console.log("STEP");
      state.execution = {
        status: state.execution.status,
        drawing: action.payload,
        id: state.execution.id,
      };
    },
    drawEnd(state, action: PayloadAction<NodePosition[]>): void {
      console.log("DONE");
      state.execution = {
        status: 'TERMINATED',
        drawing: action.payload,
        id: state.execution.id,
      };
    },
  },
});

export const {
  drawEnd,
  drawInit,
  drawStep,
  ebConnected,
  ebDisconnected,
  setId,
} = execSlice.actions;

export default execSlice.reducer;
